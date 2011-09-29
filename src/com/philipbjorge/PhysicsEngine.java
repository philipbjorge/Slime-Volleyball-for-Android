package com.philipbjorge;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

public class PhysicsEngine {
	private ArrayList<RigidBody> bodies;
	private RigidBody p1,p2;
	private RigidBody ball;
	private Rect playArea;
	// ball,p1,p2 sprite
	
	private float gravity;
	
	private float rightBound;
	private float leftBound;
	private float middleBound;
	private float bottomBound;
	
	private float p1JumpTimer, p2JumpTimer, ballTimer;
	
	AnimatedSprite s_ball, s_p1, s_p2;
	
	public PhysicsEngine(RigidBody p1, RigidBody p2, float g, Resources res)
	{
		bodies = new ArrayList<RigidBody>();
		this.p1 = p1;
		this.p2 = p2;
		
		// create p1,p2,ball sprites, include set rotation for ball
		s_p1 = new AnimatedSprite(R.drawable.green_slime, 200, 100, p1.getRadius()*2, p1.getRadius(), 4, 4, res, false);
		s_p2 = new AnimatedSprite(R.drawable.blue_slime, 200, 100, p2.getRadius()*2, p2.getRadius(), 4, 4, res, false);
		
		
		
		gravity = g;
		p1JumpTimer = 0;
		p2JumpTimer = 0;
		ballTimer = 0;
	}
	
	public void addBody(RigidBody r, Resources res) {
		bodies.add(r);
	}
	
	public void addBall(RigidBody b, Resources res) {
		ball = b;
		s_ball = new AnimatedSprite(R.drawable.ball, 64, 64, ball.getRadius()*2, ball.getRadius()*2, 1, 0, res, true);
	}
	
	public void setPlayArea(Rect r) {
		playArea = r;
		middleBound = playArea.width()/2; // adjust -p1.getRadius()
		leftBound = playArea.left+p1.getRadius();
		rightBound = playArea.right - p2.getRadius();
		bottomBound = playArea.bottom-(int)(playArea.height()*.18);
	}
	
	public void offsetP1(float dx, float dy) {
		//p1.offsetVelocity(dx, dy);
	}
	
	public void setP1VX(float dx) {
		p1.setVX(dx);
	}
	
	public void setP1VY(float dy) {
		p1.setVY(dy);
	}
	
	public void setP2VY(float dy) {
		p2.setVY(dy);
	}
	
	public void setP2VX(float dx) {
		p2.setVX(dx);
	}
	
	public void startP2Jump() {
		if(P2isGrounded())
			p2JumpTimer = 15;
	}

	public boolean P2isGrounded() {
		return (p2.getY() >= bottomBound);
	}
	
	public boolean P1isGrounded() {
		return (p1.getY() >= bottomBound);
	}
	
	public void startP1Jump() {
		if(P1isGrounded())
			p1JumpTimer = 15;
	}
	
	public void startBall() {
		if(!ballIsGrounded())
			ballTimer = 15;
	}
	
	public boolean ballIsGrounded() {
		return (ball.getY()+ball.getRadius() >= bottomBound);
	}
	
	public void drawBodies(Canvas c) {
		// Loop through bodies and draw the shapes
		//c.drawPath(p1.getDrawable(), p1.getPaint());
		//c.drawPath(p2.getDrawable(), p2.getPaint());
		//c.drawPath(ball.getDrawable(),ball.getPaint());
		for(RigidBody body : bodies) {
			c.drawPath(body.getDrawable(), body.getPaint());
		}
		
		// Draw animations
		c.drawBitmap(s_p1.getBitmap(), p1.getSpriteRect().left, p1.getSpriteRect().top, null);
		c.drawBitmap(s_p2.getBitmap(), p2.getSpriteRect().left, p2.getSpriteRect().top, null);
		c.drawBitmap(s_ball.getBitmap(), ball.getSpriteRect().left, ball.getSpriteRect().top, null);
		//c.drawBitmap(s_p1.getBitmap(), new Rect(0, 0, 200, 100), p1.getSpriteRect(), null);
		//c.drawBitmap(s_p2.getBitmap(), new Rect(0, 0, 200, 100), p2.getSpriteRect(), null);
		//c.drawBitmap(s_ball.getBitmap(), new Rect(0, 0, 64, 64), ball.getSpriteRect(), null);
	}
	
