//ECE 573 Project
//Team: Witty
//Date: 3/31/14
//Author: Brianna Heersink

package edu.arizona.ece473573.witti.activities;

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
	
	/*
	 * Gets the server files available on the server.
	 * Returns an array in the format "fileName (frameCount frames)"
	 */	
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
	
	/*
	 * Sets the server file name and frame count in the format "fileName (frameCount frames)".
	 */
	
	public void setServerFile(String fileNameAndFrames){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_FILE, fileNameAndFrames);
    	editor.apply();
    	Log.v(CAT_TAG, "server file set to " + fileNameAndFrames);
	}
	
	public String getServerFile(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String mFileSetting = sharedPreferences.getString(KEY_SERVER_FILE, "");
		String mSplitLine[] = mFileSetting.split(" ");
		String mFile = mSplitLine[0];
		// Debugging
		Log.d(CAT_TAG, "server file name: "+mFile);
		return mFile;
	}
	
	public Integer getServerFrameCount(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String mFileSetting = sharedPreferences.getString(KEY_SERVER_FILE, "");
		String mSplitLine[] = mFileSetting.split("\\(");
		String mSecondSplitLine[] = mSplitLine[1].split("\\s");
		Integer mFrames = Integer.valueOf(mSecondSplitLine[0]);
		// Debugging
		Log.d(CAT_TAG, "server frames count: "+mFrames);
		return mFrames;
	}
	
	/*
	 * Sets the demo file to be displayed.
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
