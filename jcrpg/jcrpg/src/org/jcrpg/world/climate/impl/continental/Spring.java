package org.jcrpg.world.climate.impl.continental;

import org.jcrpg.world.climate.Season;

public class Spring extends Season{

	public Spring()
	{
		dayPercentage = 60;
		setDay = day;
		setNight = night;
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


	
	
}
