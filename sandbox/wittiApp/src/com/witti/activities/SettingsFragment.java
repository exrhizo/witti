//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Author: Brianna Heersink

package com.witti.activities;

import com.witti.wittiapp.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
<<<<<<< .mine
	public static final String KEY_REFRESH_RATE = "refreshSetting";
	public static final String KEY_GROUND_SETTING = "groundSetting";
=======
	private static final String CAT_TAG = "WITTI_SettingsFragment";
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_LOCATION = "serverLocationSetting";
	public static final String KEY_SERVER_FILE = "serverFileSetting";
>>>>>>> .r562
	
	private String mRefreshValue;
	private boolean mGroundDisplayed;
	
	
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
<<<<<<< .mine
=======
        
        // Sets default summary for list preference (demo file)
        Preference mDemoFile = findPreference(KEY_DEMO_FILE);
        mDemoFile.setSummary(mDemoFile.getSharedPreferences().getString(KEY_DEMO_FILE, ""));
        
        // Sets default summary for edit text preference (server location)
        EditTextPreference mServerLocation = (EditTextPreference) findPreference(KEY_SERVER_LOCATION);
        mServerLocation.setText(defaultServerLocation);
        mServerLocation.setSummary(defaultServerLocation);
        
        // Sets default summary for edit text preference (server file)
        EditTextPreference mServerFile = (EditTextPreference) findPreference(KEY_SERVER_FILE);
        mServerFile.setSummary(mServerFile.getText());
>>>>>>> .r562
    }
    
	/**
     * Saves any settings that are changed by the user
     */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if(key.equals(KEY_REFRESH_RATE)){
			Preference changedPreference = findPreference(key);
	        // Set summary to be the user-description for the selected value
<<<<<<< .mine
			changedPreference.setSummary(sharedPreferences.getString(key, "")+" Hz");
			// Save preference to local variable
			mRefreshValue = sharedPreferences.getString(key,"");
		    // Debug values
            Log.d("setting refresh", mRefreshValue);
=======
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
		    // Debugging
            Log.v(CAT_TAG, "setting refresh: "+sharedPreferences.getString(key,""));
>>>>>>> .r562
		}
<<<<<<< .mine
		else if(key.equals(KEY_GROUND_SETTING)){
			mGroundDisplayed = sharedPreferences.getBoolean(key, false);
			// Debug values
			if (mGroundDisplayed == false){
				Log.d("setting ground", "unchecked");
=======
		else if(key.equals(KEY_TAP_SETTING)){
			// Debugging
			if (sharedPreferences.getBoolean(key, false) == false){
				Log.v(CAT_TAG, "setting tap: unchecked");
>>>>>>> .r562
			}
<<<<<<< .mine
			else if (mGroundDisplayed == true){
				Log.d("setting ground", "checked");
=======
			else {
				Log.v(CAT_TAG, "setting tap: checked");
>>>>>>> .r562
			}
		}
<<<<<<< .mine
=======
		else if(key.equals(KEY_SERVER_LOCATION) || key.equals(KEY_SERVER_FILE)){
	        // Set summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
		    // Debugging
            Log.v(CAT_TAG, "server: "+sharedPreferences.getString(key,""));			
		}
>>>>>>> .r562
	}
	
	/**
     * Returns data refresh rate from settings
     */	
	public int getRefreshRate(){
		int refreshInt = Integer.valueOf(mRefreshValue);
		return refreshInt;
		// TODO: Verify this method
	}
	
	/**
     * Returns whether the ground plane is selected to be shown
     */	
	public boolean getGroundSetting(){
		return mGroundDisplayed;
		// TODO: Verify this method
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
