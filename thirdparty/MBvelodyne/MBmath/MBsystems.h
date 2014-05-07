#ifndef MB_FILTER_H
#define MB_FILTER_H

//#include <math.h>
//#include <time.h>
#include <sys/time.h>
//#include <unistd.h>
#include "MBmath.h"

// This is for backward compatibility:

/*!
 @class MBtime
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

/*!
 @class LPF
 @abstract Implementation of a Low Pass Filter; useful for none RTOS setups
 @discussion This class low pass filters the input signal based on a filter time constant.  There is an internal timer that computes the alpha value dynamically for use with an alpha filter.
 @updated 2013-04-19
 */
class LPF {
private:
	MBtime filterTimer;
	double outputValue;
	double timeConstant;

	MBquaternion outputQuaternion;
public:

	/*!
	 @function filter
	 @discussion This function typically should be called once every loop cycle.  This updates the filtered value based on the time since the last call.
	 @abstract Updates the filter state.
	 @param input The input value to be filtered
	 @return The output of the filter
	 */
    double filter( double input );
	const MBquaternion& filter( const MBquaternion& input );

	/*!
	 @function filter
	 @discussion This function typically should be called once every loop cycle when a changing time constant is needed.  This updates the filtered value based on the time since the last call.
	 @abstract Updates the filter state.
	 @param input The input value to be filtered
	 @param timeConstantValue The new time constant of the filtes
	 @return The output of the filter
	 */
    double filter( double input, double timeConstantValue );

	/*!
	 @function setTimeConstant
	 @discussion This function typically should be called after initialization, though could be called dynamically.  This sets the time constant of the filter.
	 @abstract Updates the filter time constant.
	 @param timeConstantValue The desired LPF time constant
	 */
    void setTimeConstant( double timeConstantValue );

	/*!
	 @function output
	 @discussion This returns the last computed output of the filter
	 @abstract Gets the last filter output.
	 @return The previous output of the filter
	 */
    double output( )
	{
		return outputValue;
	};

	/*!
	 @function initialize
	 @discussion This function starts the internal time measurement.  The next time filter() is called, values are updated based in reference to this call.
	 @abstract Initializes the internal values needed for time measurement.
	 */
    void initialize();              // self explanatory
};

#endif
