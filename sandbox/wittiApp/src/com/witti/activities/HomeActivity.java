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
	
    // TODO: Go through settings first and set default settings for Launch
    public void openLaunch(View view) {
        Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
        startActivity(intent);
	}
 
    // TODO: Go through settings first and set default settings for Demo
    public void openDemo(View view) {
        Intent intent = new Intent(HomeActivity.this, DisplayActivity.class);
        startActivity(intent);
	}
    
    public void openSettings(View view) {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
	}
    
    

}
