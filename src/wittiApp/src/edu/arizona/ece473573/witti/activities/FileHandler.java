//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Author: Alex Warren

package edu.arizona.ece473573.witti.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.witti.wittiapp.R;

import edu.arizona.ece473573.witti.cloudview.CloudSurfaceView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class FileHandler {

    private CloudSurfaceView mCloudSurfaceView;
    
	final static int MAX_PARTICLES = 7364;   //7364;
    final static int PARTICLE_SIZE = 3;
	
	private float[] mVertices;
	
	public FileHandler(CloudSurfaceView view){
		mVertices = new float[MAX_PARTICLES * PARTICLE_SIZE];
		mCloudSurfaceView = view;
	}
	
	public float[] getVertexArray(){
		return mVertices;
	}
	
	public void DownloadFileHTTP(URL url){
		//TODO: check network connectivity; but it needs a context for that
		//could be done in the activity before this method gets called
		new DownloadTask().execute(url);
	}
	
	public void DownloadFileRAW(){

      
		// TO READ FILE FROM RAW USING DEMO SETTINGS:
        Context context = mCloudSurfaceView.getContext().getApplicationContext();
        // Gets requested demo file from settings and reads it in
        WittiSettings settings = new WittiSettings(context);
        String mFile = settings.getDemoFile();
        InputStream is = mCloudSurfaceView.getContext().getApplicationContext()
        		.getResources().openRawResource(context.getResources().getIdentifier(mFile,
        		"raw", context.getPackageName()));
		
        // HARDCODED INPUT FILE:
		//InputStream is = mCloudSurfaceView.getContext().getApplicationContext()
        //                  .getResources().openRawResource(R.raw.yxz_points_less);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        
        binaryParse(in);
        
	}

	private Integer binaryParse(BufferedReader in) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class DownloadTask extends AsyncTask<URL, Void, Integer>{

		@Override
		protected Integer doInBackground(URL... params) {
						
			try {
				return downloadFile(params[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		}
		
		private Integer downloadFile(URL url) throws IOException{
			
			InputStream is = null;
			BufferedReader in = null;
			
			try{
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				
				conn.connect();
				int response = conn.getResponseCode();
				if(response != 200){
					return null;
				}
				
				try {
					is = conn.getInputStream();
			        in = new BufferedReader(new InputStreamReader(is));
					
				} catch (IOException e){
					return null;
				}
				
				return binaryParse(in);
				
			} catch(IOException e) {
				return null;
			} 
		}

		@Override
		protected void onPostExecute(Integer result){
			
		}
	}

}
