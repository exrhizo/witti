//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Alex Warren, Brianna Heersink, Brian Smith

package edu.arizona.ece473573.witti.sequence;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.util.Log;
import edu.arizona.ece473573.witti.activities.DisplayActivity;
import edu.arizona.ece473573.witti.cloudview.PointCloud;

/**
 * CloudSequence Class for holding a sequence of point clouds and loading them.
 *
 * Provides 'current' PointCloud to PointCloudArtist
 * Provides an interface to load new frames and change settings.
 *
 * Contains the inner classes LoadFileTask and DownloadDataTask
 * used for loading frames from res/raw or http address.
 * 
 * @author Alex Warren
 * @author Brian Smith
 * @author Brianna Heersink
 */
public class CloudSequence {
    //Error codes returned by ASYNC tasts
    //These are in the process of being implemented
    public static final int SUCCESS = 0;
    public static final int ERROR_NO_CONNECTION = 1;
    public static final int ERROR_IO = 2;
    public static final int ERROR_CANCEL = 3;

    private static final String CAT_TAG = "WITTI_CloudSequence";
    
    //Used for signaling to test, that async task is finished
    public CountDownLatch signal = new CountDownLatch(1);

    private ArrayList<PointCloud> mSequence; //list of point clouds
    private Map<Integer, ParseTask> mTasks;  //list of currently running async task
    private int mCurrentFrame;               //index of current frame
    private DisplayActivity mDisplay;        //reference to activity

    //Boolean values to control behavior
    private Boolean mInDemoMode;
    private Boolean mIsLive;              //currently unimplemented A requirement
    private Boolean mIncrementOnLoad;     //default to true, included to allow prefetching
    //private Boolean mHasSettings;

    private String mUrlBase;              //web address to download from
    private String mSequenceTitle;        //title of web sequence to load

    private String mResourceTitle;        //title of raw/res to load

    private int mAvailableFrameCount;     //number of frames available to load

    //private Lock mSettingsLock;  //potential way to prevent race conditions, instead be careful ;)
    
    /**
     * Constructor for CloudSequence provides initialization
     * 
     * @param display reference to the display activity.
     */
    public CloudSequence(DisplayActivity display){
        mDisplay = display;
        mCurrentFrame = 0; //There are no frames yet
        mSequence = new ArrayList<PointCloud>();
        mTasks = new HashMap<Integer, ParseTask>();
        //This may give a performance warning but
        //we are not concerned with performance for this
        //HashMap has useful methods
    }
    
    /**
     * Makes settings copy from the WittiSettings object in the display.
     *
     * Copies are made so that changes in settings don't cause race conditions
     * with the async tasks or opengl thread.
     * 
     * This should be called while the SurfaceView is paused so that drawing is
     * suspended.
     * 
     * @param inDemoMode true causes loads to be from local files, false for HTTP.
     */
    public void loadSettings(Boolean inDemoMode) {
        //pause draw if changing settings
        cancelTasks();
        mIsLive = false;     //not yet implemented
        mInDemoMode = inDemoMode; //demo or online load
        if(inDemoMode){
        	Log.v(CAT_TAG, "Loading settings for demo.");
        }
        else{
        	Log.v(CAT_TAG, "Loading settings for server.");
        }
        mUrlBase = mDisplay.mSettings.getServerLocation();
        if (mUrlBase.charAt(mUrlBase.length()-1) != '/'){
            mUrlBase = mUrlBase + '/';
        }
        if (mInDemoMode){
            //Load demo settings
        	mSequenceTitle = mDisplay.mSettings.getDemoFile();
        	mResourceTitle = mDisplay.mSettings.getDemoFile();
        	mAvailableFrameCount = mDisplay.mSettings.getDemoFrameCount();
        }else{
            //Load server settings
        	mSequenceTitle = mDisplay.mSettings.getServerFile();
        	mResourceTitle = mDisplay.mSettings.getServerFile();
        	mAvailableFrameCount = mDisplay.mSettings.getServerFrameCount();
        }
        mIncrementOnLoad = true; //no prefetching so autoincrementing is ok

        //initialize the sequence to null
        mSequence = new ArrayList<PointCloud>();
        for (int ii = 0; ii < mAvailableFrameCount; ii++){
            mSequence.add(null);
        }
        mCurrentFrame = 0;
    }

