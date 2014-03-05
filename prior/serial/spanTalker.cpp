/*

 Can be used to send specific messages to the SPAN CPT
 in order to get it set up and configured correctly
 
 Includes the ability to tune the lever arm, and to 
 determine whether or not the right mode is available, and
 whether kinematic or static config is necessary
*/

#include <cstdio>
#include <cstdlib>
#include <iostream>
#include "serial.h"
#include <termios.h>
#include <sys/ioctl.h>
#include <sys/fcntl.h>
#include <curses.h>

#define READ_BUFFER_SIZE 10000
#define WRITE_BUFFER_SIZE 10000

#include <jaus.h>								// Header file for JAUS types, structs and messages
#include <openJaus.h>							// Header file for the OpenJAUS specific C/C++ code base

int readFromHardware( int fd, char* buffer );
void gposReadyState(OjCmpt myGpos);				// Function prototype for My GPOS ready state callback routine

static int fd;
static char readBuffer[READ_BUFFER_SIZE];
static char writeBuffer[WRITE_BUFFER_SIZE];

int main(void)
{
	OjCmpt myGpos;														// Variable that will store the component reference
    
	myGpos = ojCmptCreate("My GPOS", JAUS_GLOBAL_POSE_SENSOR, 1.0);		// Create the component
	if(myGpos == NULL)
	{
		printf("Error creating component\n");
		return 1;
	}
    
	ojCmptAddService(myGpos, JAUS_GLOBAL_POSE_SENSOR);					// Add GPOS service type
	ojCmptAddServiceInputMessage(myGpos, JAUS_GLOBAL_POSE_SENSOR, JAUS_QUERY_GLOBAL_POSE, 0xFF);
	ojCmptAddServiceOutputMessage(myGpos, JAUS_GLOBAL_POSE_SENSOR, JAUS_REPORT_GLOBAL_POSE, 0xFF);
    
	ojCmptSetStateCallback(myGpos, JAUS_READY_STATE, gposReadyState);	// Set ready state callback
    
	ojCmptSetState(myGpos, JAUS_READY_STATE);							// Set the current state to ready
    
    // first, let's open up the port
    char device[] = "/dev/ttyS1";
    fd = open( device, O_RDWR | O_NOCTTY | O_NONBLOCK );
    if( fd < 0 )
    {
		printf( "Error! Could not open %s\n, aborting.\n", device );
	}
    /* sets to 9600, 8N1 */
    set_interface_attribs( fd, B9600, 0);
    /* almost all docco says 115200, but it didn't work for JMS */
    //set_interface_attribs( fd, B115200, 0);
    /* set to no blocking */
    set_blocking( fd, 0 );
    
	memset(writeBuffer, 0, sizeof(writeBuffer));

    
	ojCmptRun(myGpos);													// Begin running state machine
	bool RUNNING = TRUE;	
    while (RUNNING) {
	std::cout << "What is thy bidding, my master? >";
	//getchar( );
	std::string readFromStdin;
	getline(std::cin, readFromStdin);
	std::cout << "read: " << readFromStdin << std::endl;
	std::string s = readFromStdin;
	if( s.compare("q") == 0 ||
		s.compare("quit") == 0 ||
		s.compare("^[") == 0 )
	{
		std::cout << "Exiting..." << std::endl;
		RUNNING=FALSE;
		continue;
	}
	else
	{
		// we try to send this string to the device, 
		// and see what it says back
		s += "\r\n";
		strcpy(writeBuffer, s.c_str( ) );
		write(fd,writeBuffer,s.length( ));
		//sleep(1);
		//tcflush( fd,TCOFLUSH);

		//write(fd,"\n",1);
		std::cout<<"writing "<< writeBuffer <<std::endl;
		s = "";
	}

    }
    
	ojCmptDestroy(myGpos);												// Shutdown and destroy component
    
	return 0;
}

void gposReadyState(OjCmpt myGpos)										// Ready state callback function
{
    readFromHardware(fd, readBuffer);
}


// reads (blocking?) from the hardware for new data
// returns 1 if data is new, 0 otherwise
int readFromHardware( int fd, char* buffer )
{
    //printf("Read from Hardware\n");
    int result = 0; // isNew = false
    char tmp2[10000];
//	else
	{
		// let's make sure we write to a nice, quiet place
		readLine( fd, buffer, READ_BUFFER_SIZE, '\n' );
            printf( "%s", buffer );
        // result = readAll(myData->fd, &tmp2, '\n');
        //result = readAll(myData->fd, &(myData->buffer), '\n');
		// NOTE: we don't print the \n because it comes in the msg
        //printf("Read %d bytes.", result );
        //	printf( "%s", tmp2);
        
        // HACK
        while( bytesAvailable( fd ) > 200 )
        {
            // let's gobble up a BUNCH
            readLine( fd, buffer, READ_BUFFER_SIZE, '\n' );
            printf( "%s", buffer );
            
        }
        //printf( "%s", myData->buffer );
        if( strlen( buffer ) > 0 )
        {
            result = 1;
        }
        
    }
    //printf("leaving Read from Hardware\n");
    return result;
}
