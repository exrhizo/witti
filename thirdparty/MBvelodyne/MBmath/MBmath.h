/*!
 @header libMBmath
 libBmath provides easy to use matrices and vectors for C++.  In the future, other math objects may be included.
 @copyright 12 Cent Dwarf
 @author Matt Bunting
 @updated 2012-02-13
 @version 0.0.1
 */

#ifndef BUNTING_MATH
#define BUNTING_MATH

#include <math.h>

#ifdef __APPLE__
	#define UNIX
#elif __linux__
	#define UNIX
#endif

#ifdef UNIX
	#define MBdouble double
	#define DEBUG_MATRICES
	#include <iostream>
#else
	#include <stdlib.h>
	#define MBdouble float
#endif

#define MB_PI (3.1415926535897)


/*!
 * @class MBmatrix
 * @abstract Handles a numerical matrix type object
 * @discussion The MBmatrix may be used to store a type of numerical 2D matrix.  The user may set the dimensions of the matrix and perform operations on the matrix.  Such operations allows the user to perform, with appropriate matrix dimensions:
 *
 * MBmatrix = MBmatrix
 *
 * MBmatrix += MBmatrix
 *
 * MBmatrix -= MBmatrix
 *
 * MBmatrix *= MBdouble
 *
 * MBmatrix /= MBdouble
 *
 * MBmatrix = MBmatrix + MBmatrix
 *
 * MBmatrix = MBmatrix - MBmatrix
 *
 * MBmatrix = MBmatrix * MBdouble
 *
 * MBmatrix = MBdouble * MBmatrix
 *
 * MBmatrix = MBmatrix / MBdouble
 *
 *
 * There also exists a number of other functions to be computed from matrices, such as computing inverse, transpose, cofactors, etc.  If any dimension mismatch occurs, the MBmetrix will avoid performing the operation and use printf to display an error message.
 * @updated 2013-02-13
 */

#define Lmat scratch[1]
#define Umat scratch[2]
#define Pmat scratch[3]
#define LUy scratch[4]
#define LUx scratch[5]
#define LUPb scratch[6]

/**
 * @opt all
 * @note Class
 */
class MBmatrix
{
private:
	void setAsNew();
	void deleteMe();
protected:
	int _m, _n, _s;	// number of elements
    MBdouble * _v;	// values
	int numScratch;
	MBmatrix *scratch;
    MBdouble garbage;

	// For LU decomposition optimization:
	//MBmatrix *L, *U, *P;
	//MBmatrix *LUx, *LUy, *LUPb;

#ifdef UNIX
	float *_u;
	void copyToFloat() {
		for (int i = 0; i < _s; i++) {
			_u[i] = _v[i];
		}
	};
	char *label;
#endif

	void setupScratch( int number );

public:



	MBmatrix( const MBmatrix& param );

	MBmatrix ();

	MBmatrix (int,int);
    MBdouble & operator() (const int&, const int&);
    MBmatrix & operator = (const MBmatrix&);
    MBmatrix & operator += (const MBmatrix&);
    MBmatrix & operator -= (const MBmatrix&);
    MBmatrix & operator *= (const MBmatrix&);
    MBmatrix & operator *= (const MBdouble&);
	MBmatrix & operator += (const MBdouble&);
    MBmatrix & operator /= (const MBdouble&);
    MBmatrix & operator ^= (const int&);

	const MBmatrix& operator + (const MBmatrix&) const;
	const MBmatrix& operator - (const MBmatrix&) const;
    const MBmatrix& operator * (const MBdouble&) const;
    const MBmatrix& operator / (const MBdouble&) const;
    const MBmatrix& operator ^ (const int&) const;
    const MBmatrix& operator * (const MBmatrix&) const; // dot product

	~MBmatrix ();

#ifdef UNIX
	/*!
	 @function getName
	 @abstract Transposes the matrix
	 @discussion This operation transposes the matrix.
	 @return Returns the transpose of this matrix.
	 */
    const char* getName() { return label; };
#else
	/*!
	 @function armConstruct
	 @abstract Constructs the matrix.
	 @discussion The Arm compiler does not support "new" or "delete".  Because malloc is used, the default constructor never gets called.  This MUST be called after allocating a new MBmatrix using malloc.
	 */
    void armConstruct();

