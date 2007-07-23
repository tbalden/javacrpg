/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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


public class Mountain extends Geography implements Surface{


	public static final String TYPE_MOUNTAIN = "MOUNTAIN";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_MOUNTAIN+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK = new NotPassable(TYPE_MOUNTAIN+"_GROUND_ROCK");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_MOUNTAIN+"_GROUND");
	public static final SideSubType SUBTYPE_INTERSECT = new NotPassable(TYPE_MOUNTAIN+"_GROUND_INTERSECT");
	public static final SideSubType SUBTYPE_INTERSECT_BLOCK = new NotPassable(TYPE_MOUNTAIN+"_GROUND_INTERSECT_BLOCK");

	static Side[] ROCK = {new Side(TYPE_MOUNTAIN,SUBTYPE_ROCK)};
	static Side[] GROUND = {new Side(TYPE_MOUNTAIN,SUBTYPE_GROUND)};
	static Side[] STEEP = {new Side(TYPE_MOUNTAIN,SUBTYPE_STEEP)};
	static Side[] INTERSECT = {new Side(TYPE_MOUNTAIN,SUBTYPE_INTERSECT)};
	static Side[] BLOCK = {new Side(TYPE_MOUNTAIN,SUBTYPE_INTERSECT_BLOCK)};
	
	static Side[][] MOUNTAIN_ROCK = new Side[][] { null, null, null,null,null,ROCK };
	static Side[][] MOUNTAIN_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] MOUNTAIN_INTERSECT_NORTH = new Side[][] { INTERSECT, BLOCK, BLOCK,BLOCK,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_INTERSECT_EAST = new Side[][] { BLOCK, INTERSECT, BLOCK,BLOCK,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_INTERSECT_SOUTH = new Side[][] { BLOCK, BLOCK, INTERSECT,BLOCK,BLOCK,BLOCK };
	static Side[][] MOUNTAIN_INTERSECT_WEST = new Side[][] { BLOCK, BLOCK, BLOCK,INTERSECT,BLOCK,BLOCK };
	static Side[][] STEEP_NORTH = new Side[][] { STEEP, null, null,null,null,null };
	static Side[][] STEEP_EAST = new Side[][] { null, STEEP, null,null,null,null };
	static Side[][] STEEP_SOUTH = new Side[][] { null, null, STEEP,null,null,null };
	static Side[][] STEEP_WEST = new Side[][] { null, null, null,STEEP,null,null };


	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	
	public Mountain(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id, parent, loc);
		if (sizeX/2<sizeY || sizeZ/2<sizeY) throw new Exception ("Mountain too steep!");
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}

	

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		int relX = worldX-origoX*magnification;
		int relY = worldY-origoY*magnification;
		int relZ = worldZ-origoZ*magnification;
		int realSizeX = sizeX*magnification-1;
		int realSizeY = sizeY*magnification-1;
		int realSizeZ = sizeZ*magnification-1;
		
		
		if (relX==0 && relY==0 && (relZ==0 ||relZ==realSizeZ) || relX==realSizeX && relY==0 && (relZ==0 ||relZ==realSizeZ))
		{
			Side[][] side = null;
			if (relX == 0 && relY == 0 && relZ == 0) {
				side = MOUNTAIN_INTERSECT_WEST;
			} 
			if (relX == 0 && relY == 0 && relZ == realSizeZ) {
				side = MOUNTAIN_INTERSECT_NORTH;
			} 
			if (relX == realSizeX && relY == 0 && relZ == 0) {
				side = MOUNTAIN_INTERSECT_SOUTH;
			} 
			if (relX == realSizeX && relY == 0 && relZ == realSizeZ) {
				side = MOUNTAIN_INTERSECT_EAST;
			}
				
			return new Cube(this,side,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
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

		
		if (relX == gapX && relZ == gapZ) {
			returnCube = true;
			returnSteep = MOUNTAIN_INTERSECT_WEST;
		} 
		if (relX == gapX && relZ == realSizeZ - gapZ) {

			returnCube = true;
			returnSteep = MOUNTAIN_INTERSECT_NORTH;
		} 
		if (relX == realSizeX - gapX && relZ == gapZ) {
			returnCube = true;
			returnSteep = MOUNTAIN_INTERSECT_SOUTH;
		} 
		if (relX == realSizeX - gapX && relZ == realSizeZ - gapZ) {
			returnCube = true;
			returnSteep = MOUNTAIN_INTERSECT_EAST;
		}
		
		if (relX>=gapX && relX<=gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relX==gapX && gapXNext!=gapX) {
				steepDirection = J3DCore.WEST;
				returnSteep = STEEP_WEST;
			}
		}
		
		if (relX<=realSizeX-gapX && relX>=realSizeX-gapXNext && relZ>gapZ && relZ<realSizeZ-gapZ)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relX==realSizeX-gapX && gapXNext!=gapX) {
				steepDirection = J3DCore.EAST;
				returnSteep = STEEP_EAST;
			}
		}
		if (relZ>=gapZ && relZ<=gapZNext && relX>gapX &&  relX<realSizeX-gapX)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relZ==gapZ && gapZNext!=gapZ) {
				steepDirection = J3DCore.SOUTH;
				returnSteep = STEEP_SOUTH;
			}
		}
		if (relZ<=realSizeZ-gapZ && relZ>=realSizeZ-gapZNext && relX>gapX && relX<realSizeX-gapX)
		{
			returnCube = true;
			// if on the edge of the mountain and above is not on the edge too, we can use STEEP!
			if (relZ==realSizeZ-gapZ && gapZNext!=gapZ) {
				steepDirection = J3DCore.NORTH;
				returnSteep = STEEP_NORTH;
			}
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
				// we can put on it!!				
				return new Cube(this,MOUNTAIN_GROUND,worldX,worldY,worldZ,steepDirection);
			}
			return null;
		}
		//boolean cubeAbove = getCube( worldX,  worldY+1,  worldZ)!=null;
		Side[][] s = returnSteep!=null?returnSteep:MOUNTAIN_ROCK;
		Cube c = null;
		c = new Cube(this,s,worldX,worldY,worldZ,steepDirection);
		return c;
	}

	int GROUND_LEVEL = 0;
	int GROUND_LEVEL_CONTAINER = 1;
	
	public int[] isGroundLevel(int worldX, int worldY, int worldZ) {
		int relX = worldX-origoX*magnification;
		int relY = worldY-origoY*magnification;
		int relZ = worldZ-origoZ*magnification;
		int realSizeX = sizeX*magnification-1;
		int realSizeY = sizeY*magnification-1;
		int realSizeZ = sizeZ*magnification-1;
		
		
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
		} 
		if (relX == gapX && relZ == realSizeZ - gapZ) {

			returnCube = true;
		} 
		if (relX == realSizeX - gapX && relZ == gapZ) {
			returnCube = true;
		} 
		if (relX == realSizeX - gapX && relZ == realSizeZ - gapZ) {
			returnCube = true;
		}
		
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
				// we can put on it!!				
				return new int[]{GROUND_LEVEL_CONTAINER,SurfaceHeightAndType.NOT_STEEP};
			}
			return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
		}
		return new int[]{-1,SurfaceHeightAndType.NOT_STEEP};
	}
	
	
	public SurfaceHeightAndType getPointSurfaceData(int worldX, int worldZ) {
		int realSizeY = sizeY*magnification-1;
		for (int i=0; i<=realSizeY; i++)
		{
			int[] ret = isGroundLevel(worldX, origoY*magnification+i, worldZ);
			if (ret[0]>-1)
			{
				return new SurfaceHeightAndType(origoY*magnification+i,true,ret[1]);
			}
		}
		return new SurfaceHeightAndType(origoY+realSizeY,false,SurfaceHeightAndType.NOT_STEEP);
	}


	

}
