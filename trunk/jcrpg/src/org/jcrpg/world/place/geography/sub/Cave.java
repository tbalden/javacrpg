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

package org.jcrpg.world.place.geography.sub;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.generator.program.algorithm.GenAlgoAdd;
import org.jcrpg.world.generator.program.algorithm.GenAlgoAddParams;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Surface;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;

public class Cave extends Geography implements Surface {

	
	public static int LIMIT_NORTH = 1;
	public static int LIMIT_EAST = 2;
	public static int LIMIT_SOUTH = 4;
	public static int LIMIT_WEST = 8;
	
	public static final String TYPE_CAVE = "CAVE";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_CAVE+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK = new NotPassable(TYPE_CAVE+"_ROCK");
	public static final SideSubType SUBTYPE_BLOCK = new NotPassable(TYPE_CAVE+"_BLOCK");
	public static final SideSubType SUBTYPE_BLOCK_GROUND = new GroundSubType(TYPE_CAVE+"_BLOCK_GROUND");
	public static final SideSubType SUBTYPE_WALL = new NotPassable(TYPE_CAVE+"_WALL");
	public static final SideSubType SUBTYPE_WALL_REVERSE = new NotPassable(TYPE_CAVE+"_WALL_REVERSE");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_CAVE+"_GROUND",true);
	public static final SideSubType SUBTYPE_ENTRANCE = new SideSubType(TYPE_CAVE+"_ENTRANCE",true);

	static Side[] ROCK = {new Side(TYPE_CAVE,SUBTYPE_ROCK)};
	static Side[] BLOCK = {new Side(TYPE_CAVE,SUBTYPE_BLOCK)};
	static Side[] BLOCK_GROUND = {new Side(TYPE_CAVE,SUBTYPE_BLOCK_GROUND)};
	static Side[] GROUND = {new Side(TYPE_CAVE,SUBTYPE_GROUND)};
	static Side[] ENTRANCE_GROUND = {new Side(TYPE_CAVE,SUBTYPE_GROUND), new Side(TYPE_GEO,SUBTYPE_ROCK_DOWNSIDE)};
	static Side[] WALL = {new Side(TYPE_CAVE,SUBTYPE_WALL)};
	static Side[] WALL_REVERSE = {new Side(TYPE_CAVE,SUBTYPE_WALL_REVERSE)};
	static Side[] ENTRANCE = {new Side(TYPE_CAVE,SUBTYPE_ENTRANCE)};
	
	static Side[][] CAVE_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] CAVE_CEILING = new Side[][] { null, null, null,null,WALL,null };
	static Side[][] CAVE_GROUND_CEILING = new Side[][] { null, null, null,null,WALL,GROUND };
	static Side[][] CAVE_NORTH = new Side[][] { WALL_REVERSE, null, null,null,null,null };
	static Side[][] CAVE_EAST = new Side[][] { null, WALL_REVERSE, null,null,null,null };
	static Side[][] CAVE_SOUTH = new Side[][] { null, null, WALL_REVERSE,null,null,null };
	static Side[][] CAVE_WEST = new Side[][] { null, null, null,WALL_REVERSE,null,null };

	static Side[][] CAVE_ROCK = new Side[][] { BLOCK, BLOCK, BLOCK,BLOCK,ROCK,null };
	static Side[][] CAVE_ROCK_NO_MODEL = new Side[][] { BLOCK, BLOCK, BLOCK,BLOCK, BLOCK_GROUND,null };
	//static Side[][] CAVE_ROCK = new Side[][] { WALL, WALL, WALL,WALL,GROUND,WALL };

	static Side[][] CAVE_ENTRANCE_NORTH = new Side[][] { ENTRANCE, BLOCK, EMPTY_SIDE,BLOCK,BLOCK_GROUND,ENTRANCE_GROUND };
	static Side[][] CAVE_ENTRANCE_EAST = new Side[][] { BLOCK, ENTRANCE, BLOCK,EMPTY_SIDE,BLOCK_GROUND,ENTRANCE_GROUND };
	static Side[][] CAVE_ENTRANCE_SOUTH = new Side[][] { EMPTY_SIDE, BLOCK, ENTRANCE,BLOCK,BLOCK_GROUND,ENTRANCE_GROUND };
	static Side[][] CAVE_ENTRANCE_WEST = new Side[][] { BLOCK, EMPTY_SIDE, BLOCK,ENTRANCE,BLOCK_GROUND,ENTRANCE_GROUND };
	
	
	public int density,entranceSide, levelSize;
	public int maxLevels = 1;
	
	/**
	 * If you want an all internal cubed cave set this to true - used for encoutner ground.
	 */
	public boolean alwaysInsideCubesForEncounterGround = false;

	public Cave(String id, Place parent, PlaceLocator loc,int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int density, int entranceSide, int levelSize, boolean fillBoundaries ) throws Exception{
		super(id, parent, loc,worldGroundLevel,worldHeight,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,fillBoundaries);
		ruleSet.presentWhereBaseExists = false;
		ruleSet.genType = GenAlgoAdd.GEN_TYPE_NAME;
		ruleSet.genParams = new Object[] { new GenAlgoAddParams(new String[]{"Mountain"}, 100, new int[]{0}) };
		this.density = density;
		this.entranceSide = entranceSide;
		this.levelSize = levelSize;
		returnsGeoOutsideHeight = false;
		placeNeedsToBeEnteredForEncounter = true;
		audioDescriptor.ENVIRONMENTAL = new String[] {"cave_drip1","cave_drip2","cave_drip3","cave_wind1"};
	}
	
	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView)
	{
		Cube c = getCubeBase(key, worldX, worldY, worldZ, farView);
		if (c==null) {
			return null;
		}
		//c.onlyIfOverlaps = true;
		//c.overwrite = true;
		if (c.overwritePower>0)
		{
			c.overwrite = true;
		}
		if (c.overwritePower!=2)
		{
			c.internalCube = true; // except entrance all is inside
			c.internalLight = true;
		}
		
		return c;
	}
	
	public int ENTRANCE_DISTANCE = 8;
	public int ENTRANCE_LEVEL = 0;

	private Cube getCubeBase(long key, int worldX, int worldY, int worldZ, boolean farView)
	{
		if (worldY>=worldHeight || worldY>=worldGroundLevel+maxLevels*levelSize) return null;

		
		
		int[] values = calculateTransformedCoordinates(worldX, worldY, worldZ);
		//int[] blockUsedSize = getBlocksGenericSize(blockSize, worldX, worldZ);
		int realSizeX = values[0];
		//int realSizeY = values[1];
		int realSizeZ = values[2];
		int relX = values[3];
		int relY = values[4];
		int relZ = values[5];

		if (relX<0 || relZ<0 || relY<0 || relX>realSizeX || relZ>realSizeZ)
		{
			return null;		
		}
		int height = (int)getPointHeightOutside(worldX, worldZ, farView);
		int FARVIEW_GAP = 1;
		int inTheCaveHeight = height+((World)getRoot()).worldGroundLevel - worldGroundLevel;
		
		boolean entranceOverwrite = false;
		int tmpWorldX = 0;
		int tmpWorldZ = 0;
		
		
		for (int i=-4; i<5; i++)
		{
			// TODO a better measuring if we are deep enough in the mountain under the ground level,
			// checking till we get +2 under the ground on the entrance level.
			tmpWorldX = worldX+i;
			tmpWorldZ = worldZ+i;
			int tmpHeightX = (int)getPointHeightOutside(tmpWorldX, worldZ, farView);
			int tmpHeightZ = (int)getPointHeightOutside(worldX, tmpWorldZ, farView);
			if (Math.abs(i)==1)
			{
				// looking for unnecessary ground block to replace with cave ground ---------------------------------------------------------------------------------------------------------------------------------
				if ((relZ%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel+1 && tmpHeightX+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel+1)
				{
					Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
					int kind = (int)getCubeKindOutside(-1, tmpWorldX, worldY, worldZ, farView)[4];
					if ((kind==K_STEEP_WEST || kind==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
					{
						c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
						c.overwritePower = 2;
					}
					if (c!=null)
					{
						return inTheCaveHeight>=0?c:null;
					}
				}
				if ((relZ%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel+1 && tmpHeightX+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel+1+1)
				{
					Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
					int kind = (int)getCubeKindOutside(-1, tmpWorldX, worldY, worldZ, farView)[4];
					if ((kind==K_STEEP_WEST || kind==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
					{
						c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
						c.overwritePower = 2;
					}
					if (c!=null)
					{
						return inTheCaveHeight>=0?c:null;
					}
				}
				if ((relX%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel+1 && tmpHeightZ+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel+1)
				{
					int kind = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ, farView)[4];
					Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
					if ((kind==K_STEEP_NORTH||kind==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
					{
						c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
						c.overwritePower = 2;
					}
					if (c!=null)
					{
						return inTheCaveHeight>=0?c:null;
					}
				}
				if ((relX%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel+1 && tmpHeightZ+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel+1+1)
				{
					int kind = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ, farView)[4];
					Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
					if ((kind==K_STEEP_NORTH||kind==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
					{
						c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
						c.overwritePower = 2;
					}
					if (c!=null)
					{
						return inTheCaveHeight>=0?c:null;
					}
				}
			}
			
			// looking for Cave Entrance ---------------------------------------------------------------------------------------------------------------------------------
			if ((relZ%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel && tmpHeightX+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel)
			{
				int kind = (int)getCubeKindOutside(-1, tmpWorldX, worldY, worldZ, farView)[4];
				int kindNext = (int)getCubeKindOutside(-1, tmpWorldX+1, worldY, worldZ, farView)[4];
				int kindPrev = (int)getCubeKindOutside(-1, tmpWorldX-1, worldY, worldZ, farView)[4];
				Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
				if ((kind==K_STEEP_WEST || kind==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
				{
					c = new Cube(this,CAVE_ENTRANCE_EAST,worldX,worldY,worldZ);
					c.overwritePower = 2;
				}
				if ((kindNext==K_STEEP_WEST || kindNext==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
				{
					entranceOverwrite = true;
				}
				if ((kindPrev==K_STEEP_WEST || kindPrev==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
				{
					entranceOverwrite = true;
				}
				//System.out.println("ENTRANCE EAST for "+i+" "+c);
				if (c!=null)
				{
					c.internalLight = true;
					return inTheCaveHeight>=0?c:null;
				}
			}
			if ((relZ%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel && tmpHeightX+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel+1)
			{
				int kind = (int)getCubeKindOutside(-1, tmpWorldX, worldY, worldZ, farView)[4];
				int kindNext = (int)getCubeKindOutside(-1, tmpWorldX+1, worldY, worldZ, farView)[4];
				int kindPrev = (int)getCubeKindOutside(-1, tmpWorldX-1, worldY, worldZ, farView)[4];
				Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
				if ((kind==K_STEEP_WEST || kind==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
				{
					c = new Cube(this,CAVE_ENTRANCE_EAST,worldX,worldY,worldZ);
					c.overwritePower = 2;
				}
				if ((kindNext==K_STEEP_WEST || kindNext==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
				{
					entranceOverwrite = true;
				}
				if ((kindPrev==K_STEEP_WEST || kindPrev==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
				{
					entranceOverwrite = true;
				}
				//System.out.println("ENTRANCE EAST for "+i+" "+c);
				if (c!=null)
				{
					return inTheCaveHeight>=0?c:null;
				}
			}
			if ((relX%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel && tmpHeightZ+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel)
			{
				int kind = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ, farView)[4];
				int kindNext = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ+1, farView)[4];
				int kindPrev = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ-1, farView)[4];
				Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
				if ((kind==K_STEEP_NORTH||kind==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
				{
					c = new Cube(this,CAVE_ENTRANCE_NORTH,worldX,worldY,worldZ);
					c.overwritePower = 2;
				}
				if ((kindNext==K_STEEP_NORTH || kindNext==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
				{
					entranceOverwrite = true;
				}
				if ((kindPrev==K_STEEP_NORTH || kindPrev==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
				{
					entranceOverwrite = true;
				}
				//System.out.println("ENTRANCE NORTH for "+i+" "+c);
				if (c!=null)
				{
					return inTheCaveHeight>=0?c:null;
				}
			}
			if ((relX%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel && tmpHeightZ+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel+1)
			{
				int kind = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ, farView)[4];
				int kindNext = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ+1, farView)[4];
				int kindPrev = (int)getCubeKindOutside(-1, worldX, worldY, tmpWorldZ-1, farView)[4];
				Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
				if ((kind==K_STEEP_NORTH||kind==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
				{
					c = new Cube(this,CAVE_ENTRANCE_NORTH,worldX,worldY,worldZ);
					c.overwritePower = 2;
				}
				if ((kindNext==K_STEEP_NORTH || kindNext==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
				{
					entranceOverwrite = true;
				}
				if ((kindPrev==K_STEEP_NORTH || kindPrev==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
				{
					entranceOverwrite = true;
				}
				//System.out.println("ENTRANCE NORTH for "+i+" "+c);
				if (c!=null)
				{
					return inTheCaveHeight>=0?c:null;
				}
				
			}
		}
		

		// only calculate if 1 cube below the normal surface ground
		if (height+((World)getRoot()).worldGroundLevel<=worldY+1)
		{
			return null;
		}
		
		
		int per = HashUtil.mixPercentage(worldX, relY/levelSize, worldZ);
		if ((relZ%ENTRANCE_DISTANCE==2 || relX%ENTRANCE_DISTANCE==2) && relY==ENTRANCE_LEVEL) {
			per+=20;
		}
		if (per<density)
		{
			Cube c = new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
			c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
			// internal space to 0 too!
			return c;
		}
		boolean ceiling = true;
		Cube c = new Cube(this,CAVE_GROUND_CEILING,worldX,worldY,worldZ);
		if (worldRelHeight>1) {
			if (relY%levelSize==0)
			{
				if (relY%levelSize==levelSize-1 || relY==inTheCaveHeight-1 || relY==inTheCaveHeight-2)
				{
					// leave the Ground/ceiling type
					ceiling = true;
				} else
				{
					ceiling = false;
					c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
					
				}
			}
			else if (relY%levelSize==levelSize-1 && relY+2<=inTheCaveHeight)
			{
				ceiling = true;
				c = new Cube(this,CAVE_CEILING,worldX,worldY,worldZ);
			}
			else c = new Cube(this,new Side[][]{null,null,null,null,null,null},worldX,worldY,worldZ);
		}
		{
			// not entrance port, no overwrite
			c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
			if (ceiling)
			{
				if ((relY==inTheCaveHeight || relY==inTheCaveHeight-1 || relY==inTheCaveHeight-2))
				{
					int YNorth = (int)getPointHeight(relX, relZ+FARVIEW_GAP, realSizeX, realSizeZ,worldX,shrinkToWorld(worldZ+FARVIEW_GAP), farView)/FARVIEW_GAP +((World)getRoot()).worldGroundLevel - worldGroundLevel;
					int YSouth = (int)getPointHeight(relX, relZ-FARVIEW_GAP, realSizeX, realSizeZ,worldX,shrinkToWorld(worldZ-FARVIEW_GAP), farView)/FARVIEW_GAP +((World)getRoot()).worldGroundLevel - worldGroundLevel;
					int YWest = (int)getPointHeight(relX-FARVIEW_GAP, relZ, realSizeX, realSizeZ,shrinkToWorld(worldX-FARVIEW_GAP),worldZ, farView)/FARVIEW_GAP +((World)getRoot()).worldGroundLevel - worldGroundLevel;
					int YEast = (int)getPointHeight(relX+FARVIEW_GAP, relZ, realSizeX, realSizeZ,shrinkToWorld(worldX+FARVIEW_GAP),worldZ, farView)/FARVIEW_GAP+((World)getRoot()).worldGroundLevel - worldGroundLevel;
					if (YEast<inTheCaveHeight || inTheCaveHeight==0)
					{
						c.merge(new Cube(this,CAVE_EAST,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
					}
					if (YSouth<inTheCaveHeight || inTheCaveHeight==0)
					{
						c.merge(new Cube(this,CAVE_SOUTH,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
					}
					if (YWest<inTheCaveHeight || inTheCaveHeight==0)
					{
						c.merge(new Cube(this,CAVE_WEST,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
					}
					if (YNorth<inTheCaveHeight || inTheCaveHeight==0)
					{
						c.merge(new Cube(this,CAVE_NORTH,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
					}
					
					c.overwritePower = 2;	
				} else
				{
					c.overwritePower = 1;
				}
			}
		}
		return c;

	}

	SurfaceHeightAndType[] cachedType = null;
	SurfaceHeightAndType[] cachedNonType = null;
	
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ, boolean farView ) {
		// TODO !!!this is not complete enough for a multilevel cave!!! we should iterate through all possible levels
		// adding to wGLevel + levelSize * level and gather surfaces 
		if (getCubeBase(-1, worldX, worldGroundLevel, worldZ, farView)==null)
		{
			if (cachedNonType==null)
			{
				cachedNonType = new SurfaceHeightAndType[] { new SurfaceHeightAndType(this,worldGroundLevel,false,SurfaceHeightAndType.NOT_STEEP) };
			}
			return cachedNonType;
		}
		int per = HashUtil.mixPercentage(worldX, 0, worldZ);
		if (per>=density)
		{
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(" CAVE CAN "+ worldX+ " "+worldZ+" " +worldGroundLevel);
			if (cachedType==null) cachedType = new SurfaceHeightAndType[]{new SurfaceHeightAndType(this,worldGroundLevel,true,SurfaceHeightAndType.NOT_STEEP)};
			return cachedType;
		}
		if (cachedNonType==null)
		{
			cachedNonType = new SurfaceHeightAndType[] { new SurfaceHeightAndType(this,worldGroundLevel,false,SurfaceHeightAndType.NOT_STEEP) };
		}
		return cachedNonType;
	}


}
