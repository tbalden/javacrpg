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
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

/**
 * Base class for roads and such.
 * @author pali
 *
 */
public class EconomicGround extends Economic {

	public static final String TYPE_ECOGROUND = "ECOGROUND";
	
	public static final SideSubType SUBTYPE_STAIRS = new Climbing(TYPE_ECOGROUND+"_STAIRS");
	static
	{
		SUBTYPE_STAIRS.audioStepType = AudioServer.STEP_STONE;
	}
	static Side[] STAIRS = new Side[]{new Side(TYPE_ECOGROUND,SUBTYPE_STAIRS)};

	static Side[][] STEPS_NORTH = new Side[][] { STAIRS, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK,BLOCK };
	static Side[][] STEPS_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STAIRS,I_EMPTY,BLOCK,BLOCK };
	static Side[][] STEPS_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STAIRS,BLOCK,BLOCK };
	static Side[][] STEPS_EAST = new Side[][] { I_EMPTY, STAIRS, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,BLOCK };

	public EconomicGround(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)  throws Exception {
		super(id,soilGeo,parent, loc, homeBoundaries, owner);
		this.origoX = origoX;this.origoY = origoY;this.origoZ = origoZ;
		this.sizeX = sizeX;this.sizeY = sizeY;this.sizeZ = sizeZ;
		this.groundLevel = groundLevel;
		boundaries = BoundaryUtils.createCubicBoundaries(1, sizeX, sizeY, sizeZ, origoX, origoY, origoZ);
		boundaries.boundaryPlace = this;
	}
	
	
	public static HashMap<Integer, Cube> hmKindCubeOverride = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCubeOverride_FARVIEW = new HashMap<Integer, Cube>();
	
	static 
	{
		hmKindCubeOverride.put(K_NORMAL_GROUND, new Cube(null,House.EXTERNAL,0,0,0));
		hmKindCubeOverride.put(K_STEEP_EAST, new Cube(null,STEPS_EAST,0,0,0));
		hmKindCubeOverride.put(K_STEEP_WEST, new Cube(null,STEPS_WEST,0,0,0));
		hmKindCubeOverride.put(K_STEEP_NORTH, new Cube(null,STEPS_NORTH,0,0,0));
		hmKindCubeOverride.put(K_STEEP_SOUTH, new Cube(null,STEPS_SOUTH,0,0,0));

		hmKindCubeOverride_FARVIEW.put(K_NORMAL_GROUND, new Cube(null,House.EXTERNAL,0,0,0));
	}
	

	@Override
	public Cube getCubeObject(int kind, boolean farView) {
		Cube c = farView?hmKindCubeOverride_FARVIEW.get(kind):hmKindCubeOverride.get(kind);
		return c;
	}

	public EconomicGround()
	{
		super(null,null,null,null,null,null);
	}

	@Override
	public boolean overrideGeoHeight() {
		return false;
	}

	
	
	@Override
	public int getCubeKind(long key, int worldX, int worldY, int worldZ, boolean farView) {
		return super.getCubeKindOutside(key, worldX, worldY, worldZ, farView);
	}

	@Override
	protected int getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView) {
		// use the height defined by the geography here...
		int h = getPointHeightOutside(worldX, worldZ, farView);
		System.out.println("getPointHeightInside FROM ECOGROUND "+h);
		return h;
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
