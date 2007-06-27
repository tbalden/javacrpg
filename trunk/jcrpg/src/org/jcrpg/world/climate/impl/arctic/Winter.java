package org.jcrpg.world.climate.impl.arctic;

import org.jcrpg.world.climate.Season;
import org.jcrpg.world.climate.conditions.Cold;

public class Winter extends Season {

	public Winter() throws Exception 
	{
		conditions.add(new Cold(70));
	}
	
}
