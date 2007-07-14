package org.jcrpg.world.climate.impl.desert;

import org.jcrpg.world.climate.Season;
import org.jcrpg.world.climate.conditions.Cold;
import org.jcrpg.world.climate.conditions.Rain;

public class Spring extends Season{

	public Spring() throws Exception 
	{
		dayPercentage = 50;
		conditions.add(new Cold(20));
		conditions.add(new Rain(40));
	}

}
