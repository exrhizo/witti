//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Author: Brianna Heersink

package com.witti.activities;

import com.witti.wittiapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class HomeActivity extends Activity {

	private static final String CAT_TAG = "WITTI_HomeActivity";
	
	public CharSequence[] serverFilesAvailable;
	
	private Context mContext = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
    }
	 
	 /**
     * Opens DisplayActivity with settings for Launch mode (data from computer). 
     */
    public void openLaunch(View view) {
		WittiSettings settings = new WittiSettings(this);
		serverFilesAvailable = settings.getServerFilesAvailable();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select server data to display");
		builder.setItems(serverFilesAvailable, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int selection) {
		        //Toast.makeText(getApplicationContext(), mServerDataAvailable[item], Toast.LENGTH_SHORT).show();
		    	Log.v(CAT_TAG, "server data selected: "+serverFilesAvailable[selection]);
		    	
		    	// Stores file name and number of frames into settings
		    	WittiSettings settings = new WittiSettings(mContext);
		    	settings.setServerFile(serverFilesAvailable[selection].toString());
		    	
		    	Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
				startActivity(intent);
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
 
     /**
     * Opens DisplayActivity with settings for Demo mode (data from phone). 
     */
    public void openDemo(View view) {
        // TODO: Configure demo settings.
		Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
        startActivity(intent);
	}
     
    public void openSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
	}
    
    public void openPathTracing(View view) {
        Intent intent = new Intent(HomeActivity.this, PathTracingActivity.class);
        startActivity(intent);
	}
    
    

}
