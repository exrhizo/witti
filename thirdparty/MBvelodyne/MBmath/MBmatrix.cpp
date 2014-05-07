#include "MBmath.h"

#ifdef _cplusplus
extern "C"
{
#endif

    /*//////////////////////////////////*/
    /*        -Funny Overloaded-        */
    /*//////////////////////////////////*/
    const MBmatrix& operator*(MBdouble c, const MBmatrix& param)
    {
        return param * c;
    }

    /*//////////////////////////////////*/
    /*       -Basic Initializers-       */
    /*//////////////////////////////////*/
    void MBmatrix::set_size(int new_m, int new_n)
    {
		if (_s != (new_m*new_n))
		{
			_m = new_m;          // set the newly assigned size
			_n = new_n;          // set the newly assigned size
			_s = _m*_n;

#ifdef UNIX
			if(_v != NULL) {
				delete [] _v;
				delete [] _u;
			}
			_v = new (std::nothrow) MBdouble[_s];  // now reallocate with new size
			_u = new (std::nothrow) float[_s];

#else
			if(_v != NULL) {
				free(_v);//delete [] v;
				//free(u);//delete [] u;
			}
			_v = (MBdouble *)malloc(sizeof(MBdouble)*_s);//new (nothrow) MBdouble[s];  // now reallocate with new size
			//u = (float *)malloc(sizeof(float)*s);//new (nothrow) float[s];
#endif
			if(_v == NULL) {
#ifdef DEBUG_MATRICES
				std::cout << "Error: could not allocate memory\n";
#endif
			}

			if (_s > 0) {
				setupScratch( 1 );
			}
		}
		for(int i = 0; i < _s; i++)
		{
			_v[i] = 0;  // just set to zero vaues for no reason
		}
    }

    void MBmatrix::name(const char * array)
    {
        int i = 0;

        while(*(array+i) != 0)
			i++;

#ifdef UNIX
		if(label != NULL)
			delete [] label;

        label = new (std::nothrow) char [i+1];

		for(int j = 0; j <= i; j++)
			label[j] = array[j];
#else
        //if(label != NULL)
		//	free(label);//delete [] label;

        //label = (char *)malloc(sizeof(char)*(i+1));//new (nothrow) char [i+1];
#endif

    }

#ifdef UNIX
	float* MBmatrix::pointer()
	{
		copyToFloat();
		return _u;
	}
#endif

	void MBmatrix::setupScratch( int number )
	{
		if(numScratch != number)
		{
#ifdef UNIX
			if (scratch != NULL) {
				delete [] scratch;
			}
			scratch = new MBmatrix[number];
#else
			if (scratch != NULL) {
				for (int i = 0; i < numScratch; i++) {
					scratch[i].armDestruct();
				}
				free(scratch);
			}
			if (number > 0) {
				scratch = (MBmatrix *) malloc( number*sizeof(MBmatrix) );
				for (int i = 0; i < number; i++) {
					scratch[i].armConstruct();
				}
			} else {
				scratch = NULL;
			}
#endif
			numScratch = number;
		}
	}

    /*//////////////////////////////////*/
    /*   -Constructors/Destructors-     */
    /*//////////////////////////////////*/
	void MBmatrix::setAsNew() {
		_v = NULL;
#ifdef UNIX
		_u = NULL;
		label = NULL;
#endif
		_m = 0;
		_n = 0;
		_s = 0;

		numScratch = 0;
		scratch = NULL;
	}

	void MBmatrix::deleteMe() {
		if (_v != NULL) {
#ifdef DEBUG_MATH
			char str1[32];
			if(label != NULL) sprintf(str1,"%s",label);
			std::cout << "Cleared memory! Deleted " << str1 << ". matrix size:" << s << std::endl;
			std::cout << "\tlabel: " << label << std::endl;
#endif
#ifdef UNIX
			delete [] _v;
			delete [] _u;
        }
		if( label != NULL)
			delete [] label;

		if (scratch != NULL) {
			delete [] scratch;
		}
#else
		free(_v);// delete [] v;
		//free(u);//delete [] u;
	}

	if (scratch != NULL) {
		setupScratch(0);
	}

#endif
}

