package org.jcrpg.world.place;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.place.geography.Plain;

public class Place {

	Boundaries boundaries;
	
	Object model;
	

	public Object getModel()
	{
		return model;
	}
	
	
	public Boundaries getBoundaries() {
		return boundaries;
	}


	public void setBoundaries(Boundaries boundaries) {
		this.boundaries = boundaries;
	}


	public boolean loadModelFromFile(){
		return false;
	}
	public boolean generateModel(){
		return false;
	}
	
	public String id;
	
	public PlaceLocator loc;
	
	public Place(String id, PlaceLocator loc)
	{
		
	}
	
	public Cube getCube(int worldX, int worldY, int worldZ)
	{
		return null;
	}

}
