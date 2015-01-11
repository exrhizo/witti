//ECE 573 Project
//Team: Witty
//Date: 4/18/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.activities;

import android.app.Activity;
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
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        //Initialize settings to default values
        PreferenceManager.setDefaultValues(this, R.layout.preferences, false);
    }
	 
    public void openLaunch(View view) {
    	// Opens DisplayActivity in launch mode
    	Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
    	intent.putExtra("inDemoMode", false);
		startActivity(intent);
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