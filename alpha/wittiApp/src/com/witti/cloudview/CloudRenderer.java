//ECE 573 Project
//Team: Witty
//Date: 3/13/14
//Authors: Brianna Heersink, Brian Smith, Alex Warren

package com.witti.cloudview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class CloudRenderer implements Renderer {
    private static final String CAT_TAG = "WITTI_Renderer";
    private CloudSurfaceView mCloudSurfaceView;
    private CloudDrawer mCloudDrawer;

    //Matricies to store the model, view and projection for the "camera"
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    //Combined matrix used in shader
    private float[] mMVPMatrix = new float[16];

    public CloudRenderer(CloudSurfaceView view) {
        Log.v(CAT_TAG, "CloudRenderer constructor");
        mCloudSurfaceView = view;
        mCloudDrawer = new CloudDrawer(view);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        //Log.v(CAT_TAG, "MVP matrix: "+floatArrayToString(mMVPMatrix));
        mCloudDrawer.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.v(CAT_TAG, "CloudRenderer onSurfaceChanged");
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 250.0f;
        
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.v(CAT_TAG, "CloudRenderer onSurfaceCreated");
        // Position the eye behind the origin.
        final float eyeX = 30.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 30.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        Matrix.setIdentityM(mModelMatrix, 0);
        //Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
        
    }
    
    public String floatArrayToString(float[] arr){
        StringBuilder result = new StringBuilder();
        for (int ii=0; ii<arr.length; ii++){
            result.append(arr[ii]);
            result.append(' ');
        }
        return result.toString();
    }
    
}
