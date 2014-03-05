#include "velodyneDataDriver.h"
#include "velodyneDataReader.h"
#include <cassert>
#include <iostream>

#define VELODYNE_FREQUENCY 5

OjCmpt VelodyneDataDriver::create(int execType_in, VelodyneReader *reader)
{
  OjCmpt result;
  vdrData *data = NULL;
  char szName[256] = "VelodyneDataDriver";
  if (execType_in == 1 ) {
    strcat(szName, "_dataReader");
  }
  result = ojCmptCreate(szName, JAUS_RANGE_SENSOR, VELODYNE_FREQUENCY);
  
  // hopefully, this evaluates to true!
  if (result == NULL) {
    // bad things, dood
    std::cout << "Error creating VelodyneDataDriver..aborting." << std::endl;
    return result;
  }
  else {
    // add this service
    ojCmptAddService(result, JAUS_RANGE_SENSOR);
    
    // given this, we can't actually support (yet?) the 
    // RELATIVE_OBJECT_POSITION updates, but we should, soon...
//    ojCmptAddServiceInputMessage(result, JAUS_RANGE_SENSOR, 
//                                 JAUS_EXPERIMENTAL_QUERY_VELODYNE_DATA, 
//                                 0xFF);
    ojCmptAddServiceOutputMessage(result, JAUS_RANGE_SENSOR, 
                                 JAUS_EXPERIMENTAL_REPORT_VELODYNE_DATA, 
                                 0xFF);
    
    // who processes our messages as they arrive
    ojCmptSetMessageProcessorCallback(result, VelodyneDataDriver::processMessage);
    
    // what do we do when we're initializing?
    ojCmptSetStateCallback(result, JAUS_INITIALIZE_STATE, VelodyneDataDriver::init);
    // what do we do when we're in standby?
    ojCmptSetStateCallback(result, JAUS_STANDBY_STATE, VelodyneDataDriver::standby);
    // what do we do when we're ready?
    ojCmptSetStateCallback(result, JAUS_READY_STATE, VelodyneDataDriver::ready);
    ojCmptSetState( result, JAUS_INITIALIZE_STATE );
    
    // tell the world that we support this service connection (I guess?)
    ojCmptAddSupportedSc(result, JAUS_EXPERIMENTAL_REPORT_VELODYNE_DATA);
    
    ojCmptSetMessageCallback(result, JAUS_EXPERIMENTAL_QUERY_VELODYNE_DATA, 
                             VelodyneDataDriver::respondToQuery);
    
    // Create the user data
    data = (vdrData*)(malloc(sizeof(vdrData)));
    data->execType = execType_in;
    // initially, we have no msg
    data->msg = NULL;
    data->location = new std::string("not used." );
    data->frame = 0; // the initial frame number...
    data->reader = reader; // was assigned prior to creation, but we will delete it...
    ojCmptSetUserData(result, (void *)data);
    
  }

  return result;
  
}

void VelodyneDataDriver::respondToQuery( OjCmpt cmpt, JausMessage request )
{
  std::cout << "Received JAUS_EXPERIMENTAL_QUERY_VELODYNE_DATA message" << std::endl;
  JausMessage txMessage;
  vdrData *data = (vdrData*)ojCmptGetUserData(cmpt);

  // get the next message
  if (data->msg == NULL) {
    data->msg = data->reader->getNext();
  }

  JausAddress addr;
  addr = ojCmptGetAddress(cmpt);
  jausAddressCopy( data->msg->source, addr);
  jausAddressDestroy(addr);
  jausAddressCopy( data->msg->destination, request->source);
  data->msg->sequenceNumber = data->frame;
  
  data->msg->sequenceNumber = data->frame;
  // OK, now do the sending
  txMessage = velodyneDataMessageToJausMessage(data->msg);
  ojCmptSendMessage(cmpt, txMessage);
  
  jausMessageDestroy(txMessage);
  
  // all's well...
  
}

void VelodyneDataDriver::destroy( OjCmpt cmpt )
{
  vdrData *data = (vdrData*)ojCmptGetUserData(cmpt);
  std::cout << "Shutting down VelodyneDataDriver" << std::endl;
  
  // turn off the service connection
  ojCmptRemoveSupportedSc(cmpt, JAUS_EXPERIMENTAL_REPORT_VELODYNE_DATA);
  // delete the data reader (hopefully after it is gone, there will 
  // not be any more calls to send out data
  if (data->msg != NULL) {
    velodyneDataMessageDestroy(data->msg);
    data->msg = NULL;
  }
  delete data->reader;
  delete data->location;
  ojCmptDestroy(cmpt);
  
}

