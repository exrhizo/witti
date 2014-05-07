#include "velodyne.h"

#ifdef _cplusplus
extern "C"
{
#endif

	//#define USE_MB_MATH
	Velodyne::Velodyne() {
		saveToCartesianFile = false;
		computeCartesian = false;
		cartesianFileName = "cartesianfile.dat";
		cartesianFileNameTemp = "cartesianfile.dat.tmp";
		cartesianfile=fopen(cartesianFileName.c_str(),"ab");
		pcapOpened = false;
		packetsCaptured = 0;

		// Computed cartesian coordinates:
		Px.set_size(FULL_MEASUREMENTS_PER_PACKET, 32*4);
		Py.set_size(FULL_MEASUREMENTS_PER_PACKET, 32*4);
		Pz.set_size(FULL_MEASUREMENTS_PER_PACKET, 32*4);
		Px.name("Px");
		Py.name("Py");
		Pz.name("Pz");

		// Measurements:
		dt.set_size(FULL_MEASUREMENTS_PER_PACKET, 128);
		dt.name("dt");
		ct.set_size(FULL_MEASUREMENTS_PER_PACKET, 128);
		ct.name("ct");
		rot.set_size(FULL_MEASUREMENTS_PER_PACKET, 4);
		rot.name("rot");
		phi.set_size(FULL_MEASUREMENTS_PER_PACKET, 128);
		phi.name("phi");

		// Calibration data:
		totalOffset.set_size(3,1);
		totalOffset.name("totalOffset");
		rotPhi.name("rotPhi");
		rotAlpha.name("rotAlpha");
		pointLocation.set_size(3,1);
		pointLocation.name("pointLocation");
		rotTheta.name("rotTheta");
		truePoint.name("truePoint");

		dtmin = -400.00;
		dtmax = 100000.00;
		zmin = -1000;
		heightFromGround = 1.3;

		threads = new pthread_t[FULL_MEASUREMENTS_PER_PACKET];
		threadInfo = new VelodyneThreadInfo[FULL_MEASUREMENTS_PER_PACKET];
		for (int i = 0; i < FULL_MEASUREMENTS_PER_PACKET; i++) {
			threadInfo[i].correctionValues = &correctionValues;
			//threadInfo[i].truePacket =
			threadInfo[i].viewingAngle = 360;
			threadInfo[i].heightFromGround = heightFromGround;
			threadInfo[i].dtmax = dtmax;
			threadInfo[i].dtmin = dtmin;
			threadInfo[i].zmin = zmin;

			threadInfo[i].dmsb = &dmsb;
			threadInfo[i].dlsb = &dlsb;
			threadInfo[i].Ir = &Ir;
			threadInfo[i].Ix = &Ix;

			threadInfo[i].phi = &phi;
			threadInfo[i].dt = &dt;
			threadInfo[i].rot = &rot;
			threadInfo[i].ct = &ct;

			threadInfo[i].Px = &Px;
			threadInfo[i].Py = &Py;
			threadInfo[i].Pz = &Pz;

			threadInfo[i].dataIndex = i;
		}
	}

	void Velodyne::enableCartesian( bool trueOrFalse ) {
		computeCartesian = trueOrFalse;
	}

	void Velodyne::useCartesianFile( bool trueOrFalse ) {
		saveToCartesianFile = trueOrFalse;
		if (saveToCartesianFile) {
			enableCartesian(saveToCartesianFile);
		}
	}

	Velodyne::~Velodyne() {
		if( pcapOpened ) {
			printf("Closing, captured %d pakets at a frequency of %f Hz\n\n", packetsCaptured, timer.favg);
			pcap_close(fp);
		}
		if (cartesianfile) fclose(cartesianfile);
	}

	void Velodyne::loadCalibration( const char *directory ) {
		loadCalibration(std::string(directory));
	}

	void Velodyne::loadCalibration( std::string directory ) {
		std::string dataFile;
		dataFile = directory + "/cval.dat";
		cval = readMATLAB(dataFile.c_str());
		cval.name("cval");

		dataFile = directory + "/dmsb.dat";
		dmsb = readMATLAB(dataFile.c_str());
		dmsb.name("dmsb");
		dmsb += -1;

		dataFile = directory + "/dlsb.dat";
		dlsb = readMATLAB(dataFile.c_str());
		dlsb.name("dslb");
		dlsb += -1;

		dataFile = directory + "/Ir.dat";
		Ir = readMATLAB(dataFile.c_str());
		Ir.name("Ir");
		Ir += -1;

		dataFile = directory + "/Ix.dat";
		Ix = readMATLAB(dataFile.c_str());
		Ix.name("Ix");
		Ix += -1;

		correctionValues.set_size(cval.cols(), cval.rows()*2);
		correctionValues.name("correctionValues");
		for (int i = 0; i < cval.cols(); i++) {
			for (int j = 0; j < (cval.rows()/2); j++) {
				correctionValues(i, j + 0*cval.rows()/2) = cval(j + cval.rows()/2, i);	// First set is lower
				correctionValues(i, j + 1*cval.rows()/2) = cval(j, i);	// Second-fourth are all the upper lasers
				correctionValues(i, j + 2*cval.rows()/2) = cval(j, i);
				correctionValues(i, j + 3*cval.rows()/2) = cval(j, i);
			}
		}

	}

	int Velodyne::openFile( const char *filename ) {
		useFile = true;

		if ((fp = pcap_open_offline(filename,	// name of the device
                                    errbuf							// error buffer
                                    )) == NULL)
        {
            fprintf(stderr,"\nError opening adapter\n");
			pcapOpened = false;
            return -1;
        }
		pcapOpened = true;
		timer.initialize();

		return 0;
	}

	int Velodyne::openAdapter( const char *adaptername ) {
		useFile = false;

		//		std::cout << "Attempting to open ethernet adapter: " << adaptername << "...";
		if ((fp = pcap_open_live(adaptername,//d->name,	// name of the device
								 65536,							// portion of the packet to capture.
								 // 65536 grants that the whole packet will be captured on all the MACs.
								 1,								// promiscuous mode (nonzero means promiscuous)
								 1000,							// read timeout
								 errbuf							// error buffer
								 )) == NULL)
		{

			//			std::cout << "Error opening adapter." << std::endl;
			pcapOpened = false;
			return -1;
		}
		//		std::cout << "Done!" << std::endl;
		pcapOpened = true;
		timer.initialize();

		return 0;
	}

	int Velodyne::capturePacket() {
		int res = 0;

		if (pcapOpened) {
			if (useFile) {	// Impose a delay when using a file to simulate reality
				usleep(100);
			}


			if((res = pcap_next_ex( fp, &header, &pkt_data)) > 0)
			{
				packetsCaptured++;
				timer.update();

				const u_char *truePacket = &pkt_data[DATA_BEGINNING];	// Points to the beginning of the 1200 point packet

				for (int j = 0; j < FULL_MEASUREMENTS_PER_PACKET; j++) { // for each of the three slices
					for ( int i = 0; i<4; i++)	// this loop checks for the beginning of a rotation
					{
						int baseLocationForThisMeasurement = i*MEASUREMENT_LENGTH + j*MEASUREMENT_LENGTH*4;
						const u_char *measurement = &truePacket[baseLocationForThisMeasurement];	// points to the 100 set group of points

						rot(j,i) =	((float)(((int)measurement[ROTATION_LOCATION_MSB] << 8) |
											 ((int)measurement[ROTATION_LOCATION_LSB]))) * MB_PI/18000.0;; //Rotation value
					}
				}


				if (computeCartesian) {
					//std::cout << "Creating threads" << std::endl;

					// start thread:
					for (int i = 0; i < FULL_MEASUREMENTS_PER_PACKET; i++) {
						threadInfo[i].truePacket = (u_char *)truePacket;
						pthread_create(&threads[i], NULL, &processVelodyne, (void *)threadInfo);
					}
					//std::cout << "Joining threads" << std::endl;
					// join threads:
					for (int i = 0; i < FULL_MEASUREMENTS_PER_PACKET; i++) {
						pthread_join(threads[i], NULL);
					}


					if (saveToCartesianFile) {
						float *xx, *yy, *zz;
						for (int i = 0; i < FULL_MEASUREMENTS_PER_PACKET; i++) {
							if (threadInfo[i].saveMe){
								for (int j = 0; j < Py.cols(); ++j){
									xx = Py.pointer() + i*Px.cols() + j;
									yy = Px.pointer() + i*Px.cols() + j;
									zz = Pz.pointer() + i*Px.cols() + j;
									if (*xx + *yy + *zz != 0 && rand()%4==0){
										fwrite(xx, sizeof(float), 1, cartesianfile);
										fwrite(yy, sizeof(float), 1, cartesianfile);
										fwrite(zz, sizeof(float), 1, cartesianfile);
									}
								}
							}
						}
					}

				}
			}
		}

		return res;
	}


	void Velodyne::setCartesianFileName(int framenum, std::string path){
		std::ostringstream ss;
		if (cartesianfile != NULL)  {
			fclose(cartesianfile);
			rename(cartesianFileNameTemp.c_str(), cartesianFileName.c_str());
		}
        ss  << path << "/live_frame_" << std::setw( 4 ) 
        //ss  << path << "/parking_lot_" << std::setw( 4 ) 
            << std::setfill( '0' ) << framenum << ".bin";
		cartesianFileName = ss.str();
		ss << ".tmp";
        cartesianFileNameTemp = ss.str();
		cartesianfile = fopen(cartesianFileNameTemp.c_str(),"ab");
	}



	const MBmatrix readMATLAB(const char *filename) {
		using namespace std;


#define MAX_CHARS_PER_LINE (2048)
#define MAX_TOKENS_PER_LINE (512)
		const char* const DELIMITER = "   ";
		MBmatrix ret;



		// read each line of the file
		string buf;//[MAX_CHARS_PER_LINE];
		// array to store memory addresses of the tokens in buf
		const char* token[MAX_TOKENS_PER_LINE] = {}; // initialize to 0

		cout << "About to try to read the file: " << filename << endl;
		for (int k = 0; k < 2; k++) {

			ifstream fin;
			fin.open(filename); // open a file
			if (!fin.good())
				return ret; // exit if file not found

			int a = 0, b = 0;
			while(getline(fin, buf))
			{
				// parse the line into blank-delimited tokens
				int n = 0; // a for-loop index


				// parse the line
				token[0] = strtok((char *)buf.c_str(), DELIMITER); // first token
				if (token[0]) // zero if line is blank
				{
					a++;
					b = 0;
					for (n = 1; n < MAX_TOKENS_PER_LINE; n++)
					{
						b++;
						token[n] = strtok(0, DELIMITER); // subsequent tokens
						if (!token[n])
							break; // no more tokens
					}
				}
				
				if (k > 0){	// Load the data into the matrix on second run
					for (int i = 0; i < n; i++) {
						ret(a-1, i) = atof(token[i]);
					}
				}
			}
			if (k == 0) {
				cout << " Matrix size: " << a << "x" << b << endl;
				ret.set_size(a, b);
			}
			
		}
		
		
		return ret;
	}

//#define USE_MB_MATH
	void *processVelodyne(void *arg)
	{
		VelodyneThreadInfo *threadInfo = (VelodyneThreadInfo *)arg;
		double Dcorr;
		double Vo;
		double Ho;
		double alpha;
		double theta;

#ifdef USE_MB_MATH
		MBmatrix totalOffset(3,1), truePoint, pointLocation(3,1);
		MBmatrix rotAlpha, rotTheta, rotPhi;
#endif

		if(((*threadInfo->rot)(threadInfo->dataIndex,0) < (threadInfo->viewingAngle)*MB_PI/360.0) || ((*threadInfo->rot)(threadInfo->dataIndex,0) > (360-threadInfo->viewingAngle/2)*MB_PI/180.0))
		{
			threadInfo->saveMe = true;
			for (int i = 0; i < 128; i++) {	// for all 128 laser measurements

				Dcorr = (*threadInfo->correctionValues)(2, i);				// Distance correction factor
				Vo = (*threadInfo->correctionValues)(3, i)/100;				// Vertical offset, in Z-direction
				Ho = (*threadInfo->correctionValues)(4, i)/100;				// Horizontal offset, in X-direction
				theta = (*threadInfo->correctionValues)(1, i)*MB_PI/180.0;	// Vertical correction angle
				alpha = (*threadInfo->correctionValues)(0, i)*MB_PI/180.0;	// Rotational correction angle

				Vo += threadInfo->heightFromGround;	// account for height from ground
				//#define USE_MB_MATH
				// precompute:
#ifdef USE_MB_MATH
				totalOffset(0,0) = Ho;
				totalOffset(2,0) = -Vo;

				rotAlpha.makeZRotation( -alpha );
				rotTheta.makeXRotation( theta );

#else
				double cost = cos(theta), st = sin(theta);
				double ca = cos(alpha), sa = sin(alpha);
#endif
				//for (int j = 0; j < FULL_MEASUREMENTS_PER_PACKET; j++) {	// for each of the three slices
				int j = threadInfo->dataIndex;
					int baseLocationForThisMeasurement = j*MEASUREMENT_LENGTH * 4;	// 4 groups per total laser measurement
					const u_char *measurement = &threadInfo->truePacket[baseLocationForThisMeasurement];	// points the beginning of the set of 4 100-point measurements

					(*threadInfo->ct)(j, i) = measurement[(int)(*threadInfo->Ix)(0, i)];


					(*threadInfo->phi)(j, i) = (*threadInfo->rot)(j, (int)(*threadInfo->Ir)(0, i));
					//phi(j, i) = rot(j, i) * MB_PI/180.0;
					(*threadInfo->dt)(j, i) = (((int)measurement[(int)(*threadInfo->dmsb)(0, i)]) << 8) + (int)measurement[(int)(*threadInfo->dlsb)(0, i)];	// Measured point in Y-direction (I think)
					(*threadInfo->dt)(j, i) = (0.2 * (*threadInfo->dt)(j, i) + Dcorr)/100.0;


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
					rotPhi.makeZRotation( (*threadInfo->phi)(j, i) );


					// Consider the point dt, measured in y:
					// [0 1 0]		(becomes: [0 dt 0])
					// First rotation about X:
					// [0	cos(theta)	sin(theta)]
					// Second rotation about -alpha:
					// [(cos(theta)-sin(-alpha)	cos(theta)cos(-alpha)	1]	=	[cos(theta)sin(alpha)	cos(theta)cos(alpha)	1]
					// Third rotation around phi:
					// [cos(theta)(cos(phi)sin(alpha)-sin(phi)cos(alpha))	cos(theta)(sin(phi)sin(alpha)+cos(phi)cos(alpha))	1]
					//pointRotationLocation = rotPhi * rotAlpha * rotTheta * pointLocation;
					pointLocation(1,0) = (*threadInfo->dt)(j, i);	// point is located in y

					// We could perform the above then subtract, or simplify thing to save a few multiplications:
					// truePoint = pointLocation - totalOffset = rotPhi*rotAlpha*(rotTheta*pointLocation - totalOffset);
					truePoint = rotPhi*rotAlpha*(rotTheta*pointLocation - totalOffset);

					(*threadInfo->Py)(j, i) = truePoint(0,0);	// Y and X need to be swapped.
					(*threadInfo->Px)(j, i) = truePoint(1,0);
					(*threadInfo->Pz)(j, i) = truePoint(2,0);
#else

					// This is the C version from the matlab script, but now implements the MBmatrices to compute:

					double cp = cos((*threadInfo->phi)(j, i)), sp = sin((*threadInfo->phi)(j, i));
					(*threadInfo->Py)(j, i) = (*threadInfo->dt)(j, i) * cost*(sp*ca - cp*sa) - Ho*(cp*ca + sp*sa);
					(*threadInfo->Px)(j, i) = (*threadInfo->dt)(j, i) * cost*(cp*ca + sp*sa) - Ho*(sp*ca - cp*sa);
					(*threadInfo->Pz)(j, i) = (*threadInfo->dt)(j, i) * st + Vo;

#endif
				}
		} else {
			threadInfo->saveMe = true;//false;
		}
		pthread_exit(NULL);
		
	}


#ifdef _cplusplus
}
#endif