package org.jcrpg.world.climate;

import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.time.Time;

public class ClimatePart {

	public Boundaries boundaries;
	public ClimatePart parent;

	public String id;

	public ClimatePart(String id, ClimatePart parent)
	{
		this.id = id;
		this.parent = parent;
		
	}
	
	public CubeClimateConditions getCubeClimate(Time time, int worldX, int worldY, int worldZ)
	{
		return null;
	}

	public Boundaries getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(Boundaries boundaries) {
		this.boundaries = boundaries;
	}

}
