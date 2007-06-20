package org.jcrpg.world.climate.impl.generic;

import org.jcrpg.world.climate.DayTime;
import org.jcrpg.world.climate.conditions.Warm;
import org.jcrpg.world.climate.conditions.light.Light;


public class Day extends DayTime{

	
	public Day()
	{
		conditions.add(new Light());
		conditions.add(new Warm());
	}
	
}
