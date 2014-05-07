/*
 * Copyright (c) 1999 - 2005 NetGroup, Politecnico di Torino (Italy)
 * Copyright (c) 2005 - 2006 CACE Technologies, Davis (California)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Politecnico di Torino, CACE Technologies
 * nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

#ifdef _MSC_VER
/*
 * we do not want the warnings about the old deprecated and unsecure CRT functions
 * since these examples can be compiled under *nix as well
 */
#define _CRT_SECURE_NO_WARNINGS
#endif

//#define RAW_DATA_FILE
#define CARTESIAN_DATA_FILE

#include <stdlib.h>
#include <stdio.h>
#include <fstream>
#include <iostream>
#include <sstream>
#include <string>

#include "velodyne.h"

#include "MBmath.h"


int findAllDevices() {
	// This was copied from the saveDump.c file, but does not do what it intends to do, hence it's removal from main()
	pcap_if_t *alldevs, *d;
	u_int inum, i;

	char errbuf[PCAP_ERRBUF_SIZE];

	int numDevs;
	if((numDevs = pcap_findalldevs(&alldevs, errbuf)) == -1)
	{
		fprintf(stderr,"Error in pcap_findalldevs_ex: %s\n", errbuf);
		exit(1);
	}
	printf("number of devices =%d\n", numDevs);
	inum=1;
	if (inum < 1 || inum > i)
	{
		printf("\nInterface number out of range.\n");
		/* Free the device list */
		pcap_freealldevs(alldevs);
		return -1;
	}
	/* Jump to the selected adapter */
	for (d=alldevs, i=0; i< inum-1 ;d=d->next, i++);
	printf("Adapter=%d\n",i);
	printf("Device name=%s\n",d->name);

	return 0;
}

void printUsage( char *argv[] ) {
	std::cout << "Usage:" << std::endl;
	std::cout << "\t" << argv[0] << " " << "[-i adapter] [-f file]" << std::endl<< std::endl;
	std::cout << "\t-i adapter : network adapter to use (en0, wlan0, lo, etc.)" << std::endl;
	std::cout << "\t-f file    : read from 'file'" << std::endl << std::endl;
}


