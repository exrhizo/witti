//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Brian Smith

package edu.arizona.ece473573.witti.activities;

import android.opengl.Matrix;
import android.util.Log;

/**
 * This class controls the camera matrix. It is the primary way of changing the current view 
 * 
 * 
 */
public class CloudCamera {
	
	private final String debug = "WITTI_DEBUG";
	
    //private long mLastUpdateTime;
    private float[] mViewMatrix;
    float eyeX, eyeY;
    float lookX, lookY, lookZ;
    float mThetaDecay;
    
    private float mSpinRate = 0.85f;

    public CloudCamera(){
    	
    	mViewMatrix = new float[16];

        eyeX = 0f; eyeY = 0f;

        lookX = 0.0f;
        lookY = 20.0f;
        lookZ = 10.0f;
        
        mThetaDecay = 0.0f;

        //mLastUpdateTime = System.currentTimeMillis();
    	
    }
    
    public float[] getViewMatrix(){
    	return mViewMatrix;
    }

    /**
     * This is a helper function to call the setLookAtM which updates
     * the current camera matrix
     * 
     * @param 	eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ: float values
     * 			for current camera matrix
     */
    public void setCamera(float eyeX, float eyeY, float eyeZ, 
            float lookX, float lookY, float lookZ, 
            float upX, float upY, float upZ) {
    		
    	Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    /**
     * Sets the server web address to be used in Launch mode.
     * 
     * @param 	theta: The current angle calculated from the CloudDisplayView based on how far apart
     * 					the motion events were
     */
	public void rotateCamera(float thetaX, float thetaY) {
		
		float tempX, tempY, tempZ;
		
		//Simple rotation matrix calculations to rotate about the Z-axis
		tempX = (float) ((lookX * Math.cos(thetaX)) - (lookY * Math.sin(thetaX)));
		tempY = (float) ((lookX * Math.sin(thetaX)) + (lookY * Math.cos(thetaX)));
		lookX = tempX;
		lookY = tempY;
		
		//Add the rotation about the y-axis
		tempX = (float) ((lookX * Math.cos(thetaY)) - (lookZ * Math.sin(thetaY)));
		tempZ = (float) ((-lookX * Math.sin(thetaY)) + (lookZ * Math.cos(thetaY)));
		lookX = tempX;
		lookZ = tempZ;

		this.setCamera(0.0f, -10.0f, 10.0f,
				lookX, lookY, lookZ,
                0.0f,   0.0f,  1.0f);
		
	}
	
	public void rotateCameraPassiveInit(float theta)
	{
		mThetaDecay = theta;
	}

    public void update(long time){
        //float dt = mLastUpdateTime - time;
        //mLastUpdateTime = time;
        
        
        if(Math.abs(mThetaDecay) > 0.005f)
        {
            //Log.d(debug, "theta: " + mThetaDecay);
        	mThetaDecay = mThetaDecay * mSpinRate;
        	rotateCamera(mThetaDecay, 0.0f);
        }
        else
        {
        	mThetaDecay = 0.0f;
        }

    }
}
