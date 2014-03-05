/*
 * MAT-file creation program
 *
 * See the MATLAB External Interfaces/API Guide for compiling information.
 *
 * Calling syntax:
 *
 *   matcreat
 *
 * Create a MAT-file which can be loaded into MATLAB.
 *
 * This program demonstrates the use of the following functions:
 *
 *  matClose
 *  matGetVariable
 *  matOpen
 *  matPutVariable
 *  matPutVariableAsGlobal
 *
 * Copyright 1984-2005 The MathWorks, Inc.
 */
/* $Revision: 1.13.4.2 $ */

/*
 Edited by Jonathan Sprinkle to read velodyne data into
 a "controllable" struct for use in other C programs
 */

#include <cstdio>
#include <string> /* For strcmp() */
#include <cstdlib> /* For EXIT_FAILURE, EXIT_SUCCESS */
#include <iostream>
#include <sstream>
#include "mat.h"
#include "velodyneDataReader.h"

VelodyneDataReader::VelodyneDataReader( const char* file )
:filename( file ), fileStatus(-1), xs( NULL ),
ys(NULL), data(NULL), matfile(NULL), 
frames(0), numRows(0), currentFrame(0)
{
  // HACK: should read this in from the cmd line
//  const char* file = "velodyne_data.mat";
  
  // open the matfile in readonly fashion
  matfile = matOpen( file, "r" );
  if (matfile == NULL) {
    std::cout << "Aborting...file read is NULL" << std::endl;
    return;
  }
  
  // we expect variables called "x", "y" and "xdata"
  mx_xs = matGetVariable(matfile, "x");
  if (mx_xs == NULL) {
    std::cout << "Error reading x...aborting." << std::endl;
    return;
  }
  
  mx_ys = matGetVariable(matfile, "y");
  if (mx_ys == NULL) {
    std::cout << "Error reading y...aborting." << std::endl;
    return;
  }
  
  mx_data = matGetVariable(matfile, "xdata");
  if (mx_data == NULL) {
    std::cout << "Error reading xdata...aborting." << std::endl;
    return;
  }
  
  int numXs = mxGetNumberOfElements(mx_xs);
  int numYs = mxGetNumberOfElements(mx_ys);
  assert( numXs == numYs );
  frames = numXs;
  numRows = mxGetNumberOfElements(mx_data)/4.0;
  
  // set our internal values to be appropriate
  xs = mxGetPr(mx_xs);
  ys = mxGetPr(mx_ys);
  data = mxGetPr(mx_data);
  
  // figure out the dimensionality of everything, and
  // then report what we found
  fileStatus = 0; // 0==GOOD
  std::cout << "OK, we are at least able to read." << std::endl;
  
}

std::string VelodyneDataReader::getDataInfo( ) const
{
  std::stringstream result;
  if( data && xs && ys )
  {
    result << "Recovered " << frames <<
    // divide this by four, since there four elements in each point
      " frames (" << numRows <<
      " data points in total)" << std::endl;
  }
  return result.str( );
}

int VelodyneDataReader::rcToIndex(int row, int col )
{
  return (row+(col*numRows));
}

VelodyneDataMessage VelodyneDataReader::getMessageForFrame( int i )
{
  VelodyneDataMessage result = NULL;
  
  if (i >= frames) {
    std::cout << "Resetting current frame to 0 (to loop infinitely)." << std::endl;
    currentFrame = 0;
  }
  i=currentFrame;
  if( i < frames )
  {
    // first element to fetch from data
    int xval = (int)xs[i]-1;
    // final element to fetch from data
    int yval = (int)ys[i]-1;
//    double *rawFrames = mxGetPr(mx_data);
//    
//    std::cout << "About to allocate a double array of width 4, length " << yval-xval <<
//      "; xval=" <<xval<< ",yval=" << yval << std::endl;
    
//    typedef struct {
//      double x;
//      double y;
//      double z;
//      double c;
//    } pointTmp;
//    
//    // Good heavens, C is ugly; but, we need raw memory to assign to
//    pointTmp *frameData = (pointTmp *)malloc(sizeof(pointTmp)*(yval-xval));
//
//    // X
//    frameData[0] = (double *)malloc(sizeof(double)*(yval-xval));
//    // Y
//    frameData[1] = (double *)malloc(sizeof(double)*(yval-xval));
//    // Z
//    frameData[2] = (double *)malloc(sizeof(double)*(yval-xval));
//    // C
//    frameData[3] = (double *)malloc(sizeof(double)*(yval-xval));
    
    // too bad we can't just take the transpose and assign. Sigh.
    
    result = velodyneDataMessageCreate();
    
    VelodyneDataSample sample = velodyneDataSampleCreate();
    for( int j=xval; j<yval; j++ )
    {
      // we have to get the encoded point, which in matlab
      // is using a call ijton or something like that
      VelodyneDataPoint p = velodyneDataPointCreate();
      p->x = data[rcToIndex(j, 0)];
      p->y = data[rcToIndex(j, 1)];
      p->z = data[rcToIndex(j, 2)];
      p->c = data[rcToIndex(j, 3)];
      jausArrayAdd(sample->points, p);
      sample->numPoints = sample->numPoints + 1;
    }
    jausArrayAdd(result->samples, sample);
    result->numSamples = 1;
    result->sequenceNumber = i;
    
    // fake the timestamp
    JausTime tmpTime;
    tmpTime = jausTimeCreate();
    jausTimeSetCurrentTime(tmpTime);
    JausUnsignedInteger timestamp = jausTimeGetTimeStamp(tmpTime);
    result->timestamp = timestamp;
    jausTimeDestroy(tmpTime);
  }
  else {
    std::cout << "Invalid frame (" << i << "), returning NULL message." << std::endl;
  }

  return result;
}

VelodyneDataReader::~VelodyneDataReader( )
{
  mxDestroyArray( mx_xs );
  mxDestroyArray( mx_ys );
  mxDestroyArray( mx_data );
  if( matClose( matfile ) != 0 ) { std::cout << "Problem closing file=" << filename << std::endl; }
}