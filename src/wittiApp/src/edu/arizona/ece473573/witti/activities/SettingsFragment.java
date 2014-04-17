//ECE 573 Project
//Team: Witty
//Date: 4/16/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.witti.wittiapp.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	private static final String CAT_TAG = "WITTI_SettingsFragment";
	public static final String KEY_DEMO_CATEG = "demoPreferencesKey";
	public static final String KEY_SERVER_CATEG = "launchPreferencesKey";
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_DEMO_FRAMES = "demoFramesSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_LOCATION = "serverLocationSetting";
	public static final String KEY_SERVER_FILE = "serverFileSetting";
	public static final String KEY_SERVER_FRAMES = "serverFramesSetting";

	private String defaultServerLocation = "www.rhizomatos.com/static/lidar/"; //http:// ??
	
	public SettingsFragment() {
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Loads the preferences from preferences.xml
        addPreferencesFromResource(R.layout.preferences);
        
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
        
        // Removes settings for server frame counts from display (not necessary for user to see and change)
        PreferenceCategory mLaunchCategory = (PreferenceCategory) findPreference(KEY_SERVER_CATEG);
        EditTextPreference mServerFrames = (EditTextPreference) findPreference(KEY_SERVER_FRAMES);
        mLaunchCategory.removePreference(mServerFrames);
        
        // Removes settings for demo frame counts from display (not necessary for user to see and change)        
        PreferenceCategory mDemoCategory = (PreferenceCategory) findPreference(KEY_DEMO_CATEG);
        Preference mDemoFrames = (Preference) findPreference(KEY_DEMO_FRAMES);
        mDemoCategory.removePreference(mDemoFrames);
        
    }
    
	/**
     * Saves any settings that are changed by the user within the SettingsFragment
     */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference changedPreference = findPreference(key);
		if(key.equals(KEY_DEMO_FILE)){
	        // Sets summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
		    // Debugging
            Log.v(CAT_TAG, "setting demo file to "+sharedPreferences.getString(key,""));
            
            // Sets frame values for demo file selected
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(sharedPreferences.getString(key, "").equals("sweep")){
            	editor.putString(KEY_DEMO_FRAMES, "5");
            }
            else if (sharedPreferences.getString(key, "").equals("dummy")){
            	editor.putString(KEY_DEMO_FRAMES, "3");
            }
            editor.apply();
            Log.v(CAT_TAG, "setting demo frames to "+sharedPreferences.getString(KEY_DEMO_FRAMES, ""));
		}
		else if(key.equals(KEY_TAP_SETTING)){
			// Debugging
			if (sharedPreferences.getBoolean(key, false) == false){
				Log.v(CAT_TAG, "setting tap: unchecked");
			}
			else {
				Log.v(CAT_TAG, "setting tap: checked");
			}
		}
		else if(key.equals(KEY_SERVER_LOCATION) || key.equals(KEY_SERVER_FILE)){
	        // Sets summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
		    // Debugging
            Log.v(CAT_TAG, "server: "+sharedPreferences.getString(key,""));			
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
