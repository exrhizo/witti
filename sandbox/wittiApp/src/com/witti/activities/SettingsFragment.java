//ECE 573 Project
//Team: Witty
//Date: 3/31/14
//Author: Brianna Heersink

package com.witti.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.witti.wittiapp.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	
	private String mDemoFile;
	private boolean mTapToRefresh;
	private String mAppMode;
	
	
	public SettingsFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Possibly customize action bar for settings if after Launch/Demo 
		// to bring to DisplayActivity
        //getActivity().setTheme(R.style.SettingsVisualizationTheme);
        
        // Loads the preferences from preferences.xml
        addPreferencesFromResource(R.layout.preferences);
       
        /*
         * Edit and verify if we want the functionality to load settings based on current mode
        // Edits the default settings based on the selected mode
        WittiSettings settings = new WittiSettings(getActivity().getApplicationContext());
        mAppMode = settings.getMode();
        
        if(mAppMode == "demo"){
        	settings.setTapToRefresh(true);
        	settings.setDemoFile("file1");
        }
        else if(mAppMode == "launch"){
        	settings.setTapToRefresh(false);
        	// TODO: Select NONE or gray out setting
        	settings.setDemoFile("file1");        
        }
        */

    }
    
	/**
     * Saves any settings that are changed by the user within the SettingsFragment
     */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if(key.equals(KEY_DEMO_FILE)){
			Preference changedPreference = findPreference(key);
	        // Set summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
			// Save preference to local variable
			mDemoFile = sharedPreferences.getString(key,"");
		    // Debug values
            Log.d("setting refresh", mDemoFile);
		}
		else if(key.equals(KEY_TAP_SETTING)){
			mTapToRefresh = sharedPreferences.getBoolean(key, false);
			// Debug values
			if (mTapToRefresh == false){
				Log.d("setting tap", "unchecked");
			}
			else if (mTapToRefresh == true){
				Log.d("setting tap", "checked");
			}
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

}
