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
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;

import com.jme.math.Vector3f;

public class Ocean extends Water {

	public static final String TYPE_LAKE = "LAKE";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_LAKE+"_WATER");
	public static final NotPassable SUBTYPE_ROCKSIDE = new NotPassable(TYPE_LAKE+"_ROCKSIDE");
	public static final GroundSubType SUBTYPE_ROCKBOTTOM = new GroundSubType(TYPE_LAKE+"_ROCKBOTTOM");
	public static final Swimming SUBTYPE_WATER_EMPTY = new Swimming(TYPE_LAKE+"_WATER_EMPTY");

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
	private int worldGroundLevel;
	int groundLevel;
	
	int centerX, centerZ, realSizeX, realSizeZ;
	Vector3f center = new Vector3f();

	public Ocean(String id, Place parent, PlaceLocator loc, int groundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int depth, int noWaterPercentage) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		this.depth = depth;
		centerX = sizeX*magnification/2;
		centerZ = sizeZ*magnification/2;
		realSizeX = sizeX*magnification;
		realSizeZ = sizeZ*magnification;
		
		center.set(centerX, centerZ, 0);
		
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		this.groundLevel = groundLevel;
		worldGroundLevel=groundLevel*magnification;
		this.noWaterPercentage = noWaterPercentage;
	}

	@Override
	public int getDepth(int x, int y, int z) {
		return depth;
	}

	@Override
	public Cube getWaterCube(int x, int y, int z, Cube geoCube,
			SurfaceHeightAndType surface) {
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
				if (!isWaterPoint(x+1, y, z))
				{
					eastRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_EAST,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				if (!isWaterPoint(x-1, y, z))
				{
					westRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_WEST,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				if (!isWaterPoint(x, y, z+1))
				{
					northRock = true;
					Cube c2 = new Cube (this,LAKE_ROCKSIDE_NORTH,x,y,z,surface.steepDirection);
					c = new Cube(c,c2,x,y,z,surface.steepDirection);
				}
				if (!isWaterPoint(x, y, z-1))
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
	public boolean isWaterPoint(int x, int y, int z) {
		x = J3DCore.getInstance().shrinkToWorld(x);
		z = J3DCore.getInstance().shrinkToWorld(z);
		int xMinusMag = J3DCore.getInstance().shrinkToWorld(x-magnification);
		int zMinusMag = J3DCore.getInstance().shrinkToWorld(z-magnification);
		int xPlusMag = J3DCore.getInstance().shrinkToWorld(x+magnification);
		int zPlusMag = J3DCore.getInstance().shrinkToWorld(z+magnification);

		int smallCoastSize = 5;
		int xMinusMagSmall = J3DCore.getInstance().shrinkToWorld(x-smallCoastSize);
		int zMinusMagSmall = J3DCore.getInstance().shrinkToWorld(z-smallCoastSize);
		int xPlusMagSmall = J3DCore.getInstance().shrinkToWorld(x+smallCoastSize);
		int zPlusMagSmall = J3DCore.getInstance().shrinkToWorld(z+smallCoastSize);
		
		int localX = x-origoX;
		int localY = y-worldGroundLevel;
		int localZ = z-origoZ;
		temp.set(localX, localZ, 0);
		if (worldGroundLevel-y <= depth && worldGroundLevel-y>=0) 
		{
			//if (temp.distance(center)<realSizeX/2)
			{
				if (getGeographyHashPercentage((x/magnification), 0, (z)/magnification)<noWaterPercentage)
				{
					return false;
				}
				boolean coastIt = false;
				int coastPartSize = Math.max(1, magnification/10);
				int coastPartSizeSmall = smallCoastSize;
				
				boolean coastWest = false;
				boolean coastEast = false;
				boolean coastNorth = false;
				boolean coastSouth = false;
				if (localX%magnification<coastPartSize)
				{
					if (getGeographyHashPercentage(((xMinusMag)/magnification), 0, (z)/magnification)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastWest = true;
					}
				} else
				if (localX%magnification>=magnification-coastPartSize)
				{
					if (getGeographyHashPercentage(((xPlusMag)/magnification), 0, (z)/magnification)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastEast = true;
					}
				}

				if (localZ%magnification<coastPartSize)
				{
					if (getGeographyHashPercentage(((x)/magnification), 0, (zMinusMag)/magnification)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastSouth = true;
					}
				} else
				if (localZ%magnification>=magnification-coastPartSize)
				{
					if (getGeographyHashPercentage(((x)/magnification), 0, (zPlusMag)/magnification)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastNorth = true;
				}
				}
				
				if (coastIt) 
				{
					boolean smallCoastIt = false;

					int perVariation = (int)((getGeographyHashPercentage(x/coastPartSize, 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					if (perVariation!=0)
					{
						//if (true) return false;
						if (coastNorth)
						{
							if (localZ%magnification<=magnification-coastPartSize+coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return false;
						} else
						if (coastSouth)
						{
							if (localZ%magnification>=coastPartSize-coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return false;
						} else
						if (coastEast)
						{
							if (localX%magnification<=magnification-coastPartSize+coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return false;
						} else
						if (coastWest)
						{
							if (localX%magnification>=coastPartSize-coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return false;
						} else
						{
							return false;
						}
					} else
					{
						//if (true) return true;
						
						if (coastSouth)
						{
							if (localZ%magnification<=magnification-coastPartSize+coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return true;
						} else
						if (coastNorth)
						{
							if (localZ%magnification>=coastPartSize-coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return true;
						} else
						if (coastWest)
						{
							if (localX%magnification<=magnification-coastPartSize+coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return true;
						} else
						if (coastEast)
						{
							if (localX%magnification>=coastPartSize-coastPartSizeSmall)
							{
								smallCoastIt = true;
							} else
								return true;
						} else
						{
							return true;
						}
						
					}
					perVariation = (int)((getGeographyHashPercentage(x/coastPartSizeSmall, 0, z/coastPartSizeSmall)/50d))-1; // +/- 1 cube
					if (perVariation!=0)
					{
						return false;
					}
					return true;
				} else
				{
					return true;
				}
			}
		}
		return false;
	}

}