void VelodyneDataDriver::init( OjCmpt cmpt )
{
  vdrData *data = (vdrData*)ojCmptGetUserData(cmpt);
  if (data) {
    if (data->execType == 1) {
      if (data->reader == NULL) {
        std::cout << "Error: data->reader is NULL...probably something bad." << std::endl;
        return;
      }
      else {
        std::cout << "Reader is initialized." << std::endl;
      }
      ojCmptSetState(cmpt, JAUS_STANDBY_STATE);
    }
  }
  
}

void VelodyneDataDriver::standby( OjCmpt cmpt )
{
  vdrData *data = (vdrData*)ojCmptGetUserData(cmpt);
  if ( data && data->reader ) {
    // we go straight to being 'ready'
    std::cout << "Transitiong to [READY]" << std::endl;
    ojCmptSetState(cmpt, JAUS_READY_STATE);
  }
  else {
    ojCmptSetState(cmpt, JAUS_INITIALIZE_STATE);
  }

}

void VelodyneDataDriver::ready( OjCmpt cmpt )
{
  vdrData *data = (vdrData*)ojCmptGetUserData(cmpt);
  if ( data && data->reader ) {
    
    JausMessage txMessage;
    ServiceConnection scList;
    ServiceConnection sc;

    assert( data->reader );
    // we read whenever we are active, now
    if (data->msg != NULL) {
      velodyneDataMessageDestroy(data->msg);
      data->msg = NULL;
    }
    data->msg = data->reader->getNext();
    data->frame += 1;
    
    // send to everyone who wants it...
    if( ojCmptIsOutgoingScActive(cmpt, JAUS_EXPERIMENTAL_REPORT_VELODYNE_DATA) )
    {
      std::cout << "data->frame=" << data->frame << std::endl;
//      message = velodyneDataMessageCreate();
//      message = data->reader->getNext();
      VelodyneDataMessage message = data->msg;
      JausAddress addr;
      addr = ojCmptGetAddress(cmpt);
      jausAddressCopy( message->source, addr);
      jausAddressDestroy(addr);
      message->sequenceNumber = data->frame;
      
      scList = ojCmptGetScSendList(cmpt, JAUS_EXPERIMENTAL_REPORT_VELODYNE_DATA);
      sc = scList;
      while( sc )
      {
        std::cout << "Sending to service connections..." << std::endl;
        // NOT quite sure why I shouldn't do this...
        //        jausAddressDestroy(message->destination);
        jausAddressCopy(message->destination, sc->address);
        // do we need ->presenceVector here? HACK
        // this is what it SHOULD be...but seq num not incrementing
        message->sequenceNumber = sc->sequenceNumber;
        message->properties.scFlag = JAUS_SERVICE_CONNECTION_MESSAGE;
        
        // OK, now do the sending
        txMessage = velodyneDataMessageToJausMessage(message);
//        std::cout << "composed message with seq#=" << message->sequenceNumber << std::endl;
        ojCmptSendMessage(cmpt, txMessage);
//        std::cout << "sent message with seq#=" << txMessage->sequenceNumber << std::endl;
        jausMessageDestroy(txMessage);
        sc = sc->nextSc;
      }
      // we no longer remove this, since we keep it around
//      velodyneDataMessageDestroy(message);
      ojCmptDestroySendList(scList);
    }
    
    
  }
  else {
    std::cout << "Reached end of recorded data..returning to STANDBY mode." << std::endl;
    ojCmptSetState(cmpt, JAUS_STANDBY_STATE);
  }
}

void VelodyneDataDriver::processMessage( OjCmpt cmpt, JausMessage msg )
{
  // This method is ridiculously uninteresting...
//  switch (msg->commandCode) 
//  {
//		default:
//      std::cout << "Received message..." << std::endl;
//      std::cout << "Command code=" << jausCommandCodeString(msg->commandCode) << std::endl;
			ojCmptDefaultMessageProcessor(cmpt, msg);
//			break;
//  }
  
}