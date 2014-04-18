//ECE 573 Project
//Team: Witty
//Date: 4/17/14
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
	public static final String KEY_DEMO_FILE = "demoFileSetting";
	public static final String KEY_DEMO_FRAMES = "demoFramesSetting";
	public static final String KEY_TAP_SETTING = "tapToRefreshSetting";
	public static final String KEY_SERVER_LOCATION = "serverLocationSetting";
	public static final String KEY_SERVER_FILE = "serverFileSetting";
	public static final String KEY_SERVER_FRAMES = "serverFramesSetting";
	
	private Context mSettingsContext;
	
	public WittiSettings(Context context) {
		this.mSettingsContext = context;
	}
	
    /**
     * Sets the server web address to be used in Launch mode.
     * 
     * @param 	webAddress	is a web address at which the server data can be accessed
     */
	public void setServerLocation(String webAddress){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_LOCATION, webAddress);
    	editor.apply();
	}
	
    /**
     * Gets the server web address to be used in Launch mode.
     * 
     * @return 		the server web address
     */
	public String getServerLocation(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String location = sharedPreferences.getString(KEY_SERVER_LOCATION, "");
		// Debugging
		Log.d(CAT_TAG, "server location: "+location);
		return location;
	}
	
    /**
     * Gets the server files available on the server.
     * 
     * @return 		an array in the format "fileName frameCount".
     * @throws 	IOException
     */
	public CharSequence[] getServerFilesAvailable(){
		//Read from raw file
		InputStream is = mSettingsContext.getResources().openRawResource(R.raw.server_data_available);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        
	    CharSequence mStringLine;
		ArrayList<CharSequence> mArrayListFiles = new ArrayList<CharSequence>();
	    
		//parse into array
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
	
    /**
     * Sets the base server file name to be used in Launch mode.
     * 
     * @param 	fileName	is a base file name containing Lidar data
     */	
	public void setServerFile(String fileName){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_FILE, fileName);
    	editor.apply();
    	Log.v(CAT_TAG, "server file set to " + fileName);
	}
	
    /**
     * Gets the currently selected server file.
     * 
     * @return 		the selected server file name
     */
	public String getServerFile(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String mFile = sharedPreferences.getString(KEY_SERVER_FILE, "");
		Log.d(CAT_TAG, "server file name: "+mFile);
		return mFile;
	}
	
    /**
     * Sets the server file frame count to be used in Launch mode.
     * 
     * @param 	frameCount	is the number of frames available for a file on the server
     */	
	public void setServerFrameCount(Integer frameCount){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_SERVER_FRAMES, frameCount.toString());
    	editor.apply();
    	Log.v(CAT_TAG, "server frames set to " + frameCount);
	}
	
    /**
     * Gets the frame count for the currently selected server file.
     * 
     * @return 		the frame count for the selected server file name
     */
	public Integer getServerFrameCount(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String mFramesString = sharedPreferences.getString(KEY_SERVER_FRAMES, "");
		Integer mFramesInt = Integer.valueOf(mFramesString);
		Log.d(CAT_TAG, "server frames count: "+mFramesInt);
		return mFramesInt;
	}
	
    /**
     * Sets the demo file to be displayed as well as associated frame count.
     * 
     * @param 	fileName	is a base file name containing Lidar data
     * @param 	frameCount	is the number of frames available for a file on the server
     */	
	public void setDemoFile(String fileName, Integer frameCount){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		SharedPreferences.Editor editor = sharedPrefs.edit();
    	editor.putString(KEY_DEMO_FILE, fileName);
    	editor.putString(KEY_DEMO_FRAMES, frameCount.toString());
    	editor.apply();
	}
	
    /**
     * Gets the currently selected demo file.
     * 
     * @return 		the selected demo file name
     */
	public String getDemoFile(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String mFile = sharedPreferences.getString(KEY_DEMO_FILE, "");
		return mFile;
	}
	
    /**
     * Gets the frame count for the currently selected demo file.
     * 
     * @return 		the frame count for the selected demo file name
     */
	public Integer getDemoFrameCount(){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mSettingsContext);
		String mFrameCount = sharedPreferences.getString(KEY_DEMO_FRAMES, "0");
		return Integer.valueOf(mFrameCount);
	}

}