	/*!
	 @function armDestruct
	 @abstract Destructs the matrix.
	 @discussion The Arm compiler does not support "new" or "delete".  Because malloc is used, the default destructor never gets called.  This MUST be called before deallocating an MBmatrix allocated with malloc.
	 */
    void armDestruct();
#endif

	/*!
	 @function fastValue
	 @abstract Value access.
	 @discussion The overloaded operator (int,int) performs error checking on indices, but this method removes the checking for faster access.
	 */
	MBdouble & fastValue(const int& m_des, const int& n_des);

	/*!
	 @function T
	 @abstract Transposes the matrix
	 @discussion This operation transposes the matrix.
	 @return Returns the transpose of this matrix.
	 */
    const MBmatrix& T();

	/*!
	 @function I
	 @abstract Makes an identity matrix.
	 @discussion This operation makes and identity matrix.
	 @param dimension The number of rows/colums for a matrix of size dimension x dimension.
	 */
    void makeI( int dimension );

	/*!
	 @function Inv
	 @abstract Inverts the matrix.
	 @discussion This operation returns the inverse of the matrix.  The matrix dimensions must be square, otherwise nothing will be returned.  WARNING, this function does not currently check for other factors needed for invertibility.
	 @return Returns the inverse of the matrix.
	 */
    const MBmatrix& Inv();

	/*!
	 @function det
	 @abstract Computes matrix determinant.
	 @discussion Computes matrix determinant, if square.
	 @return Returns the determinant of the matrix.
	 */
    MBdouble det();

	/*!
	 @function Cof
	 @abstract Computes cofactor matrix.
	 @discussion Computes cofactor of the matrix.  This is useful for computing the matrix inverse.
	 @return Returns the cofactor of the matrix.
	 */
    const MBmatrix& Cof();

	/*!
	 @function fastLinearSystemSolve
	 @abstract Computes the solution to a linea system Ax = b.
	 @discussion This returns a solution x matrix to the linear system Ax = b.  This avoids using an inverse to hopefully run faster in time-critical code.
	 @param b The b matrix in the linear system.
	 @return The x matrix solution of the system.
	 */
	const MBmatrix& fastLinearSystemSolve( MBmatrix& b );

	/*!
	 @function performLUDecomp
	 @abstract Computes LU decomposition matrices.
	 @discussion Computes LU decomposition of the matrix.  Matrices may be retrieved later with Lmatrix() and Umatrix();
	 */
    void performLUDecomp();

	/*!
	 @function Lmatrix
	 @abstract Returns L computed from performLUDecomp().
	 @discussion Returns L computed from performLUDecomp(). performLUDecomp() must be called before this function returns something useful.
	 @return Returns the L of the matrix.
	 */
    const MBmatrix& Lmatrix()
	{
		return Lmat;
	};

	/*!
	 @function Umatrix
	 @abstract Returns U computed from performLUDecomp().
	 @discussion Returns U computed from performLUDecomp(). performLUDecomp() must be called before this function returns something useful.
	 @return Returns the U of the matrix.
	 */
    const MBmatrix& Umatrix()
	{
		return Umat;
	};

	/*!
	 @function Pmatrix
	 @abstract Returns P computed from performLUDecomp().
	 @discussion Returns P computed from performLUDecomp(). performLUDecomp() must be called before this function returns something useful.
	 @return Returns the P of the matrix.
	 */
    const MBmatrix& Pmatrix()
	{
		return Pmat;
	};

	/*!
	 @function SubM
	 @abstract returns the submatrix of the matrix
	 @discussion This function determins the submatrix of this matrix given a desired row and column.  Essentially, this function copies the matrix, then "removes" the given column and row, and returns the fully modified matrix.  This is useful for computing the cofactor matrix.
	 @param row The row to "remove".
	 @param column The column to "remove".
	 @return Returns the submatrix of the matrix.
	 */
    const MBmatrix& SubM(int row, int column);

