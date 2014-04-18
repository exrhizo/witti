//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Brianna Heersink
package edu.arizona.ece473573.witti.activities;

import edu.arizona.ece473573.witti.R;

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
		
		// Sets the settings fragment to be displayed on the screen
    	getFragmentManager().beginTransaction().replace(android.R.id.content, 
                new SettingsFragment()).commit();
    	PreferenceManager.setDefaultValues(this, R.layout.preferences, false);
	}

}
