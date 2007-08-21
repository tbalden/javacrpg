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

public class Cave extends Geography {

	
	public static int LIMIT_NORTH = 1;
	public static int LIMIT_EAST = 2;
	public static int LIMIT_SOUTH = 4;
	public static int LIMIT_WEST = 8;
	
	public static final String TYPE_CAVE = "CAVE";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_CAVE+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_ROCK = new NotPassable(TYPE_CAVE+"_ROCK");
	public static final SideSubType SUBTYPE_BLOCK = new NotPassable(TYPE_CAVE+"_BLOCK");
	public static final SideSubType SUBTYPE_WALL = new NotPassable(TYPE_CAVE+"_WALL");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_CAVE+"_GROUND");
	public static final SideSubType SUBTYPE_ENTRANCE = new SideSubType(TYPE_CAVE+"_ENTRANCE");

	static Side[] ROCK = {new Side(TYPE_CAVE,SUBTYPE_ROCK),new Side(TYPE_CAVE,SUBTYPE_GROUND)};
	static Side[] BLOCK = {new Side(TYPE_CAVE,SUBTYPE_BLOCK)};
	static Side[] GROUND = {new Side(TYPE_CAVE,SUBTYPE_GROUND)};
	static Side[] WALL = {new Side(TYPE_CAVE,SUBTYPE_WALL)};
	static Side[] ENTRANCE = {new Side(TYPE_CAVE,SUBTYPE_ENTRANCE)};
	
	static Side[][] CAVE_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] CAVE_CEILING = new Side[][] { null, null, null,null,GROUND,null };
	static Side[][] CAVE_GROUND_CEILING = new Side[][] { null, null, null,null,GROUND,GROUND };
	static Side[][] CAVE_NORTH = new Side[][] { WALL, null, null,null,null,null };
	static Side[][] CAVE_EAST = new Side[][] { null, WALL, null,null,null,null };
	static Side[][] CAVE_SOUTH = new Side[][] { null, null, WALL,null,null,null };
	static Side[][] CAVE_WEST = new Side[][] { null, null, null,WALL,null,null };

	static Side[][] CAVE_ROCK = new Side[][] { BLOCK, BLOCK, BLOCK,BLOCK,ROCK,GROUND };

	static Side[][] CAVE_ENTRANCE_NORTH = new Side[][] { ENTRANCE, BLOCK, null,BLOCK,null,GROUND };
	static Side[][] CAVE_ENTRANCE_EAST = new Side[][] { BLOCK, ENTRANCE, null,null,null,GROUND };
	static Side[][] CAVE_ENTRANCE_SOUTH = new Side[][] { null, BLOCK, ENTRANCE,BLOCK,null,GROUND };
	static Side[][] CAVE_ENTRANCE_WEST = new Side[][] { BLOCK, null, BLOCK,ENTRANCE,null,GROUND };
	
	
	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	int density,entranceSide, walledSide;

	public Cave(String id, Place parent, PlaceLocator loc,int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int density, int entranceSide, int walledSide) throws Exception{
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
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}
	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ)
	{
		int relX = worldX-origoX*magnification;
		int relY = worldY-origoY*magnification;
		int relZ = worldZ-origoZ*magnification;
		int realSizeX = sizeX*magnification-1;
		int realSizeY = sizeY*magnification-1;
		int realSizeZ = sizeZ*magnification-1;
		
		if (relX==0 || relX==realSizeX)
		{
			Cube c = new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
			c.overwrite = true;
			if (relZ%4==2)
			{
				
				if (relX==0 && (entranceSide&LIMIT_SOUTH)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_EAST,worldX,worldY,worldZ);
					c.overwrite = true;
				} else
				if (relX==realSizeX && (entranceSide&LIMIT_NORTH)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_EAST,worldX,worldY,worldZ);
					c.overwrite = true;
				} else
				if (relX==0 && (walledSide&LIMIT_SOUTH)==0)
				{
					return null;
				} else
				if (relX==realSizeX && (walledSide&LIMIT_NORTH)==0)
				{
					return null;
				}
				return c;
			} else
			{
				if (relX==0 && (walledSide&LIMIT_SOUTH)==0)
				{
					return null;
				} else
				if (relX==realSizeX && (walledSide&LIMIT_NORTH)==0)
				{
					return null;
				}
			}
			return c;
		} else
		if (relZ==0 || relZ==realSizeZ)
		{
			Cube c = new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
			c.overwrite = true;
			if (relX%4==2)
			{
				if (relZ==0 && (entranceSide&LIMIT_WEST)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_NORTH,worldX,worldY,worldZ);
					c.overwrite = true;
				} else
				if (relZ==realSizeZ && (entranceSide&LIMIT_EAST)>0)
				{
					c = new Cube(this,CAVE_ENTRANCE_NORTH,worldX,worldY,worldZ);
					c.overwrite = true;
				} else
				if (relZ==0 && (walledSide&LIMIT_WEST)==0)
				{
					return null;
				} else
				if (relZ==realSizeZ && (walledSide&LIMIT_EAST)==0)
				{
					return null;
				}
				return c;
			} else
			{
				if (relZ==0 && (walledSide&LIMIT_WEST)==0)
				{
					return null;
				} else
				if (relZ==realSizeZ && (walledSide&LIMIT_EAST)==0)
				{
					return null;
				}
			}
			return c;
		} 
		else {
			int per = HashUtil.mixPercentage(worldX/2, worldY/2, worldZ/2);
			
			if (per<density)
			{
				return new Cube(this,CAVE_ROCK,worldX,worldY,worldZ);
			}
		
			Cube c = new Cube(this,CAVE_GROUND_CEILING,worldX,worldY,worldZ);
			c.overwrite = true;
			return c;
		}
	}
	

}
