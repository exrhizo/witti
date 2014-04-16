//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package com.witti.cloudview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class PointCloud {
    FloatBuffer mVertexBuffer;

    public PointCloud(FloatBuffer buffer) {
        loadVertices(buffer);
    }
    
    public loadVertices(FloatBuffer buffer){
        mVertexBuffer = buffer;
    }
    
}
