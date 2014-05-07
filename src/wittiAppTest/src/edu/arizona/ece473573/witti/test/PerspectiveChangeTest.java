package edu.arizona.ece473573.witti.test;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.widget.Button;
import edu.arizona.ece473573.witti.activities.CloudCamera;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.cloudview.CloudSurfaceView;

public class PerspectiveChangeTest extends ActivityInstrumentationTestCase2<DisplayActivity>{
	
	private CloudCamera mCloudCamera;
	private CloudSurfaceView mCloudSurface;
	private DisplayActivity mActivity;
	private Button mRefreshButton;
	
	public PerspectiveChangeTest(){
		super(DisplayActivity.class);
	}
	
    /**
     * Launches the DisplayActivity 
     */
	@Override
	protected void setUp() throws Exception{
		
		super.setUp();
		setActivityInitialTouchMode(false);

		
		Intent intent = new Intent();
		intent.putExtra("inDemoMode", true);
		intent.putExtra("inTestMode", true);
		
		setActivityIntent(intent);
		mActivity = getActivity();
		mCloudCamera = mActivity.mCamera;
		mCloudSurface = mActivity.mCloudSurfaceView;
		
	}
	
	public void testPerspectiveChange()
	{
		

		Instrumentation inst = getInstrumentation();
		//Testing with theta = 0.08 rad for thetaX & thetaY
		//--> currX - prevX && currY - prevY == 28.8
		float currX = 40f;
		float currY = 50f;
		
		float prevX = 11.2f;
		float prevY = 21.2f;
		
		//Create the down motion event using the prev values
		MotionEvent down =  MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
				MotionEvent.ACTION_DOWN, prevX, prevY, 0);
		
		inst.sendPointerSync(down);
		
		//Create the move motion event using the curr values
		MotionEvent move = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 
				MotionEvent.ACTION_MOVE, currX, currY, 0);
		
		inst.sendPointerSync(move);
		
		SystemClock.sleep(100);
		
		Assert.assertTrue(mCloudCamera.getThetaX() == 0.08f);
		Assert.assertTrue(mCloudCamera.getThetaY() == 0.08f);
	}
}
