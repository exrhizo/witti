//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren

package com.rhizomatos.acceletracer;

import android.app.Application;

/**
 * Extend application and add accelerometerData as a global variable so I don't
 * have to serialize it. Would have made more sense to load the data in the
 * display activity but I defined it otherwise - although it does make sense for
 * errors to happen in the launch app, so really is this so bad?
 * 
 * @author Alex Warren
 */
public class AcceleTracerApp extends Application {
    private AccelerometerData accelerometerData;

    public AccelerometerData getAccelerometerData() {
        return accelerometerData;
    }

    public void setAccelerometerData(AccelerometerData data) {
        accelerometerData = data;
    }
}
