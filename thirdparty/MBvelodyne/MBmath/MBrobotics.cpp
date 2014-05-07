#include "MBrobotics.h"

#ifdef _cplusplus
extern "C"
{
#endif

	MBInverseKinematics::MBInverseKinematics()
	{
		savedT = MB_PI;
		t = 0;
		motorAngles.set_size(6);
		endEffectorLocation.set_size(3,1);

		baseLocation.set_size(3,1);
		configuration = UNSUPPORTED;
	}

	MBInverseKinematics::~MBInverseKinematics()
	{

	}

	int MBInverseKinematics::compute( MBvector& location )
	{
		switch (configuration) {
			case THREE_DOF:

				break;

			case SIX_DOF:
				return iterativeZYYXZYInverseKinematics( linkConfiguration, location );
				break;

			default:
				break;
		}
		return -1;
	}

	const MBvector& MBInverseKinematics::getMotorAngles()
	{
		return motorAngles;
	}

	void MBInverseKinematics::setConfiguration( MBmatrix &linkConfig )//, MBmatrix &axisConfig )
	{
		linkConfiguration = linkConfig;
		//axisConfiguration = axisConfig;
		bool test = true;

		// This setter must ensure that this inverse kinematics implementation can solve it
		if ((linkConfiguration.cols() == 4)) {// && (axisConfiguration->cols() == 3)) {
			for (int i = 1; i < 4; i++) {
				test &= linkConfiguration(1,i) == 0;
			}
			if ((linkConfiguration(0,1) == 0) &&
				(linkConfiguration(2,2) == 0) && test)
			{
				configuration = THREE_DOF;
			}

		} else if ((linkConfiguration.cols() == 7)) {// && (axisConfiguration->cols() == 6)) {
			for (int i = 1; i < 7; i++) {
				test &= linkConfiguration(1,i) == 0;
			}
			for (int i = 4; i < 7; i++) {
				test &= linkConfiguration(2,i) == 0;
			}

			if ((linkConfiguration(0,1) == 0) &&
				(linkConfiguration(2,2) == 0) && test)
			{
				configuration = SIX_DOF;
			}
		}
		if (configuration != UNSUPPORTED) {
			for (int j = 0; j < linkConfiguration.cols(); j++) {
				P[j].set_size(3,1);
				for (int i = 0; i < 3; i++) {
					P[j](i, 0) = linkConfiguration(i,j);
				}
			}
		}
	}
	
	void MBInverseKinematics::setBase(MBmatrix& location, MBquaternion& orientation)
	{
		if ((location.rows() == 3) && (location.cols() == 1)) {
			baseLocation = location;
		}
		baseOrientation(0) = orientation(0);
		baseOrientation(1) = -orientation(1);	// negate the rotation
		baseOrientation(2) = -orientation(2);
		baseOrientation(3) = -orientation(3);
	}

	// Uses an iterative method to solve for all motor angles given location and final oreintation in quaternions
	int MBInverseKinematics::iterativeZYYXZYInverseKinematics( MBmatrix& linkConfig, MBvector& location )
	{
		float pitch, roll, yaw;
		MBvector newLocation(3);
		MBmatrix baseOrientationMatrix;
		//MBmatrix &P[0] = linkConfig[0];
		//MBmatrix &P[1] = linkConfig[1];
		//MBmatrix &P[2] = linkConfig[2];
		//MBmatrix &P[3] = linkConfig[3];
		//MBmatrix &P[4] = linkConfig[4];
		//MBmatrix &P[5] = linkConfig[5];

		// Let's play around for a little bit.  First let's figure out where P5 should be based on quaternions
		//MBquaternion endEffectorQuaternion;
		//endEffectorQuaternion.makeFromAngleAndAxis( 0, );
		//endEffectorQuaternion = GLKQuaternionMultiply(endEffectorQuaternion, GLKQuaternionMakeWithAngleAndAxis(MB_PI/2, 0, 1, 0));

		endEffectorQuaternion(0) = location(3);
		endEffectorQuaternion(1) = location(4);
		endEffectorQuaternion(2) = location(5);
		endEffectorQuaternion(3) = location(6);

		// First translate relativeto the base location:
		newLocation(0) = location(0);// - baseLocation(0,0);
		newLocation(1) = location(1);// - baseLocation(1,0);
		newLocation(2) = location(2);// - baseLocation(2,0);
		newLocation -= baseLocation;
		// Then rotate about the base's orientation
		baseOrientationMatrix = baseOrientation.makeRotationMatrix();
		newLocation = baseOrientationMatrix * newLocation;
		// Also apply the new angle to the endEffectorQuat:
		endEffectorQuaternion = baseOrientation * endEffectorQuaternion;

		// Now apply!
		//endEffectorLocation(0,0) = newLocation(0) - P[0](0,0);
		//endEffectorLocation(1,0) = newLocation(1) - P[0](1,0);
		//endEffectorLocation(2,0) = newLocation(2) - P[0](2,0) - P[1](2,0);
		endEffectorLocation = newLocation - P[0];
		endEffectorLocation(2,0) -=  P[1](2,0);
		//endEffectorQuaternion.name("endEffectorQuaternion");
		//endEffectorQuaternion.print_stats();

		MBmatrix P6orientation = endEffectorQuaternion.makeRotationMatrix();
		//P6orientation.name("P6orientation");
		//P6orientation.print_stats();
		MBmatrix P6location = endEffectorLocation - (P6orientation * P[6]);
		//P6location = GLKVector3Subtract( endEffectorLocation, P6location );
		//printf("P6location: x: %.2f\ty: %.2f\tz: %.2f\n", P6location(0,0), P6location(1,0), P6location(2,0));

		// Vector following the radius at t = 0:
		MBmatrix u(3,1);
		u = P6orientation * P[6];
		u.normalize();
		u *= -1;	//should be in opposite direction
		//u.name("u");
		//u.print_stats();

		// Vector that defines the plane the circle is on:
		MBmatrix n(3,1);
		n(1,0) = 1;		// This particular method only fors for a 6th axis pointing in the y-direction
		n = P6orientation * n;
		n.normalize();


		// Have to implement a search algorithm to determine t
		// Check for a NaN:
		if (t != t) {
			t = 0;
		}
		// Some parameters to define the circle:
		MBmatrix v1 = u * P[5](0,0);
		MBmatrix v2 = n * P[5](0,0);
		v2 = u.cross(v2);

		float error = 1000;
		float olderror = error;
		int iterations = 0;
		float direction = 1;

		float tibiaAngle;
		float femurAngle;
		float coxaAngle;


		MBmatrix P34norm;
		MBmatrix P3NewUpNorm(3,1);
		//MBmatrix P4location(3,1);
		//MBmatrix P5location(3,1);
		MBmatrix R1, R2, R3;
		MBmatrix uc;
		MBmatrix P34;

		while ((error > .005118) && (iterations < 1000)) {
			iterations++;

			// based on t, determine P5location:
			P5location = P6location + cos(t)*v1 + sin(t)*v2;

			// Perform some IK type stuff on this point:
			float xyDist = sqrtf(P5location(0,0)*P5location(0,0) + P5location(1,0)*P5location(1,0));
			float jointDist = sqrtf( powf( P5location(2,0), 2.0) + powf( P5location(0,0), 2.0) + powf( P5location(1,0), 2.0) );
			float tibiaDist = sqrtf( powf( P[3](2,0), 2.0) + powf( P[3](0,0)+P[4](0,0), 2.0) );
			float femurDist = P[2](0,0);
			tibiaAngle = MB_PI + atan2f(P[3](2,0), P[3](0,0)+P[4](0,0)) - ( acosf( ( femurDist*femurDist + tibiaDist*tibiaDist - jointDist*jointDist) / (2*tibiaDist*femurDist) ));

			float femurDiff  = acosf( ( femurDist*femurDist - tibiaDist*tibiaDist + jointDist*jointDist) / (2*jointDist*femurDist) );
			femurAngle = -(atan2f(P5location(2,0), xyDist) + femurDiff);
			coxaAngle = atan2f( P5location(1,0), P5location(0,0) );

			R1.makeZRotation(coxaAngle);
			R2.makeYRotation(femurAngle);
			R3.makeYRotation(tibiaAngle);

			P4location = P[4];
			P4location = R3*(P4location + P[3]);
			P4location = R2*(P4location + P[2]);
			P4location = R1*(P4location + P[1]);
			//MBmatrix P3location = P3;
			P3location = P[3];
			P3location = R3*P3location;
			P3location = R2*(P3location + P[2]);
			P3location = R1*(P3location + P[1]);
			P34 = P4location - P3location;
			P34norm = P34;
			P34norm.normalize();
			//printf("P34: x: %.2f\ty: %.2f\tz: %.2f", P34.x, P34.y, P34.z);

			uc = n.cross(P5location - P6location);
			uc.normalize();
			// also multiply by cos(t), however all we need is the normal vector.  Done!
			float ucDotP34 = uc.dot( P34norm );

			if (ucDotP34 >= 0) {
				error = ucDotP34;
				if (error > olderror) {
					direction *= -.99;
				}
				t += ucDotP34/2 * direction;
			} else if (ucDotP34 < 0){

				error = -ucDotP34;
				if (error > olderror) {
					direction *= -.99;
				}
				t += ucDotP34/2 * direction;
			} else {
				// We have a NaN somewhere, attempt to recover:
				savedT += MB_PI/500;
				error = 1313;
				t = savedT;
			}

		}

		// We now have an acceptable solution for the last motor:
		t += MB_PI;
		if (t < 0) {
			t = 2*MB_PI - fmodf( -t, 2*MB_PI);
		} else {
			t = fmodf( t, 2*MB_PI);
		}
		t -= MB_PI;
		roll = t;

		// Using that solution, we can solve for the 5th joint angle using the cross product definition method:
		MBmatrix P45norm = P6location - P5location;
		P45norm.normalize();
		MBmatrix P34normCrossP45norm = P34norm.cross( P45norm );
		float P34normCrossP45normDotUc = P34normCrossP45norm.dot( uc );
		yaw = asinf( P34normCrossP45normDotUc );
		if (((P34norm.dot( P45norm )) < 0)) {
			yaw = MB_PI - yaw;
		}
		yaw = fmodf(yaw + MB_PI, 2*MB_PI) - MB_PI;

		// this is for getting a later orientation (does not need to be iterated)
		P3NewUpNorm(2,0) = 1;
		P3NewUpNorm = R1*R2*R3 * P3NewUpNorm;
		// Cross product definition:
		MBmatrix P3UpCrossUc = P3NewUpNorm.cross( uc );
		float P3UpCrossUcDotP34norm = P3UpCrossUc.dot( P34norm );
		pitch = asinf( P3UpCrossUcDotP34norm );
		if (((P3NewUpNorm.dot( uc )) < 0)) {
			pitch = MB_PI - pitch;
		}
		pitch = fmodf(pitch + MB_PI, 2*MB_PI) - MB_PI;

		// Load the solved angles:
		motorAngles(0) = coxaAngle;
		motorAngles(1) = femurAngle;
		motorAngles(2) = tibiaAngle;
		motorAngles(3) = pitch;
		motorAngles(4) = yaw;
		motorAngles(5) = roll;

		if ((coxaAngle != coxaAngle) ||
			(femurAngle != femurAngle) ||
			(tibiaAngle != tibiaAngle) ||
			(pitch != pitch) ||
			(yaw != yaw) ||
			(roll != roll)) {
			return -1;
		}
		return 0;
	}


	// Specify a location vector with motor angles of the last 3DOF:
	const MBvector inverseKinematics3DOF( MBmatrix *linkConfig, MBvector& location )
	{
		MBvector ret(6);
		ret.name("invereseKinematics Result");
		float x = location(0) - linkConfig[0](0,0);
		float y = location(1) - linkConfig[0](1,0);
		float z = location(2) - linkConfig[0](2,0) - linkConfig[1](2,0);

		// This method is not that great.  Ideally I need to apply an angle to the motor, not have things operate in CC
		// I need to modify the configuration of the "tibia".  The tibia has 4DOF, three are meant for orientation, one for position
		// I cannot think of IK being only for simple construction of linear tibia, instead need to consider offset in x, and y & z

		// For the above description, I need to figure out the location of the end effect relative to the tibia first.
		MBmatrix R4(3,3), R5(3,3), R6(3,3);
		R4.makeXRotation(location(3));
		R5.makeZRotation(location(4));
		R6.makeYRotation(location(5));

		//WOAH! CURRENTLY BROKEN!!!!!
		//MBmatrix trueTibia;
		MBmatrix trueTibia = R4*(R5*(R6*linkConfig[5] + linkConfig[4]) + linkConfig[3]) + linkConfig[2];
		//MBmatrix trueTibia = R4*(R5*(R6*P6 + P5) + P4) + P3;
		//trueTibia(2,0) -= 25;
		trueTibia.name("\ntrueTibia");
		//trueTibia.print_stats();
		//printf("x: %.2f\ty:%.2f\tz:%.2f", trueTibia(0,0), trueTibia(1,0), trueTibia(2,0));


		/////////////
		// First we can compute the coxa angle:
		/////////////
		// find the true point of the beginning of the tibia:
		//printf("xyMag = %.2f", xyMag);
		//float xyMag = femurd * cos(femurTheta);
		//float tibiaXYMag = sqrtf(trueTibia(1,0)*trueTibia(1,0) + pow( -trueTibia(2,0)*sin(femurTheta + tibiaTheta) + trueTibia(0,0)*cos(femurTheta + tibiaTheta) ,2.0));
		//float coxaThetaOffset = acos( (-tibiaXYMag*tibiaXYMag + xyMag*xyMag + (x*x + y*y)) /(2*xyMag*sqrtf(x*x + y*y)));

		// method to figure out coxa without femur angle dependence:
		float xyMag = sqrtf(x*x + y*y);
		float coxaThetaOffset = asin( trueTibia(1,0) / xyMag );
		if (coxaThetaOffset != coxaThetaOffset) {
			coxaThetaOffset = 0;
		}
		float coxaTheta = atan2f(y, x) - coxaThetaOffset;
		//printf("coxaTheta = %.2f", coxaTheta);

		/////////////
		// Trying out new IK for unconventional tibia:
		/////////////

		// based on desired x, y, z, we can find d:
		float femurd = linkConfig[1](0,0);
		float d = sqrtf(x*x + y*y + z*z);
		// then we can find v:
		float v = ( d*d - trueTibia(2,0)*trueTibia(2,0) - trueTibia(0,0)*trueTibia(0,0) - trueTibia(1,0)*trueTibia(1,0) - femurd*femurd)/(2.0 * femurd);
		float C = sqrt(trueTibia(2,0)*trueTibia(2,0) + trueTibia(0,0)*trueTibia(0,0) );
		//float delta = asin(trueTibia(0,0) / C);
		float delta = acos( -trueTibia(2,0) / C);
		float tibiaTheta = -(asin( v/C ) - delta);
		if (C == 0) {
			//tibiaTheta = 0;
		}
		//printf("v = %.3f", v);

		//printf("tibiaTheta = %.3f", tibiaTheta);





		/////////////
		// With the known tibia solution, solve for the femur:
		/////////////
		// The method was not genral for both X and Y.  Let's try this:
		// First, back off rotation (probably a pretty messy method):
		float backOffX = x * cos(coxaTheta) + y * sin(coxaTheta);

		// Now try old method:
		float zxMag = sqrtf(trueTibia(0,0)*trueTibia(0,0) + trueTibia(2,0)*trueTibia(2,0));
		float femurOffset = atan2f(z, backOffX);
		// Law of Sines solution:
		//float femurTheta = MB_PI - (femurOffset + asin(zxMag * sin( MB_PI + tibiaTheta) / sqrtf(x*x + z*z)));
		// Law of Cosines solution:
		float femurTheta = - (femurOffset + acos((femurd*femurd + (backOffX*backOffX + z*z) - zxMag*zxMag)/(2*femurd*sqrtf(backOffX*backOffX + z*z))));



		//printf("y = %.2f\tfemurTheta = %.3f\toldIK = %.3f", y, femurTheta, angularVelocities[1]);

		if (tibiaTheta == tibiaTheta) {
			ret(2) = tibiaTheta;
		}
		if (femurTheta == femurTheta) {
			ret(1) = femurTheta;
		}
		if (coxaTheta == coxaTheta) {
			ret(0) = coxaTheta;
		}

		ret(3) = location(3);
		ret(4) = location(4);
		ret(5) = location(5);

		return ret;
	}


	// Computes forward kinematics given set of link definitions and actuator angles
	const MBvector forwardKinematics( MBmatrix& linkConfig, MBvector& angles )
	{
		MBmatrix p[6], configuredP;
		MBmatrix R[6], newR;

		for (int i = 0; i<6; i++)
		{
			p[i].set_size(3, 1);
			R[i].set_size(3, 3);
			for (int j = 0; j < 3; j++) {
				p[i](j,0) = linkConfig(j,i);
			}
		}

		MBmatrix finalRotation;

		R[0].makeZRotation(angles(0));
		R[1].makeYRotation(angles(1));
		R[2].makeYRotation(angles(2));
		R[3].makeXRotation(angles(3));
		R[4].makeZRotation(angles(4));
		R[5].makeYRotation(angles(5));

		newR = R[5];
		configuredP = R[5] * p[5];
		for (int i=4;i>=0;i--){
			MBmatrix oldR = newR;
			MBmatrix oldP = configuredP;

			newR = R[i]*newR;
			configuredP = R[i]*(oldP + p[i]);
		}
		// configuredP = R5 * p5
		// configuredP =

		finalRotation = R[0]*R[1]*R[2]*R[3]*R[4]*R[5];
		//finalRotation.name("finalRotation");
		//finalRotation.print_stats();

		//configuredP.name("configuredP");
		//configuredP.print_stats();

		MBvector quat = finalRotation.quaternion();
		MBvector eulerAngles = quat.xyzAnglesFromQuaternion();
		MBvector ret(7);
		ret.name("forwardKinematicsResult");
		ret(0) = configuredP(0,0);
		ret(1) = configuredP(1,0);
		ret(2) = configuredP(2,0);
		ret(3) = quat(0);
		ret(4) = quat(1);
		ret(5) = quat(2);
		ret(6) = quat(3);
		return ret;
	}

	// This function converts the desired angles to actual motor values
	// There is no calibration, this assumes perfect construction
	const MBvector convertToMotorAngles(MBvector& desiredAngles)
	{
		/*
		 MBvector ret(6);
		 ret.name("Converted Motor Angles");

		 ret(0) = desiredAngles(0) + (-MB_PI/4.0);
		 ret(1) = -desiredAngles(1) + (-MB_PI/2);
		 ret(2) = -desiredAngles(2) + (MB_PI/2);
		 ret(3) = desiredAngles(3);
		 ret(4) = -desiredAngles(4);
		 ret(5) = desiredAngles(5);



		 ret += 5.0*MB_PI/6.0;	// give all a 150 degree offset (now 0 is 150).
		 ret *= 180.0/MB_PI;
		 */
		MBvector ret;
		ret = desiredAngles;
		ret *= 180.0/MB_PI;

		return ret;
	}

	const MBvector makeEndEffectorVector(const MBvector& location, const MBquaternion& orientation) {
		MBvector ret(7);
		for(int i = 0; i < 3; i++)
		{
			ret(i) = location.Val(i,0);
		}
		for(int i = 0; i < 4; i++)
		{
			ret(i+3) = orientation.Val(i,0);
		}
		return ret;
	}
	
#ifdef _cplusplus
}
#endif
