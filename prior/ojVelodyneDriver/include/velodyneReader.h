/*
 Edited by Jonathan Sprinkle to read velodyne data into
 a "controllable" struct for use in other C programs
 */
#ifndef VELODYNE_READER_H
#define VELODYNE_READER_H

#include <string>
#include "mat.h"
#include <velodyneDataMessage.h>

/**
 Abstract class definition
 */
class VelodyneReader 
{
public:
  VelodyneReader( ) { }
  virtual ~VelodyneReader( ) { }
  virtual int getStatus( ) const=0;
  virtual std::string getDataInfo( ) const=0;
  virtual VelodyneDataMessage getNext( )=0;

protected:
  // we do not permit assignment or copy...
  VelodyneReader( const VelodyneReader& other ) { }
  VelodyneReader& operator=( const VelodyneReader& other );
};


#endif // VELODYNE_READER_H
