//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Brian Smith, Alex Warren

package edu.arizona.ece473573.witti.cloudview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import edu.arizona.ece473573.witti.activities.CloudCamera;

public class CloudSurfaceView extends GLSurfaceView {

    private static final String CAT_TAG = "WITTI_CloudSurfaceView";
    CloudRenderer mRenderer;
    private CloudCamera mCamera;
    PointCloud mCloudPoints;

    //Touch vars
    private float prevX, prevY, currX, currY;
    private float thetaX, thetaY;
    private final float radius = 360;

    //NotZoom = 2; Zoom = 1; Rotate = 0;
    //NotZoom used to indicate that a zoom happened, but there's possibly still
    //one pointer on the screen still. To keep things simple, if a zoom happens
    //a rotation can't happen until both pointers have been lifted
    private int state;

    //Zoom vars
    private float prevDist; //Initial distance between finters/pointers
    private float currDist; //Updated distance between fingers/pointers

    public CloudSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize();

        state = 0;
    }

    public CloudSurfaceView(Context context) {
        super(context);

        initialize();

        state = 0;
    }

    @Override
    public void setRenderer(Renderer renderer) {
        //super.setEGLConfigChooser(8,8,8,8,16,0);
        super.setRenderer(renderer);
        mRenderer = (CloudRenderer) renderer;
    }

    private void initialize() {
        Log.v(CAT_TAG, "CloudRenderer initialize");
    }

    /**
     * The touch events are used primarily for rotating the camera
     */
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        super.onTouchEvent(me);
        int maskedAction = me.getActionMasked();
        /*
    	 * When a user begins to drag their fingers across the screen,
    	 * the distance between each point is used to calcuate an angle,
    	 * the same way a segment of a circle maps out an angle. The LENGTH
    	 * constant is used as the radius
    	 */
        switch (maskedAction) {
            case MotionEvent.ACTION_UP:
                //Only send this if we're in a rotational state
                if (state == 0) mCamera.rotateCameraPassiveInit();
                break;
            case MotionEvent.ACTION_DOWN:
                //Only revert back to rotation on a one finger press
                //ie, if a zoom is initiated, it can only revert back to rotation
                //after both fingers have been lifted
                state = 0;
                prevX = me.getX();
                prevY = me.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(debug, "action move");
                if (state == 0) {
                    currX = me.getX();
                    currY = me.getY();

                    //Much simpler angle calculation
                    thetaX = (currX - prevX) / radius;
                    thetaY = (prevY - currY) / radius;

                    mCamera.rotateCamera(thetaX, thetaY);

                    prevX = currX;
                    prevY = currY;
                } else if (state == 1) {
                    currDist = getDistance(me);
                    mCamera.zoomCamera((currDist - prevDist) * 0.05f);
                    prevDist = currDist;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                state = 1; //Two fingers implies zooming action
                prevDist = getDistance(me);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                state = 2;//Revert back to
                break;
            default:
                break;

        }

        return true;

    }

    public void setCamera(CloudCamera cc) {
        mCamera = cc;
    }

    private float getDistance(MotionEvent me) {
        float xDist, yDist;
        xDist = me.getX(0) - me.getX(1);
        yDist = me.getY(0) - me.getY(1);
        return (float) Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }

    public float getRadius() {
        return radius;
    }


}
