//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Alex Warren

package edu.arizona.ece473573.witti.cloudview;

import java.nio.FloatBuffer;

import android.util.Log;

/**
 * PointCloud Class for representing a frame of LiDAR data.
 *
 * Mainly a wrapper for a FloatBuffer of vertices. In future
 * releases, this can contain information about the ground plane
 * other code related to creating a path.
 * 
 * @author Alex Warren
 */
public class PointCloud {
    private static final String CAT_TAG = "WITTI_PointCloud";
    public FloatBuffer mVertexBuffer;
    public float mMinZ;
    public float mMaxZ;
    public float mHeight;

    /**
     * Constructor for PointCloud.
     * 
     * @param buffer a directly allocated array of floats representing vertices.
     */
    public PointCloud(FloatBuffer buffer) {
        mVertexBuffer = buffer;
        mMinZ = -100;
        mMaxZ = 100;
    }
    
    public void setMinMax(){
        mVertexBuffer.rewind();
        float temp;
        mMinZ = mVertexBuffer.get();
        mMaxZ = mMinZ;
        while(mVertexBuffer.remaining() > 0){
            temp = mVertexBuffer.get();
            if (temp < mMinZ) mMinZ = temp;
            if (temp > mMaxZ) mMaxZ = temp;
        }
        mHeight = mMaxZ - mMinZ;
        Log.v(CAT_TAG, "setMinMax. Min: " + Float.toString(mMinZ)
                                 +"Max: " + Float.toString(mMaxZ));
    }
    // public void loadVertices(FloatBuffer buffer){
    //     mVertexBuffer = buffer;
    // }
    
}
