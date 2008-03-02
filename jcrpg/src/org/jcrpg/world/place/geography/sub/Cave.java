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
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_CAVE+"_GROUND");
	public static final SideSubType SUBTYPE_ENTRANCE = new SideSubType(TYPE_CAVE+"_ENTRANCE");

	static Side[] ROCK = {new Side(TYPE_CAVE,SUBTYPE_ROCK)};
	static Side[] BLOCK = {new Side(TYPE_CAVE,SUBTYPE_BLOCK)};
	static Side[] BLOCK_GROUND = {new Side(TYPE_CAVE,SUBTYPE_BLOCK_GROUND)};
	static Side[] GROUND = {new Side(TYPE_CAVE,SUBTYPE_GROUND)};
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

	static Side[][] CAVE_ENTRANCE_NORTH = new Side[][] { ENTRANCE, BLOCK, EMPTY_SIDE,BLOCK,BLOCK_GROUND,GROUND };
	static Side[][] CAVE_ENTRANCE_EAST = new Side[][] { BLOCK, ENTRANCE, BLOCK,EMPTY_SIDE,BLOCK_GROUND,GROUND };
	static Side[][] CAVE_ENTRANCE_SOUTH = new Side[][] { EMPTY_SIDE, BLOCK, ENTRANCE,BLOCK,BLOCK_GROUND,GROUND };
	static Side[][] CAVE_ENTRANCE_WEST = new Side[][] { BLOCK, EMPTY_SIDE, BLOCK,ENTRANCE,BLOCK_GROUND,GROUND };
	
	
	public int density,entranceSide, levelSize;

	public Cave(String id, Place parent, PlaceLocator loc,int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int density, int entranceSide, int levelSize, boolean fillBoundaries ) throws Exception{
		super(id, parent, loc,worldGroundLevel,worldHeight,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,fillBoundaries);
		ruleSet.presentWhereBaseExists = false;
		ruleSet.genType = GenAlgoAdd.GEN_TYPE_NAME;
		ruleSet.genParams = new Object[] { new GenAlgoAddParams(new String[]{"MountainNew"}, 100, new int[]{0}) };
		this.density = density;
		this.entranceSide = entranceSide;
		this.levelSize = levelSize;
	}
	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ, boolean farView)
	{
		Cube c = getCubeBase(worldX, worldY, worldZ, farView);
		if (c==null) {
			return null;
		}
		//c.onlyIfOverlaps = true;
		c.overwrite = true;
		if (c.overwritePower!=2)
			c.internalCube = true; // except entrance all is inside
		return c;
	}
	
	public int ENTRANCE_DISTANCE = 8;
	public int ENTRANCE_LEVEL = 0;

	private Cube getCubeBase(int worldX, int worldY, int worldZ, boolean farView)
	{
		if (worldY>=worldHeight) return null;

		int kind = getCubeKindOutside(worldX, worldY, worldZ, farView);
		
		
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
		int height = getPointHeightOutside(worldX, worldZ, farView);
		if ((relZ%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel && height+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel)
		{
			Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
			if ((kind==K_STEEP_WEST || kind==K_STEEP_EAST) && (relX<realSizeX&& relX>0))
			{
				c = new Cube(this,CAVE_ENTRANCE_EAST,worldX,worldY,worldZ);
				c.overwritePower = 2;
			}
			if (c!=null)
				return c;
		}
		if ((relX%ENTRANCE_DISTANCE==2) && worldY==ENTRANCE_LEVEL+worldGroundLevel && height+((World)getRoot()).worldGroundLevel==ENTRANCE_LEVEL+worldGroundLevel)
		{
			Cube c = null;//new Cube(this,EMPTY,worldX,worldY,worldZ);
			if ((kind==K_STEEP_NORTH||kind==K_STEEP_SOUTH) && (relZ<realSizeZ&& relZ>0))
			{
				c = new Cube(this,CAVE_ENTRANCE_NORTH,worldX,worldY,worldZ);
				c.overwritePower = 2;
			}
			return c;
		}
		
		
		if (height+((World)getRoot()).worldGroundLevel<=worldY)
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
		Cube c = new Cube(this,CAVE_GROUND_CEILING,worldX,worldY,worldZ);
		if (worldRealHeight>1) {
			if (relY%levelSize==0)
				c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
			else if (relY%levelSize==levelSize-1)
				c = new Cube(this,CAVE_CEILING,worldX,worldY,worldZ);
			else c = new Cube(this,new Side[][]{null,null,null,null,null,null},worldX,worldY,worldZ);
		}
		c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
		return c;

	}

	SurfaceHeightAndType[] cachedType = null;
	SurfaceHeightAndType[] cachedNonType = null;
	
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ, boolean farView ) {
		if (getCubeBase(worldX, worldGroundLevel, worldZ, farView)==null)
		{
			if (cachedNonType==null)
			{
				cachedNonType = new SurfaceHeightAndType[] { new SurfaceHeightAndType(worldGroundLevel,false,SurfaceHeightAndType.NOT_STEEP) };
			}
			return cachedNonType;
		}
		// TODO !!!this is not complete enough for a multilevel cave!!!
		int per = HashUtil.mixPercentage(worldX, (worldGroundLevel-(origoY*magnification)%levelSize)/levelSize, worldZ);
		if (per>=density)
		{
			if (cachedType==null) cachedType = new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel,true,SurfaceHeightAndType.NOT_STEEP)};
			return cachedType;
		}
		if (cachedNonType==null)
		{
			cachedNonType = new SurfaceHeightAndType[] { new SurfaceHeightAndType(worldGroundLevel,false,SurfaceHeightAndType.NOT_STEEP) };
		}
		return cachedNonType;
	}
	

}