#ifndef UNIX
void MBmatrix::armConstruct() {
	setAsNew();
	set_size(m, n);
}
void MBmatrix::armDestruct() {
	deleteMe();
}
#endif

MBmatrix::MBmatrix()
{
	setAsNew();
	set_size(_m, _n);
}

// Copy constructor
MBmatrix::MBmatrix( const MBmatrix& param )
{
	setAsNew();
	*this = param;
}

MBmatrix & MBmatrix::operator= (const MBmatrix& param)
{
	if (this != &param) // only run if it is not the same object
	{
		this->set_size(param.Rows(),param.Cols()); // should deallocate then reallocate...

		for(int j = 0; j < this->_s; j++)
		{
			this->_v[j] = param._v[j];
		}
	}
#ifdef DEBUG_MATRICES
//	 else printf("Uhhh, why are you trying to set a matrix to itself? :-/\n");
#endif
	return *this;
}

// size specification constructor:
MBmatrix::MBmatrix(int size_rows, int size_cols)
{
	setAsNew();
	set_size(size_rows, size_cols);
}



MBmatrix::~MBmatrix()
{
	deleteMe();
}


/*//////////////////////////////////*/
/*     -Overloaded Operators-       */
/*//////////////////////////////////*/
MBdouble & MBmatrix::operator() (const int& m_des, const int& n_des)
{
	if( (m_des >= 0 && m_des < _m) && (n_des >= 0 && n_des < _n) )
		return *(_v + m_des + _m*n_des);
#ifdef DEBUG_MATRICES
	else
		std::cout << "index out of bounds for MBmatrix " << label << std::endl;
#endif

	return garbage;
}

MBdouble & MBmatrix::fastValue(const int& m_des, const int& n_des)
{
	return *(_v + m_des + _m*n_des);
}



MBmatrix & MBmatrix::operator+=(const MBmatrix &param)
{
	if( (param.Rows() != _m) || (param.Cols() != _n) )
	{
#ifdef DEBUG_MATRICES
		std::cout << "Error: matrix sizes do not agree between matrices " << label << " and " << param.label << " ( " << _m << "," <<  _n << " != " << param.Rows() << "," << param.Rows() << " ), performing no += operation" << std::endl;
#endif
	} else {
		for(int j = 0; j < _s; j++)
			_v[j] += param.Lin_val(j);

	}
	return *this;
}

MBmatrix & MBmatrix::operator-=(const MBmatrix &param)
{
	if( (param.Rows() != _m) || (param.Cols() != _n) )
	{
#ifdef DEBUG_MATRICES
		std::cout << "Error: matrix sizes do not agree between matrices " << label << " and " << param.label << " ( " << _m << "," <<  _n << " != " << param.Rows() << "," << param.Rows() << " ), performing no -= operation" << std::endl;
#endif
	} else {
		for(int j = 0; j < _s; j++)
			_v[j] -= param.Lin_val(j);
	}

	return *this;
}

MBmatrix & MBmatrix::operator*=(const MBmatrix& param)  // A *= B (A = A*B)
{
	(*this) = (*this) * param;
	return (*this);
}

MBmatrix & MBmatrix::operator*=(const MBdouble& param)
{
	for(int j = 0; j < _s ;j++)
		_v[j] *= param;

	return *this;
}

MBmatrix & MBmatrix::operator+=(const MBdouble& param)
{
	for(int j = 0; j < _s; j++)
		_v[j] += param;

	return *this;
}

MBmatrix & MBmatrix::operator/=(const MBdouble& param)
{
	for(int j = 0; j < _s; j++)
		_v[j] /= param;

	return *this;
}

