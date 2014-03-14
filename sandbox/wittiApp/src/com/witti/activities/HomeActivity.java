//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Author: Brianna Heersink

package com.witti.activities;

import com.witti.wittiapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends Activity {

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
        // TODO: Configure launch settings.
		Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
        startActivity(intent);
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
    
    

}
