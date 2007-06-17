package org.jcrpg.world.place;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.River;

public class World extends Place {

	public HashMap<String, Geography> geographies;
	public HashMap<String, Political> politicals;
	public HashMap<String, Economic> economics;

	public static final String TYPE_WORLD = "WORLD";
	public static final Swimming SUBTYPE_OCEAN = new Swimming(TYPE_WORLD+"_OCEAN");
	
	public boolean WORLD_IS_GLOBE = true;

	static Side[][] OCEAN = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side(TYPE_WORLD,SUBTYPE_OCEAN)} };
	
	
	public int sizeX, sizeY, sizeZ, magnification;

	public World(String id, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ) throws Exception {
		super(id, null, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, 0, 0, 0));
		geographies = new HashMap<String, Geography>();
		politicals = new HashMap<String, Political>();
		economics = new HashMap<String, Economic>();
	}
	
	
	public int getSeaLevel(int magnification)
	{
		return ((sizeY*this.magnification)/2)/magnification;
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
		
		if (WORLD_IS_GLOBE) {
			
			worldX = worldX%(sizeX*magnification);
			//worldY = worldX%(sizeY*magnification); // in dir Y no globe
			worldZ = worldZ%(sizeZ*magnification); // TODO Houses dont display going round the glob??? Static fields in the way or what?
		}
		
		if (boundaries.isInside(worldX, worldY, worldZ))
		{
			for (Economic eco : economics.values()) {
				if (eco.getBoundaries().isInside(worldX, worldY, worldZ))
					return eco.getCube(worldX, worldY, worldZ);
			}
			for (Geography geo : geographies.values()) {
				if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
					return geo.getCube(worldX, worldY, worldZ);
			}
			return worldY==(sizeY*magnification/2)?new Cube(this,OCEAN,worldX,worldY,worldZ):null;
		}
		else return null;
	}

}
