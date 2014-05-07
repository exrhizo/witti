/*!
 @header libMBtime
 libMBtime provides easy to use matrices and vectors for C++.  In the future, other math objects may be included.
 @copyright 12 Cent Dwarf
 @author Matt Bunting
 @updated 2012-02-13
 @version 0.0.1
 */

#ifndef BUNTING_TIME
#define BUNTING_TIME

#include <time.h>
#include <sys/time.h>
#include <math.h>
#include <unistd.h>

/*!
 @class Time
 @abstract Keeps track of time; useful for none RTOS setups
 @discussion This keeps track of time such as running time and change in loop time.
 @updated 2013-02-12
 */
class MBtime {
private:
    struct timeval tv;              // what handles the time
    double fnum;                    // the number of frequency measurements
    double timecycle,oldtimecycle;  // to calculate the difference in time
	double dTimeBuffer[10];
	double fpsLPF;
	double fps;
public:
    double favg, dtime, runningtime;// average freq, delta time, overal time

	/*!
	 @function update
	 @discussion This function typically should be called once every loop cycle.  This updates the measured components based on the last call.
	 @abstract Updates internal time values.
	 */
    void update();                  // updates to get new time values

	/*!
	 @function initialize
	 @discussion This function starts the internal time measurement.  The next time update() is called, values are updated based in reference to this call.
	 @abstract Initializes the internal values needed for time measurement.
	 */
    void initialize();              // self explanatory

	/*!
	 @function runningTime
	 @discussion This returns the last computed running time since initialization.
	 @abstract Gets the last running time computation.
	 @return The time since intialize() was called.
	 */
    double runningTime( )
	{
		return runningtime;
	};

	/*!
	 @function dTime
	 @discussion This returns the last computed difference in time between the last update() and update() prior to that.
	 @abstract Gets the last computed delta time
	 @return The delta time between the previous two update() calls.
	 */
    double dTime( )
	{
		return dtime;
	};

	/*!
	 @function frequencyAverage
	 @discussion This returns the last computed total update frequency average of update() calls.  This is averaged over the entire lifespan.
	 @abstract Gets the last computed average frequency of calls of update()
	 @return The average frequency of update() calls.
	 */
    double frequencyAverage( )
	{
		return favg;
	};

	/*!
	 @function frequency
	 @discussion This returns the last computed running update frequency average of update() calls.  This is a moving average from the last ten samples.
	 @abstract Gets the last computed moving average of frequency from calls of update()
	 @return The moving average of frequency from update() calls.
	 */
    double frequency( )
	{
		return fps;
	};
};


#endif
