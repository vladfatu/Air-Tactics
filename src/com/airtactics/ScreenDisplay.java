package com.airtactics;

public class ScreenDisplay {
	public static Panel inGamePanel, playPanel, inGamePanel2;
	public static float screenWidth;
	public static float screenHeight;
	public static float density;
	public static void setDensity()
	{
		density = screenHeight / 480;
	}
}
