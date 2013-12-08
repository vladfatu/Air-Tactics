package com.airtactics;

public class Point {
	
	public int x,y;

	Point()
	{
		x = 0;
		y = 0;
	}
	Point(int x1, int y1)
	{
		x = x1;
		y = y1;
	}
	public Boolean equals(Point p)
	{
		if (x == p.x && y == p.y) return true;
		else return false;
	}
}
