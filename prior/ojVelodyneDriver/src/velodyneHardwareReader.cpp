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
#include "velodyneHardwareReader.h"
//#include "mat.h"
#include <scatter.h>

// used by the pcap call
#define LINE_LEN 16

#define TEST_NO_HARDWARE 0

#define MIN_X  -5
#define MAX_X   5
#define MIN_Y   0
#define MAX_Y  15
#define MIN_Z -10
#define MAX_Z  10


VelodyneHardwareReader::VelodyneHardwareReader( )
: VelodyneReader( ), portname( "" ), fileStatus(-1), 
  xflag(0), pkt_data(NULL), data( NULL ), binfile(NULL)
{
//  if( !mclInitializeApplication(NULL, 0) )
//  {
//  }
//  libscatteredPointsSingleCallInitialize();
	nextPoint = 0;
	pointsToEncode = 0;
}

int VelodyneHardwareReader::rcToIndex(int row, int col, int numPoints )
{
  return (row+(col*numPoints));
}

VelodyneDataMessage VelodyneHardwareReader::getNext(int maxPoints)
{
  VelodyneDataMessage result = NULL;
  result = velodyneDataMessageCreate();
	
  if (maxPoints == -1) maxPoints = 2500;
  
  // do the pcap dance...read the next packet
  if (nextPoint >= pointsToEncode) {
    rotationResult = 0;
#ifdef TEST_NO_HARDWARE
    rotationResult = 1;
#else
    std::cout << "Reading from hardware" << std::endl;
    rotationResult = readrotation();
#endif
  }
	  
  if( rotationResult != 0)
  {
    // Success!!
    if (nextPoint >= pointsToEncode) {
      pointsToEncode = readdata();
    }
//    std::cout << "Able to read a rotation..." << std::endl;
    if( pointsToEncode > 0 )
    {
//      std::cout << "Able to read data..." << std::endl;
      // success; now, create the message
      result = velodyneDataMessageCreate();
      
//      int pointsToEncode = numPoints;
//      pointsToEncode = 2500;
//      pointsToEncode = 5;
//      std::cout << "WARNING: Creating only " << pointsToEncode << " points for test purposes..." << std::endl;
      
      VelodyneDataSample sample = velodyneDataSampleCreate();
      for( int j=0; j+nextPoint<pointsToEncode && j<maxPoints; j++ )
      {
        // we have to get the encoded point, which in matlab
        // is using a call ijton or something like that
        VelodyneDataPoint p = velodyneDataPointCreate();
        p->x = data[rcToIndex(j+nextPoint, 0, pointsToEncode)];
        p->y = data[rcToIndex(j+nextPoint, 1, pointsToEncode)];
        p->z = data[rcToIndex(j+nextPoint, 2, pointsToEncode)];
        p->c = data[rcToIndex(j+nextPoint, 3, pointsToEncode)];
        if( p->x > MIN_X && p->x < MAX_X &&
            p->y > MIN_Y && p->y < MAX_Y  &&
            p->z > MIN_Z && p->z < MAX_Z )
        {
          jausArrayAdd(sample->points, p);
          sample->numPoints = sample->numPoints + 1;
        }
      }
      nextPoint+=maxPoints;
      jausArrayAdd(result->samples, sample);
      result->numSamples = 1;
//      result->sequenceNumber = sequence++;
      std::cout << "Added only points within range, total points=" <<
      sample->numPoints << std::endl;
      
      // HACK: should get the timestamp from the device's packet,
      // not from here, when we deploy a real system
      JausTime tmpTime;
      tmpTime = jausTimeCreate();
      jausTimeSetCurrentTime(tmpTime);
      JausUnsignedInteger timestamp = jausTimeGetTimeStamp(tmpTime);
      result->timestamp = timestamp;
      jausTimeDestroy(tmpTime);
      
    }    
  }
  else {
    std::cout << "Invalid packet...hardware may not be connected. Returning NULL message" << std::endl;
  }

  
  return result;
}

/**
 Opens the binfile and reads the data into an mxarray.
 This is done by making the call to the matlab library
 that is base don Erick's code.
 */
int VelodyneHardwareReader::readdata( )
{
  // 0 is a bad result here...means no points read
  int result = 0;
  //  bool call = mlfLibscatteredPointsSingleCall(1, &mx_data);
  // C++-only version of converting packets to scattered points...
  result = scatteredPoints( );
  if( result > 0 )
  {
    // OK to go on...
  }
  else {
    // bad things...
    std::cout << "Result of scatteredPoints( ) = " << result << ", aborting." << std::endl;
    assert( NULL );
  }

  return result;
}

