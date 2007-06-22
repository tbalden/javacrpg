package org.jcrpg.world.climate.impl.continental;

import org.jcrpg.world.climate.DayTime;
import org.jcrpg.world.climate.Season;
import org.jcrpg.world.time.Time;

public class Spring extends Season{

	public Spring()
	{
		
	}
	
	static Day day;
	static Night night;
	static
	{
		try {
			day = new Day();
			night = new Night();
		}catch (Exception ex)
		{			
		}
	}

	@Override
	public DayTime getDayTime(Time time) {
		int p = time.getCurrentDayPercent();
		if (p>30 && p<70) return day;
		return night;
	}

	
	
}
