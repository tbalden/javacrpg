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
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class Cave extends Geography {

	
	public static final String TYPE_CAVE = "CAVE";
	public static final SideSubType SUBTYPE_STEEP = new Climbing(TYPE_CAVE+"_GROUND_STEEP");
	public static final SideSubType SUBTYPE_WALL = new NotPassable(TYPE_CAVE+"_WALL");
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_CAVE+"_GROUND");

	static Side[] ROCK = {new Side(TYPE_CAVE,SUBTYPE_WALL)};
	static Side[] GROUND = {new Side(TYPE_CAVE,SUBTYPE_GROUND)};
	static Side[] WALL = {new Side(TYPE_CAVE,SUBTYPE_WALL)};
	
	static Side[][] CAVE_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] CAVE_CEILING = new Side[][] { null, null, null,null,GROUND,null };
	static Side[][] CAVE_GROUND_CEILING = new Side[][] { null, null, null,null,GROUND,GROUND };
	static Side[][] CAVE_NORTH = new Side[][] { WALL, null, null,null,null,null };
	static Side[][] CAVE_EAST = new Side[][] { null, WALL, null,null,null,null };
	static Side[][] CAVE_SOUTH = new Side[][] { null, null, WALL,null,null,null };
	static Side[][] CAVE_WEST = new Side[][] { null, null, null,WALL,null,null };
	
	
	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	/**
	 * Size of cavities
	 */
	int spaceSizeRatioHorizontal,spaceSizeRatioVertical;
	/**
	 * how fast to go deeper
	 */
	int	steepness;
	/**
	 * Ratio of spaces vs. ducts 
	 */
	int	ductSpaceRatioHorizontal,ductSpaceRatioVertical;
	
	

	public Cave(String id, Place parent, PlaceLocator loc,int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int spaceSizeRatioHorizontal, int ductSpaceRatioHorizontal,int spaceSizeRatioVertical, int ductSpaceRatioVertical) throws Exception{
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		this.spaceSizeRatioHorizontal = spaceSizeRatioHorizontal;
		this.ductSpaceRatioHorizontal = ductSpaceRatioHorizontal;
		this.spaceSizeRatioVertical = spaceSizeRatioVertical;
		this.ductSpaceRatioVertical = ductSpaceRatioVertical;
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
		
		int spaceX = relX/spaceSizeRatioHorizontal;
		int spaceY = relY/spaceSizeRatioVertical;
		int spaceZ = relZ/spaceSizeRatioHorizontal;
		int spacePartX = (relX%spaceSizeRatioHorizontal);
		int spacePartY = (relY%spaceSizeRatioVertical);
		int spacePartZ = (relZ%spaceSizeRatioHorizontal);
		
		boolean insideX = spacePartX>ductSpaceRatioHorizontal || spacePartX<spaceSizeRatioHorizontal-ductSpaceRatioHorizontal-1;
		boolean rimXMin = (relX%spaceSizeRatioHorizontal)==ductSpaceRatioHorizontal;  
		boolean rimXMax = (relX%spaceSizeRatioHorizontal)==spaceSizeRatioHorizontal-ductSpaceRatioHorizontal-1;
		
		boolean insideY = spacePartY>ductSpaceRatioVertical || spacePartY<spaceSizeRatioVertical-ductSpaceRatioVertical-1;
		boolean rimYMin = (relY%spaceSizeRatioVertical)==ductSpaceRatioVertical;  
		boolean rimYMax = (relY%spaceSizeRatioVertical)==spaceSizeRatioVertical-ductSpaceRatioVertical-1;
		
		boolean insideZ = spacePartZ>ductSpaceRatioHorizontal || spacePartZ<spaceSizeRatioHorizontal-ductSpaceRatioHorizontal-1;
		boolean rimZMin = (relZ%spaceSizeRatioHorizontal)==ductSpaceRatioHorizontal;  
		boolean rimZMax = (relZ%spaceSizeRatioHorizontal)==spaceSizeRatioHorizontal-ductSpaceRatioHorizontal-1;
		
		if (!(rimXMin || rimXMax || rimZMin || rimZMax)) {
			if (rimYMin && rimYMax)
			{
				return new Cube(this,CAVE_GROUND_CEILING,worldX,worldY,worldZ);
			}
			else
			if (rimYMin)
			{
				return new Cube(this,CAVE_GROUND,worldX,worldY,worldZ);
			} else
			if (rimYMax)
			{
				return new Cube(this,CAVE_CEILING,worldX,worldY,worldZ);
			}
		}
		if (!rimXMin && !rimXMax)
		{
			if (rimZMin)
			{
				return new Cube(this,CAVE_NORTH,worldX,worldY,worldZ);
			} else
			if (rimZMax)
			{
				return new Cube(this,CAVE_SOUTH,worldX,worldY,worldZ);
			}
		}
		if (rimXMin)
		{
			return new Cube(this,CAVE_EAST,worldX,worldY,worldZ);
		} else
		if (rimXMax)
		{
			return new Cube(this,CAVE_WEST,worldX,worldY,worldZ);
		}
		if (insideX&&insideY&&insideZ)
		{
			//return new Cube(this,)
		}
	
		return null;
	}
	

}
