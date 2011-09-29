package com.philipbjorge;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.Rect;

public class Match {
	private int p1_score;
	private int p2_score;
	private boolean possessionLeft;
	
	private Rect playRect;
	private Rect leftButton, rightButton, jumpButton;
	// TODO: leftButton,rightBUtton,jumpButton sprites
	
	private float dt;
	
	private PhysicsEngine phys;
	
	public Match(int scoreA, int scoreB, boolean possessionA)
	{
		p1_score = scoreA;
		p2_score = scoreB;
		possessionLeft = possessionA;
	}
	
	public void render(Canvas c) {
		// TODO: Draw the BG (with vectors)
		c.drawColor(Color.LTGRAY);
		
		// TODO: Remove this temporary play area
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		c.drawRect(playRect, p);
		
		// Draw the rigid bodies
		phys.drawBodies(c);
		
		// TODO: Draw the score and control buttons
		// Draw buttons to fill the bottom (18%) and expand to fill the empty space
		p.setColor(Color.RED);
		c.drawRect(leftButton, p);
		
		p.setColor(Color.BLUE);
		c.drawRect(rightButton, p);
		
		p.setColor(Color.YELLOW);
		c.drawRect(jumpButton, p);
	}
	
	public void setResolution(int aWidth, int aHeight, int width, int height, float g, Resources res) {
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(android.graphics.Color.BLUE);
		Paint p2 = new Paint(p);
		p2.setColor(Color.RED);
		Paint p3 = new Paint(p);
		p3.setColor(Color.GREEN);
		
		playRect = new Rect((aWidth-width)/2, (aHeight-height), width, height-(int)((height-aHeight)*.18));
		
		leftButton = new Rect(playRect.left, playRect.bottom-(int)(playRect.height()*.18), playRect.width()/6, playRect.bottom);
		rightButton = new Rect(playRect.left+playRect.width()/6, playRect.bottom-(int)(playRect.height()*.18), 2*playRect.width()/6, playRect.bottom);
		jumpButton = new Rect((int)(playRect.right*.7), playRect.bottom-(int)(playRect.height()*.18), (int)(playRect.right*.9), playRect.bottom);
		
		int p1X = (int)(aWidth*.25);
		
		phys = new PhysicsEngine(new RigidBody(p1X, leftButton.top, (int)(width*0.05), true, false, p), new RigidBody((int)(aWidth*.75), leftButton.top, (int)(width*0.05), true, false, p2), g, res);
		
		// ball
		phys.addBall(new RigidBody((int)(aWidth*.25), 100, (int)(width*0.017), false, false, p), res);
		// net
		float top = playRect.bottom-(int)(playRect.height()*.31);
		phys.addBody(new RigidBody(aWidth/2, top, playRect.bottom-top, false, true, p3), res);
		
		phys.setPlayArea(playRect);
	}
	
	// if true, next button image
	public boolean leftPressed(float x, float y) {
		return leftButton.contains((int)x,(int)y);
	}
	
	public boolean rightPressed(float x, float y) {
		return rightButton.contains((int)x,(int)y);
	}
	
	public boolean jumpPressed(float x, float y) {
		return (jumpButton.contains((int)x, (int)y));
	}
	
	public void startP1Jump() {
		phys.startP1Jump();
	}
	
	public void setDT(float dt) {
		this.dt = dt;
	}
	
	public void setP1VX(float dx) {
		phys.setP1VX(dx);
	}
	
	public void setP1VY(float dy) {
		phys.setP1VY(dy);
	}
	
	public void offsetP1(float dx, float dy) {
		phys.offsetP1(dx, dy);
	}
	
	public void startP2Jump() {
		phys.startP2Jump();
	}
	
	public void setP2VX(float dx) {
		phys.setP2VX(dx);
	}
	
	public void offsetP2(float dx, float dy) {
		//phys.offsetP2(dx, dy);
	}
	
	public void update() {
		phys.integrate(dt);
	}
	
	public PointF getBallPos() {
		return phys.getBallPos();
	}
	
	public PointF getP2Pos() {
		return phys.getP2Pos();
	}
}
