/*
 Author: Jonathan Sprinkle
 Calls the velodyneDataReader to test it.
 */

#include <cstdio>
#include <string> /* For strcmp() */
#include <cstdlib> /* For EXIT_FAILURE, EXIT_SUCCESS */
#include <iostream>
// define this to 1 if you just want to read in the file, and don't want
// to actually create the OJ stuff
#define JUSTREAD 0
//#define FAKEDATA 0
//#define HARDWARE 1
#ifndef HARDWARE
#define HARDWARE 0
#endif

#if FAKEDATA
#include "mat.h"
#include "velodyneDataReader.h"
#elif HARDWARE
#include "velodyneHardwareReader.h"
#endif
#include "velodyneDataDriver.h"
#include <openJaus.h>
#include <scatter.h>

// time (in ms) to sleep after sending previous packet
// It takes approximately 20 ms to send a packet, so
// add that time to your time to estimate the time between
// NOTE: actual hardware runs at ~30Hz (0.033 period)
#define SLEEP_TIME 13

void usage( )
{
  std::cout << "Please specify full path to data file (e.g., ../velodyne_data.mat)" << std::endl;
}

int main( int argc, char** argv )
{
  if( argc == 2 || HARDWARE == 1)
  {
#if JUSTREAD
//    VelodyneDataReader vdr("velodyne_data.mat");
    VelodyneDataReader vdr(argv[1]);
    if( vdr.getFileStatus( ) != 0 )
    {
      return EXIT_FAILURE;
    }
    
    while (true) {
      
    
    // now, we print out a bunch of info
    std::cout << vdr.getDataInfo( ) << std::endl;
    std::vector<VelodyneDataMessage> msgs;
    // preallocate the space to save time when adding
    msgs.reserve(vdr.getNumFrames());
    int numFrames = vdr.getNumFrames();
    for (int i=0; i<numFrames; i++) {
      VelodyneDataMessage msg = vdr.getMessageForFrame( i );
      msg->sequenceNumber = i;

//      char *tmpStr=NULL;
//      tmpStr = velodyneDataMessageToString(msg);
//      std::cout << "Message=" << tmpStr;
//      free(tmpStr);
      msgs.push_back(msg);
      // up to this time right here takes approximately
      // 20 MS
      ojSleepMsec(SLEEP_TIME);
    }
    
    char *tmpStr=new char[20];
    for (std::vector<VelodyneDataMessage>::iterator it=msgs.begin( ); 
         it != msgs.end( ); it++) {
      VelodyneDataMessage msg = (VelodyneDataMessage)*it;
      JausTime t = jausTimeCreate();
      jausTimeSetTimeStamp(t, msg->timestamp);
      jausTimeTimeToString(t, tmpStr);
      std::cout << "Received Message sequence=" << msg->sequenceNumber << 
      " at time=" << tmpStr <<std::endl;
      velodyneDataMessageDestroy(msg);
      jausTimeDestroy(t);
    }
    
//    std::cout << "Press any key to continue...check your memory now." << std::endl;
//    char c = getchar();
//    
    delete tmpStr;
      
    }

    
#else
#if FAKEDATA
    // we allocate this memory here, but cmpt will delete it when it goes away.
    VelodyneReader *reader = new VelodyneDataReader( argv[1] );
#elif HARDWARE
    // we allocate this memory here, but cmpt will delete it when it goes away.
    VelodyneReader *reader = new VelodyneHardwareReader( );
#endif
    // here is the ojCmpt implementation
    OjCmpt cmpt = VelodyneDataDriver::create(1, reader);
    if( ojCmptRun(cmpt) )
    {
      std::cout << "Party's over...quitting." << std::endl;
      ojCmptDestroy(cmpt);
    }
    std::cout << "Press [ESC] to quit." << std::endl;
    // 27 == ESCAPE
    while ( getchar() != 27 ) {
      // do nothing
//      ojSleepMsec(100);
    }
    std::cout << "Thanks for being an awesome component...good bye." << std::endl;
    ojCmptDestroy(cmpt);
#endif  
  }
  else {
    usage( );
  }
  
  return EXIT_SUCCESS;
  
}