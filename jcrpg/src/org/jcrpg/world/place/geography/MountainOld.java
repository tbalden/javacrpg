/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.place.geography;

import org.jcrpg.apps.Jcrpg;
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


public class MountainOld extends Geography implements Surface{


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
	
	public MountainOld(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.mountainRealSizeY = worldHeight - worldGroundLevel;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("MOUNTAIN SIZE = "+mountainRealSizeY+ " --- "+worldGroundLevel/magnification+" - "+ origoY);
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
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = blockUsedSize[0]; 
		int realSizeY = (int) ( (mountainRealSizeY-1) * ( ((Math.min(blockUsedSize[0],blockUsedSize[1])))*1d/blockSize ) );
		int realSizeZ = blockUsedSize[1];
		int relX = (worldX%blockSize)-(blockSize-realSizeX)/2;//-origoX*magnification;
		int relY = worldY-worldGroundLevel;
		int relZ = (worldZ%blockSize)-(blockSize-realSizeZ)/2;//-origoZ*magnification;
		
		if (relY>realSizeY) return null;
		
		if (relX<0 || relZ<0 || relX>realSizeX || relZ>realSizeZ)
		{
			if (relY==0) 
			{
				Cube c = new Cube(this,MOUNTAIN_GROUND,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
				return c;
			}
			return null;
			
		}
		
		int proportionateXSizeOnLevelY = realSizeX - (int)(realSizeX * ((relY*1d)/(realSizeY)));
		int proportionateZSizeOnLevelY = realSizeZ - (int)(realSizeZ * ((relY*1d)/(realSizeY)));
		int gapX = ((realSizeX) - proportionateXSizeOnLevelY)/2;
		int gapZ = ((realSizeZ) - proportionateZSizeOnLevelY)/2;
		int proportionateXSizeOnLevelYNext = realSizeX - (int)(realSizeX * (((relY+1)*1d)/(realSizeY)));
		int proportionateZSizeOnLevelYNext = realSizeZ - (int)(realSizeZ * (((relY+1)*1d)/(realSizeY)));
		int gapXNext = ((realSizeX) - proportionateXSizeOnLevelYNext)/2;
		int gapZNext = ((realSizeZ) - proportionateZSizeOnLevelYNext)/2;

		int proportionateXSizeOnLevelYPrev = realSizeX - (int)(realSizeX * (((relY-1)*1d)/(realSizeY)));
		int proportionateZSizeOnLevelYPrev = realSizeZ - (int)(realSizeZ * (((relY-1)*1d)/(realSizeY)));
		int gapXPrevious = ((realSizeX) - proportionateXSizeOnLevelYPrev)/2;
		int gapZPrevious = ((realSizeZ) - proportionateZSizeOnLevelYPrev)/2;

		
		boolean returnCube = false;
		Side[][] returnSteep = null;
		int steepDirection = SurfaceHeightAndType.NOT_STEEP; //W E S N

		int overwritePower = 1;
		
		// *** Intersections at the corner of the mountain ***
		if (relX == gapX && relZ == gapZ) {
			returnCube = true;
			if (relX == gapXNext && relZ == gapZNext) {
				returnSteep = MOUNTAIN_ROCK_VISIBLE; // too steep mountain needs Rock
			} else {
				returnSteep = MOUNTAIN_INTERSECT_WEST;
				steepDirection = J3DCore.BOTTOM;
			}
		} 
		if (relX == gapX && relZ == realSizeZ - gapZ) {

			returnCube = true;
			if (relX == gapXNext && relZ == realSizeZ - gapZNext) {
				returnSteep = MOUNTAIN_ROCK_VISIBLE; // too steep mountain needs Rock
			} else {
				returnSteep = MOUNTAIN_INTERSECT_NORTH;
				steepDirection = J3DCore.BOTTOM;
			}
		} 
		if (relX == realSizeX - gapX && relZ == gapZ) {
			returnCube = true;
			if (relX == realSizeX - gapXNext && relZ == gapZNext) {
				returnSteep = MOUNTAIN_ROCK_VISIBLE; // too steep mountain needs Rock
			} else {
				returnSteep = MOUNTAIN_INTERSECT_SOUTH;
				steepDirection = J3DCore.BOTTOM;
			}
		} 
		if (relX == realSizeX - gapX && relZ == realSizeZ - gapZ) {
			returnCube = true;
			if (relX == realSizeX - gapXNext && relZ == realSizeZ - gapZNext) {
				returnSteep = MOUNTAIN_ROCK_VISIBLE; // too steep mountain needs Rock
			} else {
				returnSteep = MOUNTAIN_INTERSECT_EAST;
				steepDirection = J3DCore.BOTTOM;
			}
		}
		
		if (!returnCube) {
			// *** steeps ***
			if (relX>=gapX && relX<=gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
				if (relX==gapX && gapXNext!=gapX) {
					steepDirection = J3DCore.WEST;
					returnSteep = STEEP_WEST;
				} else
				{
					returnSteep = MOUNTAIN_ROCK_VISIBLE; 
					overwritePower = 0; // inside rock
				}
			}
			
			if (relX<=realSizeX-gapX && relX>=realSizeX-gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
				if (relX==realSizeX-gapX && gapXNext!=gapX) {
					steepDirection = J3DCore.EAST;
					returnSteep = STEEP_EAST;
				} else
				{
					returnSteep = MOUNTAIN_ROCK_VISIBLE; 
					overwritePower = 0; // inside rock
				}
			}
			if (relZ>=gapZ && relZ<=gapZNext && relX>gapX &&  relX<realSizeX-gapX)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
				if (relZ==gapZ && gapZNext!=gapZ) {
					steepDirection = J3DCore.SOUTH;
					returnSteep = STEEP_SOUTH;
				} else
				{
					returnSteep = MOUNTAIN_ROCK_VISIBLE; 
					overwritePower = 0; // inside rock
				}
			}
			if (relZ<=realSizeZ-gapZ && relZ>=realSizeZ-gapZNext && relX>gapX && relX<realSizeX-gapX)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
				if (relZ==realSizeZ-gapZ && gapZNext!=gapZ) {
					steepDirection = J3DCore.NORTH;
					returnSteep = STEEP_NORTH;
				} else
				{
					returnSteep = MOUNTAIN_ROCK_VISIBLE; 
					overwritePower = 0; // inside rock
				}
			}
		
		}
		
