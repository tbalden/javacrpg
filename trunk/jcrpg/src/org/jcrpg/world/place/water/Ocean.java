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

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;

import com.jme.math.Vector3f;

/**
 * Ocean - one earth like foundation water geography. It's with normal boundaries, with maximum magnification,
 * boundaries won't tell if it is a waterpoint, you must use isWaterPoint to know it.
 * @author illes
 */
public class Ocean extends Water {

	public static final String TYPE_OCEAN = "OCEAN";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_OCEAN+"_WATER",WATER_COLOR);
	public static final SideSubType SUBTYPE_WATER_SHALLOW = new SideSubType(TYPE_OCEAN+"_WATER",WATER_COLOR);
	public static final NotPassable SUBTYPE_ROCKSIDE = new NotPassable(TYPE_OCEAN+"_ROCKSIDE");
	public static final GroundSubType SUBTYPE_ROCKBOTTOM = new GroundSubType(TYPE_OCEAN+"_ROCKBOTTOM");
	public static final Swimming SUBTYPE_WATER_EMPTY = new Swimming(TYPE_OCEAN+"_WATER_EMPTY",WATER_COLOR);

	public static Side SHALLOW_WATER_SIDE = new Side(TYPE_OCEAN,SUBTYPE_WATER_SHALLOW);
	public static Side[] WATER = {new Side(TYPE_OCEAN,SUBTYPE_WATER)};
	static Side[] ROCKSIDE = {new Side(TYPE_OCEAN,SUBTYPE_ROCKSIDE)};
	static Side[] ROCKBOTTOM = {new Side(TYPE_OCEAN,SUBTYPE_ROCKBOTTOM)};
	static Side[] WATER_EMPTY = {new Side(TYPE_OCEAN,SUBTYPE_WATER_EMPTY)};

	static Side[][] LAKE_WATER = new Side[][] { null, null, null,null,null,WATER };
	static Side[][] LAKE_ROCKSIDE_NORTH = new Side[][] { ROCKSIDE, null, null,null,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_SOUTH = new Side[][] { null, null, ROCKSIDE,null,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_EAST = new Side[][] { null, ROCKSIDE, null,null,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_WEST = new Side[][] { null, null, null,ROCKSIDE,null,WATER_EMPTY };
	static Side[][] LAKE_ROCKSIDE_BOTTOM = new Side[][] { null, null, null, null, null,null };//ROCKBOTTOM };


	public int noWaterPercentage = 0;
	//public int worldGroundLevel;

	/**
	 * How dense the water parts should stick together
	 */
	public int density;
	
	int centerX, centerZ, realSizeX, realSizeZ;

	public Ocean(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int depth, int noWaterPercentage, int density) throws Exception {
		super(id,parent,loc,worldGroundLevel,depth,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,true);
		this.worldGroundLevel--;
		realSizeX = sizeX*magnification;
		realSizeZ = sizeZ*magnification;
		
		//setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		this.noWaterPercentage = noWaterPercentage;
		this.density = density;
	}

	@Override
	public int getDepth(int x, int y, int z) {
		return depth;
	}

	@Override
	public Cube getWaterCube(int x, int y, int z, Cube geoCube,
			SurfaceHeightAndType surface, boolean farView) {
		
		if (!isWaterPoint(x, y, z, farView)) return geoCube;
		if (y/(farView?J3DCore.FARVIEW_GAP:1)==worldGroundLevel/(farView?J3DCore.FARVIEW_GAP:1) && !noWaterInTheBed) 
		{
			Cube c = new Cube (this,LAKE_WATER,x,y,z,SurfaceHeightAndType.NOT_STEEP);
			c.waterCube = true;
			return c;
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
				if (geoCube!=null && geoCube.overwrite) c = new Cube (this,EMPTY,x,y,z,SurfaceHeightAndType.NOT_STEEP);
				c.waterCube = true;
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
	
	@Override
	public boolean isAlgorithmicallyInside(int worldX, int worldY, int worldZ) {
		return isWaterPointSpecial(worldX, worldY, worldZ, false,false);
	}
	

	public boolean isWaterPointSpecial(int x, int y, int z, boolean coasting, boolean farView)
	{
		
		x = shrinkToWorld(x);
		z = shrinkToWorld(z);
		int xMinusMag = shrinkToWorld(x-magnification);
		int zMinusMag = shrinkToWorld(z-magnification);
		int xPlusMag = shrinkToWorld(x+magnification);
		int zPlusMag = shrinkToWorld(z+magnification);

		// large coast variation size
		int coastPartSize = Math.max(1, 10);
		// small coast variation size
		int coastPartSizeSmall = 1;
		
		int localX = x-origoX;
		int localY = y-worldGroundLevel;
		int localZ = z-origoZ;
		temp.set(localX, localZ, 0);
		
		int CONST_FARVIEW = farView?J3DCore.FARVIEW_GAP:1;
		
		if ((worldGroundLevel/CONST_FARVIEW)-(y/CONST_FARVIEW) <= depth && (worldGroundLevel/CONST_FARVIEW)-(y/CONST_FARVIEW)>=0) 
		{
			{
				int densModifier = calcDensModifier(x, z, density, magnification);//getGeographyHashPercentage((x/(magnification*density)), 0, (z)/(magnification*density)) - 50;
				
				if ((getGeographyHashPercentage((x/magnification), 0, (z)/magnification)+densModifier)<noWaterPercentage)
				{
					return false;
				}
				
				if (!coasting) return true; // just a magnified bigmap view detail is required, return now!

				// let's see if there is overlapping geography with non 0 height neigbor cube -> no water there!
				/*for (int dx=-1; dx<=1; dx++)
				{
					for (int dz=-1; dz<=1; dz++)
					{
						int Y = (int)getPointHeightOutside(shrinkToWorld(x+dx),shrinkToWorld(z+dz), farView);
						if (Y>0)
							return false;
					}
				}*/
				//int cY = getPointHeightOutside(shrinkToWorld(x),shrinkToWorld(z));
				//if (cY<=0 && y<=worldGroundLevel) return true;
				
				int densModifierXPlus = calcDensModifier(xPlusMag, z, density, magnification);//getGeographyHashPercentage((xPlusMag/(magnification*density)), 0, (z)/(magnification*density)) - 50;
				int densModifierZPlus = calcDensModifier(x, zPlusMag, density, magnification);//getGeographyHashPercentage((x/(magnification*density)), 0, (zPlusMag)/(magnification*density)) - 50;
				int densModifierXMinus = calcDensModifier(xMinusMag, z, density, magnification);//getGeographyHashPercentage((xMinusMag/(magnification*density)), 0, (z)/(magnification*density)) - 50;
				int densModifierZMinus = calcDensModifier(x, zMinusMag, density, magnification);//getGeographyHashPercentage((x/(magnification*density)), 0, (zMinusMag)/(magnification*density)) - 50;

				// tells if the big block has coastal are
				boolean coastIt = false;
				// these will tell which side has coastal area
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
					}
				}
				if (localZ%magnification>=magnification-coastPartSize)
				{
					if ((getGeographyHashPercentage(((x)/magnification), 0, (zPlusMag)/magnification)+densModifierZPlus)<noWaterPercentage)
					{
						// no water in next part
						coastIt = true;
						coastNorth = true;
					}
				}
				
				if (coastIt) 
				{
					// figure out small coasting needed for the coordinates...
					
					boolean smallCoastIt = false;
					// which side to small coast in this coastal block :-D
					boolean smallCoastNorth = false;
					boolean smallCoastSouth = false;
					boolean smallCoastWest = false;
					boolean smallCoastEast = false;

					int perVariation = (int)((getGeographyHashPercentage(x/coastPartSize, 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					// calculating neigboring coastal region's perVariations, to tell if internal small coast is needed
					int perVariationPrevX = (int)((getGeographyHashPercentage((shrinkToWorld(x-coastPartSize)/coastPartSize), 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					int perVariationNextX = (int)((getGeographyHashPercentage((shrinkToWorld(x+coastPartSize)/coastPartSize), 0, z/coastPartSize)/50d))-1; // +/- 1 cube
					int perVariationPrevZ = (int)((getGeographyHashPercentage((x/coastPartSize), 0, (shrinkToWorld(z-coastPartSize)/coastPartSize))/50d))-1; // +/- 1 cube
					int perVariationNextZ = (int)((getGeographyHashPercentage((x/coastPartSize), 0, (shrinkToWorld(z+coastPartSize)/coastPartSize))/50d))-1; // +/- 1 cube
					
					// a big lot of ugly coding here for coastal region's water-nowater calculation...
					
					if (perVariation!=0)
					{
						// no water here...deciding small coasting near the no water block's limit
						if (coastNorth)
						{
							if (localZ%magnification<=(magnification-coastPartSize) + coastPartSizeSmall)
							{
								
								smallCoastIt = true;
								smallCoastNorth  = true;
							}
							// checking neigbour coast part both sides
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
							// checking neigbour coast part both sides
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
							// checking neigbour coast part both sides
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
							// checking neigbour coast part both sides
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
							//if (coastWest || coastEast) if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("BONG BONG BONG SOUTH ERROR"); 
							if (localZ%magnification<=coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastSouth = true;
							}
							// checking neigbour coast part both sides
							// second boolean is to check if this is the end of a big block and is there a coast to this direction, if so, don't coast!
							if (perVariationPrevX!=0 && !(localX%magnification<=coastPartSize)) 
							{
								if (localX%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastWest  = true;
								}
							}
							if (perVariationNextX!=0 && !(localX%magnification>=magnification-coastPartSize))
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
							//if (coastWest || coastEast) if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("BONG BONG BONG NORTH ERROR"); 
							if (localZ%magnification>=magnification - coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastNorth = true;
							}
							// checking neigbour coast part both sides
							// second boolean is to check if this is the end of a big block and is there a coast to this direction, if so, don't coast!
							if (perVariationPrevX!=0 && !(localX%magnification<=coastPartSize))
							{
								if (localX%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastWest  = true;
								}
							}
							if (perVariationNextX!=0 && !(localX%magnification>=magnification-coastPartSize))
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
							// checking neigbour coast part both sides
							// second boolean is to check if this is the end of a big block and is there a coast to this direction, if so, don't coast!
							if (perVariationPrevZ!=0 && !(localZ%magnification<=coastPartSize))
							{
								if (localZ%coastPartSize<=coastPartSizeSmall)
								{
									smallCoastIt = true;
									smallCoastSouth  = true;
								}
							}
							if (perVariationNextZ!=0 && !(localZ%magnification>=magnification-coastPartSize))
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
							if (localX%magnification>=magnification - coastPartSizeSmall)
							{
								smallCoastIt = true;
								smallCoastEast = true;
							}
							// checking neigbour coast part both sides
							// second boolean is to check if this is the end of a big block and is there a coast to this direction, if so, don't coast!
							if (perVariationPrevZ!=0 && !(localZ%magnification<=coastPartSize))
							{
								if (localZ%coastPartSize<=coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastSouth  = true;
								}
							}
							if (perVariationNextZ!=0 && !(localZ%magnification>=magnification-coastPartSize))
							{
								if (localZ%coastPartSize>=coastPartSize-coastPartSizeSmall)
								{
									
									smallCoastIt = true;
									smallCoastNorth = true;
								}
							}
						}
						if (!smallCoastIt) 
						{
							return true;
						}
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
							return false;
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
	
	//private HashMap<Long, Boolean> tmpWaterPointCache = new HashMap<Long, Boolean>();
	
	@Override
	public boolean isWaterPoint(int x, int y, int z, boolean farView) {
		
		/*Boolean r = null;
		if (!farView)
		{
			long keyNew = Boundaries.getKey(x, y, z);
			r = tmpWaterPointCache.get(keyNew);
			if (tmpWaterPointCache.size()>100)
			{
				tmpWaterPointCache.clear();
			}
			if (r==null)
			{
				r = isWaterPointSpecial(x, y, z, true, farView); 
				tmpWaterPointCache.put(keyNew, r);
			}
			return r;
		}*/
		
		return isWaterPointSpecial(x, y, z, true, farView);
	}

	@Override
	public boolean isWaterBlock(int worldX, int worldY, int worldZ) {
		return isWaterPointSpecial(worldX, worldY, worldZ, false, false);
	}

}
