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

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
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
	public static final SideSubType SUBTYPE_INTERSECT_EMPTY = new Climbing(TYPE_MOUNTAIN+"_GROUND_INTERSECT_EMPTY");
	public static final SideSubType SUBTYPE_INTERSECT_BLOCK = new GroundSubType(TYPE_MOUNTAIN+"_GROUND_INTERSECT_BLOCK");

	static Side[] ROCK_VISIBLE = {new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_BLOCK_VISIBLE)};
	static Side[] ROCK = {new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK_BLOCK)};
	static Side[] GROUND = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND)};
	static Side[] STEEP = {new Side(TYPE_MOUNTAIN,SUBTYPE_STEEP)};
	static Side[] INTERSECT = {new Side(TYPE_MOUNTAIN,SUBTYPE_INTERSECT)};
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
		int x1 = sizeX / 2;
		int z1 = sizeZ / 2;
		
		int x2 = x;
		int z2 = z;
		
		int r = sizeX / 2 + sizeX / 8;
		
		int Y = r*r - ( (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1) );
		return Y;

	}
	
	public static final int K_STEEP_NORTH = 0, K_STEEP_SOUTH = 2, K_STEEP_EAST = 1, K_STEEP_WEST = 3;
	public static final int K_ROCK_BLOCK = 4, K_NORMAL_GROUND = 5;
	public static final int K_INTERSECT_NORTH_EAST = 6, K_INTERSECT_EAST_SOUTH = 7, K_INTERSECT_SOUTH_WEST = 8, K_INTERSECT_WEST_NORTH = 9;
	
	
	public int getCubeKind(int worldX, int worldY, int worldZ)
	{
		int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldY, worldZ);
		int realSizeX = blockUsedSize[0]; 
		int realSizeY = (int) ( (mountainRealSizeY-1) * ( ((Math.min(blockUsedSize[0],blockUsedSize[1])))*1d/blockSize ) );
		int realSizeZ = blockUsedSize[1];
		int relX = (worldX%blockSize)-(blockSize-realSizeX)/2;//-origoX*magnification;
		int relY = worldY-worldGroundLevel;
		int relZ = (worldZ%blockSize)-(blockSize-realSizeZ)/2;//-origoZ*magnification;
		
		int Y = getPointHeight(relX, relZ, realSizeX, realSizeZ);
		int YNorth = getPointHeight(relX, relZ+1, realSizeX, realSizeZ);
		int YSouth = getPointHeight(relX, relZ-1, realSizeX, realSizeZ);
		int YWest = getPointHeight(relX-1, relZ, realSizeX, realSizeZ);
		int YEast = getPointHeight(relX+1, relZ, realSizeX, realSizeZ);
		return K_NORMAL_GROUND;
		
	}

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		int kind = getCubeKind(worldX, worldY, worldZ);
		return null;
	}

	int GROUND_LEVEL = 0;
	int GROUND_LEVEL_CONTAINER = 1;
	
	
	
	public int[] isGroundLevel(int worldX, int worldY, int worldZ) {
		int kind = getCubeKind(worldX, worldY, worldZ);

		return new int[]{GROUND_LEVEL,SurfaceHeightAndType.NOT_STEEP};
		//return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
	}
	
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ) {
		int realSizeY = mountainRealSizeY-1;
		for (int i=0; i<=realSizeY; i++)
		{
			int[] ret = isGroundLevel(worldX, worldGroundLevel+i, worldZ);
			if (ret[0]>-1)
			{
				int r = ret[1];
				return new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel+i,true,r)};
			}
		}
		return  new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldHeight,false,SurfaceHeightAndType.NOT_STEEP)};
	}


	

}
