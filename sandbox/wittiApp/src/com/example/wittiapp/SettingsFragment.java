package com.example.wittiapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_REFRESH_RATE = "refreshSetting";
	public static final String KEY_GROUND_SETTING = "groundSetting";
	
	private String mRefreshValue;
	private boolean mGroundDisplayed;
	
	
	public SettingsFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Customize action bar for settings if after Launch/Demo to bring to DisplayActivity
        //getActivity().setTheme(R.style.SettingsVisualizationTheme);
        
        // Loads the preferences from preferences.xml
        addPreferencesFromResource(R.layout.preferences);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if(key.equals(KEY_REFRESH_RATE)){
			Preference changedPreference = findPreference(key);
	        // Set summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, "")+" Hz");
			// Save preference to local variable
			mRefreshValue = sharedPreferences.getString(key,"");
		    // Debug values
            Log.d("setting refresh", mRefreshValue);
		}
		else if(key.equals(KEY_GROUND_SETTING)){
			mGroundDisplayed = sharedPreferences.getBoolean(key, false);
			// Debug values
			if (mGroundDisplayed == false){
				Log.d("setting ground", "unchecked");
			}
			else if (mGroundDisplayed == true){
				Log.d("setting ground", "checked");
			}
		}
	}
	
	// TODO: Check if this get methods works
	public int getRefreshRate(){
		int refreshInt = Integer.valueOf(mRefreshValue);
		return refreshInt;
	}
	
	// TODO: Check if this get methods works
	public boolean getGroundSetting(){
		return mGroundDisplayed;
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
