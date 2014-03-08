//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren

package com.rhizomatos.acceletracer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

/**
 * Much of this code is heavily adapted from a demo ffrom baynine stuudios They
 * used opgl es1 so I used the other tutorial to switch to es2. I apapted this
 * from code that showed particles under the influence of graivty with bounce.
 * 
 * @author Alex Warren
 * @author bayninestudios
 *         http://www.bayninestudios.com/2010/04/particle-system-tutorial
 *         -on-android/
 */
public class ParticleSystem implements Iterable<Particle> {
    protected Particle[] mParticles;
    private static final String TAG = "ATracer_ParticleS";
    private int particleCount;
    private int youngestParticle = -1;
    private boolean isFirstGeneration = true;
    private int currentSize;
    protected Particle[] mZParticles;

    private float SPEED_FACTOR = 3.5f;

    // this is used to track the time of the last update so that
    // we can calculate a frame rate to find out how far a particle
    // has moved between updates
    private long lastTime;

    private float[] acceleration;
    private float[] acceleration_norm;
    private float acceleration_magnitude;
    private static final float EMITTER_SIZE = 1f;

    public ParticleSystem(int maxParticles)
    {
        particleCount = maxParticles;
        mParticles = new Particle[particleCount];
        setAcceleration(0.0f, 0.0f, 0.0f);

        // loop through all the particles and create new instances of each one
        for (int i = 0; i < particleCount; i++) {
            mParticles[i] = new Particle();
        }

        mZParticles = new Particle[particleCount];
        System.arraycopy(mParticles, 0, mZParticles, 0, particleCount);

        lastTime = System.currentTimeMillis();
        currentSize = 0;
    }

    /**
     * Sets acceleration variables, that control the velocity of newly
     * initialized particles and thier color.
     * 
     * @param x acceleration in the x direction.
     * @param y acceleration in the x direction.
     * @param z acceleration in the x direction.
     */
    public void setAcceleration(float x, float y, float z) {
        float c;
        acceleration = new float[] {
                x, y, z
        };
        acceleration_magnitude = (float) Math.sqrt((x * x) + (y * y) + (z * z));
        c = (acceleration_magnitude != 0) ? 1f / acceleration_magnitude : 1;
        acceleration_norm = new float[] {
                c * x, c * y, c * z
        };
        // Log.v(TAG,
        // "Setting acceleration "+Float.toString(x)+" "+Float.toString(y)+" "+Float.toString(z));
    }

    /**
     * Initialize particle i based on current acceleration variables.
     * 
     * @param i particle array element to initialize with new value
     */
    private void initParticle(int i)
    {
        mParticles[i].x = acceleration_norm[0] * EMITTER_SIZE;
        mParticles[i].y = acceleration_norm[1] * EMITTER_SIZE;
        mParticles[i].z = acceleration_norm[2] * EMITTER_SIZE;

        mParticles[i].dx = acceleration[0] * SPEED_FACTOR;
        mParticles[i].dy = acceleration[1] * SPEED_FACTOR;
        mParticles[i].dz = acceleration[2] * SPEED_FACTOR;

        // a value from 0 (going back) to 1 (going forward)
        float fade = (1f + acceleration_norm[2] / acceleration_magnitude) / 2f;

        // set color (r,g,b) from .5 to 1 so they are mostly bright
        mParticles[i].setColor((fade + Math.abs(acceleration_norm[1])) / 2f,
                (fade + Math.abs(acceleration_norm[2])) / 2f,
                (fade + Math.abs(acceleration_norm[0])) / 2f);
    }

    /**
     * Update the particle system, move everything
     * initialize one particle per update overwritting if necessary
     */
    public void update()
    {
        // calculate time between frames in seconds
        long currentTime = System.currentTimeMillis();
        float timeFrame = (currentTime - lastTime) / 1000f;

        // replace the last time with the current time.
        lastTime = currentTime;

        initParticle(++youngestParticle);
        if (youngestParticle == particleCount - 1) {
            isFirstGeneration = false;
            youngestParticle = -1;
        }

        currentSize = (isFirstGeneration) ? youngestParticle + 1 : particleCount;
        // move the particles
        for (int i = 0; i < currentSize; i++) {
            mParticles[i].x = mParticles[i].x + (mParticles[i].dx * timeFrame);
            mParticles[i].y = mParticles[i].y + (mParticles[i].dy * timeFrame);
            mParticles[i].z = mParticles[i].z + (mParticles[i].dz * timeFrame);
        }
    }

    /**
     * Create an iterator to go over all the particles in z order for use in rendering
     */
    @Override
    public Iterator<Particle> iterator() {
        // Sort the particles for Z ordering
        Arrays.sort(mZParticles, 0, currentSize, new Comparator<Particle>() {
            public int compare(Particle p1, Particle p2) {
                if (p1.z > p2.z) {
                    return -1;
                } else if (p1.z < p2.z) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        Iterator<Particle> it = new Iterator<Particle>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < currentSize;
            }

            @Override
            public Particle next() {
                return mZParticles[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

}
