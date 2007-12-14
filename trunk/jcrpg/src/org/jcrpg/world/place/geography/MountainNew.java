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

package org.jcrpg.world.place.geography;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Surface;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.WorldSizeBitBoundaries;


public class MountainNew extends Geography implements Surface{


	public static final String TYPE_MOUNTAIN = "MOUNTAIN";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_MOUNTAIN+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK_BLOCK = new NotPassable(TYPE_MOUNTAIN+"_GROUND_ROCK");
	public static final SideSubType SUBTYPE_ROCK_BLOCK_VISIBLE = new NotPassable(TYPE_MOUNTAIN+"_GROUND_ROCK_VISIBLE");
	public static final SideSubType SUBTYPE_ROCK_SIDE = new NotPassable(TYPE_MOUNTAIN+"_GROUND_ROCK_SIDE");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_MOUNTAIN+"_GROUND");
	public static final SideSubType SUBTYPE_INTERSECT = new Climbing(TYPE_MOUNTAIN+"_GROUND_INTERSECT");
	public static final SideSubType SUBTYPE_CORNER = new Climbing(TYPE_MOUNTAIN+"_GROUND_CORNER");
	public static final SideSubType SUBTYPE_INTERSECT_EMPTY = new Climbing(TYPE_MOUNTAIN+"_GROUND_INTERSECT_EMPTY");
	public static final SideSubType SUBTYPE_INTERSECT_BLOCK = new GroundSubType(TYPE_MOUNTAIN+"_GROUND_INTERSECT_BLOCK");

	static Side[] ROCK_VISIBLE = {new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_BLOCK_VISIBLE)};
	static Side[] ROCK = {new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_BLOCK)};
	static Side[] GROUND = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND)};
	static Side[] STEEP = {new Side(TYPE_MOUNTAIN,SUBTYPE_STEEP)};
	static Side[] INTERSECT = {new Side(TYPE_MOUNTAIN,SUBTYPE_INTERSECT)};
	static Side[] CORNER = {new Side(TYPE_MOUNTAIN,SUBTYPE_CORNER)};
	static Side[] I_EMPTY = {new Side(TYPE_MOUNTAIN,SUBTYPE_INTERSECT_EMPTY)};
	static Side[] BLOCK = {new Side(TYPE_MOUNTAIN,SUBTYPE_INTERSECT_BLOCK)};
	static Side[] INTERNAL_ROCK_SIDE = null;//{new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_SIDE)};
	
	static Side[][] MOUNTAIN_ROCK = new Side[][] { null, null, null,null,null,ROCK };
	static Side[][] MOUNTAIN_ROCK_VISIBLE = new Side[][] { null, null, null,null,null,ROCK_VISIBLE };
	static Side[][] MOUNTAIN_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] MOUNTAIN_INTERSECT_NORTH = new Side[][] { INTERSECT, I_EMPTY, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_INTERSECT_EAST = new Side[][] { I_EMPTY, INTERSECT, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_INTERSECT_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, INTERSECT,I_EMPTY,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_INTERSECT_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,INTERSECT,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_CORNER_NORTH = new Side[][] { CORNER, I_EMPTY, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_CORNER_EAST = new Side[][] { I_EMPTY, CORNER, I_EMPTY,I_EMPTY,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_CORNER_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, CORNER,I_EMPTY,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_CORNER_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,CORNER,BLOCK,BLOCK };
	static Side[][] STEEP_NORTH = new Side[][] { STEEP, null, INTERNAL_ROCK_SIDE,null,null,null };
	static Side[][] STEEP_EAST = new Side[][] { null, STEEP, null,INTERNAL_ROCK_SIDE,null,null };
	static Side[][] STEEP_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, null, STEEP,null,null,null };
	static Side[][] STEEP_WEST = new Side[][] { null, INTERNAL_ROCK_SIDE, null,STEEP,null,null };


	//public int groundLevel;
	public int worldGroundLevel;
	public int worldHeight;
	private int mountainRealSizeY;
	public int blockSize; 
	
	public MountainNew(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.mountainRealSizeY = worldHeight - worldGroundLevel;
		System.out.println("MOUNTAIN SIZE = "+mountainRealSizeY+ " --- "+worldGroundLevel/magnification+" - "+ origoY);
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

	public int getPointHeight(int x, int z, int sizeX, int sizeZ)
	{
		if (x<0 || z<0 || x>=sizeX || z>=sizeZ) return 0;
		int x1 = sizeX / 2;
		int z1 = sizeZ / 2;
		
		int x2 = x;
		int z2 = z;
		
		int r = sizeX / 2;
		
		int Y = r*r - ( (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1) );
		Y+=(HashUtil.mixPercentage(x/3, z/3, 0))-50;
		int ret = Math.min(0,-Y/30); // valley
		//int ret = Math.max(0,Y/30); // mountain
		return ret;

	}
	
	public static final int K_EMPTY = -1, K_STEEP_NORTH = 0, K_STEEP_SOUTH = 2, K_STEEP_EAST = 1, K_STEEP_WEST = 3;
	public static final int K_ROCK_BLOCK = 4, K_NORMAL_GROUND = 5;
	public static final int K_INTERSECT_NORTH = 6, K_INTERSECT_EAST = 7, K_INTERSECT_SOUTH = 8, K_INTERSECT_WEST = 9;
	public static final int K_CORNER_SOUTH = 10, K_CORNER_NORTH = 11, K_CORNER_WEST = 12, K_CORNER_EAST = 13;
	
	public static HashMap<Integer, Cube> hmKindCube = new HashMap<Integer, Cube>();
	static {
		hmKindCube.put(K_EMPTY, null);
		hmKindCube.put(K_NORMAL_GROUND, new Cube(null,MOUNTAIN_GROUND,0,0,0));
		hmKindCube.put(K_ROCK_BLOCK, new Cube(null,MOUNTAIN_ROCK_VISIBLE,0,0,0));
		hmKindCube.put(K_STEEP_NORTH, new Cube(null,STEEP_NORTH,0,0,0,0));
		hmKindCube.put(K_STEEP_EAST, new Cube(null,STEEP_EAST,0,0,0,1));
		hmKindCube.put(K_STEEP_SOUTH, new Cube(null,STEEP_SOUTH,0,0,0,2));
		hmKindCube.put(K_STEEP_WEST, new Cube(null,STEEP_WEST,0,0,0,3));
		hmKindCube.put(K_INTERSECT_SOUTH, new Cube(null,MOUNTAIN_INTERSECT_SOUTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_NORTH, new Cube(null,MOUNTAIN_INTERSECT_NORTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_WEST, new Cube(null,MOUNTAIN_INTERSECT_WEST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_INTERSECT_EAST, new Cube(null,MOUNTAIN_INTERSECT_EAST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_SOUTH, new Cube(null,MOUNTAIN_CORNER_SOUTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_NORTH, new Cube(null,MOUNTAIN_CORNER_NORTH,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_WEST, new Cube(null,MOUNTAIN_CORNER_WEST,0,0,0,J3DCore.BOTTOM));
		hmKindCube.put(K_CORNER_EAST, new Cube(null,MOUNTAIN_CORNER_EAST,0,0,0,J3DCore.BOTTOM));
	}

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int NORTH_EAST = 4, SOUTH_EAST = 5, SOUTH_WEST = 6, NORTH_WEST = 7;

	public static final int C_NORMAL = 8, C_HALF = 9;
	int P_EQUAL = 0, P_GREATER = 1, P_LESSER = 2;
	int P_GE = 3, P_LE = 4;
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
			if (Y-1==directionYs[i])
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
			if (Y+1==directionYs[i])
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
	
	public int getCubeKind(int worldX, int worldY, int worldZ)
	{
		int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldY, worldZ);
		int realSizeX = blockSize;//blockUsedSize[0]; 
		int realSizeY = (int) ( (mountainRealSizeY-1) * ( ((Math.min(blockUsedSize[0],blockUsedSize[1])))*1d/blockSize ) );
		int realSizeZ = blockSize;//blockUsedSize[1];
		int relX = (worldX%blockSize);//-(blockSize-realSizeX)/2;//-origoX*magnification;
		int relY = worldY-worldGroundLevel;
		int relZ = (worldZ%blockSize);//-(blockSize-realSizeZ)/2;//-origoZ*magnification;
		
		int Y = getPointHeight(relX, relZ, realSizeX, realSizeZ);
		int YNorth = getPointHeight(relX, relZ+1, realSizeX, realSizeZ);
		int YNorthEast = getPointHeight(relX+1, relZ+1, realSizeX, realSizeZ);
		int YNorthWest = getPointHeight(relX-1, relZ+1, realSizeX, realSizeZ);
		int YSouth = getPointHeight(relX, relZ-1, realSizeX, realSizeZ);
		int YSouthEast = getPointHeight(relX+1, relZ-1, realSizeX, realSizeZ);
		int YSouthWest = getPointHeight(relX-1, relZ-1, realSizeX, realSizeZ);
		int YWest = getPointHeight(relX-1, relZ, realSizeX, realSizeZ);
		int YEast = getPointHeight(relX+1, relZ, realSizeX, realSizeZ);

		int[][] eval = evaluate(Y, new int[]{YNorth,YEast,YSouth,YWest,YNorthEast, YSouthEast, YSouthWest, YNorthWest});
		if (Y==relY) 
		{
			// 0 half side is bigger
			/*if (eval[P_LESSER][C_HALF]==0 && eval[P_GE][C_HALF]==)
			{
				if (eval[P_EQUAL][C_NORMAL]==3 || eval[P_EQUAL][C_NORMAL]==2 && eval[P_GREATER][C_NORMAL]==1)
				{
					if (eval[P_LESSER][NORTH]==1)
					{
						return K_STEEP_SOUTH;
					}
					if (eval[P_LESSER][EAST]==1)
					{
						return K_STEEP_WEST;
					}
					if (eval[P_LESSER][SOUTH]==1)
					{
						return K_STEEP_NORTH;
					}
					if (eval[P_LESSER][WEST]==1)
					{
						return K_STEEP_EAST;
					}
				}
			}*/
			if (eval[P_LESSER][C_HALF]==3)
			{
				//if (eval[P_LESSER][C_NORMAL]==2)
				//{
					return K_CORNER_EAST;
				//}
				
			}			
			// two half side is bigger
			if (eval[P_LESSER][C_HALF]==2)
			{
				if (eval[P_LESSER][NORTH_EAST] == 1 && eval[P_LESSER][NORTH_WEST]==1)
				{
					if (eval[P_LESSER][C_NORMAL]==1)
					{
						if (eval[P_LESSER][NORTH]==1)
						{
							return K_STEEP_SOUTH;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==2) {
						if (eval[P_LESSER][NORTH]==1 && eval[P_LESSER][WEST]==1)
						{
							return K_CORNER_EAST;
						}
						if (eval[P_LESSER][NORTH]==1 && eval[P_LESSER][EAST]==1)
						{
							return K_CORNER_SOUTH;
						}
					}
				}
				if (eval[P_LESSER][NORTH_EAST] == 1 && eval[P_LESSER][SOUTH_EAST]==1)
				{
					if (eval[P_LESSER][C_NORMAL]==1)
					{
						if (eval[P_LESSER][EAST]==1)
						{
							return K_STEEP_WEST;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==2) {
						if (eval[P_LESSER][EAST]==1 && eval[P_LESSER][SOUTH]==1)
						{
							return K_CORNER_WEST;
						}
						if (eval[P_LESSER][EAST]==1 && eval[P_LESSER][NORTH]==1)
						{
							return K_CORNER_SOUTH;
						}
					}
				}
				if (eval[P_LESSER][SOUTH_EAST] == 1 && eval[P_LESSER][SOUTH_WEST]==1)
				{
					if (eval[P_LESSER][C_NORMAL]==1)
					{
						if (eval[P_LESSER][SOUTH]==1)
						{
							return K_STEEP_NORTH;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==2) {
						if (eval[P_LESSER][SOUTH]==1 && eval[P_LESSER][WEST]==1)
						{
							return K_CORNER_NORTH;
						}
						if (eval[P_LESSER][SOUTH]==1 && eval[P_LESSER][EAST]==1)
						{
							return K_CORNER_WEST;
						}
					}
				}
				if (eval[P_LESSER][SOUTH_WEST] == 1 && eval[P_LESSER][NORTH_WEST]==1)
				{
					if (eval[P_LESSER][C_NORMAL]==1)
					{
						if (eval[P_LESSER][WEST]==1)
						{
							return K_STEEP_EAST;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==2) {
						if (eval[P_LESSER][WEST]==1 && eval[P_LESSER][NORTH]==1)
						{
							return K_CORNER_EAST;
						}
						if (eval[P_LESSER][WEST]==1 && eval[P_LESSER][SOUTH]==1)
						{
							return K_CORNER_NORTH;
						}
					}
				}
			}
			
			
			// one half side is bigger
			
			if (eval[P_LESSER][C_HALF]==1 && eval[P_EQUAL][C_HALF]==3)
			{
				if (eval[P_LESSER][NORTH_EAST]==1)
				{
					if (eval[P_EQUAL][NORTH]==1 && eval[P_EQUAL][EAST]==1)
					{
						return K_INTERSECT_WEST;
					}
					if (eval[P_EQUAL][NORTH]==0 && eval[P_EQUAL][EAST]==0)
					{
						return K_CORNER_SOUTH;
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]==0 || eval[P_GREATER][NORTH]==0 || eval[P_GREATER][EAST]==0)
					{
						if (eval[P_LESSER][NORTH]==1)
						{
							return K_STEEP_SOUTH;
						}
						if (eval[P_LESSER][EAST]==1)
						{
							return K_STEEP_WEST;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]!=0)
					{
						if (eval[P_LESSER][NORTH]==1)
						{
							return K_CORNER_WEST;
						}
						if (eval[P_LESSER][EAST]==1)
						{
							return K_CORNER_SOUTH;
						}
					}
				}
				if (eval[P_LESSER][NORTH_WEST]==1) // good
				{
					if (eval[P_EQUAL][NORTH]==1 && eval[P_EQUAL][WEST]==1)
					{
						return K_INTERSECT_SOUTH;
					}
					if (eval[P_EQUAL][NORTH]==0 && eval[P_EQUAL][WEST]==0)
					{
						return K_CORNER_EAST;
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]==0 || eval[P_GREATER][NORTH]==0 || eval[P_GREATER][WEST]==0)
					{
						if (eval[P_LESSER][NORTH]==1)
						{
							return K_STEEP_SOUTH;
						}
						if (eval[P_LESSER][WEST]==1)
						{
							return K_STEEP_EAST;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]!=0)
					{
						if (eval[P_LESSER][NORTH]==1)
						{
							return K_CORNER_SOUTH;
						}
						if (eval[P_LESSER][WEST]==1)
						{
							return K_CORNER_EAST;
						}
					}
				}
				if (eval[P_LESSER][SOUTH_EAST]==1)
				{
					if (eval[P_EQUAL][SOUTH]==1 && eval[P_EQUAL][EAST]==1)
					{
						return K_INTERSECT_NORTH;
					}
					if (eval[P_EQUAL][SOUTH]==0 && eval[P_EQUAL][EAST]==0)
					{
						return K_CORNER_WEST;
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]==0 || eval[P_GREATER][SOUTH]==0 || eval[P_GREATER][EAST]==0)
					{
						if (eval[P_LESSER][SOUTH]==1)
						{
							return K_STEEP_NORTH;
						}
						if (eval[P_LESSER][EAST]==1)
						{
							return K_STEEP_WEST;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]!=0)
					{
						if (eval[P_LESSER][SOUTH]==1)
						{
							return K_CORNER_NORTH;
						}
						if (eval[P_LESSER][EAST]==1)
						{
							return K_CORNER_WEST;
						}
					}
				}
				if (eval[P_LESSER][SOUTH_WEST]==1) // good
				{
					if (eval[P_EQUAL][SOUTH]==1 && eval[P_EQUAL][WEST]==1)
					{
						return K_INTERSECT_EAST;
					}
					if (eval[P_EQUAL][SOUTH]==0 && eval[P_EQUAL][WEST]==0)
					{
						return K_CORNER_NORTH;
					}
					if (eval[P_LESSER][C_NORMAL]==1 && (eval[P_GREATER][C_HALF]==0 || eval[P_GREATER][SOUTH]==0 || eval[P_GREATER][WEST]==0) )
					{
						if (eval[P_LESSER][SOUTH]==1)
						{
							return K_STEEP_NORTH;
						}
						if (eval[P_LESSER][WEST]==1)
						{
							return K_STEEP_EAST;
						}
					}
					if (eval[P_LESSER][C_NORMAL]==1 && eval[P_GREATER][C_HALF]!=0)
					{
						if (eval[P_LESSER][SOUTH]==1)
						{
							return K_CORNER_EAST;
						}
						if (eval[P_LESSER][WEST]==1)
						{
							return K_CORNER_NORTH;
						}
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
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ) {
		int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, 0, worldZ);
		int realSizeX = blockSize;//blockUsedSize[0]; 
		int realSizeY = (int) ( (mountainRealSizeY-1) * ( ((Math.min(blockUsedSize[0],blockUsedSize[1])))*1d/blockSize ) );
		int realSizeZ = blockSize;//blockUsedSize[1];
		int relX = (worldX%blockSize);//-(blockSize-realSizeX)/2;//-origoX*magnification;
		//int relY = worldY-worldGroundLevel;
		int relZ = (worldZ%blockSize);//-(blockSize-realSizeZ)/2;//-origoZ*magnification;

		int Y = getPointHeight(relX, relZ, realSizeX, realSizeZ);
		int kind = getCubeKind(worldX, Y, worldZ);
		if (kind>=0 && kind<=4)
		{
			return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+Y,true,kind)};
		} else
		if (kind>=6)
		{
			return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+Y,true,J3DCore.BOTTOM)};
		}
		return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+Y,true,SurfaceHeightAndType.NOT_STEEP)};
	}


	

}