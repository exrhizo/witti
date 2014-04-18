//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Alex Warren

package edu.arizona.ece473573.witti.cloudview;

import java.nio.FloatBuffer;

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
    public FloatBuffer mVertexBuffer;

    /**
     * Constructor for PointCloud.
     * 
     * @param buffer a directly allocated array of floats representing vertices.
     */
    public PointCloud(FloatBuffer buffer) {
        mVertexBuffer = buffer;
    }
    
    // public void loadVertices(FloatBuffer buffer){
    //     mVertexBuffer = buffer;
    // }
    
}
