package com.example.pathfinder;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener implements GestureDetector.OnGestureListener {

	private static final String DEBUG = "app";
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		Log.d(DEBUG, "onDown (" + arg0.getX() + ", " + arg0.getY() + ")");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		Log.d(DEBUG, "onFling (" + arg0.getX() + ", " + arg0.getY() + ")");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		Log.d(DEBUG, "onLongPress (" + arg0.getX() + ", " + arg0.getY() + ")");
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		Log.d(DEBUG, "onScroll (" + arg0.getX() + ", " + arg0.getY() + ")");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		Log.d(DEBUG, "onShowPress (" + arg0.getX() + ", " + arg0.getY() + ")");
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		Log.d(DEBUG, "onSingleTapUp (" + arg0.getX() + ", " + arg0.getY() + ")");
		return false;
	}

}
