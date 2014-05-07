#ifndef VELODYNE_H
#define VELODYNE_H

#include <pcap.h>
#include <fstream>

#include "MBmath.h"
#include "MBtime.h"

#define LINE_LEN 16

#define MEASUREMENTS_PER_PACKET (12)
#define FULL_MEASUREMENTS_PER_PACKET (3)
#define MEASUREMENT_LENGTH (100)

#define DATA_BEGINNING (42)
#define ROTATION_LOCATION_LSB (2)
#define ROTATION_LOCATION_MSB (3)

#define DISTANCE_LSB (4)
#define DISTANCE_MSB (5)
#define INTENSITY_LOCATION (6)
#define DATA_POINT_OFFSET (3)

typedef struct {
	MBmatrix *correctionValues;
	double viewingAngle;
	double rotationAngle;
	double heightFromGround;
	u_char *truePacket;
	double dtmin, dtmax;
	double zmin;

	MBmatrix *dmsb;
	MBmatrix *dlsb;
	MBmatrix *Ir;
	MBmatrix *Ix;

	MBmatrix *phi;	// Rotation angle;
	MBmatrix *dt;	// Measured distance
	MBmatrix *rot;
	MBmatrix *ct;	// Intensity

	MBmatrix *Px;	// Cartesian location
	MBmatrix *Py;
	MBmatrix *Pz;

	int dataIndex;
	bool saveMe;
}GLvelodyneThreadInfo;

class GLvelodyne {
private:
	bool useFile;
	bool computeCartesian;
	bool pcapOpened;
	int packetsCaptured;
	MBtime timer;
	FILE *cartesianfile;
	bool saveToCartesianFile;

	// PCAP:
	pcap_t *fp;		// Handles the Ethernet port
	struct pcap_pkthdr *header;
	const u_char *pkt_data;
	char errbuf[PCAP_ERRBUF_SIZE];

	GLvelodyneThreadInfo *threadInfo;

	// Calibration data:
	MBmatrix cval;
	MBmatrix dmsb;
	MBmatrix dlsb;
	MBmatrix Ir;
	MBmatrix Ix;
	MBmatrix correctionValues;
	MBmatrix totalOffset;
	MBmatrix rotPhi, rotAlpha;
	MBmatrix pointLocation;
	MBmatrix rotTheta;
	MBmatrix truePoint;
	// The variables below are from the plotxdataRT.m script
	// Some sizes are preset, specifically to 3 rows because each packet contains
	// 12 sets of points, each set of 4 is enouch for a single slice.  Therefore
	// The total number of slices should be 3.
	double Dcorr;
	double Vo;
	double Ho;
	double alpha;
	double theta;

	// Computed cartesian coordinates:
	MBmatrix Px;	// Cartesian location
	MBmatrix Py;
	MBmatrix Pz;

	// Raw measurements:
	MBmatrix phi;	// Rotation angle;
	MBmatrix dt;	// Measured distance
	MBmatrix rot;
	MBmatrix ct;	// Intensity

	// Other parameters:
	double heightFromGround;

	// Data selection stuff (ugly implmementation)
	double dtmin, dtmax;
	double zmin;

	pthread_t *threads;


public:
	GLvelodyne();
	~GLvelodyne();

	void loadCalibration( std::string directory );
	void loadCalibration( const char *directory );

	int openFile( const char *filename );
	int openAdapter( const char *adaptername );

	int capturePacket();

	void enableCartesian( bool trueOrFalse );
	void useCartesianFile( bool trueOrFalse );
};

const MBmatrix readMATLAB(const char *filename);

void *processVelodyne(void *arg);

#endif