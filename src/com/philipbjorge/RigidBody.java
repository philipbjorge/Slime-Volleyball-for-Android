package com.philipbjorge;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.RectF;

public class RigidBody {
	// Have movement properties (x,y,dx,dy,accel?)
	// Have a mass
	// Have a collision shape
	Path drawable;
	Paint drawablePaint;
	
	PointF dpPos;
	PointF dpVel;
	
	RectF drawRect;
	
	float radius, ox, oy;
	
	public RigidBody(float x, float y, float r, boolean isSemi, boolean isLine, Paint p) {
		ox = x;
		oy = y;
		
		radius = r;
		drawablePaint = p;
		
		dpPos = new PointF(x,y);
		dpVel = new PointF(0,0.01f);
		
		// Create the shapes to draw
		drawable = new Path();
		if(!isLine){
			if(isSemi) {
				drawable.moveTo(x, y);
				drawRect = new RectF(dpPos.x-radius, dpPos.y-radius, dpPos.x+radius, dpPos.y);
				drawable.addArc(new RectF(dpPos.x-radius, dpPos.y-radius, dpPos.x+radius, dpPos.y+radius), 180, 180);
				drawable.close();
			} else {
				drawable.moveTo(x, y);
				drawRect = new RectF(dpPos.x-r, dpPos.y-r, dpPos.x+r, dpPos.y+r);
				drawable.addCircle(dpPos.x, dpPos.y, r, Path.Direction.CW);
			}
		} else {
			// make line
			drawable.moveTo(0,0);
			drawRect = new RectF(x-2, y, x+2, y+r);
			drawable.addRect(x-2, y, x+2, y+r, Direction.CCW);
		}
	}
	
	public void resetPos() {
		dpVel.x = 0;
		dpVel.y = 0.01f;
		
		float offX = ox-dpPos.x;
		float offY = oy-dpPos.y;
		
		dpPos.x += offX;
		dpPos.y += offY;
		
		drawable.offset(offX, offY);
		
		drawRect.offset(offX, offY);
	}
	
	public RectF getSpriteRect() {
		return drawRect;
	}
	
	public void setVX(float dx) {
		dpVel.x = dx;
	}
	
	public void setVY(float dy) {
		dpVel.y = dy;
	}	
	
	public float getX() {
		return dpPos.x;
	}
	
	public float getY() {
		return dpPos.y;
	}
	
	public void offsetPosition(float x, float y) {
		drawRect.offset(x, y);
		dpPos.offset(x, y);
		drawable.offset(x, y);
	}
	
	public float getRadius() {
		return radius;
	}
	
	public PointF getVelocity() {
		return dpVel;
	}
	
	public Paint getPaint() {
		return drawablePaint;
	}
	
	public Path getDrawable() {
		return drawable;
	}
	
	public void setPaint(Paint a) {
		drawablePaint = a;
	}
}