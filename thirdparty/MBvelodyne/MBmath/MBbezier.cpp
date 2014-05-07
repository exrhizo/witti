#include "MBmath.h"
//#define DEBUG_MATH
#ifdef _cplusplus
extern "C"
{
#endif

	MBbezier::MBbezier() {

		initialized = false;
	}

	MBbezier::MBbezier(int order) {
		setDegree( order );
	}

	void MBbezier::initialize() {
		degree = 4;

		createM();
		createT();
		initialized = true;
	}

	MBbezier::~MBbezier() {
		if (initialized) {

		}
	}

	void MBbezier::setDegree(int order) {
		initialized = true;
		degree = order;

		createM();
		createT();
	}

	void MBbezier::createM() {
		if (degree == 4) {

			M.set_size(4,4);
			M(0,0) =  1;	M(0,1) = -3;	M(0,2) =  3;	M(0,3) = -1;
			M(1,0) =  0;	M(1,1) =  3;	M(1,2) = -6;	M(1,3) =  3;
			M(2,0) =  0;	M(2,1) =  0;	M(2,2) =  3;	M(2,3) = -3;
			M(3,0) =  0;	M(3,1) =  0;	M(3,2) =  0;	M(3,3) =  1;
		} else if(degree == 3) {
			M.set_size(3,3);
			M(0,0) =  1;	M(0,1) = -2;	M(0,2) =  1;
			M(1,0) =  0;	M(1,1) =  2;	M(1,2) = -2;
			M(2,0) =  0;	M(2,1) =  0;	M(2,2) =  1;
		} else if(degree == 2) {
			M.set_size(2,2);
			M(0,0) =  1;	M(0,1) = -1;
			M(1,0) =  0;	M(1,1) =  1;
		} else {
			initialized = false;
		}
	}

	void MBbezier::createT() {
		if ((degree > 1) && (degree <= 4)) {
			T.set_size(degree, 1);
			T(0,0) = 1;
		}
	}

	void MBbezier::setT( MBdouble t ) {
		int i;
		//T(0,0) = 1;
		if(t < 0)
			t = 0;
		if(t > 1)
			t = 1;

		for (i = 1; i < degree; i++) {
			T(i, 0) = T(i-1, 0) * t;
		}
	}

	const MBmatrix& MBbezier::compute(MBdouble t) {
		//const MBmatrix ret;
		if (initialized) {
			setT( t );
			return P*M*T;
		}
		return P;
	}

	const MBmatrix MBbezier::getP( int choose ) {
		MBvector ret(P.rows());
		for (int i = 0; i < P.rows(); i++) {
			ret(i) = P(i, choose);
		}
		return ret;
	}

	void MBbezier::setP(const MBmatrix& p, int choose) {
		if (!initialized) {
			initialize();
		}
		if ((choose >= 0) && (choose < degree)) {

			//if (((p.rows() == 3) || (p.rows() == 4)) && (p.cols() == 1)) {
			if ( p.Cols() == 1 ) {
				if (P.rows() != p.Rows()) {
					P.set_size(p.Rows(), degree);
				}

				for (int i = 0; i < p.Rows(); i++) {
					P(i, choose) = p.Val(i, 0);
				}
			}
		}
	}

	void MBbezier::setStartVector(const MBmatrix& p) {
		setP(p, 0);
	}

	void MBbezier::setFinalVector(const MBmatrix& p) {
		setP(p, degree-1);
	}
    
#ifdef _cplusplus
}
#endif
