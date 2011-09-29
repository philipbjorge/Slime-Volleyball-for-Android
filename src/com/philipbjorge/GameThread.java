package com.philipbjorge;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	private final static int MAX_FPS = 40;
	private final static int MAX_FRAME_SKIPS = 5;
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	private int jumpTimer = 60;
	
	private boolean running;
	private boolean isAI;
	private boolean resSet;
	
	private int w,h,gw,gh;
	
	private Match m;
	
	private SurfaceHolder surfaceHolder;
	private GameView gamePanel; // gamePanel has getContext()
	
	private boolean movingLeft, movingRight, jumping;
	
	private AI ai;
	
	public GameThread(SurfaceHolder s, GameView g, boolean isSingle) {
		super();

		// sets AI flag
		isAI = isSingle;
		
		resSet = false;
		movingLeft = false;
		movingRight = false;
		jumping = false;
		
		if(isAI)
			// TODO: Instantiate AI
		
		surfaceHolder = s;
		gamePanel = g;
	}
	
	public void setRunning(boolean r) {
		running = r;
	}
	
	public void setResolution(int aWidth, int aHeight, int gWidth, int gHeight) {
		w = aWidth;
		h = aHeight;
		gw = gWidth;
		gh = gHeight;
		
		m = new Match(0, 0, true);
		m.setDT(FRAME_PERIOD);
		m.setResolution(aWidth, aHeight, gWidth, gHeight, gh/40, gamePanel.getResources());
		
		ai = new AI(aWidth/2, m.getBallPos(), m.getP2Pos());
		
		resSet = true;
	}
	
	public void updateControls(MotionEvent e) {
		/*int ptrIndex = -1;
		if(e.getPointerCount() > 1) 
			ptrIndex = (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT; 
		*/
		int a = e.getActionMasked();
		int aIndex = e.getActionIndex();
		int x,y;
		switch(a) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_MOVE:
				x = (int)e.getX(aIndex);
				y = (int)e.getY(aIndex);
				
				if(m.leftPressed(x, y)) {
					movingLeft = true;
					movingRight = false;
				}
				if(m.rightPressed(x, y)) {
					movingLeft = false;
					movingRight = true;
				}
				if(m.jumpPressed(x, y)) { // checks for player on ground
					jumping = true;
				}
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
				x = (int)e.getX(aIndex);
				y = (int)e.getY(aIndex);
				
				if(m.leftPressed(x, y))
					movingLeft = false;
				if(m.rightPressed(x, y))
					movingRight = false;
				if(m.jumpPressed(x, y))
					jumping = false;
			break;
		}
	}
	
	private void p1Move() {
		if(movingLeft) {
			m.setP1VX(-gw/110);   // 100 is a movement constant, lower = faster
		}
		if(movingRight) {
			m.setP1VX(gw/110);
		}
		if(!movingLeft && !movingRight) {
			m.setP1VX(0);
		}
		if(jumping)
			m.startP1Jump();
	}
	
	@Override
	public void run() {
		Canvas canvas;
		
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
		
		sleepTime = 0;
		
		while(running) {
			canvas = null;
			// try locking the canvas for exclusive 
			try {
				canvas = this.surfaceHolder.lockCanvas();
				
				// update and render both access the same objects. Need to keep them from accessing same object at same time.
				synchronized(surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					
					p1Move();
					if(isAI)
					{
						// TODO: Implement AI
						// AI.update();
						ai.update(m.getBallPos(), m.getP2Pos());
						//m.setP2VX(0);
						switch(ai.move()) {
						case 'u':
							m.startP2Jump();
							break;
						case 'l':
							m.setP2VX(-gw/110);
							break;
						case 'r':
							m.setP2VX(gw/110);
							break;
						case 'p':
							//upright
							m.startP2Jump();
							m.setP2VX(gw/110);
							break;
						case 'q':
							//upleft
							m.startP2Jump();
							m.setP2VX(-gw/110);
							break;
						}
						
					} else {
						// TODO: Implement user controls via UDP
					}
					
					m.update();
					
					// Draw to canvas
					m.render(canvas);
					
					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					// Fast Phone: Sleeping between frames
					if(sleepTime > 0) {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}
					
					// Slow Phone: Catch up without rendering
					while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// TODO: Check game speed on multiple platforms
						p1Move();
						m.update();
						sleepTime += FRAME_PERIOD; // add a frame to check if we are in the next frame
						framesSkipped++;
						//
					}
				}
			} finally {
				if(canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
		} // end running loop
	}
}