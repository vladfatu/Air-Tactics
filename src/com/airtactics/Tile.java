package com.airtactics;

public class Tile {
	public int value,i,j;
	public boolean hit,selected,visible;
	public Sprite s;
	Tile(int i1, int j1)
	{
		i=i1;j=j1;
		//visible is just for right or wrong, not or selected 
		visible = false;
		value=0;
		hit = false;
		selected = false;
	}
}
