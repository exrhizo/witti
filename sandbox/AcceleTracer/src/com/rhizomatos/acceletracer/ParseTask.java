//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren

package com.rhizomatos.acceletracer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Abstract AsyncTask for loading and parsing AccelerometerData. If the parse is
 * successful, the task starts the Display action. If not it displays errors as
 * a toast.
 * 
 * @author Alex Warren
 */
public abstract class ParseTask<X> extends AsyncTask<X, Void, AccelerometerData> {
    public static final int MAX_JUNK_LINES = 50;
    public static final int MAX_LINE_LENGTH = 150;
    private Activity mActivity;
    protected String errorString;
    public final CountDownLatch signal = new CountDownLatch(1); 

    // takes a reference to the activity for starting new activity
    public ParseTask(Activity activity) {
        this.mActivity = activity;
        errorString = "";
    }

    // After processing url, check if result isn't null and start the app or
    // display error
    protected void onPostExecute(AccelerometerData result) {
        if (result == null) {
            Toast.makeText(mActivity, "FAIL\n" + errorString, Toast.LENGTH_SHORT).show();
        } else {

            if (errorString.length()>0) {
                Toast.makeText(mActivity, errorString, Toast.LENGTH_LONG).show();
            }

            // Save the results to a Global variable
            // because serializing it is stupid :P
            AcceleTracerApp app = (AcceleTracerApp) mActivity.getApplicationContext();
            app.setAccelerometerData(result);
            // Show the results
            mActivity.startActivity(new Intent(mActivity, Display.class));
        }
        signal.countDown();
    }

    /**
     * Parses an InputStream for float triplets to make AccelerometerData
     * object. If it detects many un-parsable lines it will return null Likewise
     * if a line is much too long it will return null May also return null if
     * the min or max size of an AccelerometerData object isn't obeyed.
     * 
     * @param is an InputStream to be parsed
     * @param parent AsyncTask, for checking if it has been canceled
     * @return AccelerometerData with a 2d float array parsed from the function
     */
    public AccelerometerData parseInputStream(InputStream is) {
        ArrayList<Float[]> temp_accel = new ArrayList<Float[]>();
        String description = "";
        Boolean inComment = true;
        final String TAG = "ATracer_AsyncParse";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            int count = 0;
            while ((line = in.readLine()) != null && !isCancelled()) {
                // Look for comments
                if (inComment) {
                    if (line.charAt(0) == '%') {
                        description += line.substring(1) + "\n";
                        continue;
                    } else {
                        inComment = false;
                    }
                }
                // Errors before line parse
                if (count - temp_accel.size() > MAX_JUNK_LINES) {
                    Log.e(TAG + "DownloadDataTask", "Too much junk");
                    errorString = "Too much junk, parse stoped.\n";
                    break;
                }
                if (line.length() > MAX_LINE_LENGTH) {
                    Log.e(TAG, "Max line length exceeded");
                    errorString = "Max line length exceeded, parse stoped.\n";
                    break;
                }
                // PARSE LINE
                Float[] triple = parseLine(line);
                if (triple != null)
                    temp_accel.add(triple);
                count++;

                // Errors after parse
                if (temp_accel.size() >= AccelerometerData.MAX_SIZE) {
                    Log.d(TAG, "Stopped data points lines after "
                            + Integer.toString(AccelerometerData.MAX_SIZE));
                    errorString = "Max data points exceeded, parse stoped.\n";
                    break;
                }
            }
            if (isCancelled()) {
                Log.d(TAG, "Task cancelled");
                errorString = "Data parse task cancelled.\n";
            }
            in.close();
        } catch (IOException e) {
            Log.e(TAG, "IO Error");
            errorString += "IO Error";
            return null;
        }
        if (temp_accel.size() < AccelerometerData.MIN_SIZE) {
            Log.d(TAG, "Too few good data points.");
            errorString += "Too few good data points.";
            return null;
        }
        return new AccelerometerData(temp_accel, description);

    }

    /**
     * Parses an line for 3 floats. This method splits the line on whitespace
     * and tries to find 3 floats. It returns the first 3 that it finds. If it
     * fails to find three it returns null;
     * 
     * @param line a String that will be searched for floats
     * @return An array of Floats if three are found, null otherwise
     */
    private Float[] parseLine(String line) {
        Float[] result = {
                0f, 0f, 0f
        };
        String[] tokens = line.split("\\s");
        if (tokens.length < 3)
            return null;
        int count = 0;
        for (int ii = 0; ii < tokens.length && count < 3; ii++) {
            try {
                result[count] = Float.parseFloat(tokens[ii]);
            } catch (NumberFormatException nfe) {
                continue;
            }
            count++;
        }
        if (count < 3)
            return null;
        return result;
    }
}