// puts the values into this->data
// returns the number of samples read
// NOTE: does not create intermediate mx_(data,x,y) values...
int VelodyneHardwareReader::scatteredPoints( )
{
  int result=0;
  if( data != NULL )
  {
    delete data;
    data = NULL;
  }
  
  // a very, very, quick test
//#ifdef TEST_NO_HARDWARE
//  FILE *testF = fopen("CMakeCache.txt", "r");
//  fseek(testF, 0, SEEK_END);
//  long size = ftell(testF);
//  std::cout << "File CMakeCache.txt is " << size << " bytes." << std::endl;
//#endif
  
  // open this file, in binary form...
  binfile = fopen("datafile.bin", "rb");
  result= Scatter::createData(binfile, &data);
  fclose(binfile);
  return result;
}

/**
 Reads the packets from the velodyne amounting to a single revolution, and
 then returns.
 */
int VelodyneHardwareReader::readrotation( )
{
  int result = -1;
	char errbuf[PCAP_ERRBUF_SIZE];
  int res;
  char timestr[16];
	struct tm *ltime;
	time_t local_tv_sec;
  unsigned long int rot,rotCnt=0;
  int i=1; // iterator
  int inum=0;
  
  binfile=fopen("datafile.bin","wb"); 
  int findResult = pcap_findalldevs(&alldevs, errbuf);
  std::cout << "findResult==" << findResult << "errbuf=" << errbuf << std::endl;
  if( findResult== -1)
	{
		fprintf(stderr,"Error in pcap_findalldevs_ex: %s\n", errbuf);
		exit(1);
	}
  
  inum=1;
	if (inum < 1 || inum > i)
	{
		printf("\nInterface number out of range.\n");
		/* Free the device list */
		pcap_freealldevs(alldevs);
		return result;
	}
	/* Jump to the selected adapter */
	for (d=alldevs, i=0; i< inum-1 ;d=d->next, i++);
	
  if (d == NULL) {
    std::cout << "Adapter is NULL (is hardware hooked up?) Aborting rotation read." << std::endl;
    return 0;
  }
	/* Open the adapter */
	if ((fp = pcap_open_live(d->name,	// name of the device
                           65536,							// portion of the packet to capture. 
                           // 65536 grants that the whole packet will be captured on all the MACs.
                           1,								// promiscuous mode (nonzero means promiscuous)
                           1000,							// read timeout
                           errbuf							// error buffer
                           )) == NULL)
	{
		fprintf(stderr,"\nError opening adapter\n");
		return result;
	}
	
	/* Read the packets */
	while((res = pcap_next_ex( fp, &header, &pkt_data)) >= 0)
	{
//    std::cout << "Reading the packets" << std::endl;
    
		if(res == 0)
    /* Timeout elapsed */
			continue;
    
    /* convert the timestamp to readable format */
		local_tv_sec = header->ts.tv_sec;
		ltime=localtime(&local_tv_sec);
		strftime( timestr, sizeof timestr, "%H:%M:%S", ltime);
		
		for (i=0;i<12; i++)
		{
			rot=pkt_data[45+i*100]<<8|pkt_data[44+i*100]; //Rotation value
			
			if (rot>rotCnt)
			{
				rotCnt = rot;
			}else{	
				if (rot<20)
				{					
					rotCnt = 0;
					xflag=xflag+1;
				}
			}
		}
    // Is this comment right?
		if (xflag>0) //At least one complete revolution was captured
		{
      //			fwrite(pkt_data+42,1,1200,binfile);
      // Instead of writing to a file, we need to append some
      // memory structure based on what we have here...
      fwrite(pkt_data+42,1,1200,binfile);
      
		}
		if (xflag>1) //At least one complete revolution was captured
		{
      break;
      result = 0;
		}	
	}
  
	if(res == -1)
	{
		printf("Error reading the packets: %s\n", pcap_geterr(fp));
		return result;
	}

  std::cout << "Done...leaving." << std::endl;
  pcap_freealldevs(alldevs);
	pcap_close(fp);
  fclose(binfile);
  return result;
}

VelodyneHardwareReader::~VelodyneHardwareReader( )
{
//  libscatteredPointsSingleCallTerminate( );
//  mclTerminateApplication( );
}