	private void keepPlayersInBounds() {
		PointF p1Vel = p1.getVelocity();
		PointF p2Vel = p2.getVelocity();
		
		
		float p1NX = p1.getX() + p1Vel.x;
		float p1NY = p1.getY() + p1Vel.y;
		
		float p2NX = p2.getX() + p2Vel.x;
		float p2NY = p2.getY() + p2Vel.y;
		
		// Collision with the walls
		if(p1NX <= middleBound-p1.getRadius()-2 && p1NX >= leftBound)
		{
			p1.offsetPosition(p1Vel.x, 0);
		} else {
			if(p1NX <= middleBound-p1.getRadius()-2)
				p1.offsetPosition(leftBound-p1.getX(), 0);
			else
				p1.offsetPosition(middleBound-p1.getRadius()-2-p1.getX(), 0);
		}
		// Floors
		if(p1NY <= bottomBound)
			p1.offsetPosition(0, p1Vel.y);
		else if(p1NY > bottomBound)
			p1.offsetPosition(0, bottomBound-p1.getY());
		
		
		if(p2NX >= middleBound+p2.getRadius()+2 && p2NX <= rightBound) {
			p2.offsetPosition(p2Vel.x, 0);
		} else {
			if(p2NX >= middleBound+p2.getRadius()+2)
				p2.offsetPosition(rightBound-p2.getX(), 0);
			else
				p2.offsetPosition(middleBound+p2.getRadius()+2-p2.getX(), 0);
		}
		
		if(p2NY <= bottomBound)
			p2.offsetPosition(0, p2Vel.y);
		else if(p2NY > bottomBound)
			p2.offsetPosition(0, bottomBound-p2.getY());
	}
	
	private void applyPlayersGravity() {
		if(p1JumpTimer > 0) {
			p1JumpTimer--;
			if(p1JumpTimer < 10)
				setP1VY(-gravity * (.5f));
			else
				setP1VY(-gravity);
		} else if(p1JumpTimer > -15){
			p1JumpTimer--;
			if(p1JumpTimer > -5)
				setP1VY(gravity * (.5f));
			else
				setP1VY(gravity);
		} else {
			setP1VY(0);
		}
		
		if(p2JumpTimer > 0) {
			p2JumpTimer--;
			if(p2JumpTimer < 10)
				setP2VY(-gravity * (.5f));
			else
				setP2VY(-gravity);
		} else if(p2JumpTimer > -15){
			p2JumpTimer--;
			if(p2JumpTimer > -5)
				setP2VY(gravity * (.5f));
			else
				setP2VY(gravity);
		} else {
			setP2VY(0);
		}
	}
	
	public PointF getBallPos() {
		return new PointF(ball.getX(), ball.getY());
	}
	
	public PointF getP2Pos() {
		return new PointF(p2.getX(),p2.getY());
	}
	
	private void applyBallGravity() {
		if(ballTimer > 0) {
			ballTimer--;
			if(ballTimer < 10)
				ball.setVY(-gravity*(.5f));
			else
				ball.setVY(-gravity);
		} else if(ballTimer > -15) {
			ballTimer--;
			if(ballTimer > -5)
				ball.setVY(gravity*(.5f));
			else
				ball.setVY(gravity);
		} else {
			ball.setVY(gravity);
		}
	}
	
	// 100 on no collision
	// <=1 collision within next frame
	private float timeToCollide(RigidBody p) {
		float time = -1;
		
		float pdx = ball.getX()-p.getX();
		float pdy = ball.getY()-p.getY();
		PointF ballV = ball.getVelocity();
		PointF playerV = p.getVelocity();
		float pddx = ballV.x - playerV.x;
		float pddy = ballV.y - playerV.y;
		float radiusSquared = ball.getRadius() + p.getRadius();
		radiusSquared *= radiusSquared;
		
		float a = pddx*pddx + pddy*pddy;
		float b = 2 * ( pdx*pddx + pdy*pddy);
		float c = (pdx*pdx) + (pdy*pdy) - radiusSquared;
		float det =  (b*b) - (4*a*c);
		if(a != 0.) {
			float t = (float)((-b - Math.sqrt(det)) / (2. * a));
			if(t>=0)
			{
				if(t <= 1) {
					ball.offsetPosition(ballV.x*(0.5f*t), ballV.y*(0.5f*t));
				}
				time = t;
			}
		}
		if(time == -1)
			time = 100;
		return time;
	}
	
