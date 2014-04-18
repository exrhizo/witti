//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package edu.arizona.ece473573.witti.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.arizona.ece473573.witti.R;

import edu.arizona.ece473573.witti.cloudview.CloudRenderer;
import edu.arizona.ece473573.witti.cloudview.CloudSurfaceView;
import edu.arizona.ece473573.witti.sequence.CloudSequence;

public class DisplayActivity extends Activity {
    private static final String CAT_TAG = "WITTI_Display";
    private static final String DEBUG = "DEBUG_TAG";
    private static final int PLAY_RATE = 20; //Hz

    public CloudSurfaceView mCloudSurfaceView;
    public CloudRenderer mRenderer;
    public CloudCamera mCamera;
    public CloudSequence mSequence;
    public WittiSettings mSettings;
    
    private Boolean mInDemoMode;


    /*
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
        mRenderer.mTime += .02;
        mRenderer.setCamera((float) (10*Math.cos(mRenderer.mTime)), (float) (10*Math.sin(mRenderer.mTime)), (float) (5+5*Math.sin(.01*mRenderer.mTime)),
                                      0.0f,   0.0f,  0.0f,
                                      0.0f,   0.0f,  1.0f);
            timerHandler.postDelayed(this, 1000 / PLAY_RATE);
        }
    };*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
        

        //timerHandler.postDelayed(timerRunnable, 1000);
	}

	@Override
    protected void onPause() {
        super.onPause();
        mCloudSurfaceView.onPause();
        //timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSequence.loadSettings(mInDemoMode);
        mSequence.loadNext();
        mCloudSurfaceView.onResume();
        //timerHandler.postDelayed(timerRunnable, 1000);
    }
    
    public void launchSettingsFromDisplay(View view){
        Intent intent = new Intent(DisplayActivity.this, SettingsActivity.class);
        startActivity(intent);
    	
    }
    
    public void refreshFrame(View view){
        mSequence.refresh();
    }
}
