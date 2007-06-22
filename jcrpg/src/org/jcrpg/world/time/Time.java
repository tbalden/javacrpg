package org.jcrpg.world.time;

public class Time {

	int year, day, hour, minute, second;
	
	/**
	 * On northern hemisphere this is false, on southern should be true!
	 */
	public boolean inverseSeasons; 
	
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
