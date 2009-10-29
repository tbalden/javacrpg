package org.jcrpg.world.place;

import org.jcrpg.world.place.economic.Population;

public class WorldTypeDesc
{
	public Geography g;
	public int surfaceY;
	public Population population;
	public Economic detailedEconomic;
	public Water w;
	public void clear()
	{
		g = null;
		population = null;
		detailedEconomic = null;
		w = null;
	}
}
