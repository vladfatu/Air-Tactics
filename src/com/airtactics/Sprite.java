package com.airtactics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Sprite {
	Bitmap image;
	Panel panel;
	float left,top,width,height;
	int rotation;
	public Sprite(Resources r, int id, Panel tempPanel)
	{
		image = BitmapFactory.decodeResource(r, id);
		height = image.getHeight();
		width = image.getWidth();
		left = -width/2;
		top = -height/2;
		panel = tempPanel;
	}
	public void setPanel(Panel tempPanel)
	{
		panel = tempPanel;
	}
	public void setImage(Resources r, int id)
	{
		image = BitmapFactory.decodeResource(r, id);
		if (rotation != 0) 
		{
			Matrix m = new Matrix();
			m.postRotate(rotation);
			int w=(int)image.getWidth(),h=(int)image.getHeight();
			image = Bitmap.createBitmap(image, 0, 0, w, h, m, true);
			//width = image.getWidth();
			//height = image.getHeight();
		}
		float tempX, tempY;
		tempX = getLeft();
		tempY = getTop();
		if (height != image.getHeight() || width != image.getWidth())
		{
			height = image.getHeight();
			width = image.getWidth();
			setPosition(tempX, tempY);
		}
		
		panel.invalidate();
	}
	public void draw(Canvas canvas)
	{
		Paint paint = new Paint();
		canvas.drawBitmap(image, left, top, paint);
	}
	public void setLeft(float i)
	{
		if (left!=i-width/2) 
		{
			left = i-width/2;
			panel.invalidate();
		}
	}
	public void setTop(float i)
	{
		if (top!=i-height/2) 
		{
			top = i-height/2;
			panel.invalidate();
		}
	}
	public boolean touched(int tempX, int tempY)
	{
		if (left < tempX && left + width > tempX && top < tempY && top + height > tempY) 
			return true;
		return false;
	}
	public float getLeft()
	{
		return left+width/2;
	}
	public float getTop()
	{
		return top+height/2;
	}
	public void setPosition(float x, float y)
	{
		if (left != x-width/2 || top != y-height/2)
		{
			left = x-width/2;
			top = y-height/2;
			panel.invalidate();
		}
	}
	public void rotate(int r)
	{
		rotation = (rotation + r) % 360;
		Matrix m = new Matrix();
		m.postRotate(r);
		int w=(int)width,h=(int)height;
		image = Bitmap.createBitmap(image, 0, 0, w, h, m, true);
		width = image.getWidth();
		height = image.getHeight();
		panel.invalidate();
	}
	
}
