//ECE 573 Project
//Team: Witty
//Date: 4/18/14
//Author: Brian Smith

package edu.arizona.ece473573.witti.test;

import java.nio.FloatBuffer;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.cloudview.PointCloud;
import edu.arizona.ece473573.witti.sequence.CloudSequence;

/**
 * 
 * 	B.1 Display Data Requirement: “The phone application software shall load and display Velodyne LiDAR
 *	data on the phone.”
 */
public class DisplayDataTest extends ActivityInstrumentationTestCase2<DisplayActivity>{

	private CloudSequence mCloudSequence;
	private PointCloud mPointCloud;
	private DisplayActivity mActivity;
	
	private float[] testArray;
	private FloatBuffer AUT;
	
	public DisplayDataTest() {
		super(DisplayActivity.class);
	}

    /**
     * Launches the DisplayActivity and loads the initial test file into memory
     * 
     */
	@Override
	protected void setUp() throws Exception{
		
		super.setUp();

		Intent intent = new Intent();
		intent.putExtra("inDemoMode", true);
		intent.putExtra("inTestMode", true);
		
		setActivityIntent(intent);
		
		mActivity = getActivity();
		mCloudSequence = mActivity.mSequence;
		
		//Build the test array based
		testArray = new float[30];
		float testVal = 0.0f;
		for(int i = 0; i < 30; i ++){
			
			if(i != 0 && i % 3 == 0)
				testVal = testVal - 2.0f;
			
			testArray[i] = testVal;
			testVal = testVal + 1.0f;
			
		}
		
	}

    /**
     * Waits for the asynctask to finish then checks the float buffer containing
     * the points that will be displayed
     * 
     */
	public void testDisplayData(){

		try{
			mCloudSequence.signal.await(4, TimeUnit.SECONDS);
		}catch(InterruptedException e){
			fail("ASyncTask failed");
		}
		
		//Get the most current frame, which should be zero
		Log.d("DISPLAYDATATEST", "Current frame #: " + mCloudSequence.getCurrentFrameNum());
		mPointCloud = mCloudSequence.getCurrentFrame();
		AUT = mPointCloud.mVertexBuffer;
		
		//Check that the buffer contains correct # of values
		Assert.assertTrue(AUT.capacity() == 30);
		
		//Check that teh buffer contains expected values
		for(int i = 0; i < AUT.capacity(); i++){
			Assert.assertTrue(AUT.get(i) == testArray[i]);
		}
		
		//PointCloud class has correct metadata (??)
		
		return;
	}

}
