//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import edu.arizona.ece473573.witti.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	
	private static final String CAT_TAG = "WITTI_SettingsFragment";
	public static final String KEY_DEMO_CATEG = "demoPreferencesKey";
	public static final String KEY_SERVER_CATEG = "launchPreferencesKey";
	public static final String KEY_RESET_SETTINGS = "resetPreferencesSetting";
	public static final String KEY_REFRESH_MODE = "refreshSetting";
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_DEMO_FRAMES = "demoFramesSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_LOCATION = "serverLocationSetting";
	public static final String KEY_SERVER_FILE = "serverFileSetting";
	public static final String KEY_SERVER_FRAMES = "serverFramesSetting";
	public static final String KEY_LIVE_MODE = "liveSetting";
	
    public WittiSettings mSettings;
	public String[] serverFileNames;
	public String[] serverFileFrames;
	public String[] demoFileNames;
	public String[] demoFileFrames;
	
	public SettingsFragment() {
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Loads the preferences from preferences.xml
        addPreferencesFromResource(R.layout.preferences);
        
        // Gets list of server files available and number of frames
        mSettings = new WittiSettings(getActivity());
		CharSequence[] mServerFilesAvailable = mSettings.getServerFilesAvailable();
		serverFileNames = new String[mServerFilesAvailable.length];
		serverFileFrames = new String[mServerFilesAvailable.length];
		
		// Parses file name and frame count data into separate arrays for use in settings
		for (int i = 0; i < mServerFilesAvailable.length; i++) {
			String[] mServerFileAndFrames = ((String) mServerFilesAvailable[i]).split("\\s");
			serverFileNames[i] = mServerFileAndFrames[0];
			serverFileFrames[i] = mServerFileAndFrames[1];
		}
		
        // Sets default summary for list preference (server file)
        ListPreference mServerFileList = (ListPreference) findPreference(KEY_SERVER_FILE);
        mServerFileList.setSummary(mServerFileList.getValue());
        // Adds list of server files available to ListPreference
        mServerFileList.setEntries(serverFileNames);
        mServerFileList.setEntryValues(serverFileNames);
		
        // Gets list of demo files available and number of frames
		CharSequence[] mDemoFilesAvailable = mSettings.getDemoFilesAvailable();
		demoFileNames = new String[mDemoFilesAvailable.length];
		demoFileFrames = new String[mDemoFilesAvailable.length];
		
		// Parses file name and frame count data into separate arrays for use in settings
		for (int i = 0; i < mDemoFilesAvailable.length; i++) {
			String[] mDemoFileAndFrames = ((String) mDemoFilesAvailable[i]).split("\\s");
			demoFileNames[i] = mDemoFileAndFrames[0];
			demoFileFrames[i] = mDemoFileAndFrames[1];
		}
        
        // Sets default summary for list preference (demo file)
        ListPreference mDemoFile = (ListPreference) findPreference(KEY_DEMO_FILE);
        mDemoFile.setSummary(mDemoFile.getValue());
        // Adds list of demo files available to ListPreference
        mDemoFile.setEntries(demoFileNames);
        mDemoFile.setEntryValues(demoFileNames);
        
        // Sets default summary for edit text preference (server location)
        EditTextPreference mServerLocation = (EditTextPreference) findPreference(KEY_SERVER_LOCATION);
        mServerLocation.setSummary(mServerLocation.getText());
        
        // Removes settings for server frame counts from display (not necessary for user to see and change)
        PreferenceCategory mLaunchCategory = (PreferenceCategory) findPreference(KEY_SERVER_CATEG);
        EditTextPreference mServerFrames = (EditTextPreference) findPreference(KEY_SERVER_FRAMES);
        mLaunchCategory.removePreference(mServerFrames);
        
        // Removes settings for demo frame counts from display (not necessary for user to see and change)        
        PreferenceCategory mDemoCategory = (PreferenceCategory) findPreference(KEY_DEMO_CATEG);
        Preference mDemoFrames = (Preference) findPreference(KEY_DEMO_FRAMES);
        mDemoCategory.removePreference(mDemoFrames);
        
        Preference button = (Preference)findPreference("resetPreferencesSetting");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        	@Override
            public boolean onPreferenceClick(Preference arg0) { 
        		mSettings.resetSettings(); 
                return true;
            }
        });
        
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
            Log.v(CAT_TAG, "setting demo file to "+sharedPreferences.getString(key,""));
            
         // Sets frame values for demo file selected
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Boolean mFileFound = false;
            for (int i = 0; i < demoFileNames.length; i++) {
    			if(sharedPreferences.getString(key, "") == demoFileNames[i]){
    				editor.putString(KEY_DEMO_FRAMES, demoFileFrames[i]);
    				Log.v(CAT_TAG, "Setting demo frames to " + demoFileFrames[i]);
    				mFileFound = true;
    			}
    		}
            if(mFileFound == false){
            	// If file is not found in list of server files available, log error.
            	Log.e(CAT_TAG, "File " + sharedPreferences.getString(key, "") + " not found in list of available demo files.");
            }
            editor.apply();
            Log.v(CAT_TAG, "Demo frames set to "+sharedPreferences.getString(KEY_DEMO_FRAMES, ""));
            
            // Update value to ensure old value is not still being shown
            ListPreference mDemoFile = (ListPreference) findPreference(KEY_DEMO_FILE);
            mDemoFile.setValue(sharedPreferences.getString(key, ""));
		}
		else if(key.equals(KEY_SERVER_FILE)){
	        // Sets summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
            Log.v(CAT_TAG, "setting server file to "+sharedPreferences.getString(key,""));
            
            // Sets frame values for server file selected
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Boolean mFileFound = false;
            for (int i = 0; i < serverFileNames.length; i++) {
    			if(sharedPreferences.getString(key, "") == serverFileNames[i]){
    				editor.putString(KEY_SERVER_FRAMES, serverFileFrames[i]);
    				Log.v(CAT_TAG, "Setting server frames to " + serverFileFrames[i]);
    				mFileFound = true;
    			}
    		}
            if(mFileFound == false){
            	// If file is not found in list of server files available, log error.
            	Log.e(CAT_TAG, "File " + sharedPreferences.getString(key, "") + " not found in list of available server files.");
            }
            editor.apply();
            Log.v(CAT_TAG, "Server frames set to "+sharedPreferences.getString(KEY_SERVER_FRAMES, ""));
            
            // Update value to ensure old value is not still being shown
            ListPreference mServerFileList = (ListPreference) findPreference(KEY_SERVER_FILE);
            mServerFileList.setValue(sharedPreferences.getString(key, ""));
		}
		else if(key.equals(KEY_SERVER_LOCATION)){
	        // Sets summary to be the user-description for the selected value
			changedPreference.setSummary(sharedPreferences.getString(key, ""));
            Log.v(CAT_TAG, "setting server URL to "+sharedPreferences.getString(key,""));
            
            // Update text value to ensure old value is not still being shown
            EditTextPreference mServerLocation = (EditTextPreference) findPreference(KEY_SERVER_LOCATION);
            mServerLocation.setText(sharedPreferences.getString(key,""));
		}
		else if(key.equals(KEY_REFRESH_MODE)){
            // Update switch position to ensure old value is not still being shown
            SwitchPreference mRefreshMode = (SwitchPreference) findPreference(KEY_REFRESH_MODE);
            mRefreshMode.setChecked(sharedPreferences.getBoolean(key, false));
		}		
		else if(key.equals(KEY_LIVE_MODE)){
            // Update switch position to ensure old value is not still being shown
            SwitchPreference mRefreshMode = (SwitchPreference) findPreference(KEY_LIVE_MODE);
            mRefreshMode.setChecked(sharedPreferences.getBoolean(key, false));
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
