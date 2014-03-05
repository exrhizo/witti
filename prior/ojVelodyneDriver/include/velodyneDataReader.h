/*
 Edited by Jonathan Sprinkle to read velodyne data into
 a "controllable" struct for use in other C programs
 */
#ifndef VELODYNE_DATA_READER_H
#define VELODYNE_DATA_READER_H

#include <string>
#include "mat.h"
#include <velodyneDataMessage.h>
#include "velodyneReader.h"

class VelodyneDataReader : public VelodyneReader
{
public:
  VelodyneDataReader( const char* filename );
  virtual ~VelodyneDataReader( );
  virtual int getStatus( ) const { return getFileStatus(); }
  std::string getDataInfo( ) const;
  VelodyneDataMessage getNext( ) 
  { 
    VelodyneDataMessage result = getMessageForFrame(currentFrame); 
    currentFrame++;
    return result;
  }

protected:
  VelodyneDataMessage getMessageForFrame( int i );
  int getFileStatus( ) const { return fileStatus; }
  int getNumFrames( ) const { return frames; }

  int rcToIndex(int row, int col );
  int getNumRows( ) const { return numRows; }
  std::string filename;
  int fileStatus;
  
  double *xs;
  double *ys;
  double *data;
  MATFile *matfile;
  
  int frames;
  int numRows;
  int currentFrame;
  
  mxArray *mx_xs;
  mxArray *mx_ys;
  mxArray *mx_data;
  

  
};


#endif // VELODYNE_DATA_READER_H
