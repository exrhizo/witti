package com.example.pathfinder;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class Pallette extends View{

	private static final String DEBUG = "app";
	private ArrayList<Float> pathX;
	private ArrayList<Float> pathY;
	
	private Path line;
	private Paint linePaint;
	private Paint textPaint;
	
	
	public Pallette(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(DEBUG, "Pallette constructor");
		
		pathX  = new ArrayList<Float>();
		pathY  = new ArrayList<Float>();
		
		line = new Path();
		linePaint = new Paint();
		textPaint = new Paint();
		
		linePaint.setStyle(Style.STROKE);
		linePaint.setColor(Color.GREEN);
		linePaint.setStrokeWidth(3);
		
		textPaint.setStyle(Style.FILL);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextAlign(Align.LEFT);
		textPaint.setTextSize(48);
		textPaint.setStrokeWidth(1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				Log.d(DEBUG, "ACTION_UP: (" + event.getX() + ", " + event.getY() + ")");
				break;
			case MotionEvent.ACTION_DOWN:
				Log.d(DEBUG, "ACTION_DOWN: (" + event.getX() + ", " + event.getY() + ")");
				
				//Create a new line
				line.reset();
				
				pathX.clear();
				pathY.clear();
				
				//Add the coordinates to the array
				pathX.add(event.getX());
				pathY.add(event.getY());
				
				//move the line, this is the starting point
				line.moveTo(event.getX(), event.getY());
				
				
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(DEBUG, "ACTION_MOVE: (" + event.getX() + ", " + event.getY() + ")");
				//Add the coordinates to the array
				pathX.add(event.getX());
				pathY.add(event.getY());
				
				//move the line to the current point
				line.lineTo(event.getX(), event.getY());
				break;
			default: 
				break;
		}
		
		//invalidate the view before leaving to redraw
		invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		
		canvas.drawPath(line, linePaint);
		canvas.drawText("Array size: " + String.valueOf(pathX.size()), 
				(this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()) / 2, 
				this.getHeight() - this.getPaddingBottom(), textPaint);
		
	}
}
