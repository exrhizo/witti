//Project: Homework 2 for ECE 573 - acceletracer
//Date: 1/31/2014
//Author: Alex Warren

package com.rhizomatos.acceletracer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Simple particle class.
 * 
 * @author Alex Warren
 */
public class Particle {
    public float x, y, z;
    public float dx, dy, dz;
    public FloatBuffer mColorBuffer;
    private final int BYTES_PER_FLOAT = 4;

    public Particle() {

    }

    //Set Color creates a buffer from r, g, b values
    public void setColor(float r, float g, float b){
        float[] colorData = new float[] {r, g, b, 1.0f,
                                         r, g, b, 1.0f,
                                         r, g, b, 1.0f};
        // Initialize the buffers.
        mColorBuffer = ByteBuffer.allocateDirect(colorData.length * BYTES_PER_FLOAT)
                 .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer.put(colorData).position(0);
    }
}
