#include "MBmath.h"

#ifdef _cplusplus
extern "C"
{
#endif

	float tri(float x) {
		float y;

		x += MB_PI/2.0;
		if(x < 0)
			x = -x;

		y = 2.0*fmodf(x, 2.0*MB_PI)/MB_PI;
		if(y > 2.0)
		{
			y = 4-y;
		}

		return (y - 1.0);
	}

	float sqr(float x) {
		float y;

		x += MB_PI/2.0;
		if(x < 0)
			x = -x;

		y = fmodf(x, 2.0*MB_PI);
		if(y > MB_PI)
		{
			y = -1;
		} else {
			y = 1;
		}

		return y;
	}

	float clamp(float x, float min, float max) {
		if (x < min) {
			x = min;
		} else if(x > max) {
			x = max;
		}
		return x;
	}

#ifdef _cplusplus
    }
#endif
