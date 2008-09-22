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
package org.jcrpg.world.place.economic.residence.dungeon;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.Residence;
import org.jcrpg.world.place.economic.residence.House;
import org.jcrpg.world.place.economic.residence.WoodenHouse;

public class SimpleDungeonPart extends WoodenHouse {

	public SimpleDungeonPart() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SimpleDungeonPart(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner)
			throws Exception {
		super(id, soilGeo, parent, loc, sizeX, sizeY, sizeZ, origoX, origoY, origoZ,
				groundLevel, homeBoundaries, owner);
	}

	@Override
	public Residence getInstance(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner) {
		try {
			return new SimpleDungeonPart(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	public byte[][] labyrinthData;

	@Override
	public int getMinimumHeight() {
		return 1;
	}
	
	@Override
	public boolean overrideGeoHeight() {
		// TODO Auto-generated method stub
		//
		return true;
	}

	static Side[][] WALL_GROUND_NORTH_WEST = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL)},null, null ,{new Side(TYPE_HOUSE,SUBTYPE_WALL)},null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	
	Cube north = new Cube(null,WALL_GROUND_NORTH,0,0,0,true,true);
	Cube west = new Cube(null,WALL_GROUND_WEST,0,0,0,true,true);
	Cube northWest = new Cube(null,WALL_GROUND_NORTH_WEST,0,0,0,true,true);
	Cube gap = new Cube(null,EXTERNAL,0,0,0,true,true);
	
	@Override
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		if (labyrinthData==null)
		{
			labyrinthData = MazeTool.getLabyrinth(worldX+worldY+worldZ, sizeX+1, sizeZ+1, null);
		}
		if (kind==K_NORMAL_GROUND)
		{
			boolean horWall = false;
			boolean verWall = false;
			if ((labyrinthData[worldX-origoX][worldZ-origoZ] & MazeTool.WALL_HORI)>0)
			{
				horWall = true;
			}
			if ((labyrinthData[worldX-origoX][worldZ-origoZ] & MazeTool.WALL_VERT)>0)
			{
				verWall = true;
			}
			if (horWall&&verWall) return northWest;
			if (horWall) return north;
			if (verWall) return west;
			return gap;
		}
		return null;
	}

	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		float[] kind = getCubeKind(key, worldX, worldY, worldZ, farView);
		Cube c = getCubeObject((int)kind[4], worldX, worldY, worldZ, farView);
		if (c==null) return c;
		c = c.copy(this);
		c.x = worldX;
		c.y = worldY;
		c.z = worldZ;
		c.cornerHeights = kind;
		c.middleHeight = (kind[0]+kind[1]+kind[2]+kind[3])/4f;
		c.angleRatio = Math.max( Math.abs(kind[0]-kind[2]) , Math.max( Math.abs(kind[1]-kind[3]) , Math.max( Math.abs(kind[0]-kind[1]) , Math.abs(kind[2]-kind[3]))));
		c.geoCubeKind = (int)kind[4];
		c.pointHeightFloat = kind[9];
		c.pointHeightInt = (int)kind[9];
		return c;
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

}
