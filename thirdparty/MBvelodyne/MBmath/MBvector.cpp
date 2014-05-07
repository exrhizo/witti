#include "MBmath.h"

#ifdef _cplusplus
extern "C"
{
#endif


	/*//////////////////////////////////*/
    /*     -Overloaded Operators-       */
    /*//////////////////////////////////*/
    MBdouble & MBvector::operator() (const int& m_des)
    {
        return (*this)(m_des, 0);
        //return garbage;
    }

	MBvector & MBvector::operator= (const MBmatrix& param)
	{
		if (param.Cols() != 1) {
#ifdef DEBUG_MATH
			printf("Error: matrix assignment to vector must have 1 column, dimensionality of %s is (%d, %d), performing no = operation\n", param.label, param.Rows(), param.Cols());
#endif
		} else {
			this->set_size( param.Rows() );
			for (int i = 0; i < param.Rows(); i++) {
				(*this)(i) = param.Val(i, 0);
			}
		}


		return *this;
	}

	// Dot product
	const MBdouble MBvector::operator* (const MBvector& param) const
    {
        MBdouble result = 0;
        //    MBvector temp;
        //    temp = *this; // this is messy, should be able to doMBvector result = *this

        if( param.Size() != this->Size())
        {
#ifdef DEBUG_MATH
            printf("Error: vector sizes do not agree between vectors %s and %s ( %d != %d ), performing no * operation\n",label,param.label,this->Size(),param.Size());
#endif
        } else {
            for(int j=0;j<this->Size();j++) {
				//printf("(*this)(%d,0) = %f\n", j, (*this)(j,0));
                result += Val(j,0) * param.Val(j,0);
            }
        }

        return result;
    }


	const MBvector MBvector::xyzAnglesFromQuaternion(void)
	{
		MBvector result(3);

		result(0) = -atan2(2.0*((*this)(1)*(*this)(2) + (*this)(0)*(*this)(3)), (*this)(0)*(*this)(0) + (*this)(1)*(*this)(1) - (*this)(2)*(*this)(2) - (*this)(3)*(*this)(3));// + MB_PI;

		result(1) = -asin(-2.0*((*this)(1)*(*this)(3) - (*this)(0)*(*this)(2)));

		result(2) = atan2(2.0*((*this)(2)*(*this)(3) + (*this)(0)*(*this)(1)), (*this)(0)*(*this)(0) - (*this)(1)*(*this)(1) - (*this)(2)*(*this)(2) + (*this)(3)*(*this)(3));

		return result;
	}

	MBdouble MBvector::magnitude(void)
    {
        return sqrt((*this) * (*this));
    }


#ifdef _cplusplus
}
#endif
