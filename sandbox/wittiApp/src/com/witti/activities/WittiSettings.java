//ECE 573 Project
//Team: Witty
//Date: 3/31/14
//Author: Brianna Heersink

package com.witti.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.witti.wittiapp.R;

public class WittiSettings {
    private static final String CAT_TAG = "WITTI_WittiSettings";
	
	private Context mSettingsContext;
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_LOCATION = "serverLocationSetting";
	public static final String KEY_SERVER_FILE = "serverFileSetting";
	
	
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
		return tap;
	}
	
	/*
	 * Sets the server web address to be used in Launch mode.
	 */
	public void setServerLocation(String webAddress){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_LOCATION, webAddress);
    	editor.apply();
	}
	
	public String getServerLocation(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String location = sharedPreferences.getString(KEY_SERVER_LOCATION, "");
		// Debugging
		Log.d(CAT_TAG, "server location: "+location);
		return location;
	}
	
	public CharSequence[] getServerFilesAvailable(){
		InputStream is = mSettingsContext.getResources().openRawResource(R.raw.server_data_available);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        
	    CharSequence mStringLine;
		ArrayList<CharSequence> mArrayListFiles = new ArrayList<CharSequence>();
	    
	    try {
			while ((mStringLine = in.readLine()) != null) {
				if(mStringLine.charAt(0) != '%') {
					mArrayListFiles.add(mStringLine);
				}
			}
		} catch (IOException e) {
			Log.e(CAT_TAG, "Couldn't read from resource file.");
		}
	    
	    // Convert to CharSequence
	    CharSequence[] mCharSequenceFiles = mArrayListFiles.toArray(new CharSequence[mArrayListFiles.size()]);
	    
	    return mCharSequenceFiles;
	    	
	}
	
	public void setServerFile(String fileName){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_FILE, fileName);
    	editor.apply();
    	Log.v(CAT_TAG, "server file set to "+fileName);
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
		return file;
	}

}
