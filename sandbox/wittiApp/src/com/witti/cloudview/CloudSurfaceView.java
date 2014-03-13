package com.witti.cloudview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;

public class CloudSurfaceView  extends GLSurfaceView{
    Renderer mRenderer;
    CloudPoints mCloudPoints;

    public CloudSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCloudPoints = new CloudPoints(this);
    }
    
    public CloudSurfaceView(Context context) {
        super(context);
    }

    @Override
    public void setRenderer(Renderer renderer){
        super.setRenderer(renderer);
        mRenderer = renderer;
    }
}
