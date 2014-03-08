//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren
/**
 * Project acceletracer
 * Implement requirements given in ece473-573-hw02.pdf part 2
 * Allows user to download a text file from internet
 * or open on phone, and it will try to load the file
 * as a series of triplets representing accelerometer data.
 * 
 * Requirements:
 * <ul>
 * <li>Displays name and course info
 * <li>Allows playback of data in real time
 * <li>Load data from run time configurable web address
 * <li>Register app to accept text files
 * </ul>
 * 
 * @author      Alex Warren
 */

package com.rhizomatos.acceletracer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Launch extends Activity {
    Button buttonPlay;
    Button buttonUrl1;
    Button buttonUrl2;
    Button buttonDemo;
    EditText editUrl;
    public ParseTask mParseTask;
    private static final String TAG = "ATracer_Launch";

    /**
     * Initializes buttons and checks for file to load.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.activity_launch);

        // This try checks if the intent includes a file
        try {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                InputStream is = getContentResolver().openInputStream(intent.getData());
                // load the data into an async task, so it doesn't freeze ui
                mParseTask = (ParseTask) new LoadDataTask(this).execute(is);
                Toast.makeText(getApplicationContext(), "Loading from file.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            // Show an error if file cannot be opened
            Toast.makeText(getApplicationContext(), "Failed to open file",
                    Toast.LENGTH_SHORT).show();

        }

        // Get the edit text
        editUrl = (EditText) findViewById(R.id.edit_web_address);
        editUrl.setText(getString(R.string.launch_ulr_1));
        
        addButtonListeners();
        
        
    }

    /**
     * Initializes buttons listeners. Initializes pre-loaded url buttons and the
     * play button
     */
    private void addButtonListeners() {
        buttonPlay = (Button) findViewById(R.id.button_play);
        buttonUrl1 = (Button) findViewById(R.id.button_url_1);
        buttonUrl2 = (Button) findViewById(R.id.button_url_2);
        buttonDemo = (Button) findViewById(R.id.button_demo);

        buttonPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Click on play button");
                loadFromUrl();
            }
        });

        buttonUrl1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                editUrl.setText(getString(R.string.launch_ulr_1));
            }
        });

        buttonUrl2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                editUrl.setText(getString(R.string.launch_ulr_2));
            }
        });
        
        buttonDemo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mParseTask!=null && mParseTask.getStatus()==AsyncTask.Status.RUNNING){
                    Toast.makeText(getApplicationContext(), "Already loading.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                InputStream is = getApplicationContext().getResources().openRawResource(R.raw.tom);
                // load the data into an async task, so it doesn't freeze ui
                mParseTask = (ParseTask) new LoadDataTask(Launch.this).execute(is);
                Toast.makeText(getApplicationContext(), "Loading demo data.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initializes buttons and checks for file to load.
     */
    private void loadFromUrl() {
        Log.v(TAG, "Load from URL");
        if(mParseTask!=null && mParseTask.getStatus()==AsyncTask.Status.RUNNING){
            Toast.makeText(getApplicationContext(), "Already loading.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
            
        // AccelerometerData data = new AccelerometerData();
        // data.loadFromUrlText(editUrl.getText().toString());
        String url_text = editUrl.getText().toString();
        URL url;
        try {

            if (!url_text.startsWith("http://")) {
                url_text = "http://" + url_text;
            }
            url = new URL(url_text);
            mParseTask = (ParseTask) new DownloadDataTask(this).execute(url);
            Toast.makeText(getApplicationContext(), "Loading from "+url_text,
                    Toast.LENGTH_SHORT).show();
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), url_text + " is not a valid URL.",
                    Toast.LENGTH_SHORT).show();
        }

        // data.printToLog();
    }

    /**
     * Task for loading a url and parsing it as AccelerometerData. If the parse
     * is successful, the task starts the display action.
     */
    private class DownloadDataTask extends ParseTask<URL> {

        public DownloadDataTask(Activity activity) {
            super(activity);
        }

        // Try to load the URL as a separate thread
        protected AccelerometerData doInBackground(URL... urls) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) urls[0].openConnection();
                return parseInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                Log.e(TAG, "IO Error");
                errorString += "IO Error";
                return null;
            }
        }
    }

    /**
     * Task for loading a file and parsing it as AccelerometerData. If the parse
     * is successful, the task starts the display action.
     */
    private class LoadDataTask extends ParseTask<InputStream> {

        public LoadDataTask(Activity activity) {
            super(activity);
        }

        // Try to load the file as a separate thread
        protected AccelerometerData doInBackground(InputStream... is) {
            Log.v(TAG, is[0].toString());
            return parseInputStream(is[0]);

        }
    }

}
