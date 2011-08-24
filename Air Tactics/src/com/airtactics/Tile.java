package com.airtactics;

public class Tile {
	int value,i,j;
	boolean hit,selected,visible;
	Sprite s;
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
