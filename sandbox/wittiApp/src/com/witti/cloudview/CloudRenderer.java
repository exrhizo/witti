package com.witti.cloudview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

public class CloudRenderer implements Renderer {
    private CloudSurfaceView mCloudSurfaceView;
    private int mProgId;
    private int mTexId;
    private int mPosition;
    private int mTexture;
    private int mColor;
    private int mTimes;

    float[] fVertex = {0,0,0};
    FloatBuffer vertexBuffer;
    public ParticleRenderer(ParticleView view) {
        mCloudSurfaceView = view;
        vertexBuffer = ByteBuffer.allocateDirect(fVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(fVertex).position(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(iProgId);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTexId);
        
        GLES20.glUniform1i(iTexture, 0);
        
        mCloudSurfaceView.mgr.draw(iPosition, iMove, iTimes, iColor, iLife, iAge);
        
//      GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
        
        String strVShader = 
            "precision mediump float;" +
            "attribute vec4 a_Position;" +
            "uniform float a_time;" +
            "uniform float a_max_z;" +
            "varying float v_color;" +
            "float time;" +
            "void main(){" +
                "gl_PointSize = 10.0;" +
                "v_color = vec4(.6,.6,.6+.4*(a_Position.z/a_max_z),.5);" +
                "gl_Position = a_Position;" +
            "}";

        String strFShader =
            "precision mediump float;" +
            "uniform sampler2D u_texture;" +
            "varying vec4 v_color;" +
            "void main(){" +
                "vec4 tex = texture2D(u_texture, gl_PointCoord);" +
                "gl_FragColor = v_color * tex;" +
            "}";
        mProgId = Utils.LoadProgram(strVShader, strFShader);
        mTexId = Utils.LoadTexture(mCloudSurfaceView, R.drawable.particle);
        
        mPosition = GLES20.glGetAttribLocation(iProgId, "a_Position");
        mTexture = GLES20.glGetUniformLocation(iProgId, "u_texture");
        mColor = GLES20.glGetAttribLocation(iProgId, "a_time");
        mTimes = GLES20.glGetUniformLocation(iProgId, "a_max_z");
    }
    
}
