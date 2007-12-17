/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.world.place;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.flora.FloraCube;
import org.jcrpg.world.ai.flora.FloraDescription;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.time.Time;

public class Geography extends Place implements Surface {
	
	public int worldGroundLevel, worldHeight, blockSize, worldRealHeight;

	public static final String TYPE_GEO = "GEO";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_GEO+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK_BLOCK = new NotPassable(TYPE_GEO+"_GROUND_ROCK");
	public static final SideSubType SUBTYPE_ROCK_BLOCK_VISIBLE = new NotPassable(TYPE_GEO+"_GROUND_ROCK_VISIBLE");
	public static final SideSubType SUBTYPE_ROCK_SIDE = new NotPassable(TYPE_GEO+"_GROUND_ROCK_SIDE");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_GEO+"_GROUND");
	public static final SideSubType SUBTYPE_INTERSECT = new Climbing(TYPE_GEO+"_GROUND_INTERSECT");
	public static final SideSubType SUBTYPE_CORNER = new Climbing(TYPE_GEO+"_GROUND_CORNER");
	public static final SideSubType SUBTYPE_INTERSECT_EMPTY = new Climbing(TYPE_GEO+"_GROUND_INTERSECT_EMPTY");
	public static final SideSubType SUBTYPE_INTERSECT_BLOCK = new GroundSubType(TYPE_GEO+"_GROUND_INTERSECT_BLOCK");

	static Side[] ROCK_VISIBLE = {new Side(TYPE_GEO,SUBTYPE_ROCK_BLOCK_VISIBLE)};
	static Side[] ROCK = {new Side(TYPE_GEO,SUBTYPE_ROCK_BLOCK)};
	static Side[] GROUND = {new Side(TYPE_GEO,SUBTYPE_GROUND)};
	static Side[] STEEP = {new Side(TYPE_GEO,SUBTYPE_STEEP)};
	static Side[] INTERSECT = {new Side(TYPE_GEO,SUBTYPE_INTERSECT)};
	static Side[] CORNER = {new Side(TYPE_GEO,SUBTYPE_CORNER)};
	static Side[] I_EMPTY = {new Side(TYPE_GEO,SUBTYPE_INTERSECT_EMPTY)};
	static Side[] BLOCK = {new Side(TYPE_GEO,SUBTYPE_INTERSECT_BLOCK)};
	static Side[] INTERNAL_ROCK_SIDE = null;//{new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_SIDE)};
	
	static Side[][] GEO_ROCK = new Side[][] { null, null, null,null,null,ROCK };
	static Side[][] GEO_ROCK_VISIBLE = new Side[][] { null, null, null,null,null,ROCK_VISIBLE };
	static Side[][] GEO_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] GEO_INTERSECT_NORTH = new Side[][] { INTERSECT, I_EMPTY, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_INTERSECT_EAST = new Side[][] { I_EMPTY, INTERSECT, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_INTERSECT_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, INTERSECT,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_INTERSECT_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,INTERSECT,BLOCK,BLOCK };
	static Side[][] GEO_CORNER_NORTH = new Side[][] { CORNER, I_EMPTY, I_EMPTY,I_EMPTY,null,null};
	static Side[][] GEO_CORNER_EAST = new Side[][] { I_EMPTY, CORNER, I_EMPTY,I_EMPTY,null,null};
	static Side[][] GEO_CORNER_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, CORNER,I_EMPTY,null,null};
	static Side[][] GEO_CORNER_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,CORNER,null,null };
	static Side[][] GEO_STEEP_NORTH = new Side[][] { STEEP, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_STEEP_EAST = new Side[][] { I_EMPTY, STEEP, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,BLOCK };
	static Side[][] GEO_STEEP_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STEEP,I_EMPTY,BLOCK,BLOCK };
	static Side[][] GEO_STEEP_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STEEP,BLOCK,BLOCK };
	
	
	public static final int K_EMPTY = -1, K_STEEP_NORTH = 0, K_STEEP_SOUTH = 2, K_STEEP_EAST = 1, K_STEEP_WEST = 3;
	public static final int K_ROCK_BLOCK = 4, K_NORMAL_GROUND = 5;
	public static final int K_INTERSECT_NORTH = 6, K_INTERSECT_EAST = 7, K_INTERSECT_SOUTH = 8, K_INTERSECT_WEST = 9;
	public static final int K_CORNER_SOUTH = 10, K_CORNER_NORTH = 11, K_CORNER_WEST = 12, K_CORNER_EAST = 13;

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int NORTH_EAST = 4, SOUTH_EAST = 5, SOUTH_WEST = 6, NORTH_WEST = 7;

	public static final int C_NORMAL = 8, C_HALF = 9;
	public static final int P_EQUAL = 0, P_GREATER = 1, P_LESSER = 2;
	public static final int P_GE = 3, P_LE = 4;


	
	public static HashMap<Long, Integer> quickCubeKindCache = new HashMap<Long, Integer>();
	
	public static HashMap<Integer, Cube> hmKindCube = new HashMap<Integer, Cube>();
	static {
		hmKindCube.put(K_EMPTY, null);
		hmKindCube.put(K_NORMAL_GROUND, new Cube(null,GEO_GROUND,0,0,0));
		hmKindCube.put(K_ROCK_BLOCK, new Cube(null,0,GEO_ROCK_VISIBLE,0,0,0));
		hmKindCube.put(K_STEEP_NORTH, new Cube(null,GEO_STEEP_NORTH,0,0,0,0));
		hmKindCube.put(K_STEEP_EAST, new Cube(null,GEO_STEEP_EAST,0,0,0,1));
		hmKindCube.put(K_STEEP_SOUTH, new Cube(null,GEO_STEEP_SOUTH,0,0,0,2));
		hmKindCube.put(K_STEEP_WEST, new Cube(null,GEO_STEEP_WEST,0,0,0,3));
		hmKindCube.put(K_INTERSECT_SOUTH, new Cube(null,GEO_INTERSECT_SOUTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_NORTH, new Cube(null,GEO_INTERSECT_NORTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_WEST, new Cube(null,GEO_INTERSECT_WEST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_EAST, new Cube(null,GEO_INTERSECT_EAST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_SOUTH, new Cube(null,GEO_CORNER_SOUTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_NORTH, new Cube(null,GEO_CORNER_NORTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_WEST, new Cube(null,GEO_CORNER_WEST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_EAST, new Cube(null,GEO_CORNER_EAST,0,0,0,J3DCore.BOTTOM));
	}
	public Geography(String id, Place parent, PlaceLocator loc)
	{
		super(id,parent, loc);
	}
	public Geography(String id, Place parent,PlaceLocator loc,int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id,parent, loc);
		worldRealHeight = worldHeight - worldGroundLevel;
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.worldRealHeight = worldHeight - worldGroundLevel;
		System.out.println("MOUNTAIN SIZE = "+worldRealHeight+ " --- "+worldGroundLevel/magnification+" - "+ origoY);
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		//this.groundLevel = groundLevel;
		this.worldGroundLevel=worldGroundLevel;
		this.worldHeight = worldHeight;
		this.blockSize = magnification;
		if (fillBoundaries)
			setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		else
			setBoundaries(new WorldSizeBitBoundaries(magnification,(World)parent));
	}

	@Override
	public boolean generateModel() {
		return false;
	}

	@Override
	public boolean loadModelFromFile() {
		return false;
	}

	
	public Cube getFloraCube(int worldX, int worldY, int worldZ, CubeClimateConditions conditions, Time time, boolean onSteep)
	{
		World w = (World)getRoot();
		Cube floraCube = null;
		FloraCube fC = w.getFloraContainer().getFlora(worldX,worldY,worldZ,this.getClass(), conditions, time, onSteep);
		for (FloraDescription fd : fC.descriptions)
		{
			if (floraCube==null) {
				floraCube = fd.instanciateCube(w, worldX,worldY,worldZ);				
			}
			else {
				floraCube = new Cube(floraCube,fd.cubicForm,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
		}
		//if (floraCube!=null) floraCube
		return floraCube;
	}

	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		int kind = getCubeKind(worldX, worldY, worldZ);
		Cube c = hmKindCube.get(kind);
		if (c==null) return null;
		c = c.copy(this);
		c.x = worldX;
		c.y = worldY;
		c.z = worldZ;
		return c;
	}
	
	/**
	 * Tells the geo hashed generic size on a given block - usable for example Mountain - Cave relation.
	 * @param blockSize
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return The X and Z size of the used area in the block.
	 */
	public int[] getBlocksGenericSize(int blockSize, int worldX, int worldZ)
	{
		int realSizeX = blockSize-1 - (int)( (getGeographyHashPercentage(worldX/blockSize, 0, worldZ/blockSize)/50d)*(blockSize/2) );
		int realSizeZ = blockSize-1 - (int)( (getGeographyHashPercentage(worldZ/blockSize, 0, worldX/blockSize)/50d)*(blockSize/2) );
		realSizeX-=realSizeX%2;
		realSizeZ-=realSizeZ%2;
		return new int[]{realSizeX,realSizeZ};
	}
	
	
	/**
	 * 0 "==" , 1 ">" , 2 "<"
	 * @param Y
	 * @param directionYs
	 * @return
	 */
	public int[][] evaluate(int Y, int[] directionYs)
	{
		int[][] ret = new int[P_LE+1][C_HALF+1];
		int countEqual = 0, countGreater = 0, countLess = 0,countGreaterEq = 0, countLessEq = 0;
		int countEqual2 = 0, countGreater2 = 0, countLess2 = 0,countGreaterEq2 = 0, countLessEq2 = 0;
		for (int i=0; i<directionYs.length; i++)
		{
			if (Y==directionYs[i])
			{
				if (i<4)
				{
					countEqual++;
					ret[P_EQUAL][i] = 1;
				} else
				{
					countEqual2++;
					ret[P_EQUAL][i] = 1;
				}
			}
			if (Y>directionYs[i])
			{
				if (i<4)
				{
					countGreater++;
					ret[P_GREATER][i] = 1;
				} else
				{
					countGreater2++;
					ret[P_GREATER][i] = 1;
				}
			}
			if (Y<directionYs[i])
			{
				if (i<4)
				{
					countLess++;
					ret[P_LESSER][i] = 1;
				} else
				{
					countLess2++;
					ret[P_LESSER][i] = 1;
				}
			}
			if (Y>=directionYs[i])
			{
				if (i<4)
				{
					countGreaterEq++;
					ret[P_GE][i] = 1;
				} else
				{
					countGreaterEq2++;
					ret[P_GE][i] = 1;
				}
			}
			if (Y<=directionYs[i])
			{
				if (i<4)
				{
					countLessEq++;
					ret[P_LE][i] = 1;
				} else
				{
					countLessEq2++;
					ret[P_LE][i] = 1;
				}
			}
		
		}
		ret[P_EQUAL][C_NORMAL] = countEqual;
		ret[P_GREATER][C_NORMAL] = countGreater;
		ret[P_LESSER][C_NORMAL] = countLess;
		ret[P_GE][C_NORMAL] = countGreaterEq;
		ret[P_LE][C_NORMAL] = countLessEq;
		ret[P_EQUAL][C_HALF] = countEqual2;
		ret[P_GREATER][C_HALF] = countGreater2;
		ret[P_LESSER][C_HALF] = countLess2;
		ret[P_GE][C_HALF] = countGreaterEq2;
		ret[P_LE][C_HALF] = countLessEq2;
		return ret;
		
	}
	
	public int getPointHeightOutside(int worldX, int worldZ)
	{
		for (Geography geo:((World)getRoot()).geographies.values())
		{
			if (this!=geo)
			{
				if (geo.boundaries.isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					int[] values = geo.calculateTransformedCoordinates(worldX, worldGroundLevel, worldZ);
					return geo.getPointHeight(values[3], values[5], values[0], values[2],worldX,worldZ);
				}
			}
		}
		return 0;
	}
	
	public int getPointHeight(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ)
	{
		if (!boundaries.isInside(worldX, worldGroundLevel, worldZ))
		{
			return getPointHeightOutside(worldX, worldZ);
		}
		return 0;
	}
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ) {
		int[] values = calculateTransformedCoordinates(worldX, worldGroundLevel, worldZ);
		//int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = values[0];
		//int realSizeY = values[1];
		int realSizeZ = values[2];
		int relX = values[3];
		//int relY = values[4];
		int relZ = values[5];

		int Y = getPointHeight(relX, relZ, realSizeX, realSizeZ,worldX,worldZ);
		int kind = getCubeKind(worldX, Y, worldZ);
		if (kind>=0 && kind<=4)
		{
			return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+Y,true,kind)};
		} else
		if (kind>=6)
		{
			return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+Y,false,J3DCore.BOTTOM)};
		}
		return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+Y,true,SurfaceHeightAndType.NOT_STEEP)};
	}
	
	/**
	 * return 0 realSizeX, 1 realsizeY, 2 realSizeZ, 3 relX, 4 relY, 5 relZ 
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public int[] calculateTransformedCoordinates(int worldX, int worldY, int worldZ)
	{
		// 0 realSizeX, 1 realsizeY, 2 realSizeZ, 3 relX, 4 relY, 5 relZ 
		return new int[]{blockSize,worldRealHeight,blockSize,worldX%blockSize,worldY-worldGroundLevel,worldZ%blockSize};
	}

	/**
	 * Get Cube Kind from other outside geographies than the current geography.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public int getCubeKindOutside(int worldX, int worldY, int worldZ)
	{
		for (Geography geo:((World)getRoot()).geographies.values())
		{
			if (this!=geo)
			{
				if (geo.boundaries.isInside(worldX, worldY, worldZ))
				{
					return geo.getCubeKind(worldX,worldY,worldZ);
				}
			}
		}
		return K_EMPTY;
	}

	/**
	 * Gets the cubekind of a coordinate based on the height and the height of neighbor points.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public int getCubeKind(int worldX, int worldY, int worldZ)
	{
		if (numericId!=0) 
		{
			long key = numericId+((worldX)<< 16) + ((worldY) << 8) + ((worldZ));
			Integer cachedKind = quickCubeKindCache.get(key);
			if (cachedKind!=null) 
			{
				//System.out.println("CUBE CACHE USED!");
				return cachedKind;
			}
			int kind = getCubeKindNoCache(worldX, worldY, worldZ);
			if (quickCubeKindCache.size()>5)
			{
				quickCubeKindCache.clear();
			}
			quickCubeKindCache.put(key, kind);
			return kind;
		} else
		{
			// no right unique numbericId, use no cache 
			return getCubeKindNoCache(worldX, worldY, worldZ);
		}
	}
	
	private int getCubeKindNoCache(int worldX, int worldY, int worldZ)
	{

		int[] values = calculateTransformedCoordinates(worldX, worldY, worldZ);
		//int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = values[0];
		//int realSizeY = values[1];
		int realSizeZ = values[2];
		int relX = values[3];
		int relY = values[4];
		int relZ = values[5];
		
		int Y = getPointHeight(relX, relZ, realSizeX, realSizeZ,worldX,worldZ);
		
		int YNorth = getPointHeight(relX, relZ+1, realSizeX, realSizeZ,worldX,shrinkToWorld(worldZ+1));
		int YNorthEast = getPointHeight(relX+1, relZ+1, realSizeX, realSizeZ,shrinkToWorld(worldX+1),shrinkToWorld(worldZ+1));
		int YNorthWest = getPointHeight(relX-1, relZ+1, realSizeX, realSizeZ,shrinkToWorld(worldX-1),shrinkToWorld(worldZ+1));
		int YSouth = getPointHeight(relX, relZ-1, realSizeX, realSizeZ,worldX,shrinkToWorld(worldZ-1));
		int YSouthEast = getPointHeight(relX+1, relZ-1, realSizeX, realSizeZ,shrinkToWorld(worldX+1),shrinkToWorld(worldZ-1));
		int YSouthWest = getPointHeight(relX-1, relZ-1, realSizeX, realSizeZ,shrinkToWorld(worldX-1),shrinkToWorld(worldZ-1));
		int YWest = getPointHeight(relX-1, relZ, realSizeX, realSizeZ,shrinkToWorld(worldX-1),worldZ);
		int YEast = getPointHeight(relX+1, relZ, realSizeX, realSizeZ,shrinkToWorld(worldX+1),worldZ);

		//if (this instanceof Plain) System.out.println("-- RELY - "+relY+" - "+Y);
		int[][] eval = evaluate(Y, new int[]{YNorth,YEast,YSouth,YWest,YNorthEast, YSouthEast, YSouthWest, YNorthWest});
		if (Y==relY) 
		{

			if (eval[P_LESSER][C_NORMAL]==3)
			{
				if (eval[P_GE][NORTH]==1)
					return K_STEEP_NORTH;
				if (eval[P_GE][EAST]==1)
					return K_STEEP_EAST;
				if (eval[P_GE][SOUTH]==1)
					return K_STEEP_SOUTH;
				if (eval[P_GE][WEST]==1)
					return K_STEEP_WEST;
			}
			if (eval[P_LESSER][C_NORMAL]==1)
			{
				if (eval[P_LESSER][NORTH]==1)
					return K_STEEP_SOUTH;
				if (eval[P_LESSER][EAST]==1)
					return K_STEEP_WEST;
				if (eval[P_LESSER][SOUTH]==1)
					return K_STEEP_NORTH;
				if (eval[P_LESSER][WEST]==1)
					return K_STEEP_EAST;
			}
			if (eval[P_LESSER][C_NORMAL]==4)
			{
				int per = HashUtil.mixPercentage(worldX, worldY, worldZ);
				if (per<25)
					return K_STEEP_NORTH;
				if (per<50)
					return K_STEEP_EAST;
				if (per<75)
					return K_STEEP_SOUTH;
				return K_STEEP_WEST;
			}
			
			if (eval[P_LESSER][C_NORMAL]==2)
			{
				if (eval[P_LESSER][NORTH]==1 && eval[P_LESSER][EAST]==1)
				{
					return K_CORNER_SOUTH;
				}
				if (eval[P_LESSER][NORTH]==1 && eval[P_LESSER][WEST]==1)
				{
					return K_CORNER_EAST;
				}
				if (eval[P_LESSER][SOUTH]==1 && eval[P_LESSER][EAST]==1)
				{
					return K_CORNER_WEST;
				}
				if (eval[P_LESSER][SOUTH]==1 && eval[P_LESSER][WEST]==1)
				{
					return K_CORNER_NORTH;
				}
			}

			
			
			// one half side is bigger
			
			if (eval[P_LESSER][C_HALF]==1)// && eval[P_EQUAL][C_HALF]==3)
			{
				if (eval[P_LESSER][NORTH_EAST]==1)
				{
					if (eval[P_EQUAL][NORTH]==1 && eval[P_EQUAL][EAST]==1)
					{
						return K_INTERSECT_WEST;
					}
				}
				if (eval[P_LESSER][NORTH_WEST]==1) // good
				{
					if (eval[P_EQUAL][NORTH]==1 && eval[P_EQUAL][WEST]==1)
					{
						return K_INTERSECT_SOUTH;
					}
				}
				if (eval[P_LESSER][SOUTH_EAST]==1)
				{
					if (eval[P_EQUAL][SOUTH]==1 && eval[P_EQUAL][EAST]==1)
					{
						return K_INTERSECT_NORTH;
					}
				}
				if (eval[P_LESSER][SOUTH_WEST]==1) // good
				{
					if (eval[P_EQUAL][SOUTH]==1 && eval[P_EQUAL][WEST]==1)
					{
						return K_INTERSECT_EAST;
					}
				}
				
				
			}
			
			return K_NORMAL_GROUND;
		}
		// checking if there are lower parts on neighbor cubes that would make an empty cube visible - if so place a rock block instead
		if (Y>relY && (relY-1>=YNorth || relY-1>=YSouth|| relY-1>=YWest || relY-1>=YEast)) return K_ROCK_BLOCK; //
		if (Y>relY && (relY==YNorth || relY==YSouth|| relY==YWest || relY==YEast)) 
		{
			int i=0;
			if (relY==YNorth+1) i++;
			if (relY==YWest+1) i++;
			if (relY==YSouth+1) i++;
			if (relY==YEast+1) i++;
			if (i<=1)			
			return K_ROCK_BLOCK; //
		}
		return K_EMPTY;
	}
	
}
