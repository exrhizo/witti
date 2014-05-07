/* ---------------------------------------------------------------------------
 ** This software is in the public domain, furnished "as is", without technical
 ** support, and with no warranty, express or implied, as to its usefulness for
 ** any purpose.
 **
 ** Time.cpp
 ** A timer used to measre loop time
 **
 ** Author: Matt Bunting
 ** Email: 12centdwarf <at> gmail <dot> com
 ** -------------------------------------------------------------------------*/

#ifdef IDENT_C
static const char* const Time_C_Id =
"$Id$";
#endif

/* -- module -----------------------------------------------------------------
 **
 ** See examples for implementation
 **
 ** -- implementation ---------------------------------------------------------
 **
 **	runningtime		Running time since first initialization
 ** dtime			Difference in time since last call
 ** favg			Average update frequency
 ** update()		To be called for every iteration
 ** initialize()	To be called before loop
 **
 ** -------------------------------------------------------------------------*/

#include "MBsystems.h"

#include <math.h>
#include <time.h>
#include <sys/time.h>
#include <unistd.h>

#ifdef _cplusplus
extern "C"
{
#endif
	
	void MBtime::initialize()
	{
        favg = 30;
        fnum = 0;
        // tint = 0;
        gettimeofday(&tv, NULL);					// Grab the current time (simply an initialization)
        timecycle = tv.tv_sec + tv.tv_usec*1e-6;	// Compute the current time, in seconds
        usleep(10000);
    }

    void MBtime::update()
	{
        gettimeofday(&tv, NULL);							// Grab the current time
        oldtimecycle = timecycle;							// Store the old time
        timecycle = tv.tv_sec + tv.tv_usec*1e-6;			// Compute the current time, in seconds
        dtime = timecycle - oldtimecycle;					// Find the time difference.  This is used throughout the main loop, for velocities and temporal filters
        favg = 1./(((1./favg) * fnum + dtime)/ (fnum + 1));	// Compute the running average of the looop frequency
        fnum++;
        runningtime += dtime;

		for( int i = 9; i > 0; i--)
		{
			dTimeBuffer[i] = dTimeBuffer[i-1];
		}
		dTimeBuffer[0] = dtime;

		float currentFrequency = 0;
		float w = 5;
		for( int i = 0; i < 10; i++) {
			//currentFrequency += ((9.5 - (double)i)/10.0)/(5) * dtimeBuffer[i];
			currentFrequency += ((w/2.0 - (w/9.0)*(double)i + 5)/50) * dTimeBuffer[i];
		}
		currentFrequency = 1.0/(currentFrequency);

		float fpsAlpha;
		fpsAlpha = expf(-dtime/0.4);
		fpsLPF = fpsLPF * fpsAlpha + (1-fpsAlpha) * currentFrequency;
		fps = fpsLPF;// 1.0/dtimeLPF;
    }


#ifdef _cplusplus
}
#endif
