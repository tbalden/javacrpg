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
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		if (boundaries.isInside(worldX, worldY, worldZ))
			{
			return new Cube(this, Cube.DEFAULT_LEVEL,new Side[]{new Side(),new Side(), new Side(),new Side(), new Side(), new Side("0",worldY==0?(worldX%10!=0?Plain.SUBTYPE_GRASS:River.SUBTYPE_WATER):"0")},worldX,worldY,worldZ);
			}
		else return null;
	}

}
