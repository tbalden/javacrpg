package org.jcrpg.world.climate;

import java.util.ArrayList;

import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.time.Time;

public class ClimatePart {

	protected Boundaries boundaries;
	public ClimatePart parent;

	public String id;

	public ClimatePart(String id, ClimatePart parent)
	{
		this.id = id;
		this.parent = parent;
		
	}
	
	
	public ArrayList<Condition> getCubeClimate(Time time, int worldX, int worldY, int worldZ)
	{
		return null;
	}

}