MBmatrix & MBmatrix::operator^=(const int& param)
{
	//MBmatrix result;
	//result = *this;
	if( _m != _n )
	{
#ifdef DEBUG_MATRICES
		std::cout << "Error: matrix must be square " << label << " ( " << _m << "!=" << _n <<" ), performing no ^= operation" << std::endl;
#endif
	} else if(param == 0){
		for(int j = 0; j < _s; j++)
			_v[j] = 0;
		for(int j = 0; j < _m; j++)
			*(_v + j + _m*j) = 1;
	} else {
		if (param < 0)
		{    // find inverse first
			*this = Inv();
		}

		for(int j = 1; j < param; j++)
			*this *= *this;
	}

	return *this;
}

const MBmatrix& MBmatrix::operator+ (const MBmatrix& param) const
{
	(*scratch) = *this;
	(*scratch) += param;

	return (*scratch);
}

const MBmatrix& MBmatrix::operator- (const MBmatrix& param) const
{
	(*scratch) = *this;
	(*scratch) -= param;

	return (*scratch);
}

const MBmatrix& MBmatrix::operator* (const MBdouble& param) const
{
	(*scratch) = *this;
	(*scratch) *= param;

	return (*scratch);
}

const MBmatrix& MBmatrix::operator* (const MBmatrix& param) const
{
	// This seems like a very peculiar method, but is hand-tuned for speed and therefore looks ugly
	scratch->set_size( _m, param.Cols());
	MBdouble value;
	int rowLocation;

	if(_n == param.Rows())
	{
		for(int i = 0; i < _m; i++) // for all rows of A
		{
			for(int k = 0; k < param.Cols(); k++) // for all columns of B
			{
				value = 0;
				rowLocation = k * param.Rows();
				for(int j = 0; j < _n; j++)    // for all columns of A
				{
					value += Val(i, j) * param.Lin_val(rowLocation+j);//(j, k);
				}
				scratch->fastValue(i, k) = value;
			}
		}

	} else {
#ifdef DEBUG_MATRICES
		std::cout << "Error: matrix sizes do not agree between matrices " << label << " and " << param.label << " ( " << _m << "," <<  _n << " != " << param.Rows() << "," << param.Rows() << " ), performing no *= operation" << std::endl;
#endif
	}
	return (*scratch);
}

const MBmatrix& MBmatrix::operator/ (const MBdouble& param) const
{
	(*scratch) = *this;
	(*scratch) /= param;

	return (*scratch);
}

const MBmatrix& MBmatrix::operator^ (const int& param) const
{
	(*scratch) = *this;
	(*scratch) ^= param;

	return (*scratch);
}


/*//////////////////////////////////*/
/*        -Basic functions-         */
/*//////////////////////////////////*/

const MBmatrix& MBmatrix::T(void)    // Transpose
{
	scratch->set_size(_n, _m);
	for(int i = 0; i < _m; i++)	// columns
		for(int j = 0; j < _n; j++)	// rows
			scratch->fastValue(j,i) = (*this)(i,j);

	return (*scratch);
}

void MBmatrix::makeI( int dimension )
{
	set_size(dimension, dimension);
	for (int i = 0; i < dimension; i++) {
		this->fastValue(i,i) = 1;
	}
}

MBdouble MBmatrix::det(void) // Inverse
{
	MBdouble result = 0;
	//MBdouble temp;
	//int s = n;
	MBmatrix tempM, tempM2;
	tempM = *this;

	if(_n > 2)
	{
		for(int i = 0; i < _n; i++)    // for all cols
		{
			//temp = 1;
			//for(int k=0;k<i;k++) temp *= -1;
			tempM2 = tempM.SubM(0,i);
			if (i & 1) {
				result += -Val(0,i) * tempM2.det(); // woo recursion!
			} else {
				result += Val(0,i) * tempM2.det(); // woo recursion!
			}
		}
	} else if(_n == 2) {
		result = Val(0,0)*Val(1,1)-Val(1,0)*Val(0,1);
	} else if(_n == 1)
		result = Val(0,0);

	return result;
}

const MBmatrix& MBmatrix::Inv(void) // Inverse
{
	(*scratch) = Cof();

	return ( scratch->T()/det() );
}