    /**
     * Increment the current frame or load the next frame.
     *
     * Checks if the next frame is already being loaded before starting new load.
     * 
     */
    public void refresh(){
        int next = (mCurrentFrame + 1) % mAvailableFrameCount;
        if (mSequence.get(next) == null){
            if (! mTasks.containsKey(next)){
                loadNext();
            }
        }else{
            mCurrentFrame = next;
        }
    }

    /**
     * Starts async task to load next frame.
     *
     * Checks for the next frame that isn't already loading or loaded.
     */
    public void loadNext(){
        ParseTask pt;
        int next_frame = mCurrentFrame;
        //Find a frame to load
        while (mSequence.get(next_frame) != null && ! mTasks.containsKey(next_frame)){
            next_frame = (next_frame + 1) % mAvailableFrameCount;
            if (next_frame == mCurrentFrame){
                Log.v(CAT_TAG, "Nothing to load.");
                return; //All the frames are loaded
            }
        }

        if (mInDemoMode){
            //Load from file
            pt = new LoadFileTask(next_frame);
        }else{
            //Load from URL
            pt = new DownloadDataTask(next_frame);
        }
        pt.execute();
        mTasks.put(next_frame, pt);
    }

    /**
     * Cancel all tasks that are currently running.
     */
    public void cancelTasks(){
        //This should be called from UI thread
        //So there are not concurrent onLoadCancel calls
        Log.v(CAT_TAG, "Canceling all tasks");
        for (ParseTask task : mTasks.values()) {
            task.cancel(true);
        }
        mTasks.clear(); //clear the list
        //this may be redundant
        //tasks attempt to remove themselves when finished.
    }

    /**
     * Return PointCloud at the current frame index.
     *
     * OpenGL thread may call this concurrently.
     *
     * @return Current PointCloud, null if unloaded.
     */
    public PointCloud getCurrentFrame(){
        PointCloud current = mSequence.get(mCurrentFrame);
        if (current != null){
            return current;
        }else{
            // TODO make this some sort of waiting image
            //current = new PointCloud();
            return null;
        }
    }

    /**
     * Get current frame number.
     * @return current frame number.
     */
    public int getCurrentFrameNum(){
    	return mCurrentFrame;
    }
    
    /**
     * Get frame at index.
     * @param i frame number to return.
     * @return PointCloud at index i.
     */
    public PointCloud getSpecifiedFrame(int i){
    	return mSequence.get(i);
    }
    
    /**
     * Get size of the current sequence.
     * This also includes frames that are unloaded.
     * @return size of sequence.
     */
    public int getSequenceSize(){
    	return mSequence.size();
    }

    /**
     * Runs at completion of a load.
     *
     * Increments the frame if mIncrementOnLoad.
     * Will handle error conditions.
     * 
     * Called by LoadFileTask and DownloadDataTask onPostExecute and
     * is run on the UIThread.
     *
     * @param result error code specified by constants.
     * @param position index of frame that load attempt was made for.
     * @param error_string text describing error
     */
    private void onLoadComplete(int result, int position, String error_string){
        //Runs on UI thread, called by onPostExecute
        Log.v(CAT_TAG, "Load Complete.     Code: " + Integer.toString(result));
        Log.v(CAT_TAG, "Load Complete. Position: " + Integer.toString(position));
        if (result == SUCCESS){
            if (mIncrementOnLoad && position == mCurrentFrame + 1){
                mCurrentFrame = (mCurrentFrame + 1) % mAvailableFrameCount;
            }

        }else{
            mDisplay.displayError(error_string);
        }
        mTasks.remove(position);
    }

    /**
     * Runs when load is canceled.
     *
     * Removes the task from mTasks.
     * 
     * Called by LoadFileTask and DownloadDataTask onCancel and
     * is run on the UIThread.
     *
     * @param result error code specified by constants.
     * @param position index of frame that load attempt was made for.
     * @param error_string text describing error
     */
    private void onLoadCancel(int result, int position, String error_string){
        //Runs on UI thread, called by onCancelled
        mTasks.remove(position);
    }

    /**
     * Returns url string formated for frame specified by position.
     * 
     * Used by DownloadDataTask.
     * 
     * @param position index of frame that load attempt was made for.
     * @return url string.
     */
    protected String getUrlString(int position){
        //Will be called concurrently
        String result;
        if (mIsLive){
            //TODO A requirement
            result = "";
        }else{
            result = mUrlBase + mSequenceTitle + String.format("_%04d.bin", position);
        }
        Log.v(CAT_TAG, "Using Url: " + result);
        return result;
    }

