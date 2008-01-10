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
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;


public class MountainNew extends Geography {


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
	static Side[][] MOUNTAIN_CORNER_NORTH = new Side[][] { CORNER, I_EMPTY, I_EMPTY,I_EMPTY,null,null};
	static Side[][] MOUNTAIN_CORNER_EAST = new Side[][] { I_EMPTY, CORNER, I_EMPTY,I_EMPTY,null,null};
	static Side[][] MOUNTAIN_CORNER_SOUTH = new Side[][] { I_EMPTY, I_EMPTY, CORNER,I_EMPTY,null,null};
	static Side[][] MOUNTAIN_CORNER_WEST = new Side[][] { I_EMPTY, I_EMPTY, I_EMPTY,CORNER,null,null };
	static Side[][] STEEP_NORTH = new Side[][] { STEEP, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,null,null };
	static Side[][] STEEP_EAST = new Side[][] { I_EMPTY, STEEP, I_EMPTY,INTERNAL_ROCK_SIDE,null,null };
	static Side[][] STEEP_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STEEP,I_EMPTY,null,null };
	static Side[][] STEEP_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STEEP,null,null };


	
	public MountainNew(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int worldHeight, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc,worldGroundLevel,worldHeight,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,fillBoundaries);
		ruleSet.presentWhereBaseExists = false;
		// override default geo
		/*hmKindCube.put(K_EMPTY, null);
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
		hmKindCube.put(K_CORNER_EAST, new Cube(null,MOUNTAIN_CORNER_EAST,0,0,0,J3DCore.BOTTOM));*/
	}

	@Override
	protected int getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView)
	{
		int Y = 0;
		if (x<0 || z<0 || x>=sizeX || z>=sizeZ) return 0;
		{
			int x1 = sizeX / 2;
			int z1 = sizeZ / 2;
			
			int x2 = x;
			int z2 = z;
			
			int r = sizeX / 2;
			
			Y = r*r - ( (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1) );
		}
		{
			int x1 = sizeX / 8;
			int z1 = sizeZ / 8;
			
			int x2 = x-(sizeX/8)*3;
			int z2 = z-(sizeZ/8)*3;
			
			int r = sizeX / 8;
			
			Y += Math.max(0, r*r - ( (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1) ))*5;
		}
		//int ret = Math.min(0,-Y/30); // valley
		int ret = (int)(Math.max(0,Y*1d/(sizeX*(sizeZ/10)))*worldRealHeight); // mountain
		ret+=((HashUtil.mixPercentage(worldX/3, worldZ/3, 0))-50)/60;
		//System.out.println("MOUNTAIN HEIGHT"+ret);
		return ret;

	}
	

}
