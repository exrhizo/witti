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
static const char* const LPF_C_Id =
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

#ifdef _cplusplus
extern "C"
{
#endif
	
	void LPF::initialize()
	{
        outputValue = 0;
		timeConstant = 0;
		filterTimer.initialize();
    }

    double LPF::filter( double input )
	{
		double alpha;
		filterTimer.update();
		alpha = exp(- filterTimer.dTime()/timeConstant );
		outputValue = alpha*outputValue + (1.0 - alpha)*input;
		
		return outputValue;
    }

	const MBquaternion& LPF::filter( const MBquaternion& input )
	{
		double alpha;
		filterTimer.update();
		alpha = exp(- filterTimer.dTime()/timeConstant );
		outputQuaternion = slerp( input, outputQuaternion, alpha );

		return outputQuaternion;
    }

	void LPF::setTimeConstant( double timeConstantValue )
	{
		timeConstant = timeConstantValue;
	}

	double LPF::filter( double input, double timeConstantValue )
	{
		(*this).setTimeConstant( timeConstantValue);
		return (*this).filter( input );
	}

#ifdef _cplusplus
}
#endif
