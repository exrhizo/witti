//Alex WArren
package com.rhizomatos.acceletracer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;

public class TracerGLSurfaceView extends GLSurfaceView{
    Renderer mRenderer;
    public TracerGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TracerGLSurfaceView(Context context) {
        super(context);
    }
    @Override
    public void setRenderer(Renderer renderer){
        super.setRenderer(renderer);
        mRenderer = renderer;
    }
}