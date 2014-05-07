//ECE 573 Project
//Team: Witty
//Date: 5/7/14
//Author: Brian Smith

package edu.arizona.ece473573.witti.test;

import junit.framework.Assert;
import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import edu.arizona.ece473573.witti.activities.CloudCamera;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.cloudview.CloudSurfaceView;

/**
 * 
 * 	A.5 Perspective Change Requirement: "The phone application software shall be capable of changing
 * 											the perspective of the displayed data on the phone."
 */
public class PerspectiveChangeTest extends ActivityInstrumentationTestCase2<DisplayActivity>{
	
	private CloudCamera mCloudCamera;
	private CloudSurfaceView mCloudSurface;
	private DisplayActivity mActivity;
	
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
	
	/**
	 * 
	 * 	This test does only a rotation change. All expected values were calculated on paper by hand
	 */
	public void testRotationChange()
	{
		float epsilon = 5.96e-8f;

		//Testing with theta = 0.08 rad for thetaX & thetaY
		//--> currX - prevX && currY - prevY == 28.8
		float currX = 40f;
		float currY = 50f;
		
		float prevX = 11.2f;
		float prevY = 21.2f;
		
		mCloudCamera.rotateCamera(	(currX - prevX)/mCloudSurface.getRadius(), 
									(currY - prevY)/mCloudSurface.getRadius());
		
		SystemClock.sleep(100);
		
		Assert.assertTrue(mCloudCamera.getThetaX() == 0.08f);
		Assert.assertTrue(mCloudCamera.getThetaY() == 0.08f);
		
		SystemClock.sleep(100);
		
		//[x, y, z]
		float [] lookVals = mCloudCamera.getLook();
		
		//Comparing two floats the IBM way
		//Test value calculated on paper
		//Comparing lookX
		Assert.assertTrue(Math.abs((lookVals[0]/-1.5982939) - 1) < epsilon);
		
		//Comparing lookY
		Assert.assertTrue(Math.abs((lookVals[1]/19.073126) - 1) < epsilon);
		
		//Comparing lookZ
		Assert.assertTrue(Math.abs((lookVals[2]/11.561199) - 1) < epsilon);
	}
	

	/**
	 * 
	 * 	This test does only a rotation change then performs a zoom. 
	 * 	All expected values were calculated on paper by hand
	 */
	public void testZoom()
	{

		float epsilon = 5.96e-8f;

		//Testing with theta = 0.08 rad for thetaX & thetaY
		//--> currX - prevX && currY - prevY == 28.8
		float currX = 40f;
		float currY = 50f;
		
		float prevX = 11.2f;
		float prevY = 21.2f;
		
		mCloudCamera.rotateCamera(	(currX - prevX)/mCloudSurface.getRadius(), 
									(currY - prevY)/mCloudSurface.getRadius());
		
		//Sending a zoom value of 1.1
		mCloudCamera.zoomCamera(1.1f);
		
		SystemClock.sleep(100);
		
		float [] lookVals = mCloudCamera.getLook();
		
		float [] eyeVals = mCloudCamera.getEye();
		
		//Comparing updated eye values
		//Actual values calculated on paper
		//eyeX
		Assert.assertTrue(Math.abs((eyeVals[0]/-0.078625664) - 1) < epsilon);
		//eyeY
		Assert.assertTrue(Math.abs((eyeVals[1]/-9.061727) - 1) < epsilon);
		//eyeZ
		Assert.assertTrue(Math.abs((eyeVals[2]/10.568736) - 1) < epsilon);
		
		//Comparing looks values calculated on paper
		//eyeX
		Assert.assertTrue(Math.abs((lookVals[0]/-1.6769196) - 1) < epsilon);
		//eyeY
		Assert.assertTrue(Math.abs((lookVals[1]/20.0114) - 1) < epsilon);
		//eyeZ
		Assert.assertTrue(Math.abs((lookVals[2]/12.129935) - 1) < epsilon);
		
		
		
		
		
	}
}
