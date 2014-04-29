//ECE 573 Project
//Team: Witty
//Date: 4/18/14
//Author: Brian Smith

package edu.arizona.ece473573.witti.test;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.sequence.CloudSequence;

/**
 * 
 * 	B.2 Manual Refresh Requirement: “The phone application software shall be capable of refreshing the
 * displayed Velodyne LiDAR data manually based on user input.”
 */
public class ManualDataRefreshTest extends ActivityInstrumentationTestCase2<DisplayActivity>{

	private CloudSequence mCloudSequence;
	private DisplayActivity mActivity;
	private Button mRefreshButton;
	private Context mContext;
	
	public ManualDataRefreshTest(){
		super(DisplayActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception{
		
		super.setUp();

		
		Intent intent = new Intent();
		intent.putExtra("inDemoMode", true);
		intent.putExtra("inTestMode", true);
		
		setActivityIntent(intent);
		mActivity = getActivity();
		mCloudSequence = mActivity.mSequence;
		mRefreshButton = (Button)mActivity.findViewById(edu.arizona.ece473573.witti.R.id.displayRefreshButton);
		
		Instrumentation in = getInstrumentation();
		mContext = in.getTargetContext();
		
		PreferenceManager.setDefaultValues(mContext, edu.arizona.ece473573.witti.R.layout.preferences, false);
		
	}
	
	public void testManualRefresh() throws Throwable{
		
		
		try{
			mCloudSequence.signal.await(4, TimeUnit.SECONDS);
		}catch(InterruptedException e){
			fail("ASyncTask failed");
		}
		
		Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == 0);
		
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
		
		Assert.assertTrue(mCloudSequence.getCurrentFrameNum() == 1);
	}

}
