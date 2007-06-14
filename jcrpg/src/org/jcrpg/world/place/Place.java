package org.jcrpg.world.place;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.place.geography.Plain;

public class Place {

	protected static Side[][] EMPTY = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side()} };

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
		this.id = id;
	}
	
	public Cube getCube(int worldX, int worldY, int worldZ)
	{
		return null;
	}
	
	public final Place[] getDirectSubPlacesForCoordinates(int worldX, int worldY, int worldZ, HashMap[] placeHashmaps)
	{
		ArrayList<Place> r = new ArrayList<Place>();
		for (HashMap map : placeHashmaps) {
			for (Object place: map.values()) {
				if (((Place)place).boundaries.isInside(worldX, worldY, worldZ))
				{
					r.add((Place)place);
				}
			}			
		}
		return r.toArray(new Place[0]);		
	}

}
