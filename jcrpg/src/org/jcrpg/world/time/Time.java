package org.jcrpg.world.time;

import org.jcrpg.world.place.World;

public class Time {

	public int year, day, hour, minute, second;
	
	/**
	 * On northern hemisphere this is false, on southern should be true!
	 */
	public boolean inverseSeasons; 
	
	public int maxDay = 400, maxHour = 23, maxMinute = 59, maxSecond = 59;
	
	public Time()
	{
		
	}
	
	public Time(Time time) {
		super();
		this.year = time.year;
		this.day = time.day;
		this.hour = time.hour;
		this.minute = time.minute;
		this.second = time.second;
		maxDay = time.maxDay;
		maxHour = time.maxHour;
		maxMinute = time.maxMinute;
		maxSecond = time.maxSecond;
		inverseSeasons = time.inverseSeasons;
	}

	public float getCurrentDayPercent()
	{
		return  ((hour/(maxHour*1f))*100);
	}

	public int getCurrentYearPercent()
	{
		return  (int)((day/(maxDay*1f))*100);
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public boolean isInverseSeasons() {
		return inverseSeasons;
	}

	public void setInverseSeasons(boolean inverseSeasons) {
		this.inverseSeasons = inverseSeasons;
	}

	public int getMaxDay() {
		return maxDay;
	}

	public void setMaxDay(int maxDay) {
		this.maxDay = maxDay;
	}

	public int getMaxHour() {
		return maxHour;
	}

	public void setMaxHour(int maxHour) {
		this.maxHour = maxHour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public void tick(){
		tick(1);
	}
	
	public void tick(int seconds)
	{
		for (int i=0; i<seconds; i++) {
			second++;
			if (second>maxSecond)
			{
				second = 0;
				minute++;
				if (minute>maxMinute)
				{
					minute = 0;
					hour++;
					if (hour>maxHour)
					{
						hour = 0;
						day++;
						if (day>maxDay)
						{
							day = 0;
							year++;
						}
					}
				}
			}
		}
	}
	
	public String toString()
	{
		return "TIME: "+year+" , "+day+" , "+hour+" : "+ minute + " : "+second;
	}
	
	public Time getLocalTime(World world, int worldX, int worldY, int worldZ)
	{
		int realSize = world.sizeX*world.magnification;
		if (!world.sunLikeOnEarth) {
			worldX = worldX - realSize;
		}
		int hourPlus = (worldX/realSize) * maxHour;
		Time r = new Time(this);
		r.hour+=hourPlus;
		if (r.hour>r.maxHour)
		{
			r.hour = r.hour%(r.maxHour+1);
			r.day++;
			if (r.day>r.maxDay)
			{
				r.day=0;
				r.year++;
			}
		}
		if (world.timeSwitchOnEquator)
		{
			if (worldZ> world.sizeZ*world.magnification / 2) 
				r.inverseSeasons = true;
			else
				r.inverseSeasons = false;
		}
		return r;
		
		
	}

}