const MBmatrix& MBmatrix::Cof(void) // Inverse
{
	scratch->set_size(_m, _n);
	MBmatrix tempM, tempM2;
	tempM = *this;

	for(int i = 0; i < _m; i++)
		for(int j = 0; j < _n; j++)
		{
			//int temp = 1;
			//for(int k=0;k<(i+j);k++) temp *= -1;
			tempM2 = tempM.SubM(i,j);
			if ((i+j) & 1) {
				scratch->fastValue(i,j) = -tempM2.det();
			} else {
				scratch->fastValue(i,j) = tempM2.det();
			}
			// result(i,j) = temp * tempM2.det();
		}

	return (*scratch);
}

const MBmatrix& MBmatrix::fastLinearSystemSolve( MBmatrix& b )
{
	setupScratch(7);
	//MBmatrix x;
	//x.name("x");
	int length = b.rows();

	if (_m != _n) {
#ifdef DEBUG_MATRICES
		std::cout << "Error: \"A\" matrix must be square ( " << _m << "!=" << _n << " ), performing no fastLInearSystemSolve()" << std::endl;
#endif
	} else if (_m != length) {
#ifdef DEBUG_MATRICES
		std::cout << "Error: \"A\" matrix must have same number of rows as \"b\" ( " << _m << "!=" << length << " ), performing no fastLInearSystemSolve()" << std::endl;
#endif
	} else if (b.cols() != 1) {
#ifdef DEBUG_MATRICES
		std::cout << "Error: \"b\" matrix must only have one column, not " << b.cols() << ", performing no fastLInearSystemSolve()" << std::endl;
#endif
	} else {
		performLUDecomp();
		//MBmatrix L = A.Lmat();
		//MBmatrix U = A.Umat();
		//MBmatrix P = A.Pmat();

		// Start with forward substitution to solve Ly=Pb
		//MBmatrix y(length, 1);
		LUy.set_size(length, 1);
		//MBmatrix bp = Pmat*b;
		LUPb = Pmat*b;

		LUy(0,0) = LUPb(0,0) / Lmat(0,0);
		for (int i = 1; i < length; i++) { // for all values of y (rows of L):
			MBdouble temp = 0;
			for (int j = 0; j < i; j++) {	// for all preceding columns of L (rows of y)
				temp += Lmat(i, j) * LUy(j, 0) ;
			}
			// solution of y value:
			LUy(i,0) = (LUPb(i,0) - temp) / Lmat(i,i);
		}

		//y.name("y");
		//y.print_stats();

		// End with back substitution to solve Ux=y
		LUx.set_size(length, 1);
		LUx(length-1, 0) = LUy(length-1, 0) / Umat(length-1, length-1);
		for (int i = length-2; i >= 0; i--) { // for all values of y (rows of L):
			MBdouble temp = 0;
			for (int j = length-1; j > i; j--) {	// for all preceding columns of L (rows of y)
				temp += Umat(i, j) * LUx(j, 0) ;
			}
			// solution of x value:
			LUx(i,0) = (LUy(i,0) - temp) / Umat(i,i);
		}



		//x.print_stats();
	}

	return LUx;
}

const MBmatrix& linearSystemSolve( MBmatrix& A, MBmatrix& b )
{
	return A.fastLinearSystemSolve(b);
}

