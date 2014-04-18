//ECE 573 Project
//Team: Witty
//Date: 4/18/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import edu.arizona.ece473573.witti.R;

public class HomeActivity extends Activity {
	
	public String[] serverFileNames;
	public String[] serverFileFrames;
	
	private Context mContext = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        //Initialize settings
        PreferenceManager.setDefaultValues(this, R.layout.preferences, false);
    }
	 
	 /**
     * Prompts use for server file selection and opens DisplayActivity 
     */
    public void openLaunch(View view) {
		WittiSettings settings = new WittiSettings(this);
		
		CharSequence[] mServerFilesAvailable = settings.getServerFilesAvailable();
		serverFileNames = new String[mServerFilesAvailable.length];
		serverFileFrames = new String[mServerFilesAvailable.length];
		
		// Reformats file name and frame count data to be displayed in  alert dialog
		String[] mServerFilesDetailed = new String[mServerFilesAvailable.length];
		for (int i = 0; i < mServerFilesAvailable.length; i++) {
			String[] mServerFileAndFrames = ((String) mServerFilesAvailable[i]).split("\\s");
			serverFileNames[i] = mServerFileAndFrames[0];
			serverFileFrames[i] = mServerFileAndFrames[1];
			mServerFilesDetailed[i] = serverFileNames[i] + " (" + serverFileFrames[i] + " frames)";
		}
        // Builds alert dialog to give user option of which file from server to display
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select server data to display");
		builder.setItems(mServerFilesDetailed, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int selection) {		    	
		    	// Stores file name and number of frames into settings
		    	WittiSettings settings = new WittiSettings(mContext);
		    	settings.setServerFile(serverFileNames[selection]);
		    	settings.setServerFrameCount(Integer.valueOf(serverFileFrames[selection]));
		    	
		    	// Opens DisplayActivity in launch mode
		    	Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
		    	intent.putExtra("inDemoMode", false);
				startActivity(intent);
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
 
    public void openDemo(View view) {
    	//Opens DisplayActivity in demo mode
		Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
    	intent.putExtra("inDemoMode", true);
        startActivity(intent);
	}
     
    public void openSettings(View view) {
    	//Opens settings
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
	}
    
    public void openPathTracing(View view) {
    	//Opens PathTracingActivity
    	Intent intent = new Intent(HomeActivity.this, PathTracingActivity.class);
        startActivity(intent);
	}
    
    public void openAbout(View view) {
    	//Sets text color as a visited link color (purple)
    	TextView mAboutText = (TextView) findViewById(R.id.teamNameTextView);
    	mAboutText.setTextColor(Color.parseColor("#800080"));
    	//Opens a toast with team member names
    	String mToastText = getResources().getString(R.string.about);
    	int mDuration = Toast.LENGTH_LONG;
    	Toast toast = Toast.makeText(getApplicationContext(), mToastText, mDuration);
    	toast.setGravity(Gravity.CENTER, 0, 0);
    	toast.show();
	}
    
    

}