package com.airtactics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Opponent {
	
	private Random mRnd;
	public static int tileMatrix[][], AI;
	public static String internetName;
	List<Plane> planes = new ArrayList<Plane>();
	List<Plane> planes2 = new ArrayList<Plane>();
	List<Plane> planes3 = new ArrayList<Plane>();
	List<Head> heads = new ArrayList<Head>();
	public static List<Point> points = new ArrayList<Point>();
	public static int yourMatrix[][];
	Opponent(int AI1)
	{
		mRnd = new Random();
		AI = AI1;
		//this will initialize the matrix of the opponent
		tileMatrix = new int[10][];
    	for (int i=0;i<10;i++)
    		tileMatrix[i]=new int[10];
    	
    	for (int i=0;i<10;i++)
    	{
    		for (int j=0;j<10;j++)
    			tileMatrix[i][j] = 0;
    	}
    	
    	//this will initialize a version of your matrix on the opponent(it will have only "0")
    	//the actual initialization will be done in the "Air" activity
    	yourMatrix = new int[10][];
    	for (int i=0;i<10;i++)
    		yourMatrix[i]=new int[10];
    	
    	for (int i=0;i<10;i++)
    	{
    		for (int j=0;j<10;j++)
    			yourMatrix[i][j] = 0;
    	}
    	if (AI !=0) initMatrix();
	}
	
	public Point shoot()
	{
		
		Point p = new Point();
		int tempX=0, tempY=0, temp=0;
		Boolean ok = true, ok1;
		if (AI == 1)
		{
			while (ok)
	    	{
				tempX = mRnd.nextInt(10);
				tempY = mRnd.nextInt(10);
	        	if (yourMatrix[tempX][tempY] == 0) ok = false;
	    	}
		}
		else if (AI == 2)
		{
			/*if (unhitPlanesExist())
			{
				p = getUnhitPlane().points.get(3);
				
			}
			else*/ if (planes.size() == 0)
			{
				ok=true;
				while (ok)
		    	{
					p.x = mRnd.nextInt(10);
					p.y = mRnd.nextInt(10);
		        	if (yourMatrix[p.x][p.y] == 0) ok = false;
		    	}
			}
			else
			{
				ok=true;
				temp = 0;
				while (ok)
		    	{
					temp++;
					if (temp > 10000) 
					{
						ok1=true;
						while (ok1)
				    	{
							p.x = mRnd.nextInt(10);
							p.y = mRnd.nextInt(10);
				        	if (yourMatrix[p.x][p.y] == 0) ok1 = false;
				    	}
						ok = false;
					}
					else if (planes3.size() != 0) p = planes3.get(mRnd.nextInt(planes3.size())).getHead();
					else if (planes2.size() != 0) p = planes2.get(mRnd.nextInt(planes2.size())).getHead();
					else p = planes.get(mRnd.nextInt(planes.size())).getHead();
					if (yourMatrix[p.x][p.y] == 0) 
					{		
						ok = false;
					}
					else if (yourMatrix[p.x][p.y] == -1 || yourMatrix[p.x][p.y] == 2) 
					{
						removePlanesWithPoint(p);
						if (planes.size() == 0)
						{
							ok1=true;
							while (ok1)
					    	{
								p.x = mRnd.nextInt(10);
								p.y = mRnd.nextInt(10);
					        	if (yourMatrix[p.x][p.y] == 0) ok1 = false;
					    	}
							ok = false;
						}
					}
		    	}
			}
			/*int k=0;
			k = mRnd.nextInt(2);
			if (k == 0) p.x = mRnd.nextInt(10);
			else p.x = mRnd.nextInt(6)+2;
			k = mRnd.nextInt(2);
			if (k == 0) p.y = mRnd.nextInt(10);
			else p.y = mRnd.nextInt(6)+2;*/
			tempX = p.x;
			tempY = p.y;
			
		}
		p.x = tempX;
		p.y = tempY;
		
		
		//temp = new Point(tempX, tempY);
		//removePlanesWithPoint(temp);
		return p;
	}
	
	public static Boolean checkPoint(Point p)
	{
		for (int i=0;i<points.size();i++)
			if (p.equals(points.get(i)) && (yourMatrix[p.x][p.y]==-1 || yourMatrix[p.x][p.y]==2)) return true;
		return false;
	}
	
	public Boolean unhitPlanesExist()
	{
		for (int i=0;i<heads.size();i++)
			if (heads.get(i).unhitPlanes.size()>0) return true;
		return false;
	}
	
	public Plane getUnhitPlane()
	{
		for (int i=0;i<heads.size();i++)
			if (heads.get(i).unhitPlanes.size()>0) return heads.get(i).unhitPlanes.get(0);
		return null;
	}
	
	public void verifyHeads()
	{
		for (int i=0;i<heads.size();i++)
		{
			heads.get(i).verifyPlanes();
			if (heads.get(i).hitPlanes.size() == 1 && heads.get(i).unhitPlanes.size()==0)
			{
				markPlane(heads.get(i).hitPlanes.get(0));
				heads.remove(heads.get(i));
				verifyHeads();
				i--;
			}
		}
		
	}
	
	public void markPlane(Plane p)
	{
		for (int i=0;i<p.points.size();i++)
		{
			yourMatrix[p.points.get(i).x][p.points.get(i).y] = -1;
		}
		
		
	}
	
	public void resetPosiblePlanes(Point p)
	{
		Head head = new Head(p.x, p.y);
		head.generatePlanes();
		
		heads.add(head);
		verifyHeads();
		
		verifyPlanes();
	}
	
	public void verifyPlanes()
	{
		for (int i=0;i<planes3.size();i++)
			if (!planes3.get(i).checkPlane()) 
			{
				planes3.remove(i);
				i--;
			}
		for (int i=0;i<planes2.size();i++)
			if (!planes2.get(i).checkPlane()) 
			{
				planes2.remove(i);
				i--;
			}
		for (int i=0;i<planes.size();i++)
			if (!planes.get(i).checkPlane()) 
			{
				planes.remove(i);
				i--;
			}
			
	}
	
	public void removePlanesWithPoint(Point p)
	{
		Point temp = new Point(p.x, p.y);
		points.add(temp);
		
		for (int i=0;i<planes.size();i++)
		{
			if (planes.get(i).checkPoint(p)) 
			{
				planes.remove(planes.get(i));
				i--;
			}
		}
		for (int i=0;i<planes2.size();i++)
		{
			if (planes2.get(i).checkPoint(p)) 
			{
				planes2.remove(planes2.get(i));
				i--;
			}
		}
		for (int i=0;i<planes3.size();i++)
		{
			if (planes3.get(i).checkPoint(p)) 
			{
				planes3.remove(planes3.get(i));
				i--;
			}
		}
		verifyHeads();
		
	}
	
	public void setPossiblePlanes(Point p)
	{
		if (AI == 2)
		{
			if (yourMatrix[p.x][p.y] == 1) 
				setPlanesAroundPoint(p);
		}
		Point temp = new Point(p.x, p.y);
		points.add(temp);
		
		verifyHeads();
	}
	
	public void initMatrix()
	{
		///////////////////////////////////
		Boolean ok;
		int y,x,k;
		for (int i=0;i<3;i++)
		{
			ok=false;
			while (!ok)
			{
				ok=true;
				k=0;
				if (AI == 2)  k= mRnd.nextInt(4);
				
		    	if (k%2==0) 
		    	{
		    		y = mRnd.nextInt(7)+1;
					x = mRnd.nextInt(8)+1;
		    	}
		    	else
		    	{
		    		y = mRnd.nextInt(8)+1;
					x = mRnd.nextInt(7)+1;
		    	}
		    	switch(k)
				{
					case 0:
					{
						if (tileMatrix[y][x]==0 && tileMatrix[y][x-1]==0 && tileMatrix[y][x+1]==0 
								&& tileMatrix[y+1][x]==0 && tileMatrix[y+2][x]==0
								&& tileMatrix[y+2][x-1]==0 && tileMatrix[y+2][x+1]==0
								&& tileMatrix[y-1][x]==0) 
						{	
							tileMatrix[y][x]=1; 
							tileMatrix[y][x-1]=1;
							tileMatrix[y][x+1]=1;
							tileMatrix[y+1][x]=1;
							tileMatrix[y+2][x]=1;
							tileMatrix[y+2][x-1]=1;
							tileMatrix[y+2][x+1]=1;
							tileMatrix[y-1][x]=2;
						}
						else
						{
							ok = false;
							
						}
						break;
					}
					case 1:
					{
						if (tileMatrix[y-1][x-1]==0 && tileMatrix[y-1][x+1]==0 && tileMatrix[y][x]==0 
								&& tileMatrix[y][x-1]==0 && tileMatrix[y][x+1]==0 && tileMatrix[y+1][x-1]==0 
								&& tileMatrix[y+1][x+1]==0 && tileMatrix[y][x+2]==0) 
						{
							tileMatrix[y-1][x-1]=1;
							tileMatrix[y-1][x+1]=1;
							tileMatrix[y][x]=1;
							tileMatrix[y][x-1]=1;
							tileMatrix[y][x+1]=1;
							tileMatrix[y+1][x-1]=1;
							tileMatrix[y+1][x+1]=1;
							tileMatrix[y][x+2]=2;
						}
						 
						else
						{
							ok = false;
							
						}
						break;
					}
					case 2:
					{
						if (tileMatrix[y-1][x]==0 && tileMatrix[y-1][x-1]==0 && tileMatrix[y-1][x+1]==0  
								&& tileMatrix[y][x]==0 && tileMatrix[y+1][x]==0 && tileMatrix[y+1][x-1]==0 
								&& tileMatrix[y+1][x+1]==0 && tileMatrix[y+2][x]==0 ) 
						{
							tileMatrix[y-1][x]=1;
							tileMatrix[y-1][x-1]=1;
							tileMatrix[y-1][x+1]=1;
							tileMatrix[y][x]=1;
							tileMatrix[y+1][x]=1;
							tileMatrix[y+1][x-1]=1;
							tileMatrix[y+1][x+1]=1;
							tileMatrix[y+2][x]=2;
							
						} 
						else
						{
							ok = false;
							
						}
						break;
					}
					case 3:
					{
						if (tileMatrix[y-1][x]==0 && tileMatrix[y-1][x+2]==0 && tileMatrix[y][x]==0  
								&& tileMatrix[y][x+1]==0 && tileMatrix[y][x+2]==0 && tileMatrix[y+1][x]==0 
								&& tileMatrix[y+1][x+2]==0 && tileMatrix[y][x-1]==0) 
						{
							tileMatrix[y-1][x]=1;
							tileMatrix[y-1][x+2]=1;
							tileMatrix[y][x]=1;
							tileMatrix[y][x+1]=1;
							tileMatrix[y][x+2]=1;
							tileMatrix[y+1][x]=1;
							tileMatrix[y+1][x+2]=1;
							tileMatrix[y][x-1]=2;
							
						} 
						else
						{
							ok = false;
							
						}
					}
					
				}
			}
		}
		
		/////////////////////////////////
	}
	
	//generates all the possible planes that include the point p
	public void setPlanesAroundPoint(Point p)
	{
		Plane temp, temp1;
		Point tempPoint = new Point();
		
		//first set of 4 planes
		temp = new Plane();
		temp.addPoint(p);
		
		tempPoint.x = p.x+1;
		tempPoint.y = p.y;
		temp.addPoint(tempPoint);
		
		tempPoint.x = p.x+2;
		tempPoint.y = p.y;
		temp.addPoint(tempPoint);
		
		tempPoint.x = p.x+1;
		tempPoint.y = p.y-1;
		temp.addPoint(tempPoint);
		
		tempPoint.x = p.x;
		tempPoint.y = p.y-2;
		temp.addPoint(tempPoint);
		
		tempPoint.x = p.x+1;
		tempPoint.y = p.y-2;
		temp.addPoint(tempPoint);
		
		tempPoint.x = p.x+2;
		tempPoint.y = p.y-2;
		temp.addPoint(tempPoint);
		
		tempPoint.x = p.x+1;
		tempPoint.y = p.y-3;
		temp.setHead(tempPoint);
		addToPlanes(temp);
		addToPlanes(temp.rotate0to2(p));
		
		
		//////////////////////////////////////////////////////////////////
		
		temp1 = new Plane();
		temp1.addPoint(p);
		
		tempPoint.x = p.x;
		tempPoint.y = p.y+1;
		temp1.addPoint(tempPoint);
		
		tempPoint.x = p.x;
		tempPoint.y = p.y+2;
		temp1.addPoint(tempPoint);
		
		tempPoint.x = p.x+1;
		tempPoint.y = p.y+1;
		temp1.addPoint(tempPoint);
		
		tempPoint.x = p.x+2;
		tempPoint.y = p.y;
		temp1.addPoint(tempPoint);
		
		tempPoint.x = p.x+2;
		tempPoint.y = p.y+1;
		temp1.addPoint(tempPoint);
		
		tempPoint.x = p.x+2;
		tempPoint.y = p.y+2;
		temp1.addPoint(tempPoint);
		
		tempPoint.x = p.x+3;
		tempPoint.y = p.y+1;
		temp1.setHead(tempPoint);
		addToPlanes(temp1);
		addToPlanes(temp1.rotate1to3(p));
		
		restOfPlanes(0, temp, p);
		restOfPlanes(1, temp1, p);
		
	}
	
	public void restOfPlanes(int i, Plane plane, Point p)
	{
		if (i == 0)
		{
			plane = plane.shiftLeft();
			addToPlanes(plane);
			addToPlanes(plane.rotate0to2(p));
			
			plane = plane.shiftLeft();
			addToPlanes(plane);
			addToPlanes(plane.rotate0to2(p));
			
			plane = plane.shiftRight();
			plane = plane.shiftDown();
			addToPlanes(plane);
			addToPlanes(plane.rotate0to2(p));
			
			plane = plane.shiftDown();
			addToPlanes(plane);
			addToPlanes(plane.rotate0to2(p));
			
			plane = plane.shiftLeft();
			addToPlanes(plane);
			addToPlanes(plane.rotate0to2(p));
			
			plane = plane.shiftRight();
			plane = plane.shiftRight();
			addToPlanes(plane);
			addToPlanes(plane.rotate0to2(p));
			
			
		}
		else if (i == 1)
		{
			plane = plane.shiftUp();
			addToPlanes(plane);
			addToPlanes(plane.rotate1to3(p));
			
			plane = plane.shiftUp();
			addToPlanes(plane);
			addToPlanes(plane.rotate1to3(p));
			
			plane = plane.shiftDown();
			plane = plane.shiftLeft();
			addToPlanes(plane);
			addToPlanes(plane.rotate1to3(p));
			
			plane = plane.shiftLeft();
			addToPlanes(plane);
			addToPlanes(plane.rotate1to3(p));
			
			plane = plane.shiftUp();
			addToPlanes(plane);
			addToPlanes(plane.rotate1to3(p));
			
			plane = plane.shiftDown();
			plane = plane.shiftDown();
			addToPlanes(plane);
			addToPlanes(plane.rotate1to3(p));
			
		}
	}
	
	public void addToPlanes(Plane p)
	{
		if (p.checkPlane())
		{
			if (checkPlane(planes2, p)) 
			{
				planes3.add(p);
				planes2.remove(getPlaneEqual(planes2, p));
			}
			else if (checkPlane(planes, p))
			{
				planes2.add(p);
				planes.remove(getPlaneEqual(planes, p));
			}
			else if (!checkPlane(planes3, p))
			{
				planes.add(p);
			}
		}
	}
	
	public Boolean checkPlane(List<Plane> tempPlanes, Plane tempPlane)
	{
		for (int i=0;i<tempPlanes.size();i++)
		{
			if (tempPlanes.get(i).equals(tempPlane)) return true;
		}
		return false;
	}
	
	public Plane getPlaneEqual(List<Plane> tempPlanes, Plane tempPlane)
	{
		for (int i=0;i<tempPlanes.size();i++)
		{
			if (tempPlanes.get(i).equals(tempPlane)) return tempPlanes.get(i);
		}
		return null;
	}
	
	public List<Plane> getPlanes()
	{
		return planes;
	}
	
	public int getPosition(int x, int y)
	{
		return tileMatrix[x][y];
	}
	
	public void setAI(int AI1)
	{
		AI = AI1;
	}
	public int getAI()
	{
		return AI;
	}

}
