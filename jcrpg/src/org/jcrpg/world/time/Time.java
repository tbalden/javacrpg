package org.jcrpg.world.time;

public abstract class Time {

	int year, day, hour, minute, second;
	
	int maxDay = 400, maxHour = 20;
	
	public int getCurrentDayPercent()
	{
		return  (hour/maxHour)*100;
	}

	public int getCurrentYearPercent()
	{
		return  (day/maxDay)*100;
	}

}
