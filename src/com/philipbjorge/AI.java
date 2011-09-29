package com.philipbjorge;

import android.graphics.PointF;

public class AI {
	PointF ball;
	PointF self;
	int half;
	
	public AI(int half, PointF ballPos, PointF self) {
		ball =  ballPos;
		this.self = self;
		this.half = half;
	}
	
	public void update(PointF b, PointF s) {
		ball = b;
		self = s;
	}
	
	public char move() {
		// return left,right,up,upleft=q,upright=p
		if(ball.x >= self.x) {
			if(ball.y < self.y-55)
				return 'p';
			else
				return 'r';
		} else if (ball.x >= half){
			if(ball.y < self.y-55)
				return 'q';
			else
				return 'l';
		} else {
			if(self.x < 1.3*half)
				return 'r';
			else if(self.x > 1.7*half)
				return 'l';
			else
				return 'u';
		}
	}
}
