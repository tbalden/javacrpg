/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	public void setTime(Time time)
	{
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
		return ( (hour*(maxMinute+1)*(maxSecond+1) + minute*(maxSecond+1) + second) / ((maxHour+1)*(maxMinute+1)*(maxSecond+1)*1f) ) * 100f;
	}

	public int getCurrentYearPercent()
	{
		return  (int)((day/((maxDay+1)*1f))*100);
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
			// sun doesnt go east-west, but west-east, worldX will be reversed for calculation
			worldX = worldX - realSize;
		}
		// position in west-east direction gives a percentage which determines what hour it is there from 0 hour
		int hourPlus = (worldX/realSize) * (maxHour+1); 
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
			// we must check for which hemisphere we are on, north/south?
			if (worldZ> world.sizeZ*world.magnification / 2) 
				r.inverseSeasons = true; // we ar on south, inverse seasons
			else
				r.inverseSeasons = false;
		}
		return r;
	}
	
	public int getTimeInInt()
	{
		int res = year*maxDay*maxHour*maxMinute*maxSecond;
		res+=day*maxHour*maxMinute*maxSecond;
		res+=hour*maxMinute*maxSecond;
		res+=minute*maxSecond;
		res+=second;
		return res;
	}
	
	public int diffSeconds(Time t)
	{
		return t.getTimeInInt()-getTimeInInt();
	}

}