void MBmatrix::performLUDecomp() {
	if( _m != _n )
	{
#ifdef DEBUG_MATRICES
		std::cout << "Error: matrix must be square " << label << " ( " << _m << "!=" << _n << " ), performing no LU decomposition." << std::endl;
#endif
	} else {

		setupScratch(7);

		// First handle P and U:
		Umat = *this;
		//Umat.set_size(m, m);
		Lmat.makeI(_m);
		Pmat.makeI(_m);

		for (int i = 0; i < _m; i++) {	// for all Columns
			int max_j = i;
			for (int j = i; j < _m; j++) {	// for all ROWS/subcolumns
				if (fabs( Umat(j, i) ) > fabs( Umat(max_j, i) )) {
					max_j = j;

				}
			}

			//for (int j = i; j < m; j++) {	// for all ROWS/subcolumns
			if (max_j != i) {
				for (int k = 0; k < _m; k++) {	// for all columns
					MBdouble temp = Pmat(max_j, k);
					Pmat(max_j, k) = Pmat(i, k);
					Pmat(i, k) = temp;

					temp = Umat(max_j, k);
					Umat(max_j, k) = Umat(i, k);
					Umat(i, k) = temp;

				}
				// also need to swap all rows under the diagonal:
				for (int l = 0; l < i; l++) {	// for all columns
					MBdouble temp = Lmat(max_j, l);
					Lmat(max_j, l) = Lmat(i, l);
					Lmat(i, l) = temp;
				}
			}
			//}
			// Now subtract the required amounts in each row in L and U, aftir THIS ROW:
			for (int j = (i+1); j < _m; j++) {			// repeat over all sub rows
				float factor = Umat(j,i)/Umat(i,i); 	// row value/cuurent diagonal value
				for (int k = i; k < _m; k++) {			// for columns
					Umat(j,k) -= factor * Umat(i,k);	// Current value minus factor times ith row above value
				}
				Lmat(j,i) = factor;	// same for L
			}
		}
	}
}

const MBmatrix& MBmatrix::SubM(int des_m, int des_n) // Inverse
{
	//MBmatrix result(m-1,n-1);
	scratch->set_size(_m-1, _n-1 );
	int i2 = 0, j2;

	for(int i = 0; i < _m; i++)
	{
		if(i != des_m)
		{
			j2 = 0;
			for(int j = 0; j < _n; j++)
			{
				if(j != des_n)
					scratch->fastValue(i2, j2++) = Val(i,j);
			}
			i2++;
		}
	}

	return (*scratch);
}

void MBmatrix::print_stats(void)
{
#ifdef DEBUG_MATRICES
	std::cout << "matrix: " << label << "=" << std::endl;

	if(_s == 0)
		std::cout << "\tNothing allocated..." << std::endl;
	else if(_s == 1) {
		std::cout << "\t[" << _v[0] << "]" << std::endl;
	} else {
		for(int j = 0; j < _m; j++)
		{
			std::cout << "\t[";
			for (int i = 0; i < _n; i++)
				std::cout << Val(j,i) << "\t";
			std::cout << "]" << std::endl;
		}
	}
#endif
}

float SIGN(float x) {return (x >= 0.0f) ? +1.0f : -1.0f;}
float NORM(float a, float b, float c, float d) {return sqrt(a * a + b * b + c * c + d * d);}