    /**
     * Returns resource id for the frame specified by position.
     *
     * Used by LoadFileTask.
     * 
     * @param position index of frame that load attempt was made for.
     * @return url string.
     */
    protected int getResourceId(int position){
        String name = mResourceTitle + String.format("_%04d", position);
        return mDisplay.getResources().getIdentifier(name, "raw", mDisplay.getPackageName());
    }

    /**
     * Async task to download data from a file via HTTP.
     * 
     * Inherits from ParseTask
     */
    private class DownloadDataTask extends ParseTask {
        private static final String CAT_TAG = "Witti_DownloadDataTask";
        protected int mPosition;
        /**
         * Constructor for DownloadDataTask.
         * 
         * @param position index of frame that will be loaded.
         */
        public DownloadDataTask(int position) {
            super();
            mPosition = position;
        }

        /**
         * Open URL connection and parse incoming data.
         * 
         * @param arg0 for extending abstract class
         */
        @Override
        protected Integer doInBackground(Void... arg0) {
            int error_code = 0;
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                // mPosition is defined in ParseTask = position
                URL url = new URL(CloudSequence.this.getUrlString(mPosition));
                conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                
                conn.connect();
                int response = conn.getResponseCode();
                if(response != 200){
                    //TODO error
                }

                int length = conn.getContentLength();
                if (length < 1){
                    //TODO error
                }

                is = conn.getInputStream();

                //parseBinary will return NULL if canceled
                CloudSequence.this.mSequence.set(mPosition, parseBinary(is, length));
            } catch (IOException e) {
                Log.e(CAT_TAG, "IO Errorr", e);
                e.printStackTrace();
                mErrorString += "IO Error";
                error_code = CloudSequence.ERROR_IO;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            if (isCancelled()){
                Log.d(CAT_TAG, "Task cancelled");
                mErrorString = "Download Canceled.\n";
                error_code = CloudSequence.ERROR_CANCEL;
            }
            return error_code;
        }

        /**
         * Runs on UIThread, calls CloudSequence.onLoadComplete to finish load.
         * 
         * @param result error code specified by constants.
         */
        @Override
        protected void onPostExecute(Integer result) {
            CloudSequence.this.onLoadComplete(result, mPosition, mErrorString);
        }

        /**
         * Runs on UIThread, calls CloudSequence.onLoadCancel to manage cancelation.
         * 
         * @param result error code specified by constants.
         */
        @Override
        protected void onCancelled (Integer result){
            //Runs on UI thread
            CloudSequence.this.onLoadCancel(result, mPosition, mErrorString);
        }

    }

    /**
     * Async task to download data res/raw.
     * 
     * Inherits from ParseTask
     */
    private class LoadFileTask extends ParseTask {
        private static final String CAT_TAG = "Witti_LoadFileTask";
        protected int mPosition;

        /**
         * Constructor for LoadFileTask.
         * 
         * @param position index of frame that will be loaded.
         */
        public LoadFileTask(int position) {
            super();
            mPosition = position;
        }

         /**
         * Open res/raw file and parse incoming data.
         * 
         * @param arg0 for extending abstract class
         */
        @Override
        protected Integer doInBackground(Void... arg0) {
            int error_code = 0;
            InputStream is = null;
            try {
                // mPosition is defined in ParseTask = position
               is = CloudSequence.this.mDisplay.getApplicationContext()
                          .getResources().openRawResource(CloudSequence.this.getResourceId(mPosition));

                //parseBinary will return NULL if canceled
                CloudSequence.this.mSequence.set(mPosition, parseBinary(is, is.available()));
            } catch (IOException e) {
                Log.e(CAT_TAG, "IO Error", e);
                mErrorString += "IO Error";
                error_code = CloudSequence.ERROR_IO;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
            if (isCancelled()){
                Log.d(CAT_TAG, "Task cancelled");
                mErrorString = "File Load Canceled.\n";
                error_code = CloudSequence.ERROR_CANCEL;
            }
            return error_code;
        }

        /**
         * Runs on UIThread, calls CloudSequence.onLoadComplete to finish load.
         * 
         * @param result error code specified by constants.
         */
        @Override
        protected void onPostExecute(Integer result) {
            //Runs on UI thread
            CloudSequence.this.onLoadComplete(result, mPosition, mErrorString);
        }

        /**
         * Runs on UIThread, calls CloudSequence.onLoadCancel to manage cancelation.
         * 
         * @param result error code specified by constants.
         */
        @Override
        protected void onCancelled (Integer result){
            //Runs on UI thread
            CloudSequence.this.onLoadCancel(result, mPosition, mErrorString);
        }
    }

}
