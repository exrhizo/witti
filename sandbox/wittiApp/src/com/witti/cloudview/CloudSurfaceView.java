//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package com.witti.cloudview;

import com.witti.activities.CloudCamera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class CloudSurfaceView  extends GLSurfaceView{
	
    private static final String CAT_TAG = "WITTI_CloudSurfaceView";
    private static final String DEBUG = "DEBUG_TAG";
    CloudRenderer mRenderer;
    private CloudCamera mCamera;
    PointCloud mCloudPoints;
    private GestureDetector mGestureDetector;
    
    //Touch vars
    float initialX, initialY;
    float prevX, prevY, currX, currY;
    float eyeX, eyeY;
    float lookX, lookY, lookZ;
    float theta;
    float LENGTH = 100f;

    public CloudSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(getContext(), new CameraGestureListener());
        eyeX = 0f; eyeY = 0f;

        lookX = 0.0f;
        lookY = 20.0f;
        lookZ = 10.0f;
        
        initialize();
    }
    
    public CloudSurfaceView(Context context) {
        super(context);

        mGestureDetector = new GestureDetector(getContext(), new CameraGestureListener());
        
        initialize();
    }

    @Override
    public void setRenderer(Renderer renderer){
        super.setRenderer(renderer);
        mRenderer = (CloudRenderer)renderer;
    }

    private void initialize(){
        Log.v(CAT_TAG, "CloudRenderer initialize");
        mCloudPoints = new PointCloud(this);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent me){
    	super.onTouchEvent(me);
    	
    	float tempX;
    	float tempY;
    	
    	switch(me.getAction()){
    	case MotionEvent.ACTION_UP:
    		break;
    	case MotionEvent.ACTION_DOWN:
    		prevX = initialX = me.getX();
    		prevY = initialY = me.getY();
    		break;
    	case MotionEvent.ACTION_MOVE:
    		currX = me.getX();
    		currY = me.getY();
    		
    		//theta = (float) Math.atan((currX-prevX)/LENGTH);
    		theta = (currX-prevX) / 360;
    		
    		tempX = (float) ((lookX * Math.cos(theta)) - (lookY * Math.sin(theta)));
    		tempY = (float) ((lookX * Math.sin(theta)) + (lookY * Math.cos(theta)));
    		lookX = tempX;
    		lookY = tempY;
    		
    		Log.d(DEBUG, "LookX: " + lookX + " LookY: " + lookY + " theta: " + theta);
    		
    		mCamera.setCamera(0.0f, -10.0f, 10.0f,
    				lookX, lookY, lookZ,
                    0.0f,   0.0f,  1.0f);
    		prevX = currX;
    		prevY = currY;
    		break;
    	default:
    		break;
    			
    	}
    	
    	//mGestureDetector.onTouchEvent(me);
    	/*
    	mCamera.setCamera((float) (10*Math.cos(mRenderer.mTime)), (float) (10*Math.sin(mRenderer.mTime)), (float) (5+5*Math.sin(.01*mRenderer.mTime)),
                0.0f,   0.0f,  0.0f,
                0.0f,   0.0f,  1.0f);*/
    	return true;
    	
    }
    
    public void setCamera(CloudCamera cc){
    	mCamera = cc;
    }
    
    private class CameraGestureListener implements GestureDetector.OnGestureListener {

    	@Override
    	public boolean onDown(MotionEvent arg0) {
    		// TODO Auto-generated method stub
    		return false;
    	}

    	@Override
    	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
    			float arg3) {
    		return false;
    	}

    	@Override
    	public void onLongPress(MotionEvent arg0) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
    			float arg3) {
    		// TODO Auto-generated method stub
    		return false;
    	}

    	@Override
    	public void onShowPress(MotionEvent arg0) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public boolean onSingleTapUp(MotionEvent arg0) {
    		mRenderer.mTime += 0.02;
    		return true;
    	}
    	

    }

}