const MBmatrix MBmatrix::quaternion(void)
{
	MBmatrix q(4,1);
	if ((_n == 3) && (_m == 3)) {
		MBdouble r;

		q(0,0) = ( (*this)(2,2) + (*this)(1,1) + (*this)(0,0) + 1.0f) / 4.0f;
		q(1,0) = ( (*this)(2,2) - (*this)(1,1) - (*this)(0,0) + 1.0f) / 4.0f;
		q(2,0) = (-(*this)(2,2) + (*this)(1,1) - (*this)(0,0) + 1.0f) / 4.0f;
		q(3,0) = (-(*this)(2,2) - (*this)(1,1) + (*this)(0,0) + 1.0f) / 4.0f;
		if(q(0,0) < 0.0f) q(0,0) = 0.0f;
		if(q(1,0) < 0.0f) q(1,0) = 0.0f;
		if(q(2,0) < 0.0f) q(2,0) = 0.0f;
		if(q(3,0) < 0.0f) q(3,0) = 0.0f;
		q(0,0) = sqrt(q(0,0));
		q(1,0) = sqrt(q(1,0));
		q(2,0) = sqrt(q(2,0));
		q(3,0) = sqrt(q(3,0));
		if((q(0,0) >= q(1,0)) && (q(0,0) >= q(2,0)) && (q(0,0) >= q(3,0))) {
			q(0,0) *= +1.0f;
			q(1,0) *= SIGN((*this)(1,0) - (*this)(0,1));
			q(2,0) *= SIGN((*this)(0,2) - (*this)(2,0));
			q(3,0) *= SIGN((*this)(2,1) - (*this)(1,2));
		} else if((q(1,0) >= q(0,0)) && (q(1,0) >= q(2,0)) && (q(1,0) >= q(3,0))) {
			q(0,0) *= SIGN((*this)(1,0) - (*this)(0,1));
			q(1,0) *= +1.0f;
			q(2,0) *= SIGN((*this)(2,1) + (*this)(1,2));
			q(3,0) *= SIGN((*this)(0,2) + (*this)(2,0));
		} else if((q(2,0) >= q(0,0)) && (q(2,0) >= q(1,0)) && (q(2,0) >= q(3,0))) {
			q(0,0) *= SIGN((*this)(0,2) - (*this)(2,0));
			q(1,0) *= SIGN((*this)(2,1) + (*this)(1,2));
			q(2,0) *= +1.0f;
			q(3,0) *= SIGN((*this)(1,0) + (*this)(0,1));
		} else if((q(3,0) >= q(0,0)) && (q(3,0) >= q(1,0)) && (q(3,0) >= q(2,0))) {
			q(0,0) *= SIGN((*this)(2,1) - (*this)(1,2));
			q(1,0) *= SIGN((*this)(2,0) + (*this)(0,2));
			q(2,0) *= SIGN((*this)(1,0) + (*this)(0,1));
			q(3,0) *= +1.0f;
		} else {
#ifdef DEBUG_MATRICES
			std::cout << "Coding error on quaternion: " << label << std::endl;
#endif
		}
		r = NORM(q(0,0), q(1,0), q(2,0), q(3,0));
		q(0,0) /= r;
		q(1,0) /= r;
		q(2,0) /= r;
		q(3,0) /= r;

		r = q(0,0);
		q(0,0) = q(3,0);
		q(3,0) = r;
		r = q(1,0);
		q(1,0) = q(2,0);
		q(2,0) = r;

	} else {
#ifdef DEBUG_MATRICES
		std::cout << "Error!  Quaternion may not be computed for matrix size m=" << _m << " n=" << _n << "\n - Needs to be a 3x3 matrix" << std::endl;
#endif
	}
	return q;
}

void MBmatrix::makeXRotation(MBdouble angle)
{
	if ((_m != 3) || (_n != 3)) {
		this->set_size(3, 3);
	}
	for (int i = 0; i < _s; i++) {
		_v[i] = 0;
	}


	(*this)(0, 0) = 1;
	(*this)(1, 1) = (*this)(2, 2) = cos(angle);
	(*this)(2, 1) = sin(angle);
	(*this)(1, 2) = -(*this)(2, 1);
}

void MBmatrix::makeYRotation(MBdouble angle)
{
	if ((_m != 3) || (_n != 3)) {
		this->set_size(3, 3);
	}
	for (int i = 0; i < _s; i++) {
		_v[i] = 0;
	}

	(*this)(1, 1) = 1;
	(*this)(0, 0) = (*this)(2, 2) = cos(angle);
	(*this)(0, 2) = sin(angle);
	(*this)(2, 0) = -(*this)(0, 2);
}

void MBmatrix::makeZRotation(MBdouble angle)
{
	if ((_m != 3) || (_n != 3)) {
		this->set_size(3, 3);
	}
	for (int i = 0; i < _s; i++) {
		_v[i] = 0;
	}
	(*this)(2, 2) = 1;
	(*this)(0, 0) = (*this)(1, 1) = cos(angle);
	(*this)(1, 0) = sin(angle);
	(*this)(0, 1) = -(*this)(1, 0);
}

void MBmatrix::normalize()
{
	if ((*this).cols() == 1) {
		MBdouble sum = 0;
		for (int i = 0; i < _s; i++) {
			sum += (*(_v+i)) * (*(_v+i));
		}
		MBdouble magnitude = sqrt(sum);
		for (int i = 0; i < _s; i++) {
			*(_v+i) /= magnitude;
		}
	} else {
#ifdef DEBUG_MATRICES
		std::cout << "normalize() has not been fully implemented yet for matrices with these dimensions." << std::endl;
#endif
	}
}

