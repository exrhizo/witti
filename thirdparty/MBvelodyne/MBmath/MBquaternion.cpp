#include "MBmath.h"

#ifdef _cplusplus
extern "C"
{
#endif

	MBdouble & MBquaternion::operator() (const int& m_des)
    {
        return (*this)(m_des, 0);
        //return garbage;
    }

	int MBquaternion::makeFromAngleAndAxis(MBdouble angle, MBvector& axis)
	{
		MBdouble omega, s;
		if (axis.size() != 3) {
#ifdef DEBUG_MATH
			printf("Error! makeFromAngleAndAxis must only take an axis vector of size 3.\n");
#endif
			return -1;
		}
		MBdouble l = sqrt(axis*axis);

		if (l > 0) {
			omega = 0.5 * angle;
			s = sin(omega) / l;

			(*this)(0,0) = (float)cos(omega);
			(*this)(1,0) = (float)s * axis(0);
			(*this)(2,0) = (float)s * axis(1);
			(*this)(3,0) = (float)s * axis(2);
		} else {
			// perform no rotation:
			(*this)(0,0) = 1;
			(*this)(1,0) = 0;
			(*this)(2,0) = 0;
			(*this)(3,0) = 0;
		}
		return 0;
	}

	const MBmatrix& MBquaternion::makeRotationMatrix()
	{
		//MBmatrix result(3, 3);
		(*scratch).set_size(3, 3);
		MBquaternion me = *this;

		MBdouble n = 1.0f/sqrt(me(0,0)*me(0,0) + me(1,0)*me(1,0) + me(2,0)*me(2,0) + me(3,0)*me(3,0));

		MBmatrix q = n*me;

		(*scratch)(0,0) = 1 - 2*q(2,0)*q(2,0) - 2*q(3,0)*q(3,0);
		(*scratch)(1,0) =     2*q(1,0)*q(2,0) - 2*q(0,0)*q(3,0);
		(*scratch)(2,0) =     2*q(1,0)*q(3,0) + 2*q(0,0)*q(2,0);

		(*scratch)(0,1) =     2*q(1,0)*q(2,0) + 2*q(0,0)*q(3,0);
		(*scratch)(1,1) = 1 - 2*q(1,0)*q(1,0) - 2*q(3,0)*q(3,0);
		(*scratch)(2,1) =     2*q(2,0)*q(3,0) - 2*q(0,0)*q(1,0);

		(*scratch)(0,2) =     2*q(1,0)*q(3,0) - 2*q(0,0)*q(2,0);
		(*scratch)(1,2) =     2*q(2,0)*q(3,0) + 2*q(0,0)*q(1,0);
		(*scratch)(2,2) = 1 - 2*q(1,0)*q(1,0) - 2*q(2,0)*q(2,0);

		return (*scratch).T();
	}

	const MBmatrix& MBquaternion::makeRotationMatrix4()
	{
		//MBmatrix result(3, 3);
		//(*scratch).set_size(3, 3);
		MBquaternion me = *this;
		(*scratch).makeI(4);

		MBdouble n = 1.0f/sqrt(me(0,0)*me(0,0) + me(1,0)*me(1,0) + me(2,0)*me(2,0) + me(3,0)*me(3,0));

		MBmatrix q = n*me;

		(*scratch)(0,0) = 1 - 2*q(2,0)*q(2,0) - 2*q(3,0)*q(3,0);
		(*scratch)(1,0) =     2*q(1,0)*q(2,0) - 2*q(0,0)*q(3,0);
		(*scratch)(2,0) =     2*q(1,0)*q(3,0) + 2*q(0,0)*q(2,0);

		(*scratch)(0,1) =     2*q(1,0)*q(2,0) + 2*q(0,0)*q(3,0);
		(*scratch)(1,1) = 1 - 2*q(1,0)*q(1,0) - 2*q(3,0)*q(3,0);
		(*scratch)(2,1) =     2*q(2,0)*q(3,0) - 2*q(0,0)*q(1,0);

		(*scratch)(0,2) =     2*q(1,0)*q(3,0) - 2*q(0,0)*q(2,0);
		(*scratch)(1,2) =     2*q(2,0)*q(3,0) + 2*q(0,0)*q(1,0);
		(*scratch)(2,2) = 1 - 2*q(1,0)*q(1,0) - 2*q(2,0)*q(2,0);

		return (*scratch).T();
	}

	MBquaternion & MBquaternion::operator *= (const MBquaternion& param)  // A *= B (A = A*B)
    {
        MBquaternion q1, q2, result;
		q1 = *this;
		q2 = param;
        result(0,0) = q1(0,0)*q2(0,0) - q1(1,0)*q2(1,0) - q1(2,0)*q2(2,0) - q1(3,0)*q2(3,0);
		result(1,0) = q1(0,0)*q2(1,0) + q1(1,0)*q2(0,0) + q1(2,0)*q2(3,0) - q1(3,0)*q2(2,0);
		result(2,0) = q1(0,0)*q2(2,0) - q1(1,0)*q2(3,0) + q1(2,0)*q2(0,0) + q1(3,0)*q2(1,0);
		result(3,0) = q1(0,0)*q2(3,0) + q1(1,0)*q2(2,0) - q1(2,0)*q2(1,0) + q1(3,0)*q2(0,0);

		*this = result;
        return *this;
    }

	void MBquaternion::setupScratch()
	{
		if (scratch == NULL) {
#ifdef UNIX
			scratch = new MBquaternion;
#else
			scratch = (MBquaternion *) malloc(sizeof(MBquaternion));
#endif
		}
	}

	const MBquaternion& MBquaternion::operator * (const MBquaternion& param) const
    {
        MBquaternion result;
        (*scratch) = *this; // this is messy, should be able to doMBmatrix result = *this
        (*(MBquaternion *)scratch) *= param;
        return (*(MBquaternion *)scratch);
    }

	MBquaternion & MBquaternion::operator= (const MBmatrix& param)
	{
		if ((param.Cols() != 1) && (param.Rows() != 4)) {
#ifdef DEBUG_MATH
			printf("Error: matrix assignment to vector must have 1 column, 4 rows, dimensionality of %s is (%d, %d), performing no = operation\n", param.label, param.Rows(), param.Cols());
#endif
		} else {
			//this->set_size( param.Rows() );
			for (int i = 0; i < param.Rows(); i++) {
				(*this)(i) = param.Val(i, 0);
			}
		}

		return *this;
	}

	double MBquaternion::makeAngleAndAxis( MBvector *result )
	{
		MBdouble angle = acos((*this)(0,0));
		MBdouble sa = sin(angle);

		result->set_size(3);
		(*result)(0) = (*this)(1,0) / sa;
		(*result)(1) = (*this)(2,0) / sa;
		(*result)(2) = (*this)(3,0) / sa;

		return angle * 2.0;
	}

	const MBquaternion slerp( const MBquaternion &v0, const MBquaternion &v1, MBdouble t) {
		// v0 and v1 should be unit length or else
		// something broken will happen.
		MBquaternion result, qb;

		// Compute the cosine of the angle between the two vectors.
		MBdouble cosHalfTheta = v0.dot( v1);

		if (cosHalfTheta < 0) {
			qb = -1*v1;
			cosHalfTheta = -cosHalfTheta;
		} else {
			qb = v1;
		}

#define DOT_THRESHOLD 1.0
		if (cosHalfTheta > DOT_THRESHOLD) {
			// If the inputs are too close for comfort, linearly interpolate
			// and normalize the result.

			result = v0 + t*(qb - v0);
			result.normalize();
			return result;
		}

		clamp(cosHalfTheta, -1, 1);           // Robustness: Stay within domain of acos()
		MBdouble halfTheta = acos(cosHalfTheta);  // theta_0 = angle between input vectors
		MBdouble sinHalfTheta = sqrt(1.0 - cosHalfTheta*cosHalfTheta);

		if (fabs(sinHalfTheta) < 0.001){ // fabs is floating point absolute
			result = 0.5 * v0 + 0.5 * qb;
			return result;
		}

		MBdouble ratioA = sin((1 - t) * halfTheta) / sinHalfTheta;
		MBdouble ratioB = sin(t * halfTheta) / sinHalfTheta;

		result = ratioA* v0  + ratioB *qb;
		return result;
	}

#ifdef _cplusplus
}
#endif
