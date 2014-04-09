//ECE 573 Project
//Team: Witty
//Date: 3/31/14
//Author: Brianna Heersink

package com.witti.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class WittiSettings {
	
	private Context mSettingsContext;
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_SETTING = "serverLocationSetting";
	
	
	public WittiSettings(Context context) {
		this.mSettingsContext = context;
	}
	
	/*
	 * Set whether the app is in Tap to Refresh mode.
	 */
	public void setTapToRefresh(boolean bool){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putBoolean(KEY_TAP_SETTING, bool);
    	editor.apply();
	}
	
	public Boolean getTapToRefresh(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		boolean tap = sharedPreferences.getBoolean(KEY_TAP_SETTING, false);
		// Debugging
		if (tap == false){
			Log.d("setting tap", "unchecked");
		}
		else if (tap == true){
			Log.d("setting tap", "checked");
		}
		return tap;
	}
	
	/*
	 * Sets the server web address to be used in Launch mode.
	 */
	public void setServerLocation(String webAddress){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_SETTING, webAddress);
    	editor.apply();
	}
	
	public String getServerLocation(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String location = sharedPreferences.getString(KEY_SERVER_SETTING, "");
		// Debugging
		Log.d("get location", location);
		return location;
	}
	
	/*
	 * Sets the demo file to file1, file2, or file3.
	 */
	public void setDemoFile(String fileName){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_DEMO_FILE, fileName);
    	editor.apply();
	}
	
	public String getDemoFile(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String file = sharedPreferences.getString(KEY_DEMO_FILE, "");
		// Debugging
		Log.d("get file", file);
		return file;
	}

}