	/*!
	 @function name
	 @abstract Sets the name of the MBmatrix.
	 @discussion This assigns a name to this MBmatrix.  Setting the name is not required, though it is helpful for debugging and for running the print_stats() function.  If the name is not set, then the name will appear as (null).
	 @param nameToBeSet The desired name of the MBmatrix.
	 */
	void name(const char * nameToBeSet);

	/*!
	 @function set_size
	 @abstract Sets the size(length) of the MBmatrix.
	 @discussion This erases all current data (if any) and allocates an array of the given size.
	 @param rows The number of rows for this MBmatrix.
	 @param columns The number of columns for this MBmatrix
	 */
    void set_size(int rows, int columns);

	/*!
	 @function cols
	 @abstract Returns the number of columns.
	 @discussion Returns the number of columns.
	 @return Returns the number of columns.
	 */
	const int& cols(void) { return _n; };
	const int& Cols () const { return _n; };

	/*!
	 @function rows
	 @abstract Returns the number of rowss.
	 @discussion Returns the number of rows.
	 @return Returns the number of rows.
	 */
    const int& rows(void) { return _m; };
    const int& Rows () const { return _m; };

    MBdouble Val (int m_des, int n_des) const { return *(_v + m_des + _m*n_des); }
    MBdouble Lin_val (int i) const { return *(_v + i); }

	/*!
	 @function print_stats
	 @abstract Prints statistics for the MBmatrix.
	 @discussion This function is useful for cleanly printing the name and all values within the MBmatrix.
	 */
    void print_stats();

	/*!
	 @function quaternion
	 @abstract Computes the quaternions given this rotation matrix.
	 @discussion This function computes the quaternions to acheive the same rotation as this rotation matrix.  This matrix should be a 3x3 rotation matrix which may be rotated of any abritrary order.
	 @return Returns a MBvector of size 4 containing the quarternions for this rotation matrix
	 */
	const MBmatrix quaternion();

	/*!
	 @function makeXRotation
	 @abstract Computes an X rotation matrix.
	 @discussion This changes all values of this 3x3 matrix to be of a common rotation matrix about the X-axis.
	 @param angle The angle to rotate.
	 */
	void makeXRotation(MBdouble angle);

	/*!
	 @function makeYRotation
	 @abstract Computes an Y rotation matrix.
	 @discussion This changes all values of this 3x3 matrix to be of a common rotation matrix about the Y-axis.
	 @param angle The angle to rotate.
	 */
	void makeYRotation(MBdouble angle);

	/*!
	 @function makeZRotation
	 @abstract Computes an Z rotation matrix.
	 @discussion This changes all values of this 3x3 matrix to be of a common rotation matrix about the Z-axis.
	 @param angle The angle to rotate.
	 */
	void makeZRotation(MBdouble angle);

	/*!
	 @function normalize
	 @abstract Normailizes this matrix.
	 @discussion This normalizes the matrix.  If a vector, then it is normalized using the Euclidean norm.
	 */
	void normalize();

	/*!
	 @function cross
	 @abstract Takes the cross product with the given matrix.
	 @discussion This takes the cross product of this vector on the left with the passed vector on the right. Both matrices must be of size 3x1.
	 @param matrixOnRight The vector on the right.
	 @return The cross product result.
	 */
	const MBmatrix& cross( const MBmatrix& matrixOnRight );

	#ifdef UNIX
	/*!
	 @function pointer
	 @abstract Gives the pointer to the data.
	 @discussion This returns the pointer to the matrix values.
	 @return Returns the pointer to an array of float numbers in the matrix.
	 */
    float *pointer();
#endif

	/*!
	 @function dot
	 @abstract Takes the dot product with the given matrix.
	 @discussion This takes the dot product of this vector on the left with the passed vector on the right. Both matrices must have only one column, and both must have the same number of rows.
	 @param matrixOnRight The vector on the right.
	 @return The dot product result.
	 */
	MBdouble dot( const MBmatrix& matrixOnRight ) const;
};

const MBmatrix& operator*(MBdouble c, const MBmatrix&);

/*!
 * @class MBvector
 * @abstract Handles a vector type object
 * @discussion The MBvector may be used to store a vector definition.
 *
 * This class inherets from MBmatrix.
 * @updated 2013-10-24
 */
