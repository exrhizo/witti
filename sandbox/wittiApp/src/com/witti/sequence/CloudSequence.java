package com.witti.sequence;

import java.util.ArrayList;

import com.witti.activities.WittiSettings;
import com.witti.cloudview.PointCloud;

public class CloudSequence {
    public static final int SUCCESS = 0;
    public static final int ERROR_NO_CONNECTION = 1;
    public static final int ERROR_IO = 2;
    public static final int ERROR_CANCEL = 3;

    private static final String CAT_TAG = "WITTI_CloudSequence";

    private ArrayList<PointCloud> mSequence;
    private Map<Integer, ParseTask> mTasks;
    private int mCurrentFrame;
    private Activity mDisplay;

    private Boolean mInDemoMode;
    private Boolean mIsLive;
    private Boolean mIncrementOnLoad;

    private String mUrlBase;
    private String mSequenceTitle;

    private String mResourceTitle;

    private int mAvailableFrameCount;
    
    public CloudSequence(Activity display){
        mDisplay = display;
        mCurrent = -1; //There are no frames yet
        mSequence = new ArrayList<PointCloud>();
        mTasks = new HashMap<Integer, ParseTask>();
    }
    
    public void loadSettings() {
        mUrlBase = display.mSettings.getServerLocation();
        if (mUrlBase.charAt(mUrlBase.length()-1) != '/'){
            mUrlBase = mUrlBase + '/';
        }
        mSequenceTitle = "sweep";
        mResourceTitle = "sweep";
        mAvailableFrameCount = 5;
        mIsLive = False;
        mInDemoMode = True;
        mIncrementOnLoad = True;
        mSequence = new ArrayList<PointCloud>();
        for (int ii = 0; ii < mAvailableFrameCount; ii++){
            mSequence.add(NULL);
        }
    }

    public void refresh(){
        int next = (mCurrentFrame + 1) % mAvailableFrameCount;
        if (mSequence[next] == NULL){
            if (! mTasks.containsKey(next)){
                loadNext();
            }
        }else{
            mCurrentFrame++;
        }
    }

    public void loadNext(){
        ParseTask pt;
        int next_frame = mCurrentFrame;
        while (mSequence[next_frame] != NULL && ! mTasks.containsKey(next_frame)){
            next_frame = (next_frame + 1) % mAvailableFrameCount;
            if (next_frame == mCurrentFrame){
                Log.v(CAT_TAG, "Nothing to load.");
                return;
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

    public void cancelTasks(){
        //This should be called from UI thread
        //So there are not concurrent onLoadCancel calls
        Log.v(CAT_TAG, "Canceling all tasks");
        for (ParseTask task : mTasks.values()) {
            task.cancel(True);
        }
        mTasks.clear();
    }


    public PointCloud getCurrentFrame(){
        //Can be called concurrently
        //make thread safe

        PointCloud current = mSequence[mCurrentFrame];
        if (current != NULL){
            return current;
        }else{
            // TODO make this some sort of waiting image
            current = new PointCloud();
            return current;
        }
    }

    private void onLoadComplete(int result, int position, String error_string){
        //Runs on UI thread, called by onPostExecute
        Log.v(TAG, "Load Complete.     Code: " + Integer.toString(result));
        Log.v(TAG, "Load Complete. Position: " + Integer.toString(position));
        if (result == SUCCESS){
            if (mIncrementOnLoad && position == mCurrentFrame + 1){
                mCurrentFrame ++;
            }

        }else{
            //TODO error stuff, tell the main activity
        }
        mTasks.remove(position);
    }

    private void onLoadCancel(int result, int position, String error_string){
        //Runs on UI thread, called by onCancelled
        mTasks.remove(position);
    }

    protected String getUrlString(int position){
        //Will be called concurrently
        String result;
        if (mIsLive){
            //TODO
            result = "";
        }else{
            result = mUrlBase + mSequenceTitle + String.format("_%04d.bin", position);
        }
        Log.v(CAT_TAG, "Using Url: " + result);
    }

    protected int getResourceId(int position){
        String name = mResourceTitle + String.format("_%04d", position);
        return mDisplay.getResources().getIdentifier(name, "raw", mDisplay.getPackageName())
    }


    private class DownloadDataTask extends ParseTask {
        private static final String CAT_TAG = "Witti_DownloadDataTask";

        public DownloadDataTask(int position) {
            super(position);
        }

        // Try to load the URL as a separate thread
        protected Integer doInBackground() {
            int error_code = 0;
            try {
                // mPosition is defined in ParseTask = position
                URL url = new URL(CloudSequence.this.getUrlString(mPosition));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //TODO Brian what does this stuff do?
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

                InputStream is = conn.getInputStream();

                //This will return NULL if canceled
                CloudSequence.this.mSequence[mPosition] = parseBinary(is, length);
            } catch (IOException e) {
                Log.e(TAG, "IO Error");
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
                Log.d(TAG, "Task cancelled");
                mErrorString = "Download Canceled.\n";
                error_code = CloudSequence.ERROR_CANCEL;
            }
            return error_code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //Runs on UI thread
            CloudSequence.this.onLoadComplete(result, mPosition, mErrorString);
        }

        @Override
        protected void onCancelled (Integer result){
            //Runs on UI thread
            CloudSequence.this.onLoadCancel(result, mPosition, mErrorString);
        }
    }

    /**
     * Task for loading a file and loading the point cloud result into
     * the sequence.
     */
    private class LoadFileTask extends ParseTask {
        private static final String CAT_TAG = "Witti_LoadFileTask";
        public LoadFileTask(int position) {
            super(position);
        }

                // Try to load the URL as a separate thread
        protected Integer doInBackground() {
            int error_code = 0;
            try {
                // mPosition is defined in ParseTask = position
                InputStream is = CloudSequence.this.mDisplay.getApplicationContext()
                          .getResources().openRawResource(CloudSequence.this.getResourceId(mPosition));

                //This will return NULL if canceled
                CloudSequence.this.mSequence[mPosition] = parseBinary(is, is.available());
            } catch (IOException e) {
                Log.e(TAG, "IO Error");
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
                Log.d(TAG, "Task cancelled");
                mErrorString = "File Load Canceled.\n";
                error_code = CloudSequence.ERROR_CANCEL;
            }
            return error_code;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //Runs on UI thread
            CloudSequence.this.onLoadComplete(result, mPosition, mErrorString);
        }

        @Override
        protected void onCancelled (Integer result){
            //Runs on UI thread
            CloudSequence.this.onLoadCancel(result, mPosition, mErrorString);
        }
    }

}
