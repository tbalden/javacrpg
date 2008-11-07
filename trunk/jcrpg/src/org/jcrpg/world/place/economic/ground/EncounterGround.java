/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
package org.jcrpg.world.place.economic.ground;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.economic.EconomicGround;

/**
 * used for creating parts that are grassy but not with other flora - in Encounter mode.
 * @author illes
 *
 */
public class EncounterGround extends EconomicGround {

	public EncounterGround() {
		super();
		needsFlora = true;
	}

	public EncounterGround(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner)
			throws Exception {
		super(id, soilGeo, parent, loc, sizeX, sizeY, sizeZ, origoX, origoY, origoZ,
				groundLevel, homeBoundaries, owner);
		needsFlora = true;
	}

	public static HashMap<Integer, Cube> hmKindCubeOverride = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCubeOverride_FARVIEW = new HashMap<Integer, Cube>();

	public static Side[][] STEPS_NORTH = new Side[][] { STEEP, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK, GROUND};
	public static Side[][] STEPS_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STEEP,I_EMPTY,BLOCK,GROUND};
	public static Side[][] STEPS_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STEEP,BLOCK,GROUND};
	public static Side[][] STEPS_EAST = new Side[][] { I_EMPTY, STEEP, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,GROUND};
	public static Side[][] EXTERNAL = new Side[][] { null, null, null,null,null,GROUND };

	static 
	{
		Cube ground = new Cube(null,EXTERNAL,0,0,0,true,true);
		hmKindCubeOverride.put(K_NORMAL_GROUND, ground);
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
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		Cube c = farView?hmKindCubeOverride_FARVIEW.get(kind):hmKindCubeOverride.get(kind);
		return c;
	}

	@Override
	public EconomicGround getInstance(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)
	{
		try {
			return new EncounterGround(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}

}