const MBmatrix& MBmatrix::cross( const MBmatrix& matrixOnRight )
{
	scratch->set_size(3, 1);
	if (((*this).cols() == 1) && (matrixOnRight.Cols() == 1) && ((*this).rows() == 3) && (matrixOnRight.Rows() == 3)) {
		scratch->fastValue(0,0) = (*this)(1,0) * matrixOnRight.Val(2,0) - (*this)(2,0) * matrixOnRight.Val(1,0);
		scratch->fastValue(1,0) = (*this)(2,0) * matrixOnRight.Val(0,0) - (*this)(0,0) * matrixOnRight.Val(2,0);
		scratch->fastValue(2,0) = (*this)(0,0) * matrixOnRight.Val(1,0) - (*this)(1,0) * matrixOnRight.Val(0,0);
	} else {
#ifdef DEBUG_MATRICES
		std::cout << "Error: Matrix dimensions do not agree between matrices " << label << " and " << matrixOnRight.label << "  ( " << _m << "," << _n << " X " << matrixOnRight.Rows() << "," << matrixOnRight.Cols() << " ), performing no cross operation" << std::endl;
#endif
	}

	return (*scratch);
}

MBdouble MBmatrix::dot( const MBmatrix& matrixOnRight ) const
{
	MBdouble result = 0;
	if (((*this).Cols() == 1) && (matrixOnRight.Cols() == 1) && ((*this).Rows() == matrixOnRight.Rows() )) {
		for (int i = 0; i < _s; i++) {
			result += (*this).Val(i,0) * matrixOnRight.Val(i,0);
		}
	} else {
#ifdef DEBUG_MATRICES
		std::cout << "Error: Matrix dimensions do not agree between matrices " << label << " and " << matrixOnRight.label << "  ( " << _m << "," << _n << " X " << matrixOnRight.Rows() << "," << matrixOnRight.Cols() << " ), performing no dot operation" << std::endl;
#endif
	}

	return result;
}


const MBvector GLKVector3Make(MBdouble x, MBdouble y, MBdouble z)
{
	MBvector ret(3);
	ret(0) = x;
	ret(1) = y;
	ret(2) = z;
	return ret;
}
const MBvector GLKVector4Make(MBdouble x, MBdouble y, MBdouble z, MBdouble w)
{
	MBvector ret(4);
	ret(0) = x;
	ret(1) = y;
	ret(2) = z;
	ret(3) = w;
	return ret;
}

bool GLKVectorAllEqualToVector( MBvector& first, MBvector& second)
{
	if (first.size() != second.size()) {
#ifdef DEBUG_MATRICES
		//		printf("Vector dimension mismatch in compare!");
#endif
		return false;
	} else {
		for (int i = 0; i < first.size(); i++) {
			if(first(i) != second(i))
				return false;
		}
	}
	return true;
}

const MBmatrix GLKMatrixMultiply( MBmatrix& first, MBmatrix& second)
{
	return first*second;
}

const MBvector GLKMatrix4MultiplyVector3( MBmatrix& first, MBvector& second)
{
	MBvector ret(first.rows());
	for (int i = 0; i < 3; i++) {
		ret(i) = second(i);
	}
	ret(3) = 1;


	return GLKMatrixMultiplyVector(first, ret);
}

const MBvector GLKMatrixMultiplyVector( MBmatrix& first, MBvector& second)
{
	MBvector ret(first.rows());

	for (int i = 0; i < first.rows(); i++) {
		ret(i) = 0;
		for (int j = 0; j < second.size(); j++) {
			ret(i) += first(i, j) * second(j);
		}
	}
	return ret;
}

const MBmatrix GLKMatrix4MakeTranslation(MBdouble x, MBdouble y, MBdouble z)
{
	MBmatrix ret(4, 4);
	ret(0,0) = 1;
	ret(1,1) = 1;
	ret(2,2) = 1;
	ret(3,3) = 1;
	ret(0,3) = x;
	ret(1,3) = y;
	ret(2,3) = z;
	return ret;
}

