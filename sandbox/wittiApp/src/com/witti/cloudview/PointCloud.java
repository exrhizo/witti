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
    final static int NUM_PARTICLES = 1000;
    final static int PARTICLE_SIZE = 3;
    float nTimeCounter = 0;
    //each particle contains
    //x,y,z,r,g,b,dx,dy,dz,life,age
    float[] fVertices = new float[NUM_PARTICLES * PARTICLE_SIZE];
    FloatBuffer vertexBuffer;
    private CloudSurfaceView mCloudSurfaceView;  
    //ParticleUpdateThread pThread;
    public PointCloud(CloudSurfaceView view){
        mCloudSurfaceView = view;
        //pThread = new ParticleUpdateThread(view);
        vertexBuffer = ByteBuffer.allocateDirect(fVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
    
    public void CreateParticles(int size)
    {
        //just keep it empty for now, will deal later
    }
    
    public void Setup(float centerX, float centerY, float centerZ)
    {
        float inc = 1.0f/NUM_PARTICLES;
        float vel = 0f;
        int angle;
        for (int i = 0; i < NUM_PARTICLES; i++) {
            //x,y,z
            fVertices[i*PARTICLE_SIZE + 0] = centerX;
            fVertices[i*PARTICLE_SIZE + 1] = centerY;
            fVertices[i*PARTICLE_SIZE + 2] = centerZ;
        }
        vertexBuffer.put(fVertices).position(0);
        //pThread.SetRunning(true);
        //pThread.start();
    }
    
    public void update()
    {
        nTimeCounter+=0.01;
        /*if (nTimeCounter >= 1.0)
            nTimeCounter = 0;*/
    }
    
    public void draw(int iPosition, int iMove, int iTimes, int iColor, int iLife, int iAge)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(iPosition, 3, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iPosition);
        
        vertexBuffer.position(3);
        GLES20.glVertexAttribPointer(iColor, 3, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iColor);
        
        vertexBuffer.position(6);
        GLES20.glVertexAttribPointer(iMove, 3, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iMove);
        
        vertexBuffer.position(9);
        GLES20.glVertexAttribPointer(iLife, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iLife);
        
        vertexBuffer.position(10);
        GLES20.glVertexAttribPointer(iAge, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iAge);
        
        GLES20.glUniform1f(iTimes, nTimeCounter);
        
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, NUM_PARTICLES);
    }
}
