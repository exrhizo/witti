//ECE 573 Project
//Team: Witty
//Date: 5/4/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.test;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.sequence.CloudSequence;

/**
 * 
 * 	A.1 Auto Data Refresh Requirement: “The phone application software shall be capable of refreshing the
displayed Velodyne LiDAR data automatically through a set refresh rate.”
 */
public class AutoDataRefreshTest extends ActivityInstrumentationTestCase2<DisplayActivity>{

	private CloudSequence mCloudSequence;
	private DisplayActivity mActivity;
	private Button mAutoRefreshButton;
	
	public AutoDataRefreshTest(){
		super(DisplayActivity.class);
	}
	
    /**
     * Launches the DisplayActivity
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
		mAutoRefreshButton = (Button)mActivity.findViewById(edu.arizona.ece473573.witti.R.id.displayAutoRefreshButton);
		
	}
	
	public void testAutoRefresh() throws Throwable{
		
		// App opens in manual refresh mode; wait for async task for render of first frame
		try{
			mCloudSequence.signal.await(4, TimeUnit.SECONDS);
		}catch(InterruptedException e){
			fail("ASyncTask failed");
		}
		
		// Verify first frame is loaded
		Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == 0);
		
		// Click button to switch to auto refresh mode
		runTestOnUiThread(new Runnable() {
			@Override
			public void run(){
				mAutoRefreshButton.performClick();
			}
		});
		
		// Wait 0.5 seconds for next frame to be loaded
		try{
			Thread.sleep(500);
		}catch(InterruptedException e){
			fail("Sleep interrupted");
		}
		
		// Verify second frame is loaded
		Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == 1);
		
		// Wait 0.5 seconds for next frame to be loaded
		try{
			Thread.sleep(500);
		}catch(InterruptedException e){
			fail("Sleep interrupted");
		}
		
		// Verify third frame is loaded
		Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == 2);
	}

}
