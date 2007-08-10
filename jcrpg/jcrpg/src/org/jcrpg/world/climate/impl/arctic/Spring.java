package org.jcrpg.world.climate.impl.arctic;

import org.jcrpg.world.climate.Season;
import org.jcrpg.world.climate.conditions.Cold;

public class Spring extends Season{

	
	
	public Spring() throws Exception 
	{
		dayPercentage = 100;
		conditions.add(new Cold(40));
	}

}