const MBmatrix GLKMatrix4Rotate(MBmatrix& input, MBdouble angle, MBdouble x, MBdouble y, MBdouble z)
{
	MBmatrix ret(4, 4);
	MBdouble magnitude = 0;
	magnitude = sqrtf(x*x + y*y + z*z);
	if( (x + y + z ) == 0 )
		return input;

	x /= magnitude;
	y /= magnitude;
	z /= magnitude;

	ret(0, 0) = cos(angle) + x*x*(1-cos(angle));		ret(0, 1) = x*y*(1-cos(angle)) - z*sin(angle);		ret(0, 2) = x*z*(1-cos(angle)) + y*sin(angle);
	ret(1, 0) = y*x*(1-cos(angle)) + z*sin(angle);		ret(1, 1) = cos(angle) + y*y*(1-cos(angle));		ret(1, 2) = y*z*(1-cos(angle)) - x*sin(angle);
	ret(2, 0) = z*x*(1-cos(angle)) - y*sin(angle);		ret(2, 1) = z*y*(1-cos(angle)) + x*sin(angle);		ret(2, 2) = cos(angle) + z*z*(1-cos(angle));
	ret(3, 3) = 1;


	return  input * ret;
}

const MBmatrix GLKMatrix3InvertAndTranspose( MBmatrix& input, bool *isInvertible)
{
	MBmatrix ret(3,3);

	if (isInvertible != NULL) {
		*isInvertible = true;
	}

	ret = input.Inv();

	return ret.T();
}

MBdouble GLKMathDegreesToRadians( MBdouble input)
{
	return input * MB_PI / 180.0;
}

const MBmatrix GLKMatrix4GetMatrix3( MBmatrix& input )
{
	MBmatrix ret(3, 3);

	for (int i = 0; i < 3; i++) {
		for (int j = 0; j < 3; j++) {
			ret(i, j) = input(i, j);
		}
	}

	return ret;
}

const MBmatrix GLKMatrix4MakePerspective(MBdouble fovRadians, MBdouble aspect, MBdouble near, MBdouble far)
{
	MBmatrix ret(4,4);

	MBdouble yScale = 1.0 / tan( fovRadians / 2);
	MBdouble xScale = yScale / aspect;
	MBdouble nearmfar = near - far;

	ret(0, 0) = xScale;
	ret(1, 1) = yScale;
	ret(2, 2) =  (far + near) / nearmfar; ret(2, 3) = 2*far*near / nearmfar;
	ret(3, 2) = -1;

	// z' = (z*((far + near) / nearmfar) + 1*(2*far*near / nearmfar))/z
	// => z = (z' - 1*(2*far*near / nearmfar))/((far + near) / nearmfar)
	// => z = z'/((far + near) / nearmfar) - (2*far*near / nearmfar)/((far + near) / nearmfar)
	// => z = nearmfar*z'/(far + near) - 2*far*near/(far + near)

	return ret;
}

const MBmatrix GLKMatrix4MakeOrtho(MBdouble left, MBdouble right, MBdouble bottom, MBdouble top, MBdouble nearZ, MBdouble farZ)
{
	MBmatrix ret(4,4);

	ret(0, 0) = 2.0/(right - left);
	ret(1, 1) = 2.0/(top - bottom);
	ret(2, 2) = -2.0/(farZ - nearZ);
	ret(3, 3) = 1.0;
	ret(0, 3) = - (right + left) / (right - left);
	ret(1, 3) = - (top + bottom) / (top - bottom);
	ret(2, 3) = - (farZ + nearZ) / (farZ - nearZ);

	return ret;
}

const MBvector GLKVector3Add(MBvector& first, MBvector& second){
	MBvector ret = first;//+second;
	ret += second;
	return ret;
}
const MBvector GLKVector3DivideScalar(MBvector& vector, MBdouble scalar){
	MBvector ret = vector;
	ret /= scalar;
	return ret;
}

const MBvector GLKVector3Subtract(MBvector first, MBvector second){
	MBvector ret = first;//+second;
	ret -= second;
	return ret;
}


#ifdef _cplusplus
}
#endif
