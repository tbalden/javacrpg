package org.jcrpg.world.climate.impl.generic;

import org.jcrpg.world.climate.DayTime;
import org.jcrpg.world.climate.conditions.Cool;
import org.jcrpg.world.climate.conditions.light.Dark;

public class Night extends DayTime{

	public Night()
	{
		conditions.add(new Dark());
		conditions.add(new Cool());
	}
}
