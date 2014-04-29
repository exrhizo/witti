//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package edu.arizona.ece473573.witti.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import edu.arizona.ece473573.witti.R;

import edu.arizona.ece473573.witti.cloudview.CloudRenderer;
import edu.arizona.ece473573.witti.cloudview.CloudSurfaceView;
import edu.arizona.ece473573.witti.sequence.CloudSequence;

public class DisplayActivity extends Activity {
    private static final String CAT_TAG = "WITTI_Display";

    private static final int REFRESH_RATE = 2; //Hz
    private static final int CAMERA_RATE = 50; //Hz

    public CloudSurfaceView mCloudSurfaceView;
    public CloudRenderer mRenderer;
    public CloudCamera mCamera;
    public CloudSequence mSequence;
    public WittiSettings mSettings;
    
    private Boolean mInDemoMode;
    private Boolean mAutoRefresh;

    private Long mLastRefreshTime;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            mCamera.update(currentTime);
            if (mAutoRefresh){
                if (currentTime - mLastRefreshTime > 1000/REFRESH_RATE){
                    mLastRefreshTime = currentTime;
                    mSequence.refresh();
                }
            }
            timerHandler.postDelayed(this, 1000/CAMERA_RATE);
        }
    };

     /**
     * Creation of the activity. Initialize everything.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(CAT_TAG, "onCreate");

		setContentView(R.layout.activity_display);
		
		Intent intent = getIntent();
		mInDemoMode = intent.getBooleanExtra("inDemoMode", true);

        mCamera = new CloudCamera();
        mCloudSurfaceView = (CloudSurfaceView) findViewById(R.id.cloud_surface_view);
        mCloudSurfaceView.setCamera(mCamera);

        mSettings = new WittiSettings(this);

        mSequence = new CloudSequence(this);
        mSequence.loadSettings(mInDemoMode);
        mSequence.loadNext();
  
        // tried doing this in CloudSurfaceView but caused null pointer
        mRenderer = new CloudRenderer(this, mCamera);
        mCloudSurfaceView.setEGLContextClientVersion(2);
        mCloudSurfaceView.setRenderer(mRenderer);
        
        mAutoRefresh = false;
        mLastRefreshTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 1000/CAMERA_RATE);
	}

     /**
     * Activity is paused.
     */
	@Override
    protected void onPause() {
        super.onPause();
        mCloudSurfaceView.onPause(); //pause openGl
        timerHandler.removeCallbacks(timerRunnable);
    }

     /**
     * Activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSequence.loadSettings(mInDemoMode);
        mSequence.loadNext();
        mCloudSurfaceView.onResume(); //resume openGl
        timerHandler.postDelayed(timerRunnable, 1000);
    }
    
    /**
     * For button press to open settings.
     */
    public void launchSettingsFromDisplay(View view){
        Intent intent = new Intent(DisplayActivity.this, SettingsActivity.class);
        startActivity(intent);
    	
    }
    
    /**
     * For button press to refresh frame.
     */
    public void refreshFrame(View view){
        mSequence.refresh();
    }

    public void autoRefreshFrame(View view){
        mAutoRefresh = !mAutoRefresh;
    }

    /**
     * Display an error as toast.
     */
    public void displayError(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