		if (!returnCube)
		{
			if ( (relZ<realSizeZ-gapZ && relZ>gapZ) && (relX>gapX && relX<realSizeX-gapX) )
			{
				// internal parts of the mountain
				returnCube = true;
				overwritePower = 0; // this can be overwritten default
			}
		}
		
		// *** normal grounds ***
		if (!returnCube) {

			// no cube for this coordinates, so we can put something above it, if there is rock below!!
			
			if (relX>=gapXPrevious && relX<=gapX && relZ>gapZPrevious && relZ<realSizeZ-gapZPrevious)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relX==gapXPrevious && gapX!=gapXPrevious) returnCube = false;
			}
			if (relX<=realSizeX-gapXPrevious && relX>=realSizeX-gapX && relZ>gapZPrevious && relZ<realSizeZ-gapZPrevious)
			{
				returnCube = true;
//				 if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relX==realSizeX-gapXPrevious && gapX!=gapXPrevious) returnCube = false;
			}
			if (relZ>=gapZPrevious && relZ<=gapZ && relX>gapXPrevious &&  relX<realSizeX-gapXPrevious)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relZ==gapZPrevious && gapZ!=gapZPrevious) returnCube = false;
			}
			if (relZ<=realSizeZ-gapZPrevious && relZ>=realSizeZ-gapZ && relX>gapXPrevious && relX<realSizeX-gapXPrevious)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relZ==realSizeZ-gapZPrevious && gapZ!=gapZPrevious) returnCube = false;
			}
			
			if (returnCube)
			{
				// we can put on it!!				
				Cube c = new Cube(this,MOUNTAIN_GROUND,worldX,worldY,worldZ,steepDirection);
				return c;
			}
			return null;
		}
		//boolean cubeAbove = getCube( worldX,  worldY+1,  worldZ)!=null;
		Side[][] s = returnSteep!=null?returnSteep:MOUNTAIN_ROCK;
		Cube c = null;
		c = new Cube(this,s,worldX,worldY,worldZ,steepDirection);
		c.overwritePower = overwritePower;
		if (overwritePower>0) c.overwrite = true;
		return c;
	}

	int GROUND_LEVEL = 0;
	int GROUND_LEVEL_CONTAINER = 1;
	
	
	
	public int[] isGroundLevel(int worldX, int worldY, int worldZ) {
		int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = blockUsedSize[0]; 
		int realSizeY = (int) ( (mountainRealSizeY-1) * ( ((Math.min(blockUsedSize[0],blockUsedSize[1])))*1d/blockSize ) );
		int realSizeZ = blockUsedSize[1];
		
		int relX = (worldX%blockSize)-(blockSize-realSizeX)/2;//-origoX*magnification;
		int relY = worldY-worldGroundLevel;
		int relZ = (worldZ%blockSize)-(blockSize-realSizeZ)/2;//-origoZ*magnification;

		if (relY>realSizeY) return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};

		if (relX<0 || relZ<0 || relX>realSizeX || relZ>realSizeZ)
		{
			if (relY==0) { 
				return new int[]{GROUND_LEVEL,SurfaceHeightAndType.NOT_STEEP};
			}
			return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
		}
		
		
		if (relX==0 && relY==0 && (relZ==0 ||relZ==realSizeZ) || relX==realSizeX && relY==0 && (relZ==0 ||relZ==realSizeZ))
		{
			return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
		}
		
		int proportionateXSizeOnLevelY = realSizeX - (int)(realSizeX * ((relY*1d)/(realSizeY)));
		int proportionateZSizeOnLevelY = realSizeZ - (int)(realSizeZ * ((relY*1d)/(realSizeY)));
		int gapX = ((realSizeX) - proportionateXSizeOnLevelY)/2;
		int gapZ = ((realSizeZ) - proportionateZSizeOnLevelY)/2;
		int proportionateXSizeOnLevelYNext = realSizeX - (int)(realSizeX * (((relY+1)*1d)/(realSizeY)));
		int proportionateZSizeOnLevelYNext = realSizeZ - (int)(realSizeZ * (((relY+1)*1d)/(realSizeY)));
		int gapXNext = ((realSizeX) - proportionateXSizeOnLevelYNext)/2;
		int gapZNext = ((realSizeZ) - proportionateZSizeOnLevelYNext)/2;

		int proportionateXSizeOnLevelYPrev = realSizeX - (int)(realSizeX * (((relY-1)*1d)/(realSizeY)));
		int proportionateZSizeOnLevelYPrev = realSizeZ - (int)(realSizeZ * (((relY-1)*1d)/(realSizeY)));
		int gapXPrevious = ((realSizeX) - proportionateXSizeOnLevelYPrev)/2;
		int gapZPrevious = ((realSizeZ) - proportionateZSizeOnLevelYPrev)/2;

		
		boolean returnCube = false;
		boolean returnSteep = false;
		
		int steepDirection = 0; //W E S N

		
		if (relX == gapX && relZ == gapZ) {
			returnCube = true;
			if (relX == gapXNext && relZ == gapZNext) {
				
			} else {
				steepDirection = J3DCore.BOTTOM;
			}
		} 
		if (relX == gapX && relZ == realSizeZ - gapZ) {
			returnCube = true;
			if (relX == gapXNext && relZ == realSizeZ - gapZNext) {
			} else {
				steepDirection = J3DCore.BOTTOM;
			}
		} 
		if (relX == realSizeX - gapX && relZ == gapZ) {
			returnCube = true;
			if (relX == realSizeX - gapXNext && relZ == gapZNext) {
			} else {
				steepDirection = J3DCore.BOTTOM;
			}
		} 
		if (relX == realSizeX - gapX && relZ == realSizeZ - gapZ) {
			returnCube = true;
			if (relX == realSizeX - gapXNext && relZ == realSizeZ - gapZNext) {
			} else {
				steepDirection = J3DCore.BOTTOM;
			}
		}
		
		// *** steeps ***
		if (relX>=gapX && relX<=gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relX==gapX && gapXNext!=gapX) {
				steepDirection = J3DCore.WEST;
				returnSteep = true;
			}
		}
		if (relX<=realSizeX-gapX && relX>=realSizeX-gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relX==realSizeX-gapX && gapXNext!=gapX) {
				steepDirection = J3DCore.EAST;
				returnSteep = true;
			}
		}
		if (relZ>=gapZ && relZ<=gapZNext && relX>gapX &&  relX<realSizeX-gapX)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relZ==gapZ && gapZNext!=gapZ) {
				steepDirection = J3DCore.SOUTH;
				returnSteep = true;
			}
		}
		if (relZ<=realSizeZ-gapZ && relZ>=realSizeZ-gapZNext && relX>gapX && relX<realSizeX-gapX)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relZ==realSizeZ-gapZ && gapZNext!=gapZ) {
				steepDirection = J3DCore.NORTH;
				returnSteep = true;
			}
		}
		
		if (returnCube)
		{
			if (returnSteep)
			{
				return new int[]{GROUND_LEVEL,steepDirection};
			}
			return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
		}
		
		if (!returnCube) {

			// no cube for this coordinates, so we can put something above it, if there is rock below!!
			
			if (relX>=gapXPrevious && relX<=gapX && relZ>gapZPrevious && relZ<realSizeZ-gapZPrevious)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relX==gapXPrevious && gapX!=gapXPrevious) returnCube = false;
			}
			if (relX<=realSizeX-gapXPrevious && relX>=realSizeX-gapX && relZ>gapZPrevious && relZ<realSizeZ-gapZPrevious)
			{
				returnCube = true;
//				 if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relX==realSizeX-gapXPrevious && gapX!=gapXPrevious) returnCube = false;
			}
			if (relZ>=gapZPrevious && relZ<=gapZ && relX>gapXPrevious &&  relX<realSizeX-gapXPrevious)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relZ==gapZPrevious && gapZ!=gapZPrevious) returnCube = false;
			}
			if (relZ<=realSizeZ-gapZPrevious && relZ>=realSizeZ-gapZ && relX>gapXPrevious && relX<realSizeX-gapXPrevious)
			{
				returnCube = true;
				// if on the edge of the mountain and above is not on the edge too, we cannot put something above
				if (relZ==realSizeZ-gapZPrevious && gapZ!=gapZPrevious) returnCube = false;
			}
			if (returnCube)
			{
				//if (worldY!=worldGroundLevel) {
					// we can put on it!!				
					return new int[]{GROUND_LEVEL_CONTAINER,SurfaceHeightAndType.NOT_STEEP};
				//}
			}
			return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
		}
		return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
	}
	
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ) {
		int realSizeY = mountainRealSizeY-1;
		for (int i=0; i<=realSizeY; i++)
		{
			int[] ret = isGroundLevel(worldX, worldGroundLevel+i, worldZ);
			if (ret[0]>-1)
			{
				int r = ret[1];
				return new SurfaceHeightAndType[]{new SurfaceHeightAndType(this,worldGroundLevel+i,true,r)};
			}
		}
		return  new SurfaceHeightAndType[]{new SurfaceHeightAndType(this,worldHeight,false,SurfaceHeightAndType.NOT_STEEP)};
	}


	

}
