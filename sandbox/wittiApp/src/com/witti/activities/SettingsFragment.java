//ECE 573 Project
//Team: Witty
//Date: 3/31/14
//Author: Brianna Heersink

package com.witti.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.witti.wittiapp.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_SETTING = "serverLocationSetting";
	
	private String defaultServerLocation = "www.default_server_site.com";
	
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
        
        // Sets default summary for list preference (demo file)
        Preference listPreference = findPreference(KEY_DEMO_FILE);
        listPreference.setSummary(listPreference.getSharedPreferences().getString(KEY_DEMO_FILE, ""));
        
        // Sets default summary for edit text preference (server location)
        EditTextPreference editTextPreference = (EditTextPreference) findPreference(KEY_SERVER_SETTING);
        editTextPreference.setText(defaultServerLocation);
        editTextPreference.setSummary(defaultServerLocation);
    }
    
	/**
     * Saves any settings that are changed by the user within the SettingsFragment
     */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference changedPreference = findPreference(key);
		if(key.equals(KEY_DEMO_FILE)){
	        // Set summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
		    // Debugging
            Log.d("setting refresh", sharedPreferences.getString(key,""));
		}
		else if(key.equals(KEY_TAP_SETTING)){
			// Debugging
			if (sharedPreferences.getBoolean(key, false) == false){
				Log.d("setting tap", "unchecked");
			}
			else {
				Log.d("setting tap", "checked");
			}
		}
		else if(key.equals(KEY_SERVER_SETTING)){
	        // Set summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
		    // Debugging
            Log.d("server setting", sharedPreferences.getString(key,""));			
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
