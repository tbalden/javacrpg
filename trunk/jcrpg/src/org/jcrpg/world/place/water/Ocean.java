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
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;

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

	public int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;

	public int depth = 1;
	public int noWaterPercentage = 0;
	public int worldGroundLevel;
	public int groundLevel;
	/**
	 * How dense the water parts should stick together
	 */
	public int density;
	
	int centerX, centerZ, realSizeX, realSizeZ;
	Vector3f center = new Vector3f();

	public Ocean(String id, Place parent, PlaceLocator loc, int groundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int depth, int noWaterPercentage, int density) throws Exception {
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
		this.density = density;
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
	
	public int calcDensModifier(int x, int z, int density, int magnification)
	{		
		int dm = getGeographyHashPercentage((x/(magnification*density)), 0, (z)/(magnification*density)) - 50;
		int perc = getGeographyHashPercentage(((World)getRoot()).realSizeX, ((World)getRoot()).realSizeX, ((World)getRoot()).realSizeZ);
		dm -= (getGeographyHashPercentage(perc-(x/((World)getRoot()).realSizeX) + (z/((World)getRoot()).realSizeZ),0,0) - 50)/3;
		return dm;
	}
	
	public boolean isWaterPointSpecial(int x, int y, int z, boolean coasting)
	{
		x = shrinkToWorld(x);
		z = shrinkToWorld(z);
		int xMinusMag = shrinkToWorld(x-magnification);
		int zMinusMag = shrinkToWorld(z-magnification);
		int xPlusMag = shrinkToWorld(x+magnification);
		int zPlusMag = shrinkToWorld(z+magnification);

		// large coast variation size
		int coastPartSize = Math.max(1, 20);//magnification/10);
		// small coast variation size
		int coastPartSizeSmall = 2;
		
		int localX = x-origoX;
		int localY = y-worldGroundLevel;
		int localZ = z-origoZ;
		temp.set(localX, localZ, 0);
		if (worldGroundLevel-y <= depth && worldGroundLevel-y>=0) 
		{
			{
				int densModifier = calcDensModifier(x, z, density, magnification);//getGeographyHashPercentage((x/(magnification*density)), 0, (z)/(magnification*density)) - 50;
				
				if ((getGeographyHashPercentage((x/magnification), 0, (z)/magnification)+densModifier)<noWaterPercentage)
				{
					return false;
				}
				
				if (!coasting) return true; // just a magnified bigmap view detail is required, return now!

				int densModifierXPlus = calcDensModifier(xPlusMag, z, density, magnification);//getGeographyHashPercentage((xPlusMag/(magnification*density)), 0, (z)/(magnification*density)) - 50;
				int densModifierZPlus = calcDensModifier(x, zPlusMag, density, magnification);//getGeographyHashPercentage((x/(magnification*density)), 0, (zPlusMag)/(magnification*density)) - 50;
				int densModifierXMinus = calcDensModifier(xMinusMag, z, density, magnification);//getGeographyHashPercentage((xMinusMag/(magnification*density)), 0, (z)/(magnification*density)) - 50;
				int densModifierZMinus = calcDensModifier(x, zMinusMag, density, magnification);//getGeographyHashPercentage((x/(magnification*density)), 0, (zMinusMag)/(magnification*density)) - 50;

				boolean coastIt = false;
				
				boolean coastWest = false;
				boolean coastEast = false;
				boolean coastNorth = false;
				boolean coastSouth = false;
				if (localX%magnification<coastPartSize)
				{
					if ((getGeographyHashPercentage(((xMinusMag)/magnification), 0, (z)/magnification)+densModifierXMinus)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastWest = true;
					}
				}
				if (localX%magnification>=magnification-coastPartSize)
				{
					if ((getGeographyHashPercentage(((xPlusMag)/magnification), 0, (z)/magnification)+densModifierXPlus)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastEast = true;
					}
				}
				if (localZ%magnification<coastPartSize)
				{
					if ((getGeographyHashPercentage(((x)/magnification), 0, (zMinusMag)/magnification)+densModifierZMinus)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastSouth = true;
						System.out.println("#----# Yes SOUTH "+x/magnification+ " "+z/magnification);
					}
				}
				if (localZ%magnification>=magnification-coastPartSize)
				{
					if ((getGeographyHashPercentage(((x)/magnification), 0, (zPlusMag)/magnification)+densModifierZPlus)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastNorth = true;
						System.out.println("###### Yes NORTH "+x/magnification+ " "+z/magnification);
					}
				}
				
				if (coastIt) 
				{
					// figure out small coasting needed for the coordinates...
					
					boolean smallCoastIt = false;
					boolean smallCoastNorth = false;
					boolean smallCoastSouth = false;
					boolean smallCoastWest = false;
					boolean smallCoastEast = false;

					int perVariation = (int)((getGeographyHashPercentage(x/coastPartSize, 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					int perVariationPrevX = (int)((getGeographyHashPercentage((shrinkToWorld(x-coastPartSize)/coastPartSize), 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					int perVariationNextX = (int)((getGeographyHashPercentage((shrinkToWorld(x+coastPartSize)/coastPartSize), 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					int perVariationPrevZ = (int)((getGeographyHashPercentage((x/coastPartSize), 0, (shrinkToWorld(z-coastPartSize)/coastPartSize))/50d))-1; // +/- 1 cube
					int perVariationNextZ = (int)((getGeographyHashPercentage((x/coastPartSize), 0, (shrinkToWorld(z+coastPartSize)/coastPartSize))/50d))-1; // +/- 1 cube
					if (perVariation!=0)
					{
						// TODO in internal parts (not on the coast -> it doesnt do smallcoasting naturally,
						// redesign this to do it!
						
						// no water here...deciding small coasting near the no water block's limit
						if (coastNorth)
						{
							if (localZ%magnification<=(magnification-coastPartSize) + coastPartSizeSmall)
							{
								
								smallCoastIt = true;
								smallCoastNorth  = true;
							}
							if (perVariationPrevX==0)
							{
								if (localX%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastWest  = true;
								}
							}
							if (perVariationNextX==0)
							{
								if (localX%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastEast = true;
								}
							}
						}
						if (coastSouth)
						{
							if (localZ%magnification>=coastPartSize - coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastSouth  = true;
							}
							if (perVariationPrevX==0)
							{
								if (localX%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastWest  = true;
								}
							}
							if (perVariationNextX==0)
							{
								if (localX%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastEast = true;
								}
							}
						}
						if (coastWest)
						{
							if (localX%magnification>=coastPartSize - coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastWest = true;
							}
							if (perVariationPrevZ==0)
							{
								if (localZ%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastSouth  = true;
								}
							}
							if (perVariationNextZ==0)
							{
								if (localZ%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastNorth = true;
								}
							}
						}
						if (coastEast)
						{
							if (localX%magnification<magnification-coastPartSize + coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastEast = true;
							}
							if (perVariationPrevZ==0)
							{
								if (localZ%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastSouth  = true;
								}
							}
							if (perVariationNextZ==0)
							{
								if (localZ%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastNorth = true;
								}
							}
						}
						if (!smallCoastIt) return false;
					} else
					{
						// water here...deciding small coasting near the water block's limit
						
						if (coastSouth)
						{
							if (localZ%magnification<=coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastSouth = true;
							}
							if (perVariationPrevX==0)
							{
								if (localX%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastWest  = true;
								}
							}
							if (perVariationNextX==0)
							{
								if (localX%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastEast = true;
								}
							}
						}
						if (coastNorth)
						{
							if (localZ%magnification>magnification - coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastNorth = true;
							}
							if (perVariationPrevX==0)
							{
								if (localX%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastWest  = true;
								}
							}
							if (perVariationNextX==0)
							{
								if (localX%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastEast = true;
								}
							}
						}
						if (coastWest)
						{
							if (localX%magnification<=coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastWest = true;
							}
							if (perVariationPrevZ==0)
							{
								if (localZ%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastSouth  = true;
								}
							}
							if (perVariationNextZ==0)
							{
								if (localZ%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastNorth = true;
								}
							}
						}
						if (coastEast)
						{
							if (localX%magnification>magnification - coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastEast = true;
							}
							if (perVariationPrevZ==0)
							{
								if (localZ%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastSouth  = true;
								}
							}
							if (perVariationNextZ==0)
							{
								if (localZ%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastNorth = true;
								}
							}
						}
						if (!smallCoastIt) return true;
					}
					int pV1 = 0, pV2 = 0;
					if (smallCoastEast || smallCoastWest) {
						pV1 = (int)((getGeographyHashPercentage((z)/coastPartSizeSmall, 0, 0)/50d))-1; // +/- 1 cube
						if (pV1!=0)
						{
							return false;
						}
					}
					if (smallCoastNorth || smallCoastSouth) {
						pV2 = (int)((getGeographyHashPercentage(0, 0, (x)/coastPartSizeSmall)/50d))-1; // +/- 1 cube
						if (pV2!=0)
						{
							System.out.println("###### Yes SMALL NORTH/SOUTH "+x+ " "+z);
							return false;
						} else
						{
							System.out.println("###### No SMALL NORTH/SOUTH "+x+ " "+z);
							return true;
						}
					}
						
					return true;
				} else 
				{
					// not coastal part, return true for water
					return true;
				}
			}
		}
		// absolutely not in the water
		return false;
		
	}
	
	@Override
	public boolean isWaterPoint(int x, int y, int z) {
		return isWaterPointSpecial(x, y, z, true);
	}

}
