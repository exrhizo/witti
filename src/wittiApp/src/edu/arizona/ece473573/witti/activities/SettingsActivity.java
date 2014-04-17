//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Author: Brianna Heersink
package edu.arizona.ece473573.witti.activities;

import com.witti.wittiapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity {

    /**
     * Opens SettingsFragment with default preferences. 
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		// Sets the settings fragment to be displayed on the screen
    	getFragmentManager().beginTransaction().replace(android.R.id.content, 
                new SettingsFragment()).commit();
    	PreferenceManager.setDefaultValues(this, R.layout.preferences, false);

		//getWindow.setFeatureInt(Window.FEATURE_CUSTOMER_TITLE, R.layout.settings_title);
	}

}
