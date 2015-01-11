//ECE 573 Project
//Team: Witty
//Date: 5/4/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.test;

import junit.framework.Assert;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
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
		
		
	}
	
	public void testAutoRefresh() throws Throwable{
		
		// App opens in manual refresh mode; begin auto-refresh mode by changing preference
		mActivity.mSettings.setAutoRefresh(true);
		mActivity.mSequence.loadSettings(true);
		
		// Wait 0.1 seconds for first frame to be loaded
		try{
			Thread.sleep(100);
		}catch(InterruptedException e){
			fail("Sleep interrupted");
		}
		
		// Iterate to test five refresh sequences at 2Hz
		for(int i = 0; i < 5; i++){
			// Verify frame is loaded
			Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == i);
			
			// Wait 0.5 seconds for next frame to be loaded
			try{
				Thread.sleep(500);
			}catch(InterruptedException e){
				fail("Sleep interrupted");
			}
		}
	}

}
