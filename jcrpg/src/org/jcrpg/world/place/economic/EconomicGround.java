/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package org.jcrpg.world.place.economic;

import java.util.HashMap;

import org.jcrpg.audio.AudioServer;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.water.Ocean;

/**
 * Base class for roads and such.
 * @author pali
 *
 */
public class EconomicGround extends Economic {

	public static final String TYPE_ECOGROUND = "ECOGROUND";
	
	public static final SideSubType SUBTYPE_STAIRS = new Climbing(TYPE_ECOGROUND+"_STAIRS",true);
	public static final SideSubType SUBTYPE_STREETGROUND = new GroundSubType(TYPE_ECOGROUND+"_STREETGROUND",false);
	public static final SideSubType SUBTYPE_EXTERNAL_WOODEN_GROUND = new GroundSubType(TYPE_ECOGROUND+"_EXTERNAL_GROUND",true);
	
	static
	{
		SUBTYPE_STREETGROUND.continuousSoundType = "town_street_easy";
		SUBTYPE_STREETGROUND.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_STREETGROUND.colorOverwrite = true;
		SUBTYPE_STREETGROUND.colorBytes = new byte[] {(byte)150,(byte)150,(byte)150};
		SUBTYPE_STAIRS.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_STAIRS.colorOverwrite = true;
		SUBTYPE_STAIRS.colorBytes = new byte[] {(byte)150,(byte)50,(byte)50};
	}
	public static Side[] STAIRS = new Side[]{new Side(TYPE_ECOGROUND,SUBTYPE_STAIRS)};
	public static Side[] ECOGROUND = new Side[]{new Side(TYPE_ECOGROUND,SUBTYPE_STREETGROUND)};
	

	static Side[][] STEPS_NORTH = new Side[][] { STAIRS, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK, GROUND};
	static Side[][] STEPS_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STAIRS,I_EMPTY,BLOCK,GROUND};
	static Side[][] STEPS_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STAIRS,BLOCK,GROUND};
	static Side[][] STEPS_EAST = new Side[][] { I_EMPTY, STAIRS, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,GROUND};

	public static Side[][] EXTERNAL = new Side[][] { null, null, null,null,null,ECOGROUND };
	public static Side[][] EXTERNAL_WATER_WOODEN_GROUND = new Side[][] { null, null, null,null,null,{Ocean.SHALLOW_WATER_SIDE,new Side(TYPE_ECOGROUND,SUBTYPE_EXTERNAL_WOODEN_GROUND)}};

	public EconomicGround(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)  throws Exception {
		super(id,soilGeo,parent, loc, homeBoundaries, owner);
		this.origoX = origoX;this.origoY = origoY;this.origoZ = origoZ;
		this.sizeX = sizeX;this.sizeY = sizeY;this.sizeZ = sizeZ;
		this.groundLevel = groundLevel;
		this.worldGroundLevel = origoY;
		boundaries = BoundaryUtils.createCubicBoundaries(1, sizeX, sizeY, sizeZ, origoX, origoY, origoZ);
		boundaries.boundaryPlace = this;
		//needsFlora = false;
	}
	
	
	public static HashMap<Integer, Cube> hmKindCubeOverride = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCubeOverride_FARVIEW = new HashMap<Integer, Cube>();
	
	static 
	{
		Cube ground = new Cube(null,EXTERNAL,0,0,0,true,false);
		hmKindCubeOverride.put(K_NORMAL_GROUND, ground);
		Cube waterGround = new Cube(null,EXTERNAL_WATER_WOODEN_GROUND,0,0,0,true,false);
		hmKindCubeOverride.put(K_WATER_GROUND, waterGround );		
		Cube stepsEast = new Cube(null,STEPS_EAST,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_EAST, stepsEast);
		Cube stepWest = new Cube(null,STEPS_WEST,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_WEST, stepWest);
		Cube stepNorth = new Cube(null,STEPS_NORTH,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_NORTH, stepNorth);
		Cube stepSouth = new Cube(null,STEPS_SOUTH,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_SOUTH, stepSouth);

		//hmKindCubeOverride_FARVIEW.put(K_NORMAL_GROUND, new Cube(null,House.EXTERNAL,0,0,0));
	}
	

	@Override
	public Cube getCubeObject(int kind, boolean farView) {
		Cube c = farView?hmKindCubeOverride_FARVIEW.get(kind):hmKindCubeOverride.get(kind);
		return c;
	}

	public EconomicGround()
	{
		super(null,null,null,null,null,null);
		//needsFlora = false;
	}

	@Override
	public boolean overrideGeoHeight() {
		return false;
	}

	
	
	@Override
	public float[] getCubeKind(long key, int worldX, int worldY, int worldZ, boolean farView) {
		// let's check for waters here...
		boolean water = false;
		for (Water geo:((World)getRoot()).waters.values())
		{
			{
				if (geo.getBoundaries().isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					if (geo.isWaterPoint(worldX, geo.worldGroundLevel, worldZ, farView)) water = true;
				}
			}
		}
		float[] retKind = null;
		if (soilGeo!=null && soilGeo.getBoundaries().isInside(worldX, worldY, worldZ))
		{
			retKind = soilGeo.getCubeKind(key, worldX,worldY,worldZ, farView);
		} else
		{
			retKind = super.getCubeKindOutside(key, worldX, worldY, worldZ, farView);
		}
		if (water && retKind[4]==K_NORMAL_GROUND)
		{
			retKind[4] = K_WATER_GROUND;
		}
		return retKind;
		
	}

	@Override
	protected float getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView) {
		// use the height defined by the geography here...
		if (soilGeo.getBoundaries().isInside(worldX, soilGeo.worldGroundLevel, worldZ))
		{
			int[] values = soilGeo.calculateTransformedCoordinates(worldX, soilGeo.worldGroundLevel, worldZ);
			return soilGeo.getPointHeight(values[3], values[5], values[0], values[2],worldX,worldZ, farView);
		}
		return getPointHeightOutside(worldX, worldZ, farView);
	}

	public EconomicGround getInstance(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)
	{
		try {
			return new EconomicGround(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}

}
