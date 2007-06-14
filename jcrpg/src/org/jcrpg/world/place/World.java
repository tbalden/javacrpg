package org.jcrpg.world.place;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.River;

public class World extends Place {

	public HashMap<String, Geography> geographies;
	public HashMap<String, Political> politicals;
	public HashMap<String, Economic> economics;
	
	public World(String id, PlaceLocator loc) {
		super(id, loc);
		geographies = new HashMap<String, Geography>();
		politicals = new HashMap<String, Political>();
		economics = new HashMap<String, Economic>();
	}
	

	@Override
	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean generateModel() {
		
		return true;
	}


	@Override
	public boolean loadModelFromFile() {
		// TODO Auto-generated method stub
		return false;
	}
	
	


	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		if (boundaries.isInside(worldX, worldY, worldZ))
			{
			for (Geography iterable_element : geographies.values()) {
				if (iterable_element.getBoundaries().isInside(worldX, worldY, worldZ))
					return iterable_element.getCube(worldX, worldY, worldZ);
			}
			return null;
		}
		else return null;
	}

}
