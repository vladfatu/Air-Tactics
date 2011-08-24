package com.airtactics;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

class Panel extends View {
	//public Bitmap bitmaps[];
	ArrayList<Sprite> spritesList = new ArrayList<Sprite>();
	ArrayList<Label> labelsList = new ArrayList<Label>();
    public Panel(Context context) {
        super(context);
    }
    public void addSprite(Sprite sprite)
    {
    	spritesList.add(sprite);
    	this.invalidate();
    }
    public void removeSprite(Sprite sprite)
    {
    	spritesList.remove(sprite);
    	this.invalidate();
    }
    public void addLabel(Label label)
    {
    	labelsList.add(label);
    	this.invalidate();
    }
    public void removeLabel(Label label)
    {
    	labelsList.remove(label);
    	this.invalidate();
    }
    public void removeAll()
    {
    	labelsList.removeAll(labelsList);
    	spritesList.removeAll(spritesList);
    }
    public void onDraw(Canvas canvas) {
    	Paint paint = new Paint();
    	for (int i=0;i<spritesList.size();i++)
    	{
    		canvas.drawBitmap(spritesList.get(i).image, spritesList.get(i).left, spritesList.get(i).top, paint);
    	}
    	for (int i=0;i<labelsList.size();i++)
    	{
    		canvas.drawText(labelsList.get(i).text, labelsList.get(i).position.x, labelsList.get(i).position.y, labelsList.get(i).paint);
    	}
    	
    	//background.draw(canvas);
    	//tile.draw(canvas);
    	//plane.draw(canvas);
    }
}