int main( int argc, char *argv[] )
{
	Velodyne velodyne;

	pcap_t *fp;		// Handles the Velodyne
	char errbuf[PCAP_ERRBUF_SIZE];
	int res;
	struct pcap_pkthdr *header;
	const u_char *pkt_data;
#ifdef RAW_DATA_FILE
	FILE *binfile;
#endif 
#ifdef CARTESIAN_DATA_FILE
	FILE *cartesianfile;
#endif

	char *adapter;

	int xflag=0;
	double rotCnt=0;
	MBmatrix rot(FULL_MEASUREMENTS_PER_PACKET, 4);
	rot.name("rot");

	//printf("Opening file...\n");


	if (argc != 2) {
		printUsage( argv );
		return -1;
	}
	adapter = argv[1];

	// The variables below are from the plotxdataRT.m script
	// Some sizes are preset, specifically to 3 rows because each packet contains
	// 12 sets of points, each set of 4 is enouch for a single slice.  Therefore
	// The total number of slices should be 3.

	double Dcorr = 0;
	double Vo = 0;
	double Ho = 0;
	double alpha = 0;
	double theta = 0;
	MBmatrix phi(FULL_MEASUREMENTS_PER_PACKET, 128);
	phi.name("phi");
	//MBmatrix dt(800, 1);
	MBmatrix Px(FULL_MEASUREMENTS_PER_PACKET, 32*4);
	MBmatrix Py(FULL_MEASUREMENTS_PER_PACKET, 32*4);
	MBmatrix Pz(FULL_MEASUREMENTS_PER_PACKET, 32*4);
	Px.name("Px");
	Py.name("Py");
	Pz.name("Pz");

	// Load the Matrix data:
	MBmatrix cval = readMATLAB("../calibration/cval.dat");
	cval.name("cval");
	//cval.print_stats();
	// The follwing are indices, but unfortunaley were meant for MATLAB, not C with an initial index of 0.  Hence the offset:
	MBmatrix dmsb = readMATLAB("../calibration/dmsb.dat");
	dmsb.name("dmsb");
	dmsb += -1;
	MBmatrix dlsb = readMATLAB("../calibration/dlsb.dat");
	dlsb.name("dslb");
	dlsb += -1;
	MBmatrix Ir = readMATLAB("../calibration/Ir.dat");
	Ir.name("Ir");
	Ir += -1;
	MBmatrix Ix = readMATLAB("../calibration/Ix.dat");
	Ix.name("Ix");
	Ix += -1;
	velodyne.loadCalibration( "../calibration" );
	//MBmatrix hdrtest = readMATLAB("../calibration/hdrtest.dat");	// Supplied, but unused
	//MBmatrix lhdr = readMATLAB("../calibration/Ihdr.dat");

	// Order the correciton values similar to MATLAB script
	MBmatrix correctionValues(cval.cols(), cval.rows()*2);
	correctionValues.name("correctionValues");
	for (int i = 0; i < cval.cols(); i++) {
		for (int j = 0; j < (cval.rows()/2); j++) {
			correctionValues(i, j + 0*cval.rows()/2) = cval(j + cval.rows()/2, i);	// First set is lower
			correctionValues(i, j + 1*cval.rows()/2) = cval(j, i);	// Second-fourth are all the upper lasers
			correctionValues(i, j + 2*cval.rows()/2) = cval(j, i);
			correctionValues(i, j + 3*cval.rows()/2) = cval(j, i);
		}
	}


	// For converting the raw values:
	MBmatrix dt(FULL_MEASUREMENTS_PER_PACKET, 128);
	dt.name("dt");
	MBmatrix ct(FULL_MEASUREMENTS_PER_PACKET, 128);
	ct.name("ct");


	/* Open the adapter */

	std::cout << "Attempting to open ethernet adapter: " << adapter << "...";
	if ((fp = pcap_open_live(adapter,//d->name,	// name of the device
							 65536,							// portion of the packet to capture.
							 // 65536 grants that the whole packet will be captured on all the MACs.
							 1,								// promiscuous mode (nonzero means promiscuous)
							 1000,							// read timeout
							 errbuf							// error buffer
							 )) == NULL)
	{

		std::cout << "Error opening adapter." << std::endl;
		return -1;
	}
	std::cout << "Done!" << std::endl;


	MBmatrix totalOffset(3,1);
	totalOffset.name("totalOffset");
	MBmatrix rotPhi, rotAlpha;
	rotPhi.name("rotPhi");
	rotAlpha.name("rotAlpha");
	MBmatrix pointLocation(3,1);
	pointLocation.name("pointLocation");
	MBmatrix rotTheta;
	rotTheta.name("rotTheta");
	MBmatrix truePoint;
	truePoint.name("truePoint");


	xflag = 0;
	for(int timesCaptured = 0; timesCaptured < 500; timesCaptured++)
	{
		std::cout << " ====== Capturing revolution " << timesCaptured << "... ====== " << std::endl;
#ifdef	RAW_DATA_FILE
		binfile=fopen("datafile.bin","wb");
#endif


		/* Read the packets */
		int packetCount = 0;
		rotCnt = -1;
		float closest_object = 50000;

		while((res = pcap_next_ex( fp, &header, &pkt_data)) >= 0)
		{
			//std::cout << " - Read a packet!" << std::endl;
			if(res == 0)
			/* Timeout elapsed */
				continue;

			packetCount++;


			const u_char *truePacket = &pkt_data[DATA_BEGINNING];	// Points to the beginning of the 1200 point packet

			for (int j = 0; j < FULL_MEASUREMENTS_PER_PACKET; j++) { // for each of the three slices
				for ( int i = 0; i<4; i++)	// this loop checks for the beginning of a rotation
				{
					int baseLocationForThisMeasurement = i*MEASUREMENT_LENGTH + j*MEASUREMENT_LENGTH*4;
					const u_char *measurement = &truePacket[baseLocationForThisMeasurement];	// points to the 100 set group of points

					rot(j,i) =	((int)measurement[ROTATION_LOCATION_MSB] << 8) |
								((int)measurement[ROTATION_LOCATION_LSB]); //Rotation value
				}
			}



#define VELODINE_HEIGTH_FROM_GROUND (1)


			for (int i = 0; i < 128; i++) {	// for all 128 laser measurements

				Dcorr = correctionValues(2, i);				// Distance correction factor
				Vo = correctionValues(3, i);				// Vertical offset, in Z-direction
				Ho = correctionValues(4, i);				// Horizontal offset, in X-direction
				theta = correctionValues(1, i)*MB_PI/180.0;	// Vertical correction angle
				alpha = correctionValues(0, i)*MB_PI/180.0;	// Rotational correction angle

				Vo += VELODINE_HEIGTH_FROM_GROUND;	// account for height from ground

#ifdef USE_MB_MATH
				totalOffset(0,0) = Ho;
				totalOffset(2,0) = -Vo;

				rotAlpha.makeZRotation( -alpha );
				rotTheta.makeXRotation( theta );
#endif

				for (int j = 0; j < FULL_MEASUREMENTS_PER_PACKET; j++) {	// for each of the three slices
					int baseLocationForThisMeasurement = j*MEASUREMENT_LENGTH * 4;	// 4 groups per total laser measurement
					const u_char *measurement = &truePacket[baseLocationForThisMeasurement];	// points the beginning of the set of 4 100-point measurements

					ct(j, i) = measurement[(int)Ix(0, i)];


					phi(j, i) = rot(j, (int)Ir(0, i)) * MB_PI/180.0 * 1.0/100.0;
					//phi(j, i) = rot(j, i) * MB_PI/180.0;
					dt(j, i) = (((int)measurement[(int)dmsb(0, i)]) << 8) + (int)measurement[(int)dlsb(0, i)];	// Measured point in Y-direction (I think)
					dt(j, i) = 0.2 * dt(j, i) + Dcorr;

					// Preprocessing of measured distances:
					if (dt(j, i) < 200) {
						dt(j, i) = 5000000;
					}


#ifdef USE_MB_MATH

					// Time for some Comment Vomit!
					// Apply the rotation with calibration:

					// Consider vertical and horizontal offsets (Ho and Vo):
					// [1 0 1]		(becomes: [Ho 0 -Vo])
					// First rotation about -alpha:
					// [cos(-alpha)	sin(-alpha)	1]	=	[cos(alpha)	-sin(alpha)	1]
					// Second rotation around phi:
					// [cos(phi)cos(alpha)+sin(phi)sin(alpha)	sin(phi)cos(alpha)-cos(phi)sin(alpha)	1]
					//totalRotationOffset = rotPhi * rotAlpha * totalOffset;
					rotPhi.makeZRotation( phi(j, i) );


					// Consider the point dt, measured in y:
					// [0 1 0]		(becomes: [0 dt 0])
					// First rotation about X:
					// [0	cos(theta)	sin(theta)]
					// Second rotation about -alpha:
					// [(cos(theta)-sin(-alpha)	cos(theta)cos(-alpha)	1]	=	[cos(theta)sin(alpha)	cos(theta)cos(alpha)	1]
					// Third rotation around phi:
					// [cos(theta)(cos(phi)sin(alpha)-sin(phi)cos(alpha))	cos(theta)(sin(phi)sin(alpha)+cos(phi)cos(alpha))	1]
					//pointRotationLocation = rotPhi * rotAlpha * rotTheta * pointLocation;
					pointLocation(1,0) = dt(j, i);	// point is located in y

					// We could perform the above then subtract, or simplify thing to save a few multiplications:
					// truePoint = pointLocation - totalOffset = rotPhi*rotAlpha*(rotTheta*pointLocation - totalOffset);
					truePoint = rotPhi*rotAlpha*rotTheta*pointLocation - rotPhi*rotAlpha*(totalOffset);

					Py(j, i) = truePoint(0,0);	// Y and X need to be swapped.
					Px(j, i) = truePoint(1,0);
					Pz(j, i) = truePoint(2,0);
#else

					// This is the C version from the matlab script, but now implements the MBmatrices to compute:
					Px(j, i) = dt(j, i) * cos(theta)*(sin(phi(j, i))*cos(alpha) - cos(phi(j, i))*sin(alpha)) - Ho*(cos(phi(j, i))*cos(alpha) + sin(phi(j, i))*sin(alpha));
					Py(j, i) = dt(j, i) * cos(theta)*(cos(phi(j, i))*cos(alpha) + sin(phi(j, i))*sin(alpha)) - Ho*(sin(phi(j, i))*cos(alpha) - cos(phi(j, i))*sin(alpha));
					Pz(j, i) = dt(j, i) * sin(theta) + Vo;

#endif

					// Now perform measurements:
					if ((closest_object > dt(j, i)) && (VELODINE_HEIGTH_FROM_GROUND < Pz(j, i))) {
						closest_object = dt(j, i);
					}
				}
			}
			Px /= 100;	// This results in dividing three 3X128 arrays, resulting in 1152 divides with mild overhead.  Should perform division above on Ho, Vo, and dt.
			Py /= 100;
			Pz /= 100;


#ifdef CARTESIAN_DATA_FILE

				cartesianfile=fopen("cartesianfile.dat","wb");
				/*	// test MATLAB handshaking with familiar values
				for (int i = 0; i < Px.rows(); i++) {
					for (int j = 0; j < Px.cols(); j++) {
						Px(i,j) = 1;
						Py(i,j) = 2;
						Pz(i,j) = 3;
						ct(i,j) = -10;
					}
				}
				 */
					fwrite( Px.pointer(), sizeof(float), Px.rows()*Px.cols(), cartesianfile);
					fwrite( Py.pointer(), sizeof(float), Py.rows()*Py.cols(), cartesianfile);
					fwrite( Pz.pointer(), sizeof(float), Pz.rows()*Pz.cols(), cartesianfile);
					fwrite( ct.pointer(), sizeof(float), ct.rows()*ct.cols(), cartesianfile);
				fclose(cartesianfile);
#endif


			if (packetCount > 100) {
				break;
			}

		}
#ifdef RAW_DATA_FILE
		fclose(binfile);
#endif

		std::cout << "====== Captured 1 revolution! ====== " << std::endl;
		std::cout << " Read " << packetCount << " packets" << std::endl;
		std::cout << " Closest object is at: " << closest_object/100 << "m" << std::endl;
	}

	if(res == -1)
	{
		printf("Error reading the packets: %s\n", pcap_geterr(fp));
		return -1;
	}
	
	pcap_close(fp);
	//fclose(binfile);	// already closed
	return 0;
}
