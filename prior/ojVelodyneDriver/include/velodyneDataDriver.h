#ifndef VELODYNE_DATA_DRIVER_H
#define VELODYNE_DATA_DRIVER_H


#include <jaus.h>
#include <openJaus.h>
#include <string>
#include "common.h"
#include <velodyneDataMessage.h>

class VelodyneReader;

typedef struct {
  int execType;
  std::string *location;
  // the most recently constructed message
  VelodyneDataMessage msg;
  // most recent frame number
  short frame;
  // the reader is used ONLY if we are in captured file mode
  // (i.e., execType==1
  VelodyneReader *reader;
  
} VelodyneDriverData;

typedef VelodyneDriverData vdrData;

/**
 Reads (either from recovered data, or from the 
 actual hardware) from the Velodyne sensor, and then
 proceeds to produce JAUS messages describing the data.
 
 Author: Jonathan Sprinkle
 */
// slightly modifying the structure...no longer assuming
// that we will have only static methods
class VelodyneDataDriver {
public:
  // "constructor"
  static OjCmpt create( int executionType, VelodyneReader *reader=NULL );
  // "destructor"
  static void destroy( OjCmpt cmpt );
  // used for callbacks
  static void ready( OjCmpt cmpt );
  // used for callbacks
  static void standby( OjCmpt cmpt );
  // used for callbacks
  static void init( OjCmpt cmpt );

  // custom callback for query msgs
  static void respondToQuery( OjCmpt cmpt, JausMessage request );
  
  // used for callbacks
  static void processMessage( OjCmpt cmpt, JausMessage msg );
  // called to execute this object
  void run( OjCmpt cmpt );
  
  
protected:
  
private:
  // Specify execution type as the following:
  // 0: use hardware, info as the string
  // 1: use captured file, info as the string
  // performs behavior of "create"
  VelodyneDataDriver( int executionType, std::string location );
  // performs behavior of "destroy"
  ~VelodyneDataDriver( );
  // no copying allowed
  VelodyneDataDriver( const VelodyneDataDriver& other );
  // no assigning allowed
  VelodyneDataDriver& operator=( const VelodyneDataDriver& other );
};


#endif // VELODYNE_DATA_DRIVER_H
