/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.place.water;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;

import com.jme.math.Vector3f;

public class Lake extends Water {

	public static final String TYPE_LAKE = "LAKE";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_LAKE+"_WATER",WATER_COLOR);
	public static final NotPassable SUBTYPE_ROCKSIDE = new NotPassable(TYPE_LAKE+"_ROCKSIDE");
	public static final GroundSubType SUBTYPE_ROCKBOTTOM = new GroundSubType(TYPE_LAKE+"_ROCKBOTTOM");
	public static final Swimming SUBTYPE_WATER_EMPTY = new Swimming(TYPE_LAKE+"_WATER_EMPTY",WATER_COLOR);

	static Side[] WATER = {new Side(TYPE_LAKE,SUBTYPE_WATER)};
	static Side[] ROCKSIDE = {new Side(TYPE_LAKE,SUBTYPE_ROCKSIDE)};
	static Side[] ROCKBOTTOM = {new Side(TYPE_LAKE,SUBTYPE_ROCKBOTTOM)};
	static Side[] WATER_EMPTY = {new Side(TYPE_LAKE,SUBTYPE_WATER_EMPTY)};

	static Side[][] LAKE_WATER = new Side[][] { null, null, null,null,null,WATER };
	static Side[][] LAKE_ROCKSIDE_NORTH = new Side[][] { ROCKSIDE, null, null,null,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_SOUTH = new Side[][] { null, null, ROCKSIDE,null,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_EAST = new Side[][] { null, ROCKSIDE, null,null,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_WEST = new Side[][] { null, null, null,ROCKSIDE,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_BOTTOM = new Side[][] { null, null, null, null, null,ROCKBOTTOM };

	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;

	public int depth = 1;
	int noWaterPercentage = 0;
	
	int centerX, centerZ, realSizeX, realSizeZ;
	Vector3f center = new Vector3f();

	public Lake(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int depth, int noWaterPercentage) throws Exception {
		super(id,parent,loc,worldGroundLevel,depth,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,false);

		sweetWater = true;
		
		centerX = sizeX*magnification/2;
		centerZ = sizeZ*magnification/2;
		realSizeX = sizeX*magnification;
		realSizeZ = sizeZ*magnification;
		
		center.set(centerX, centerZ, 0);
		
		this.noWaterPercentage = noWaterPercentage;
	}

	@Override
	public int getDepth(int x, int y, int z) {
		return depth;
	}

	@Override
	public Cube getWaterCube(int x, int y, int z, Cube geoCube,
			SurfaceHeightAndType surface, boolean farView) {
		if (y==worldGroundLevel && !noWaterInTheBed) 
		{
			return new Cube (this,LAKE_WATER,x,y,z,SurfaceHeightAndType.NOT_STEEP);
		} else
		{
			if (worldGroundLevel-y > depth || y==worldGroundLevel)
			{
				// below bottom, return empty
			} else 
			{
				boolean bottom = false;
				if (worldGroundLevel-y == depth)
				{
					bottom = true;
				}
				boolean northRock = false;
				boolean southRock = false;
				boolean westRock = false;
				boolean eastRock = false;
				Cube c = null;
				if (!bottom)
				{
					c = new Cube (this,EMPTY,x,y,z,surface.steepDirection);
				} else
				{
					c = new Cube (this,LAKE_ROCKSIDE_BOTTOM,x,y,z,surface.steepDirection);
				}
				// direction rockside tests
				if (!isWaterPoint(x+1, y, z, farView))
				{
					eastRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_EAST,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				if (!isWaterPoint(x-1, y, z, farView))
				{
					westRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_WEST,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				if (!isWaterPoint(x, y, z+1, farView))
				{
					northRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_NORTH,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				if (!isWaterPoint(x, y, z-1, farView))
				{
					southRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_SOUTH,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				return c;
			}
		}
		return new Cube (this,EMPTY,x,y,z,SurfaceHeightAndType.NOT_STEEP);
	}

	Vector3f temp = new Vector3f();
	
	@Override
	public boolean isWaterPoint(int x, int y, int z, boolean farView) {
		int localX = x-origoX;
		int localY = y-worldGroundLevel;
		int localZ = z-origoZ;
		temp.set(localX, localZ, 0);
		if (worldGroundLevel-y <= depth && worldGroundLevel-y>=0) 
		{
			if (temp.distance(center)<realSizeX/2)
			{
				if (HashUtil.mixPercentage(x, 0, z)<noWaterPercentage)
				{
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWaterBlock(int worldX, int worldY, int worldZ) {
		return isWaterPoint(worldX, worldY, worldZ, false);
	}

}
