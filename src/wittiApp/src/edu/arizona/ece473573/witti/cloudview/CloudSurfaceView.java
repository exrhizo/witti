//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Brian Smith, Alex Warren

package edu.arizona.ece473573.witti.cloudview;

import edu.arizona.ece473573.witti.activities.CloudCamera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CloudSurfaceView  extends GLSurfaceView{
	
    private static final String CAT_TAG = "WITTI_CloudSurfaceView";
	private final String debug = "WITTI_DEBUG";
    CloudRenderer mRenderer;
    private CloudCamera mCamera;
    PointCloud mCloudPoints;
    
    //Touch vars
    float initialX, initialY;
    float prevX, prevY, currX, currY;
    float thetaX, thetaY;

    public CloudSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initialize();
    }
    
    public CloudSurfaceView(Context context) {
        super(context);

        
        initialize();
    }

    @Override
    public void setRenderer(Renderer renderer){
        super.setRenderer(renderer);
        mRenderer = (CloudRenderer)renderer;
    }

    private void initialize(){
        Log.v(CAT_TAG, "CloudRenderer initialize");
    }

    /**
     * The touch events are used primarily for rotating the camera
     * 
     */
    @Override
    public boolean onTouchEvent(MotionEvent me){
    	super.onTouchEvent(me);
    	    	
    	/*
    	 * When a user begins to drag their fingers across the screen,
    	 * the distance between each point is used to calcuate an angle,
    	 * the same way a segment of a circle maps out an angle. The LENGTH
    	 * constant is used as the radius
    	 */
    	switch(me.getAction()){
    	case MotionEvent.ACTION_UP:
    		mCamera.rotateCameraPassiveInit(thetaX);
    		//Log.d(debug, "Action up theta: " + theta);
    		break;
    	case MotionEvent.ACTION_DOWN:
    		prevX = initialX = me.getX();
    		prevY = initialY = me.getY();
    		break;
    	case MotionEvent.ACTION_MOVE:
    		currX = me.getX();
    		currY = me.getY();
    		
    		//theta = (float) Math.atan((currX-prevX)/LENGTH);
    		//Much simpler angle calculation
    		thetaX = (currX-prevX) / 360;
    		thetaY = (currY - prevY) / 360;
    		
    		//mRenderer.setZoom(theta);
    		
    		mCamera.rotateCamera(thetaX, thetaY);
    		
    		prevX = currX;
    		prevY = currY;
    		break;
    	default:
    		break;
    			
    	}
    	
    	return true;
    	
    }
    
    public void setCamera(CloudCamera cc){
    	mCamera = cc;
    }
    
}
