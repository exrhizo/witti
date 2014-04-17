package edu.arizona.ece473573.witti.activities;

import android.opengl.Matrix;

public class CloudCamera {
	
    private float[] mViewMatrix;

    public CloudCamera(){
    	
    	mViewMatrix = new float[16];
    	
    }
    
    public float[] getViewMatrix(){
    	return mViewMatrix;
    }
    
    public void setCamera(float eyeX, float eyeY, float eyeZ, 
            float lookX, float lookY, float lookZ, 
            float upX, float upY, float upZ) {
    		
    	Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }
}
