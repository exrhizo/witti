//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren

package com.rhizomatos.acceletracer;

import java.util.Iterator;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Display activity - Displays comments and 3d view of the data. Sets up handler
 * for timed update of the particle system with timely value of acceleration.
 * Loops after complete. Shows comments with that were attached to the data
 * file.
 * 
 * @author Alex Warren
 */
public class Display extends Activity {
    private static final String TAG = "ATracer_Display";

    private static final int MAX_PARTICLES = 300;
    private static final int PLAY_RATE = 20; //Hz
    private TracerGLSurfaceView mGLView;
    private ParticleSystem mParticleSystem;
    private AccelerometerData mAccelerometerData;
    private Iterator<float[]> mAccelerometerIterator;

    // handler and runnable for timed update
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            float[] accel = {
                    0f, 0f, 0f
            };
            // check if we are at the end, if so restart
            if (!mAccelerometerIterator.hasNext()) {
                mAccelerometerIterator = mAccelerometerData.iterator();
            }
            // if we are still at the end don't get an error, just put 0s
            if (mAccelerometerIterator.hasNext()) {
                accel = mAccelerometerIterator.next();
            }
            // set the acceleration of particle system
            mParticleSystem.setAcceleration(accel[0], accel[1], accel[2]);
            timerHandler.postDelayed(this, 1000 / PLAY_RATE);
        }
    };

    // Start up the particle system, GLView and the renderer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mParticleSystem = new ParticleSystem(MAX_PARTICLES);

        ParticleRenderer renderer = new ParticleRenderer(mParticleSystem);

        mGLView = (TracerGLSurfaceView) findViewById(R.id.tracer_gl_view);
        // tried doing this in TracerGLSurfaceView but caused null pointer
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(renderer);

        AcceleTracerApp app = (AcceleTracerApp) getApplicationContext();
        mAccelerometerData = app.getAccelerometerData();
        mAccelerometerIterator = mAccelerometerData.iterator();
        // mAccelerometerData.printToLog();

        // Display comment
        TextView commentText = (TextView) findViewById(R.id.comment_text);
        commentText.setText(mAccelerometerData.description);

        timerHandler.postDelayed(timerRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        timerHandler.postDelayed(timerRunnable, 1000);
    }
}
