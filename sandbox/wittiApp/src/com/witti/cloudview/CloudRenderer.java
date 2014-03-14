package com.witti.cloudview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class CloudRenderer implements Renderer {
    private static final String CAT_TAG = "WITTI_Renderer";
    private CloudSurfaceView mCloudSurfaceView;
    private int mProgId;
    private int mTexId;
    private int mPosition;
    private int mTexture;
    private int mColor;
    private int mTimes;
    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];
    
    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    float[] fVertex = {0,0,0};
    FloatBuffer vertexBuffer;

    public CloudRenderer(CloudSurfaceView view) {
        Log.v(CAT_TAG, "CloudRenderer constructor");
        mCloudSurfaceView = view;
        vertexBuffer = ByteBuffer.allocateDirect(fVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(fVertex).position(0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glUseProgram(iProgId);
        
        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTexId);
        
        //GLES20.glUniform1i(iTexture, 0);
        
        //mCloudSurfaceView.mgr.draw(iPosition, iMove, iTimes, iColor, iLife, iAge);
        
//      GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
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
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 25.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // Set the background clear color to black.
        // GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        // GLES20.glEnable(GLES20.GL_BLEND);
        // GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
        
        // String strVShader = 
        //     "precision mediump float;" +
        //     "attribute vec4 a_Position;" +
        //     "uniform float a_time;" +
        //     "uniform float a_max_z;" +
        //     "varying float v_color;" +
        //     "float time;" +
        //     "void main(){" +
        //         "gl_PointSize = 10.0;" +
        //         "v_color = vec4(.6,.6,.6+.4*(a_Position.z/a_max_z),.5);" +
        //         "gl_Position = a_Position;" +
        //     "}";

        // String strFShader =
        //     "precision mediump float;" +
        //     "uniform sampler2D u_texture;" +
        //     "varying vec4 v_color;" +
        //     "void main(){" +
        //         "vec4 tex = texture2D(u_texture, gl_PointCoord);" +
        //         "gl_FragColor = v_color * tex;" +
        //     "}";
        // mProgId = Utils.LoadProgram(strVShader, strFShader);
        // mTexId = Utils.LoadTexture(mCloudSurfaceView, R.drawable.particle);
        
        // mPosition = GLES20.glGetAttribLocation(mProgId, "a_Position");
        // mTexture = GLES20.glGetUniformLocation(mProgId, "u_texture");
        // mTime = GLES20.glGetAttribLocation(mProgId, "a_time");
        // mMaxZ = GLES20.glGetUniformLocation(mProgId, "a_max_z");
    }
    
}
