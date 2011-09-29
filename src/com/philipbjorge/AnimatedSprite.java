package com.philipbjorge;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class AnimatedSprite {
	int currentFrame;
	int frequency;
	int count;
	int maxFrame;
	
	ArrayList<Bitmap> frames;
	
	Bitmap current;
	boolean isStatic;
	
	public AnimatedSprite(int rID, int frameWidth, int frameHeight, float w, float h, int frameCount, int frequency, Resources r, boolean isS) {
		count = 0;
		currentFrame = 0;
		this.frequency = frequency;
		isStatic = isS;
		
		//int frameWidth = (int) drawFrame.width();
		frames = new ArrayList<Bitmap>();
		for(int i = 0; i < frameCount; i++) {
			Bitmap b = Bitmap.createBitmap(BitmapFactory.decodeResource(r, rID), (i*frameWidth), 0, frameWidth, frameHeight);
			frames.add(Bitmap.createScaledBitmap(b, (int)w, (int)h, true));
		}
		maxFrame = frames.size();
		current = frames.get(0);
	}
	
	public void update() {
		if(!isStatic) {
			if(count > 59)
				count = 0;
			if(currentFrame == maxFrame)
				currentFrame = 0;
			
			if(count%frequency == 0) {
				current = frames.get(currentFrame);
				currentFrame++;
			}
			
			count++;
		}
	}
	
	public Bitmap getBitmap() {
		return current;
	}
}