	private void doBallCollision(RigidBody p) {
        int dx = (int) (2 * (ball.getX() - p.getX()));
        int dy = (int) (ball.getY() - p.getY());
        int dist = (int) Math.sqrt(dx * dx + dy * dy);
        
        float tx = p.getVelocity().x;
       /* if(tx == 0) {
        	if(dx > 0.05) {
        		tx = -playArea.width()/220;
        	} else if(dx < -0.05) {
        		tx = playArea.width()/220;
        	} else {
        		tx = 0;
        	}
        }*/
        
        int dvx = (int) (ball.getVelocity().x - tx);
        
        float ty = p.getVelocity().y;
       /* if(ty == 0)
        	ty = -.5f*gravity;*/
        
        int dvy = (int) (ball.getVelocity().y - ty);
        float ballVX = ball.getVelocity().x;
            int something = (dx * dvx + dy * dvy) / dist;
            // cap the velocity
            if (something <= 0) {
                ballVX += p.getVelocity().x - (2 * dx * something) / dist;
                if (ballVX < -1.8*gravity) {
                    ballVX = (float) (-1.8*gravity);
                }
                if (ballVX > 1.8*gravity) {
                    ballVX = (float) (1.8*gravity);
                }
            }
		
		ball.setVX(ballVX);
		
		float vy = ball.getVelocity().y;
		if(p.getVelocity().y < 0)
			vy += 3*p.getVelocity().y;
		else
			vy = -gravity;
		ball.setVY(vy);
		
		ballTimer = 15;
	}
	
	private void ballCollision() {
		// if collision with moving slime, ballTimer=15 (non-moving = 8), adjust velocities for integration
		// if collision with wall, ballTimer=8 (or something), adjust velocities for integration
		// integrate
		if(!ballIsGrounded()) {
			
			PointF ballV = ball.getVelocity();
			
			if(timeToCollide(p1) <= 1) {
				doBallCollision(p1);
				// TODO: Animate player 1
			} else if(timeToCollide(p2) <= 1) {
				doBallCollision(p2);
				// TODO: Animate player 2
			} else {
				// floors
				// floor
				if (ball.getY()+ballV.y <= playArea.bottom-(2*ball.getRadius())) {
					ball.offsetPosition(0, ballV.y);
				} else {
					ball.offsetPosition(0, playArea.bottom-(2*ball.getRadius())-ball.getY());
				}
			}
			
			// Wall Collision
			if(ball.getX()+ballV.x <= playArea.left+ball.getRadius())
			{
				ball.setVX(-ballV.x);
			} else if (ball.getX()+ballV.x >= playArea.right-ball.getRadius()) {
				ball.setVX(-ballV.x);
			}
			
			// Net collision
			RigidBody net = bodies.get(0);
			
			float bNX = ball.getX()+ballV.x;
			float bNY = ball.getY()+ballV.y+ball.getRadius();
			
			if( (bNY) >= net.getY() && bNX >= net.getX()-2-ball.getRadius() && bNX <= net.getX()+2+ball.getRadius() ) {
				// TODO: Animate net collision
				if(bNX < net.getX()) {
					// hit left
					ball.offsetPosition(net.getX()-ball.getX()-ball.getRadius()-2, 0);
					ball.setVX(-ballV.x);
				} else {
					// hit right
					ball.offsetPosition(net.getX()-ball.getX()+ball.getRadius()+2, 0);
					ball.setVX(-ballV.x);
				}
			} 
			ball.offsetPosition(ball.getVelocity().x, 0);
		} else {
			// Ball on the ground.
			// TODO: Update score
			// Reset match.
			ball.offsetPosition(playArea.width()/4-ball.getX(), -300);
			ball.setVX(0);
			
			p1JumpTimer = -16;
			p2JumpTimer = -16;
			
			p1.resetPos();
			p2.resetPos();
		}
	}
	
	public void integrate(float dt) {
		// apply ball gravity
		applyBallGravity();
		// check ball collision, adjust velocity, integrate
		ballCollision();
		
		applyPlayersGravity();
		keepPlayersInBounds();
		
		s_p1.update();
		s_p2.update();
		s_ball.update();
	}
}