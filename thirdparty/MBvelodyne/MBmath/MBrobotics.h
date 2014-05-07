#ifndef BUNTING_ROBOTICS
#define BUNTING_ROBOTICS

#include "MBmath.h"

#define RX_28_HINGE_OFFSET (25.5)
#define RX_28_SIDE_OFFSET (5.7)
#define RX_28_LONG_HOLE_OFFSET (35.8)
#define RX_28_SIDE_HOLE_OFFSET (15)
#define RX_28_HORN_TO_IDLER (35.5 + 2.75*2)

#define EX_106_DOUBLE_MATE (110)
#define EX_106_HORN_TO_IDLER (46.0 + 3.0*2)
#define EX_106_HINGE_RADIUS_BUNTING (30.0)

#define RX_64_HORN_TO_IDLER (41.0 + 3.0*2)
#define RX_64_HINGE_RADIUS (30.0)
#define RX_64_HINGE_WIDTH (RX_64_HORN_TO_IDLER + 2*2.0)

#define RX_64_ANGLE_BRACKET_BUNTING (23.5)
#define RX_28_COLUMN_BUNTING (45.0)
#define END_EFFECTOR_CC (35.0)

typedef enum {
	UNSUPPORTED,
	THREE_DOF,
	SIX_DOF
} Configuration;

//!  Combines location and orientaiton into a vector.
/*!
 Many of the functions require a size 7 vector combining location and orientation quatenrion, so this function append the quaternion to the location vector and returns.
 */
const MBvector inverseKinematics3DOF( MBmatrix& linkConfig, MBvector& location );

//!  Computes forward kinematics to get end effector location and orientation for the 6DOF arm.
/*!
 For the arm defined by axis ZYYXZY, the end effector location and orientation is calculated.
 */
const MBvector forwardKinematics( MBmatrix& linkConfig, MBvector& angles );

//!  (Deprecated) Converts the motor angles to match the arm.
/*!
	This function very specifically converts the solution angles to an AX-12 arm.
 */
const MBvector convertToMotorAngles(MBvector& desiredAngles);

//!  Combines location and orientaiton into a vector.
/*!
	Many of the functions require a size 7 vector combining location and orientation quatenrion, so this function append the quaternion to the location vector and returns.
 */
const MBvector makeEndEffectorVector(const MBvector& location, const MBquaternion& orientation);

class MBInverseKinematics {
private:
	float t;	// the angle of the 6th axis, to be iterated
	float savedT;
	MBvector motorAngles;

	MBmatrix linkConfiguration;
	//MBmatrix axisConfiguration;

	//bool canSolveThisConfiguration;
	Configuration configuration;

	MBmatrix baseLocation;	// normally 0,0,0
	MBquaternion baseOrientation;	// normally 1,0,0,0 (no rotation)

	MBquaternion endEffectorQuaternion;
	MBmatrix endEffectorLocation;

	// 6DOF specific stuff:
	MBmatrix P3location;
	MBmatrix P4location;
	MBmatrix P5location;

	MBmatrix P[7];	// 6DOF means 7 links


	//!  Solves for the ZYYXZY arm.
	/*!
	 This function uses a circle based on the radius of link 5, located at the 6th motor (Y).  A 3DOF solution is given based on a point on the circle (ZYYXZ), since the configuration of motors 4 and 5 do not effect the location of link 5.  The angle of the 6th motor is guessed, then the 3DOF solution determined if motor 5 can be placed tangent to the circle.  The angle is adjusted until the error is below a threshold.
	 */
	int iterativeZYYXZYInverseKinematics( MBmatrix& linkConfig, MBvector& location );
public:

	//!  Set the base location and orientation.
	/*!
	 This sets the location and orientation of the base.  This does not need to be called, but will default to location at the origin and no rotation.  This is useful when needing to call for inverse kinematics in global coordinates where the segment location is known.
	 */
	void setBase( MBmatrix& location, MBquaternion& orientation);

	//!  Computes Inverse Kinematics.
	/*!
	 Calling this computes the inverse kinematics solution based on the configured setup.  Configuration must be performed first.
	 */
	int compute( MBvector& location );

	//!  Returns the solution of inverse kinematics.
	/*!
	 The solution to the inverse kinematics is returned by calling this function.
	 */
	const MBvector& getMotorAngles();

	//!  Sets the link configuration for the arm.
	/*!
	 The link length configuration is set using a matrix of 3D column vectors, where each ascending column is the next link.  The links typically should extend in the x direction, as a general solution has not yet been found.  If an unsupoorted configuration is implemented, the error is set.
	 */
	void setConfiguration( MBmatrix &linkConfig );//, MBmatrix &axisConfig );

	MBInverseKinematics();
	~MBInverseKinematics();
};

#endif
