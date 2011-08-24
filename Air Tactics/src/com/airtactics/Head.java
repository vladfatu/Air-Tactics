package com.airtactics;

import java.util.ArrayList;
import java.util.List;

public class Head {
	
	Point head;
	
	List<Plane> hitPlanes;
	List<Plane> unhitPlanes;
	
	public Head(int x, int y)
	{
		head = new Point(x, y);
		hitPlanes = new ArrayList<Plane>();
		unhitPlanes = new ArrayList<Plane>();
		
	}
	
	public int verifyPlane(Plane plane)
	{
		Boolean hit = false;
		for (int i=0;i<plane.points.size();i++)
		{
			for (int j=0;j<Opponent.points.size();j++)
			{
				if (Opponent.points.get(j).equals(plane.points.get(i)))
				{
					if (Opponent.yourMatrix[Opponent.points.get(j).x][Opponent.points.get(j).y]==1) hit = true;
					else return -1;
				}
			}
		}
		if (hit) return 1;
		else return 0;
	}
	
	public void verifyPlanes()
	{
		for (int i=0;i<unhitPlanes.size();i++)
		{
			switch (verifyPlane(unhitPlanes.get(i)))
			{
				case -1 :
					unhitPlanes.remove(unhitPlanes.get(i));
					i--;
					break;
				case 1:
					hitPlanes.add(unhitPlanes.get(i));
					unhitPlanes.remove(unhitPlanes.get(i));
					i--;
			}
		}
		if (unhitPlanes.size() == 1 && hitPlanes.size() == 0)
		{
			hitPlanes.add(unhitPlanes.get(0));
			unhitPlanes.remove(0);
		}
	}
	
	public void generatePlanes()
	{
		Plane plane;
		
		
		if (head.y>2 && head.x>0 && head.x<9)
		{
			plane = new Plane();
			plane.setHead(new Point(head.x, head.y));
			plane.addPoint(new Point(head.x-1, head.y-1));
			plane.addPoint(new Point(head.x, head.y-1));
			plane.addPoint(new Point(head.x+1, head.y-1));
			plane.addPoint(new Point(head.x, head.y-2));
			plane.addPoint(new Point(head.x-1, head.y-3));
			plane.addPoint(new Point(head.x, head.y-3));
			plane.addPoint(new Point(head.x+1, head.y-3));
			
			unhitPlanes.add(plane);
		}
		if (head.y<7 && head.x>0 && head.x<9)
		{
			plane = new Plane();
			plane.setHead(new Point(head.x, head.y));
			plane.addPoint(new Point(head.x-1, head.y+1));
			plane.addPoint(new Point(head.x, head.y+1));
			plane.addPoint(new Point(head.x+1, head.y+1));
			plane.addPoint(new Point(head.x, head.y+2));
			plane.addPoint(new Point(head.x-1, head.y+3));
			plane.addPoint(new Point(head.x, head.y+3));
			plane.addPoint(new Point(head.x+1, head.y+3));
			
			unhitPlanes.add(plane);
		}
		if (head.x>2 && head.y>0 && head.y<9)
		{
			plane = new Plane();
			plane.setHead(new Point(head.x, head.y));
			plane.addPoint(new Point(head.x-1, head.y-1));
			plane.addPoint(new Point(head.x-1, head.y));
			plane.addPoint(new Point(head.x-1, head.y+1));
			plane.addPoint(new Point(head.x-2, head.y));
			plane.addPoint(new Point(head.x-3, head.y-1));
			plane.addPoint(new Point(head.x-3, head.y));
			plane.addPoint(new Point(head.x-3, head.y+1));
			
			unhitPlanes.add(plane);
		}
		if (head.x<7 && head.y>0 && head.y<9)
		{
			plane = new Plane();
			plane.setHead(new Point(head.x, head.y));
			plane.addPoint(new Point(head.x+1, head.y-1));
			plane.addPoint(new Point(head.x+1, head.y));
			plane.addPoint(new Point(head.x+1, head.y+1));
			plane.addPoint(new Point(head.x+2, head.y));
			plane.addPoint(new Point(head.x+3, head.y-1));
			plane.addPoint(new Point(head.x+3, head.y));
			plane.addPoint(new Point(head.x+3, head.y+1));
			
			unhitPlanes.add(plane);
		}
		verifyPlanes();
	}

}
