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

package org.jcrpg.world.place.geography.sub;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;

public class Cave extends Geography {

	
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

	static Side[][] CAVE_ENTRANCE_NORTH = new Side[][] { ENTRANCE, BLOCK, null,BLOCK,BLOCK_GROUND,GROUND };
	static Side[][] CAVE_ENTRANCE_EAST = new Side[][] { BLOCK, ENTRANCE, BLOCK,null,BLOCK_GROUND,GROUND };
	static Side[][] CAVE_ENTRANCE_SOUTH = new Side[][] { null, BLOCK, ENTRANCE,BLOCK,BLOCK_GROUND,GROUND };
	static Side[][] CAVE_ENTRANCE_WEST = new Side[][] { BLOCK, null, BLOCK,ENTRANCE,BLOCK_GROUND,GROUND };
	
	
	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	int density,entranceSide, walledSide, levels, entranceLength;

	public Cave(String id, Place parent, PlaceLocator loc,int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int density, int entranceSide, int walledSide, int levels, int entranceLength) throws Exception{
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		this.density = density;
		this.entranceSide = entranceSide;
		this.walledSide = walledSide;
		this.entranceLength = entranceLength;
		this.levels = levels;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}
	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ)
	{
		Cube c = getCubeBase(worldX, worldY, worldZ);
		if (c==null) return null;
		c.onlyIfOverlaps = true;
		c.overwrite = true;
		if (c.overwritePower!=1)
			c.internalLight = true; // except entrance all is inside
		return c;
	}
	
	public int ENTRANCE_DISTANCE = 8;
	public int ENTRANCE_LEVEL = 0;

	private Cube getCubeBase(int worldX, int worldY, int worldZ)
	{
		int relX = worldX-origoX*magnification;
		int relY = worldY-origoY*magnification;
		int relZ = worldZ-origoZ*magnification;
		int realSizeX = sizeX*magnification-1;
		int realSizeY = sizeY*magnification-1;
		int realSizeZ = sizeZ*magnification-1;
		
		if (relX<=entranceLength || relX>=realSizeX-entranceLength)
		{
			Cube c = new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
			if (relZ<=entranceLength || relZ>=realSizeZ-entranceLength)
			{
				// on the corners, no cube
				return null;
			}
			if (relZ%ENTRANCE_DISTANCE==2 && relY==ENTRANCE_LEVEL)
			{
				if (relX<=entranceLength && (entranceSide&LIMIT_WEST)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_EAST,worldX,worldY,worldZ);
					c.overwritePower = 1;
				} else
				if (relX>=realSizeX-entranceLength && (entranceSide&LIMIT_EAST)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_WEST,worldX,worldY,worldZ);
					c.overwritePower = 1;
				} else
				if (relX<=entranceLength && (walledSide&LIMIT_SOUTH)==0)
				{
					return null;
				} else
				if (relX>=realSizeX-entranceLength && (walledSide&LIMIT_NORTH)==0)
				{
					return null;
				}
				
				return c;
			} else
			{
				if (relX<=entranceLength && (walledSide&LIMIT_WEST)==0)
				{
					return null;
				} else
				if (relX>=realSizeX-entranceLength && (walledSide&LIMIT_EAST)==0)
				{
					return null;
				}
			}
			if (c!=null) c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
			// internal space to 0 too!
			return c;
		} else
		if (relZ<=entranceLength || relZ>=realSizeZ-entranceLength)
		{
			Cube c = new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
			if (relX%ENTRANCE_DISTANCE==2 && relY==ENTRANCE_LEVEL)
			{
				if (relZ<=entranceLength && (entranceSide&LIMIT_SOUTH)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_NORTH,worldX,worldY,worldZ);
					c.overwritePower = 1;
				} else
				if (relZ>=realSizeZ-entranceLength && (entranceSide&LIMIT_NORTH)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_SOUTH,worldX,worldY,worldZ);
					c.overwritePower = 1;
						
				} else
				if (relZ<=entranceLength && (walledSide&LIMIT_WEST)==0)
				{
					return null;
				} else
				if (relZ>=realSizeZ-entranceLength && (walledSide&LIMIT_EAST)==0)
				{
					return null;
				}
				return c;
			} else
			{
				if (relZ<=entranceLength && (walledSide&LIMIT_SOUTH)==0)
				{
					return null;
				} else
				if (relZ>=realSizeZ-entranceLength && (walledSide&LIMIT_NORTH)==0)
				{
					return null;
				}
			}
			c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
			// internal space to 0 too!
			return c;
		} 
		else {
			int per = HashUtil.mixPercentage(worldX, (worldY-(origoY*magnification)%levels)/levels, worldZ);
			
			if (per<density)
			{
				Cube c = new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
				c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
				// internal space to 0 too!
				return c;
			}
			Cube c = new Cube(this,CAVE_GROUND_CEILING,worldX,worldY,worldZ);
			if (realSizeY>=1) {
				if (relY%levels==0)
					c = new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
				else if (relY%levels==levels-1)
					c = new Cube(this,CAVE_CEILING,worldX,worldY,worldZ);
				else c = new Cube(this,new Side[][]{null,null,null,null,null,null},worldX,worldY,worldZ);
			}
			if (relX==entranceLength+1 && (relZ%ENTRANCE_DISTANCE!=2 || relY!=ENTRANCE_LEVEL))
			{
				c = new Cube(c,new Cube(this,CAVE_WEST,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
			if (relX==entranceLength-realSizeX-1 && (relZ%ENTRANCE_DISTANCE!=2 || relY!=ENTRANCE_LEVEL))
			{
				c = new Cube(c,new Cube(this,CAVE_EAST,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
			if (relZ==entranceLength+1 && (relX%ENTRANCE_DISTANCE!=2 || relY!=ENTRANCE_LEVEL))
			{
				c = new Cube(c,new Cube(this,CAVE_SOUTH,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
			if (relZ==entranceLength-realSizeZ-1 && (relX%ENTRANCE_DISTANCE!=2 || relY!=ENTRANCE_LEVEL))
			{
				c = new Cube(c,new Cube(this,CAVE_NORTH,worldX,worldY,worldZ),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
			c.overwritePower = 0; // this should overwrite only empty spaces, other geos should set their empty
			// internal space to 0 too!
			return c;
		}
	}
	

}
