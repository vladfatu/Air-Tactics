package com.airtactics;

import java.util.ArrayList;
import java.util.List;

public class Plane {
	List<Point> points = new ArrayList<Point>();
	Point head;
	int numberOfPoints;
	
	Plane()
	{
		numberOfPoints = 0;
		head = new Point();
	}
	public void setPoints(List<Point> p)
	{
		points = p;
	}
	public boolean addPoint(Point p)
	{
		Point temp = new Point(p.x,p.y);
		if (numberOfPoints < 7)
		{
			points.add(temp);
			return true;
		}
		else
			return false;
	}
	public boolean checkPoint(Point p)
	{
		if (head.equals(p)) return true;
		for (int i=0;i<points.size();i++)
			if (p.equals(points.get(i))) return true;
		return false;
	}
	public void setHead(Point p)
	{
		head.x = p.x;
		head.y = p.y;
	}
	public Point getHead()
	{
		return head;
	}
	//rotate the plane 180 degrees around the point p if the plane is directed to the right
	public Plane rotate1to3(Point p)
	{
		Plane temp = new Plane();
		Point tempPoint = new Point();
		for (int i=0; i<points.size();i++)
		{
			tempPoint.x = 2 * p.x - points.get(i).x;
			tempPoint.y = points.get(i).y;
			temp.addPoint(tempPoint);
		}
		tempPoint.x = 2 * p.x - head.x;
		tempPoint.y = head.y;
		temp.setHead(tempPoint);
		return temp;
	}
	//rotate the plane 180 degrees around the point p if the plane is directed up
	public Plane rotate0to2(Point p)
	{
		Plane temp = new Plane();
		Point tempPoint = new Point();
		for (int i=0; i<points.size();i++)
		{
			tempPoint.y = 2 * p.y - points.get(i).y;
			tempPoint.x = points.get(i).x;
			temp.addPoint(tempPoint);
		}
		tempPoint.y = 2 * p.y - head.y;
		tempPoint.x = head.x;
		temp.setHead(tempPoint);
		return temp;
	}
	
	public Plane shift(int i)
	{
		switch(i)
		{
			case 0 : shiftUp(); break;
			case 1 : shiftRight(); break;
			case 2 : shiftDown(); break;
			case 3 : shiftLeft(); 
		}
		return this;
	}
	
	public Plane shiftUp()
	{
		Plane temp = new Plane();
		Point tempPoint = new Point();
		for (int i=0; i<points.size();i++)
		{
			tempPoint.y = points.get(i).y-1;
			tempPoint.x = points.get(i).x;
			temp.addPoint(tempPoint);
		}
		tempPoint.y = head.y-1;
		tempPoint.x = head.x;
		temp.setHead(tempPoint);
		return temp;
	}
	
	public Plane shiftDown()
	{
		Plane temp = new Plane();
		Point tempPoint = new Point();
		for (int i=0; i<points.size();i++)
		{
			tempPoint.y = points.get(i).y+1;
			tempPoint.x = points.get(i).x;
			temp.addPoint(tempPoint);
		}
		tempPoint.y = head.y+1;
		tempPoint.x = head.x;
		temp.setHead(tempPoint);
		return temp;
	}
	
	public Boolean equals(Plane p)
	{
		if (!head.equals(p.head)) return false;
		for (int i=0;i<points.size();i++)
			if (!p.checkPoint(points.get(i))) return false;
		return true;
	}
	
	public Plane shiftLeft()
	{
		Plane temp = new Plane();
		Point tempPoint = new Point();
		for (int i=0; i<points.size();i++)
		{
			tempPoint.y = points.get(i).y;
			tempPoint.x = points.get(i).x-1;
			temp.addPoint(tempPoint);
		}
		tempPoint.y = head.y;
		tempPoint.x = head.x-1;
		temp.setHead(tempPoint);
		return temp;
	}
	
	public Plane shiftRight()
	{
		Plane temp = new Plane();
		Point tempPoint = new Point();
		for (int i=0; i<points.size();i++)
		{
			tempPoint.y = points.get(i).y;
			tempPoint.x = points.get(i).x+1;
			temp.addPoint(tempPoint);
		}
		tempPoint.y = head.y;
		tempPoint.x = head.x+1;
		temp.setHead(tempPoint);
		return temp;
	}
	
	public Boolean checkPlane()
	{
		if (head.x < 0 || head.x > 9 || head.y < 0 || head.y > 9 || Opponent.checkPoint(head))
			return false;
		for (int i=0;i<points.size();i++)
			if (points.get(i).x < 0 || points.get(i).x > 9 || points.get(i).y < 0 || points.get(i).y > 9 || Opponent.checkPoint(points.get(i)))
				return false;
		return true;
	}

}
