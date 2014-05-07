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


#include <stdlib.h>
//#include <stdio.h>

//#include <iostream>
//#include <sstream>
//#include <string>

#include "GLvelodyne.h"


void printUsage( char *argv[] ) {
	std::cout << "Usage:" << std::endl;
	std::cout << "\t" << argv[0] << " " << "[-i adapter] [-f file]" << std::endl<< std::endl;
	std::cout << "\t-i adapter : network adapter to use (en0, wlan0, lo, etc.)" << std::endl;
	std::cout << "\t-f file    : read from 'file'" << std::endl << std::endl;
}


int main( int argc, char *argv[] )
{
	GLvelodyne velodyne;

	std::string adapter;
	std::string filename;

	// Parse the parameters:
	for (int i = 1; i < argc; i++) {
		std::string arg = argv[i];
		if( arg == "-i" )
        {
            if( i+1 < argc )
            {
                adapter = argv[i+1];
                i++;
            }
        } else if( arg == "-f" )
        {
            if( i+1 < argc )
            {
                filename = argv[i+1];
                i++;
            }
        }
	}

	// Open the port or the adapter:
	if (adapter != "") {
		std::cout << "Attempting to open ethernet adapter: " << adapter << "...";
		if(velodyne.openAdapter(adapter.c_str())) {
			std::cout << "Error opening adapter." << std::endl;
			return -1;
		}
		std::cout << "Done!" << std::endl;
	} else if(filename != "") {
		std::cout << "Attempting to open file: " << filename << "...";
		if(velodyne.openFile(filename.c_str())) {
			std::cout << "Error opening file." << std::endl;
			return -1;
		}
		std::cout << "Done!" << std::endl;
	} else {
		printUsage(argv);
		return -1;
	}

	// Load calibration:
	velodyne.loadCalibration("../calibration");
	velodyne.useCartesianFile(true);

	std::cout << " ====== Capturing packets ====== " << std::endl;
	for(int timesCaptured = 0; timesCaptured < 10000; timesCaptured++)
	{
		// Caputre a packet:
		if(velodyne.capturePacket() < 0)
		{
			std::cout << "ERROR! please implement: printf(\"Error reading the packets: %s\n\", pcap_geterr(fp));" << std::endl;
			break;
		}

	}
	std::cout << "====== Done capturing! ====== " << std::endl;

	return 0;
}

void *processVelodyne(void *arg);
