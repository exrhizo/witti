//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package com.witti.activities;

import com.witti.cloudview.CloudRenderer;
import com.witti.cloudview.CloudSurfaceView;
import com.witti.wittiapp.R;

import android.app.Activity;
import android.os.Bundle;

public class DisplayActivity extends Activity {
    private static final String CAT_TAG = "WITTI_Display";
    private CloudSurfaceView mCloudSurfaceView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);

        
        mCloudSurfaceView = (CloudSurfaceView) findViewById(R.id.cloud_surface_view);
        // tried doing this in CloudSurfaceView but caused null pointer
        
        CloudRenderer renderer = new CloudRenderer(mCloudSurfaceView);
        mCloudSurfaceView.setEGLContextClientVersion(2);
        mCloudSurfaceView.setRenderer(renderer);
	}

	@Override
    protected void onPause() {
        super.onPause();
        mCloudSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCloudSurfaceView.onResume();
    }
	

}
