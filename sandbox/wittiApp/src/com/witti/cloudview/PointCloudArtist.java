//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package com.witti.cloudview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.witti.activities.WittiSettings;
import com.witti.wittiapp.R;

public class PointCloudArtist {
    private static final String CAT_TAG = "WITTI_PointCloudArtist";
    private CloudSurfaceView mCloudSurfaceView;

    private int mProgId;
    private int mTexId;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mTimeHandle;
    private int mMaxZHandle;
    private int mMVPHandle;

    private float mMaxZ;

    final static int MAX_PARTICLES = 7364;   //7364;
    final static int PARTICLE_SIZE = 3;

    float[] mVertices;
    FloatBuffer mVertexBuffer;

    public PointCloudArtist(CloudSurfaceView view){
        mCloudSurfaceView = view;
        loadDemo();
    }

    public void draw(float[] mMVPMatrix){

        //loadDemo();
        Log.v(CAT_TAG, "Program id: " + Integer.toString(mProgId));
        Utils.checkGlError(CAT_TAG, "Draw, Before Use Program");
        GLES20.glUseProgram(mProgId);
        Utils.checkGlError(CAT_TAG, "Use program");
        
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId);
        Utils.checkGlError(CAT_TAG, "Bind Texture");
        
        GLES20.glUniform1i(mTextureHandle, 0);
        Utils.checkGlError(CAT_TAG, "Texture handle");
        
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        Utils.checkGlError(CAT_TAG, "Buffer");

        GLES20.glUniform1f(mTimeHandle, 0f);
        GLES20.glUniform1f(mMaxZHandle, mMaxZ);
        GLES20.glUniformMatrix4fv(mMVPHandle, 1, false, mMVPMatrix, 0);
        Utils.checkGlError(CAT_TAG, "Uniforms");
        
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, MAX_PARTICLES);
        Utils.checkGlError(CAT_TAG, "Draw");
    }

    protected void initializeShaders(){
        Utils.checkGlError(CAT_TAG, "Before Initialize");
        Log.v(CAT_TAG, "initializeShaders");
        String strVShader = 
            "precision mediump float;" +
            "uniform mat4 u_MVPMatrix;" +
            "attribute vec4 a_Position;" +
            "uniform float a_time;" +
            "uniform float a_max_z;" +
            "varying vec4 v_color;" +
            "float time;" +
            "void main(){" +
                "gl_PointSize = 20.0;" +
                "v_color = vec4(.6,.6,.6+.4*(a_Position.z/a_max_z),1);" +
                "gl_Position = u_MVPMatrix * a_Position;" +
            "}";

        String strFShader = 
            "precision mediump float;" +
            "uniform sampler2D u_texture;" +
            "varying vec4 v_color;" +
            "void main(){" +
                "vec4 tex = texture2D(u_texture, gl_PointCoord);" +
                "gl_FragColor = v_color * tex;" +
                "gl_FragColor.w = .7;" +
            "}";
        
        
        
        mProgId = Utils.LoadProgram(strVShader, strFShader);
        Utils.checkGlError(CAT_TAG, "After program load");
        mTexId = Utils.LoadTexture(mCloudSurfaceView, R.drawable.particle);
        Utils.checkGlError(CAT_TAG, "After loading texture");
        
        mPositionHandle = GLES20.glGetAttribLocation(mProgId, "a_Position");
        Utils.checkGlError(CAT_TAG, "THIS Handle");
        mTextureHandle = GLES20.glGetUniformLocation(mProgId, "u_texture");
        mMVPHandle = GLES20.glGetUniformLocation(mProgId, "u_MVPMatrix");
        mTimeHandle = GLES20.glGetAttribLocation(mProgId, "a_time");
        mMaxZHandle = GLES20.glGetUniformLocation(mProgId, "a_max_z");
        Utils.checkGlError(CAT_TAG, "After getting handles");
    }

    private void loadDemo(){
        Log.v(CAT_TAG, "loadDemo");
        loadDemoFile();
        mVertexBuffer = ByteBuffer.allocateDirect(mVertices.length * 4)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(mVertices).position(0);
    }

    private void loadDemoFile(){
        Log.v(CAT_TAG, "loadDemoFile");
        mVertices = new float[MAX_PARTICLES * PARTICLE_SIZE];
        Context context = mCloudSurfaceView.getContext().getApplicationContext();
        // Gets requested demo file from settings and reads it in
        WittiSettings settings = new WittiSettings(context);
        String mFile = settings.getDemoFile();
        InputStream is = mCloudSurfaceView.getContext().getApplicationContext()
                          .getResources().openRawResource(context.getResources().getIdentifier(mFile, 
                        		  "raw", context.getPackageName()));
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        int count = 0;
        String line;
        Float[] parsed_line;
        mMaxZ = 0;
        try {
            while ((line = in.readLine()) != null && count<MAX_PARTICLES){
                parsed_line = parseLine(line);
                mVertices[count*PARTICLE_SIZE]   = parsed_line[0];
                mVertices[count*PARTICLE_SIZE+1] = parsed_line[1];
                mVertices[count*PARTICLE_SIZE+2] = parsed_line[2];
                if (parsed_line[2]>mMaxZ) mMaxZ = parsed_line[2];
                count++;
            }
            Log.v(CAT_TAG, "loaded "+Integer.toString(count)+" points from file");
            Log.v(CAT_TAG, "test value "+Float.toString(mVertices[8]));
        } catch (IOException e) {
            Log.e(CAT_TAG, "Couldn't read from resource file.");
        }
        while (count<MAX_PARTICLES){
            mVertices[count*PARTICLE_SIZE]   = 0f;
            mVertices[count*PARTICLE_SIZE+1] = 0f;
            mVertices[count*PARTICLE_SIZE+2] = 0f;
            count++;
        }
        
    }

    private Float[] parseLine(String line) {
        Float[] result = {
                0f, 0f, 0f
        };
        String[] tokens = line.split("\\s");
        if (tokens.length < 3)
            return result;
        int count = 0;
        for (int ii = 0; ii < tokens.length && count < 3; ii++) {
            try {
                result[count] = Float.parseFloat(tokens[ii]);
            } catch (NumberFormatException nfe) {
                continue;
            }
            count++;
        }
        return result;
    }
}