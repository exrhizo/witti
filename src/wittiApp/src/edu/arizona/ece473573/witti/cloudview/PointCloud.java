//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Alex Warren

package edu.arizona.ece473573.witti.cloudview;

import java.nio.FloatBuffer;

public class PointCloud {
    public FloatBuffer mVertexBuffer;

    public PointCloud(FloatBuffer buffer) {
        loadVertices(buffer);
    }
    
    public void loadVertices(FloatBuffer buffer){
        mVertexBuffer = buffer;
    }
    
}
