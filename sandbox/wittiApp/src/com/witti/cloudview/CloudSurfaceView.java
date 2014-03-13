package com.witti.cloudview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;

public class CloudSurfaceView  extends GLSurfaceView{
    private static final String CAT_TAG = "WITTI_CloudSurfaceView";
    Renderer mRenderer;
    CloudPoints mCloudPoints;

    public CloudSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    
    public CloudSurfaceView(Context context) {
        super(context);
        initialize();
    }

    @Override
    public void setRenderer(Renderer renderer){
        super.setRenderer(renderer);
        mRenderer = renderer;
    }

    private void initialize(){
        Log.v(CAT_TAG, "CloudRenderer initialize");
        mCloudPoints = new CloudPoints(this);
    }
}
