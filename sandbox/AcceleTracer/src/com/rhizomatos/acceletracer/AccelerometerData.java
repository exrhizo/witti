//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren

package com.rhizomatos.acceletracer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.util.Log;

/**
 * Wrapper for the acceleration data, loads everything as a ArrayList and
 * converts it to a plain 2d float array. Stores comments and returns an
 * Iterable.
 * 
 * @author Alex Warren
 */
public class AccelerometerData implements Iterable<float[]> {
    private static final String TAG = "ATracer_AccelerometerData";
    public static final int MAX_SIZE = 2000;
    public static final int MIN_SIZE = 8;
    private float[][] acceleration;
    private int currentSize;
    public String description;

    public AccelerometerData(ArrayList<Float[]> accelerationTemp, String description) {
        this.description = description;
        currentSize = accelerationTemp.size();
        acceleration = new float[currentSize][3];
        int i = 0;
        for (Float[] triple : accelerationTemp) {
            acceleration[i][0] = triple[0];
            acceleration[i][1] = triple[1];
            acceleration[i++][2] = triple[2];
        }
    }

    public int size() {
        return currentSize;
    }

    @Override
    public Iterator<float[]> iterator() {
        Iterator<float[]> it = new Iterator<float[]>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < currentSize && acceleration[currentIndex] != null;
            }

            @Override
            public float[] next() {
                return acceleration[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    //For debugging the data
    public void printToLog() {
        Log.v(TAG, "DUMP AccelerometerData to log");
        Log.v(TAG, description);
        for (float[] triple : acceleration) {
            Log.v(TAG,
                    Float.toString(triple[0]) + " " + Float.toString(triple[1]) + " "
                            + Float.toString(triple[2]));
        }
    }
}