class MBvector: public MBmatrix {
public:
	using MBmatrix::set_size;
	void set_size(int length)
	{
		this->set_size(length, 1);
		//printf("Error!  You cannot set the size of a Quaternion!  Remaining at size %dx%d\n", (*this).rows(), (*this).cols());
	}

	//MBquaternion & operator *= (const MBquaternion&);
	//const MBquaternion operator * (const MBquaternion&) const;


	//MBquaternion ()
	//:MBmatrix(4, 1)
	//{
	//	(*this)(0,0) = 1;	// initialize with 0 rotation
	//};


	MBdouble & operator() (const int&);
	using MBmatrix::operator();


	MBvector( const MBvector& param)
	:MBmatrix( param)
	{

	}

	MBvector( const MBmatrix& param)
	:MBmatrix( param)
	{

	}

	using MBmatrix::operator=;
	MBvector & operator= (const MBmatrix&);

	MBvector ()
	:MBmatrix()
	{

	}

	MBvector ( int length )
	:MBmatrix(length, 1)
	{
		//(*this)(0,0) = 1;	// initialize with 0 rotation
	};

	// New stuff:
	/*!
	 @function size
	 @abstract Returns the length of data within the MBvector.
	 @discussion Returns the length of data within the MBvector.
	 @return Returns the length of data within the MBvector.
	 */
	int size() { return this->rows(); };
	int Size () const { return this->Rows(); };

	// Dot product overload:
	using MBmatrix::operator*;
	const MBdouble operator* (const MBvector&) const; // dot product


	/*!
	 @function xyzAnglesFromQuaternion
	 @abstract Converts quaternion to XYZ rotation angles.
	 @discussion This returns a 3-vector containing the XYZ rotation necessary to achieve the same rotation given by this vector of quaternion.  This operation may only be computed when this MBvector is of size 4.
	 @return Returns a 3-vector containing the XYZ rotation.
	 */
	const MBvector xyzAnglesFromQuaternion();

	/*!
	 @function magnitude
	 @abstract Computes the Euclidean norm of the MBvector.
	 @discussion This returns the Euclidean 2-norm of this vector.
	 @return Returns magnitude of this MBvector.
	 */
	MBdouble magnitude();

};


/*!
 * @class MBquaternion
 * @abstract Handles a quaternion type object
 * @discussion The MBquaternion may be used to store a quaternion definition.
 *
 * This class inherets from MBmatrix.
 * @updated 2013-04-04
 */
class MBquaternion: public MBmatrix {
protected:
	//MBquaternion *scratch;
	void setupScratch();
public:
	using MBmatrix::set_size;
	void set_size(int rows, int columns)
	{
#ifdef DEBUG_MATH
		printf("Error!  You cannot set the size of a Quaternion!  Remaining at size %dx%d\n", (*this).rows(), (*this).cols());
#endif
	}



	//using MBmatrix::operator*;
	MBquaternion& operator *= (const MBquaternion&);
	const MBquaternion& operator * (const MBquaternion&) const;

	/*!
	 @function makeFromAngleAndAxis
	 @abstract Computes quaternion values from a vector and rotation angle.
	 @discussion This updates the quaternions values from a rotation angle and vector in the direction of rotation.
	 @param angle The angle to rotate.
	 @param axis The axis to rotate about.
	 */
	int makeFromAngleAndAxis(MBdouble angle, MBvector &axis);

	MBdouble & operator() (const int&);
	using MBmatrix::operator();

	MBquaternion ()
	:MBmatrix(4, 1)
	{
		(*this)(0,0) = 1;	// initialize with 0 rotation
	};

	using MBmatrix::operator=;
	MBquaternion & operator= (const MBmatrix&);


	/*!
	 @function makeRotationMatrix
	 @abstract Computes a rotation matrix.
	 @discussion This returns a 3x3 rotation matrix to perform to quaternion based rotation.
	 @return A 3x3 rotation matrix.
	 */
	const MBmatrix& makeRotationMatrix();
	const MBmatrix& makeRotationMatrix4();


