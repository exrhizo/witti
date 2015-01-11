//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Brian Smith, Brianna Heersink

package edu.arizona.ece473573.witti.activities;

import android.opengl.Matrix;

/**
 * This class controls the camera matrix. It is the primary way of changing the current view 
 * 
 * 
 */
public class CloudCamera {
	
    private final float PI = 3.14159265f;
    private float[] mViewMatrix;
    float eyeX, eyeY, eyeZ;
    float lookX, lookY, lookZ;
    float currThetaX, currThetaY;
    float mThetaDecay, mPhiDecay;
    
    float mTheta, mPhi, mMag;

    public CloudCamera(){
    	
    	mViewMatrix = new float[16];

        mTheta = 0;
        mPhi = 4 / PI;
        mMag = 20.0f;
        
        mThetaDecay = 0.0f;
        mPhiDecay = 0.0f;
    	
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
    public void setCamera(float theta, float phi, float mag) {
    		
    	mTheta = theta; mPhi = phi; mMag = mag;
    	
    	eyeX = (float) (mag*Math.cos(theta)*Math.cos(phi));
    	eyeY = (float) (mag*Math.sin(theta)*Math.cos(phi));
    	eyeZ = (float) (mag*Math.sin(phi));

    	lookX = (float) 0.0f;//(-2*mag*Math.cos(theta)*Math.cos(phi));
    	lookY = (float) 0.0f;//(-2*mag*Math.sin(theta)*Math.cos(phi));
    	lookZ = (float) 0.0f;//(-2*mag*Math.sin(phi));
    	
    	Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, 0f, 0f, 1f);
    }

    /**
     * Rotates the camera based on motion events
     * 
     * @param 	theta: The current angle calculated from the CloudDisplayView based on how far apart
     * 					the motion events were
     */
	public void rotateCamera(float theta, float phi) {
		
		mTheta += theta; mPhi += phi;
		
		if(mTheta < 0)
			mTheta = 0;
		else if(mTheta > 2*PI)
			mTheta = 2*PI;		
		
		this.setCamera(mTheta, mPhi, mMag);
		
	}
	
	public void rotateCameraPassiveInit()
	{
		mThetaDecay = mTheta;
		mPhiDecay = mPhi;
	}

    public void update(long time){
        //float dt = mLastUpdateTime - time;
        //mLastUpdateTime = time;
        
    	/*
        if(Math.abs(mThetaDecay) > 0.005f)
        {
            //Log.d(debug, "theta: " + mThetaDecay);
        	mThetaDecay = mThetaDecay * mSpinRate;
        }
        else
        {
        	mThetaDecay = 0.0f;
        }
        
        if(Math.abs(mPhiDecay) > 0.005f)
        {
        	mPhiDecay = mPhiDecay * mSpinRate;
        }
        else
        {
        	mPhiDecay = 0.0f;
        }
        
    	rotateCamera(mThetaDecay, mPhiDecay);*/

    }
    
    /**
     * Resets camera position to the original view.
     */
    public void resetCamera(){

    	mTheta = 0f; mPhi = 4 / PI; mMag = 20.0f;
        
    	this.setCamera(mTheta, mPhi, mMag);
    }

	public void zoomCamera(float zoom) {
		
		if(zoom < 0)
		{
			mMag -= 0.5;
		}
		else
		{
			mMag += 0.5;
		}
		
		this.setCamera(mTheta, mPhi, mMag);
	}
	
	public float getTheta()
	{
		return mTheta;
	}
	
	public float getPhi()
	{
		return mPhi;
	}
	
}
