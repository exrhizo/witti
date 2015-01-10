//ECE 573 Project
//Team: Witty
//Date: 4/17/14
//Authors: Alex Warren

package edu.arizona.ece473573.witti.cloudview;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

import edu.arizona.ece473573.witti.R;

import edu.arizona.ece473573.witti.activities.DisplayActivity;

/**
 * PointCloudArtist draws pointcloud with openGL.
 *
 * Initializes shaders, texture, and program for rendering
 * a point cloud. Contains draw function.
 * 
 * @author Alex Warren
 */
public class PointCloudArtist {
    private static final String CAT_TAG = "WITTI_PointCloudArtist";

    private DisplayActivity mDisplayActivity;

    //Handles for openGL objects
    private int mProgId;
    private int mTexId;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mZBottomHandle;
    private int mHeightHandle;
    private int mMVPHandle;


    public PointCloudArtist(DisplayActivity display){
        mDisplayActivity = display;
    }

    /**
     * Draws PointCloud as a points with a texture.
     * 
     * @param mMVPMatrix combined model view projection matrix
     */
    public void draw(float[] mMVPMatrix){
        //Get the current PointCloud
        PointCloud pc = mDisplayActivity.mSequence.getCurrentFrame();
        if (pc == null){
            return;
        }
        FloatBuffer vertexBuffer = pc.mVertexBuffer;
        float height = pc.mHeight;
        float zBottom = pc.mMinZ - height * (mDisplayActivity.mRenderer.mTime % 1.0f - 1.0f);
        Utils.checkGlError(CAT_TAG, "Draw, Before Use Program");
        GLES20.glUseProgram(mProgId);
        Utils.checkGlError(CAT_TAG, "Use program");
        
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId);
        Utils.checkGlError(CAT_TAG, "Bind Texture");
        
        GLES20.glUniform1i(mTextureHandle, 0);
        Utils.checkGlError(CAT_TAG, "Texture handle");
        
        vertexBuffer.position(0);
        //TODO make constants for 3 and 4
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        Utils.checkGlError(CAT_TAG, "Buffer");

        GLES20.glUniform1f(mZBottomHandle, zBottom);
        GLES20.glUniform1f(mHeightHandle, height);
        GLES20.glUniformMatrix4fv(mMVPHandle, 1, false, mMVPMatrix, 0);
        Utils.checkGlError(CAT_TAG, "Uniforms");
        
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexBuffer.capacity()/3);
        Utils.checkGlError(CAT_TAG, "Draw");
    }

    /**
     * Initializes the shaders and registers them with openGL.
     */
    protected void initializeShaders(){
        Utils.checkGlError(CAT_TAG, "Before Initialize");
        Log.v(CAT_TAG, "initializeShaders");
        //Vertex Shader
        //This gets called for each point.
        //gl_ indicates a output of the shader
        String strVShader = 
            "precision mediump float;" +
            "uniform mat4 u_MVPMatrix;" +
            "attribute vec4 a_Position;" +
            "uniform float a_z_bottom;" +
            "uniform float a_height;" +
            "varying vec4 v_color;" +
            "void main(){" +
                "gl_PointSize = 6.0;" +
                "float mix = (a_Position.z-a_z_bottom)/a_height;" +
                "mix = mix - floor(mix);" +
                "mix = 4.0 * (mix * mix - mix + .25);" +
                //"mix = (16.0 * mix * mix - 24.0 * mix + 9.0);" +
                "mix = mix * mix;" +
                "mix = mix * mix;" +
                "v_color = vec4(1.0*mix + .58 - .58*mix," +
                               "0.4*mix + .41 - .41*mix," +
                               "0.3*mix + .85 - .85*mix, .7);" +
                "gl_Position = u_MVPMatrix * a_Position;" +
            "}";

        //Fragment Shader, used for interpolating between vertices, or
        //in this case around the particle
        String strFShader = 
            "precision mediump float;" +
            "uniform sampler2D u_texture;" +
            "varying vec4 v_color;" +
            "void main(){" +
                "vec4 tex = texture2D(u_texture, gl_PointCoord);" +
                "gl_FragColor = v_color * tex;" +
            "}";
        
        
        //Load everything and get handles

        mProgId = Utils.LoadProgram(strVShader, strFShader);
        Utils.checkGlError(CAT_TAG, "After program load");
        mTexId = Utils.LoadTexture(mDisplayActivity.mCloudSurfaceView, R.drawable.particle);
        Utils.checkGlError(CAT_TAG, "After loading texture");
        
        mPositionHandle = GLES20.glGetAttribLocation(mProgId, "a_Position");
        Utils.checkGlError(CAT_TAG, "THIS Handle");
        mTextureHandle = GLES20.glGetUniformLocation(mProgId, "u_texture");
        mMVPHandle = GLES20.glGetUniformLocation(mProgId, "u_MVPMatrix");
        mZBottomHandle = GLES20.glGetUniformLocation(mProgId, "a_z_bottom");
        mHeightHandle = GLES20.glGetUniformLocation(mProgId, "a_height");
        Utils.checkGlError(CAT_TAG, "After getting handles");
    }
}