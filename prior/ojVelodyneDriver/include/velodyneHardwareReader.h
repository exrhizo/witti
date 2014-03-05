/*
 Edited by Jonathan Sprinkle to read velodyne data into
 a "controllable" struct for use in other C programs
 */
#ifndef VELODYNE_HARDWARE_READER_H
#define VELODYNE_HARDWARE_READER_H

#include <string>
#include "mat.h"
#include <velodyneDataMessage.h>
#include "velodyneReader.h"
#include "pcap.h"

class VelodyneHardwareReader :
public VelodyneReader
{
public:
  VelodyneHardwareReader( );
  virtual ~VelodyneHardwareReader( );
  virtual int getStatus( ) const { return fileStatus; }
  virtual std::string getDataInfo( ) const { return portname; }
  virtual VelodyneDataMessage getNext(int maxPoints = -1);

protected:
  int readrotation( );
  int readdata( );
  int rcToIndex( int row, int col, int numPoints );
  int scatteredPoints( );
  
  std::string portname;
  int fileStatus;
  int sequence;
  
  // pcap info
  pcap_if_t *alldevs, *d;
  pcap_t *fp;
//  std::stringbuf memoryFile;
  int xflag;
  const u_char *pkt_data;
	struct pcap_pkthdr *header;
  
  double *data;
  int nextPoint;
  int pointsToEncode;
  int rotationResult;
  FILE *binfile;
//  mxArray *mx_data;
  
};


#endif // VELODYNE_HARDWARE_READER_H
