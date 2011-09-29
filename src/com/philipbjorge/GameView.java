package com.philipbjorge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private GameThread thread;
	
	public GameView(Context context, boolean isSingle) {
		super(context);
		
		// makes this the handler of events
		getHolder().addCallback(this);
		
		// main game loop
		thread = new GameThread(getHolder(), this, isSingle);
		
		// make focusable, so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(width/height >= 2.0)
			thread.setResolution(width, height, width*2, height); // make max height
		else
			thread.setResolution(width, height, width, width/2); // make max width
		
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// Ends the thread and activity if left
		if(!hasWindowFocus) {
			thread.setRunning(false);
		    ((Activity)getContext()).finish();
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while(retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again to shut down the thread
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		thread.updateControls(event);
		return true;
	}
}