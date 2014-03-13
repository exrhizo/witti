package com.witti.activities;

import com.witti.wittiapp.R;

import android.app.Activity;
import android.os.Bundle;

public class DisplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_display);
	}
	
	

}
