/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.jcrpg.world.climate;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.world.climate.impl.generic.Day;
import org.jcrpg.world.climate.impl.generic.Night;
import org.jcrpg.world.time.Time;

public class Season implements ConditionGiver {

	public String STATIC_ID = Season.class.getCanonicalName();
	
	public ArrayList<Condition> conditions = new ArrayList<Condition>();
	
	/**
	 * Override this on different climates, tells how long is the daytime in one day.
	 */
	public int dayPercentage = 60;
	
	public DayTime setDay, setNight;
	private int halfDayPercentage = -1;
	
	static Day genericDay;
	static Night genericNight;
	static
	{
		try {
			genericDay = new Day();
			genericNight = new Night();
		}catch (Exception ex)
		{			
		}
	}
	
	public Season()
	{
		setDay = genericDay;
		setNight = genericNight;
	}
	
	/**
	 * Return percentage of night or day based on time in current season, values between -100 and 100, neg values = night, 0+ day.
	 * @param time
	 * @return -100 and 100, neg values = night, 0+ day.
	 */
	public float dayOrNightPeriodPercentage(Time time)	
	{
		if (halfDayPercentage==-1) halfDayPercentage = dayPercentage/2;
		float p = time.getCurrentDayPercent();
		//if (p!=0)Jcrpg.LOGGER.info("CURRENT DAY PERCENT = "+p);
		if (p>50-(halfDayPercentage) && p<50+(halfDayPercentage))
		{
			float r = (float) (((p-(50-(halfDayPercentage))) / (dayPercentage*1f)) * 100f);
			//if (p!=0)Jcrpg.LOGGER.info("dayOrNightPeriodPercentage = "+r);
			return r;
		}
		int nightPercentage = 100-dayPercentage;
		float pNew = (p+ (nightPercentage/2))%100;
		float r = (float)( (pNew / (nightPercentage*1f) ) * -100f );
		//if (p!=0)Jcrpg.LOGGER.info("dayOrNightPeriodPercentage = "+r);
		return r;
	}
	
	public DayTime getDayTime(Time time) {
		//float p = time.getCurrentDayPercent();
		if (dayOrNightPeriodPercentage(time)>=0) return setDay;
		return setNight;
	}

	public void getConditions(CubeClimateConditions conditions, Time time, int worldX, int worldY, int worldZ) {
		conditions.setSeason(this);
		conditions.mergeConditions(this.conditions);
		conditions.mergeConditions(getDayTime(time).conditions);
		conditions.setDayTime(getDayTime(time));
	}
	
}
