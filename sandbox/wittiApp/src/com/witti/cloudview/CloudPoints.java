package es2.learning;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import android.opengl.GLES20;

public class CloudPoints {
    final static int NUM_PARTICLES = 800;
    final static int PARTICLE_SIZE = 11;
    float nTimeCounter = 0;
    //each particle contains
    //x,y,z,r,g,b,dx,dy,dz,life,age
    float[] fVertices = new float[NUM_PARTICLES * PARTICLE_SIZE];
    FloatBuffer vertexBuffer;
    ParticleView curView;   
    ParticleUpdateThread pThread;
    public ParticleManager(ParticleView view){
        curView = view;
        pThread = new ParticleUpdateThread(view);
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
            vel += inc;
            angle = (int) (gen.nextFloat() * 360f);
            //x,y,z
            fVertices[i*PARTICLE_SIZE + 0] = centerX;
            fVertices[i*PARTICLE_SIZE + 1] = centerY;
            fVertices[i*PARTICLE_SIZE + 2] = centerZ;
            //r,g,b
            fVertices[i*PARTICLE_SIZE + 3] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 4] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 5] = gen.nextFloat();
            //dx,dy,dz
            fVertices[i*PARTICLE_SIZE + 6] = (float) (Math.cos(Math.toRadians(angle)) * vel);
            fVertices[i*PARTICLE_SIZE + 7] = (float) (Math.sin(Math.toRadians(angle)) * vel);
            fVertices[i*PARTICLE_SIZE + 8] = gen.nextFloat() - 0.5f;
            
            //life
            fVertices[i*PARTICLE_SIZE + 9] = Utils.rnd(0.5f, 1f);
            //age
            fVertices[i*PARTICLE_SIZE + 10] = Utils.rnd(0.01f, 0.1f);
        }
        vertexBuffer.put(fVertices).position(0);
        pThread.SetRunning(true);
        pThread.start();
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
