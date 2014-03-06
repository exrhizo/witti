#include <cstdlib>
#include <cstdio>
#include "pcap.h"
#include <string>
#include <iostream>
#include <fstream>

#define LIMIT_ANGLES

void usage( char *argv)
{
    printf("Usage: \n");
    printf(" %s [-rs] [-i file] [-o file] [-sssnnn]\n", argv );
    printf(" %s -o file [-sssnnn]\n", argv );
    printf("\n");
    printf(" -sssnnn : limit field of view to angles sss (start), nnn (end)\n");
    printf("           (angles are in degrees, starting from 0, opposite the connector.\n");
    printf(" -i file : read from 'file', not device.\n");
    printf(" -r      : read for one revolution from device (does not make sense with -i).\n");
    printf(" -s      : read for infinity from device (does not make sense with -i).\n");
    printf(" -o file : produce file as output.\n");
}

int main( int argc, char** argv )
{
    pcap_if_t *alldevs, *d;
	pcap_t *fp;
	u_int inum, i=1;
	char errbuf[PCAP_ERRBUF_SIZE];
	int res;
	struct pcap_pkthdr *header;
	const u_char *pkt_data;
	FILE *binfile;
	struct tm *ltime;
	char timestr[16];
	time_t local_tv_sec;
	int xflag=0;
	unsigned long int rot,rotCnt=0;
	int sss=0;
    int realtime = 0;
    int singleRev = 0;
    int nnn=360;
    
    
    std::string filename;
    std::string outfilename;
    
    // let's parse the inputs
    for (int i=1; i<argc; i++) {
        std::string arg = argv[i];
        if( arg == "-o" )
        {
            if( i+1 < argc )
            {
                outfilename = argv[i+1];
                i++;
            }
        }
        else if( arg == "-i" )
        {
            if( i+1 < argc )
            {
                filename = argv[i+1];
                i++;
            }
        }
        else if( arg == "-r" )
        {
            filename = arg;
            singleRev = 0;
        }
        else if( arg == "-s" )
        {
            filename = "-r";
            singleRev = 0;
        }
        else if( argv[i][0] == '-') {
            if( strlen(argv[i]) == 7 )
            {
                char tmp[4];
                sprintf(tmp, "%.*3s", 3, argv[i]+1);
                printf("tmp=%s\n", tmp);
                sss = atoi(tmp);
                sprintf(tmp, "%.*3s", 3, argv[i]+4);
                printf("tmp=%s\n", tmp);
                nnn = atoi(tmp);
                printf("sss=%d,nnn=%d\n", sss, nnn);
            }
        }
    }
    
    if (filename == "" ) {
        filename = "-r";
        singleRev = 1;
    }
    
    if( filename == "-realtime" ||
       filename == "-rt" ||
       filename == "-r" )
    {
        realtime = 1;
        std::cout << "Capturing packets in real time..." << std::endl;
        if (outfilename.size( ) == 0 ) {
            outfilename = "datafile.bin";
        }
//        outfilename = filename;
    }

    if ( outfilename.size( ) == 0 ) {
        outfilename = filename + ".out.bin";
    }
    

    
    std::cout << "filename = " << filename << std::endl;
    std::cout << "outfilename = " <<outfilename << std::endl;
    
//    if ( argc > 1 ) {
//        filename = std::string( argv[1] );
//        std::cout << filename << std::endl;
//    }
//    else
//    {
//        filename = "error.bin";
//    }
//
//    outfilename = filename + ".out.bin";
//    if( filename == "-realtime" ||
//       filename == "-rt" ||
//       filename == "-r" )
//    {
//        realtime = 1;
//        std::cout << "Capturing packets in real time..." << std::endl;
//        filename = "datafile.bin";
//        outfilename = filename;
//    }
    

    printf("Opening file...if you get a segfault, don't forget to type 'sudo'\n");
	binfile=fopen(outfilename.c_str(),"wb");
    
//    if (argc > 2 ) {
//        if (argv[2][0] == '-') {
//            if( strlen(argv[2]) == 7 )
//            {
//                char tmp[4];
//                sprintf(tmp, "%.*3s", 3, argv[2]+1);
//                printf("tmp=%s\n", tmp);
//                sss = atoi(tmp);
//                sprintf(tmp, "%.*3s", 3, argv[2]+4);
//                printf("tmp=%s\n", tmp);
//                nnn = atoi(tmp);
//                printf("sss=%d,nnn=%d\n", sss, nnn);
//            }
//        }
//    }
//
    if( realtime )
    {
    	if(pcap_findalldevs(&alldevs, errbuf) == -1)
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
    		return -1;
    	}
        /* Jump to the selected adapter */
    	for (d=alldevs, i=0; i< inum-1 ;d=d->next, i++);
    	printf("Adapter=%d,name=%s\n",i, d->name);
        
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
            return -1;
        }
    }
    else
    {
        printf("Device name=%s\n",filename.c_str());
        /* Open the adapter */
        if ((fp = pcap_open_offline(filename.c_str(),	// name of the device
                                    errbuf							// error buffer
                                    )) == NULL)
        {
            fprintf(stderr,"\nError opening adapter\n");
            return -1;
        }
    }
	
	
	printf("reading packets...\n");
	int j=0;
	int maxJ = 1000000;
    if( realtime && singleRev )
    {
        maxJ = 360*2; // this here's 1 revolution, my friends
//        maxJ = 3600; // this here's 1 revolution, my friends
    }
//    while( j<100 ) // JMS: this helps us keep file sizes small(ish)...
    {
        std::cout << "maxJ=" << maxJ << std::endl;
	/* Read the packets */
	while((res = pcap_next_ex( fp, &header, &pkt_data)) >= 0 && j < maxJ )
	{
        
		if(res == 0)
        /* Timeout elapsed */
			continue;
        
        /* convert the timestamp to readable format */
		local_tv_sec = header->ts.tv_sec;
		ltime=localtime(&local_tv_sec);
		strftime( timestr, sizeof timestr, "%H:%M:%S", ltime);
		xflag = 0;
		for (i=0;i<12; i++)
		{
			rot=pkt_data[45+i*100]<<8|pkt_data[44+i*100]; //Rotation value
//			printf("rot=%ld, j=%d\n",rot,j);
#ifndef LIMIT_ANGLES
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
#else
            if( (rot > sss*100 && rot < nnn*100) // covers cases where 0-360, 90-180, etc.
               || ( ( sss > nnn ) && (rot > sss*100 || rot < nnn*100 ) )
               )
            {
                xflag = 1;
            }
            else
            {
                xflag = xflag + 0;
            }
#endif
		}
		if (xflag>0) //At least one complete revolution was captured
		{
			fwrite(pkt_data+42,1,1200,binfile);
		}
        // j is incremented, we set maxJ to be low if we
        // want to stop fairly fast, otherwise it is always high;
        j++;
	}
}

	if(res == -1)
	{
		printf("Error reading the packets: %s\n", pcap_geterr(fp));
		return -1;
	}
    
	pcap_close(fp);
	fclose(binfile);
	return 0;
    
    
}