	/*!
	 @function makeAngleAndAxis
	 @abstract Computes a the rotation axis and angle from the quaternion.
	 @discussion This returns the corresponding rotaiton axis and angle defined by the quaternion.
	 @param result The axis to rotate about.
	 @return The angle of rotation.
	 */
	double makeAngleAndAxis( MBvector *result );



};

const MBquaternion slerp(const MBquaternion &v0, const MBquaternion &v1, double t);


/*!
 * @class MBbezier
 * @abstract Handles a bezier curve
 * @discussion The MBbezier may be used to handle a bezier curve, currently implemented to handle linear, quadratic, and cubic curves.
 *
 * @updated 2014-03-21
 */
class MBbezier {
private:
	bool initialized;
	char degree;
	MBmatrix P;
	MBmatrix M;
	MBmatrix T;

	void createM();
	void createT();
	void setT(MBdouble t);
	void initialize();
public:

	MBbezier();
	MBbezier(int order);
	~MBbezier();

	void setDegree(int order);
	const MBmatrix getP( int choose );
	void setP(const MBmatrix& p, int choose);
	void setStartVector(const MBmatrix& p);
	void setFinalVector(const MBmatrix& p);
	const MBmatrix& compute(MBdouble t);

};

// This is stuff to allow easier matrix math regarding cross-compiling with GLKit:

#define GLKVector4AllEqualToVector4 GLKVectorAllEqualToVector
#define GLKMatrix4Multiply GLKMatrixMultiply
//#define GLKMatrix4MultiplyVector3 GLKMatrixMultiplyVector

bool GLKVectorAllEqualToVector( MBvector& first, MBvector& second);
const MBmatrix GLKMatrixMultiply( MBmatrix& first, MBmatrix& second);
const MBvector GLKMatrixMultiplyVector( MBmatrix& first, MBvector& second);
const MBvector GLKMatrix4MultiplyVector3( MBmatrix& first, MBvector& second);	// needs to be different because of homogenous coordinates
const MBmatrix GLKMatrix4MakeTranslation(MBdouble x, MBdouble y, MBdouble z);
const MBmatrix GLKMatrix4Rotate(MBmatrix& input, MBdouble angle, MBdouble x, MBdouble y, MBdouble z);
const MBmatrix GLKMatrix3InvertAndTranspose( MBmatrix& input, bool *isInvertible);
MBdouble GLKMathDegreesToRadians( MBdouble input );
const MBmatrix GLKMatrix4GetMatrix3( MBmatrix& input );
const MBmatrix GLKMatrix4MakePerspective(MBdouble fovRadians, MBdouble aspect, MBdouble near, MBdouble far);
const MBmatrix GLKMatrix4MakeOrtho(MBdouble left, MBdouble right, MBdouble bottom, MBdouble top, MBdouble nearZ, MBdouble farZ);

const MBvector GLKVector3Make(MBdouble x, MBdouble y, MBdouble z);
const MBvector GLKVector4Make(MBdouble x, MBdouble y, MBdouble z, MBdouble w);
const MBvector GLKVector3Add(MBvector& first, MBvector& second);
const MBvector GLKVector3DivideScalar(MBvector& vector, MBdouble scalar);

const MBvector GLKVector3Subtract(MBvector& first, MBvector& second);


/*!
 @function linearSystemSolve
 @abstract Computes the solution to a linea system Ax = b.
 @discussion This returns a solution x matrix to the linear system Ax = b.  This avoids using an inverse to hopefully run faster in time-critical code.
 @param A The A matrix in the linear system.
 @param b The b matrix in the linear system.
 @return The x matrix solution of the system.
 */
const MBmatrix& linearSystemSolve( MBmatrix& A, MBmatrix& b );

/*!
 @function tri
 @abstract Implementation of a triangle function, periodic about 2*pi.
 @discussion This returns a value between -1 to 1, the output of the common triangle function.  This function is periodic about 2pi.
 @param x input.
 @return The triangle function result.
 */
float tri(float x);

/*!
 @function sqr
 @abstract Implementation of a square function, periodic about 2*pi.
 @discussion This returns a value of -1 or 1, the output of the common square function.  This function is periodic about 2pi.
 @param x input.
 @return The square function result.
 */
float sqr(float x);


float clamp(float x, float min, float max);


#endif
