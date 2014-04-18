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
import android.widget.Button;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.cloudview.PointCloud;
import edu.arizona.ece473573.witti.sequence.CloudSequence;


/**
 * 
 * 	B.5 Demo Playback Requirement: “The phone application shall be able to use XYZpoint
 *  text files stored locally for Manual Data Refresh mode.”
 */
public class DemoPlaybackTest extends ActivityInstrumentationTestCase2<DisplayActivity>{
	
	private CloudSequence mCloudSequence;
	private DisplayActivity mActivity;
	private Button mRefreshButton;
	private FloatBuffer AUT;
	private PointCloud mPointCloud;
	
	private float[] testArray_1, testArray_2;
	
	public DemoPlaybackTest(){
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
		
		setActivityIntent(intent);
		mActivity = getActivity();
		mCloudSequence = mActivity.mSequence;
		mRefreshButton = (Button)mActivity.findViewById(edu.arizona.ece473573.witti.R.id.displayRefreshButton);
		
		testArray_1 = new float[30];
		float testVal = 0.0f;
		for(int i = 0; i < 30; i ++){
			
			if(i != 0 && i % 3 == 0)
				testVal = testVal - 2.0f;
			
			testArray_1[i] = testVal;
			testVal = testVal + 1.0f;
			
		}
		
		testArray_2 = new float[30];
		testVal = 1000.0f;
		for(int i = 0; i < 30; i ++){
			
			if(i != 0 && i % 3 == 0)
				testVal = testVal - 2.0f;
			
			testArray_2[i] = testVal;
			testVal = testVal + 1.0f;
			
		}
		
	}

	public void testDemoPlayback() throws Throwable{
		
		try{
			mCloudSequence.signal.await(4, TimeUnit.SECONDS);
		}catch(InterruptedException e){
			fail("ASyncTask failed");
		}
		
		mPointCloud = mCloudSequence.getCurrentFrame();
		AUT = mPointCloud.mVertexBuffer;
		
		//Check that teh buffer contains expected values
		for(int i = 0; i < AUT.capacity(); i++){
			Assert.assertTrue(AUT.get(i) == testArray_1[i]);
		}
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run(){
				mRefreshButton.performClick();
			}
		});
		
		try{
			mCloudSequence.signal.await(4, TimeUnit.SECONDS);
		}catch(InterruptedException e){
			fail("ASyncTask failed");
		}
		
		mPointCloud = mCloudSequence.getCurrentFrame();
		AUT = mPointCloud.mVertexBuffer;
		
		//Check that teh buffer contains expected values
		for(int i = 0; i < AUT.capacity(); i++){
			Assert.assertTrue(AUT.get(i) == testArray_2[i]);
		}
		
		Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == 1);
	}

}

