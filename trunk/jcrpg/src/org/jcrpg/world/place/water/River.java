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

package org.jcrpg.world.place.water;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;

import com.jme.math.FastMath;

public class River extends Water{

	public static final String TYPE_RIVER = "RIVER";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_RIVER+"_WATER");
	public static final Climbing SUBTYPE_WATERFALL = new Climbing(TYPE_RIVER+"_WATERFALL");
	public static final SideSubType SUBTYPE_INTERSECT = new Climbing(TYPE_RIVER+"_WATERFALL_INTERSECT");
	public static final NotPassable SUBTYPE_ROCKSIDE = new NotPassable(TYPE_RIVER+"_ROCKSIDE");
	public static final GroundSubType SUBTYPE_ROCKBOTTOM = new GroundSubType(TYPE_RIVER+"_ROCKBOTTOM");
	public static final Climbing SUBTYPE_ROCKBOTTOM_STEEP = new Climbing(TYPE_RIVER+"_ROCKBOTTOM_STEEP");
	public static final Swimming SUBTYPE_WATER_EMPTY = new Swimming(TYPE_RIVER+"_WATER_EMPTY");

	static Side[] WATER = {new Side(TYPE_RIVER,SUBTYPE_WATER)};
	static Side[] WATERFALL = {new Side(TYPE_RIVER,SUBTYPE_WATERFALL)};
	static Side[] INTERSECT = {new Side(TYPE_RIVER,SUBTYPE_INTERSECT)};
	static Side[] ROCKSIDE = {new Side(TYPE_RIVER,SUBTYPE_ROCKSIDE)};
	static Side[] ROCKBOTTOM = {new Side(TYPE_RIVER,SUBTYPE_ROCKBOTTOM)};
	static Side[] ROCKBOTTOM_STEEP = {new Side(TYPE_RIVER,SUBTYPE_ROCKBOTTOM_STEEP)};
	static Side[] WATER_EMPTY = {new Side(TYPE_RIVER,SUBTYPE_WATER_EMPTY)};

	static Side[][] RIVER_WATER = new Side[][] { null, null, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_NORTH = new Side[][] { WATERFALL, null, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_SOUTH = new Side[][] { null, null, WATERFALL,null,null,WATER };
	static Side[][] RIVER_WATERFALL_WEST = new Side[][] { null, null, null,WATERFALL,null,WATER };
	static Side[][] RIVER_WATERFALL_EAST = new Side[][] { null, WATERFALL, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_NORTH_EDGE_WEST = new Side[][] { WATERFALL, null, INTERSECT,null,null,WATER };
	static Side[][] RIVER_WATERFALL_NORTH_EDGE_EAST = new Side[][] { WATERFALL, null, null,INTERSECT,null,WATER };
	
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_WEST = new Side[][] { null, INTERSECT, WATERFALL,null,null,WATER };
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_EAST = new Side[][] { INTERSECT, null, WATERFALL,null,null,WATER };
	
	static Side[][] RIVER_WATERFALL_WEST_EDGE_NORTH = new Side[][] { INTERSECT, null, null,WATERFALL,null,WATER };
	static Side[][] RIVER_WATERFALL_WEST_EDGE_SOUTH = new Side[][] { null, null, INTERSECT,WATERFALL,null,WATER };
	
	static Side[][] RIVER_WATERFALL_EAST_EDGE_NORTH = new Side[][] { INTERSECT, WATERFALL, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_EAST_EDGE_SOUTH = new Side[][] { null, WATERFALL, INTERSECT,null,null,WATER };

	static Side[][] RIVER_WATERFALL_NORTH_EDGE_WEST_DRIED = new Side[][] { null, null, INTERSECT,null,null,null };
	static Side[][] RIVER_WATERFALL_NORTH_EDGE_EAST_DRIED = new Side[][] { null, null, null,INTERSECT,null,null };
	
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_WEST_DRIED = new Side[][] { null, INTERSECT, null,null,null,null };
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_EAST_DRIED = new Side[][] { INTERSECT, null, null,null,null,null };
	
	static Side[][] RIVER_WATERFALL_WEST_EDGE_NORTH_DRIED = new Side[][] { INTERSECT, null, null,null,null,null };
	static Side[][] RIVER_WATERFALL_WEST_EDGE_SOUTH_DRIED = new Side[][] { null, null, INTERSECT,null,null,null };
	
	static Side[][] RIVER_WATERFALL_EAST_EDGE_NORTH_DRIED = new Side[][] { INTERSECT, null, null,null,null,null };
	static Side[][] RIVER_WATERFALL_EAST_EDGE_SOUTH_DRIED = new Side[][] { null, null, INTERSECT,null,null,null };

	static Side[][] RIVER_ROCKSIDE_NORTH = new Side[][] { ROCKSIDE, null, null,null,null,WATER_EMPTY };
	static Side[][] RIVER_ROCKSIDE_SOUTH = new Side[][] { null, null, ROCKSIDE,null,null,WATER_EMPTY };
	static Side[][] RIVER_ROCKSIDE_EAST = new Side[][] { null, ROCKSIDE, null,null,null,WATER_EMPTY };
	static Side[][] RIVER_ROCKSIDE_WEST = new Side[][] { null, null, null,ROCKSIDE,null,WATER_EMPTY };
	static Side[][] RIVER_ROCKSIDE_BOTTOM = new Side[][] { null, null, null, null, null,ROCKBOTTOM };
	static Side[][] RIVER_ROCKSIDE_BOTTOM_STEEP = new Side[][] { null, null, null, null, null,ROCKBOTTOM_STEEP };
	
	//
	public float curvedness = 1;
	public int curveLength = 10;
	//
	public int width = 2;
	//
	public int depth = 1;
	// where the river begins
	public int startSide = 0;
	public int endSide = 2;

	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	int realMiddleX, realMiddleZ;
	private int worldGroundLevel;
	
	public River(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int width, int depth, float curvedness, int curveLength) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		this.width = width;
		this.depth = depth;
		this.curvedness = curvedness;
		this.curveLength = curveLength;
		realMiddleX = sizeX*magnification/2;
		realMiddleZ = sizeZ*magnification/2;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}
	
	
	
	@Override
	public Cube getWaterCube(int x, int y, int z, Cube geoCube, SurfaceHeightAndType surface) {
		int widthMod1 = (int) ( ((z%(curveLength*2)>=curveLength)?-1:1)*(curvedness)*((z%curveLength)-(curvedness/2)) );
		int width1 = width+widthMod1;
		int width2 = width-widthMod1;
		if (y != surface.surfaceY || geoCube.steepDirection==SurfaceHeightAndType.NOT_STEEP) 
		{
			if ( FastMath.abs(startSide-endSide) == 2 ) 
			{
				
				int checkX = realMiddleX + origoX*magnification;
				boolean edge1 = false, edge2 = false, bottom = false, onSurface = (surface.surfaceY==y);
				if (x==checkX-width1)
				{
					edge1 = true;
				}
				if (x==checkX+width2)
				{
					edge2 = true;
				}
				if (surface.surfaceY-y == depth)
				{
					bottom = true;
				}
				
				if (x>=checkX-width1 && x<=checkX+width2)
				{
					if (onSurface) 
					{
						Cube c = new Cube (this,RIVER_WATER,x,y,z,SurfaceHeightAndType.NOT_STEEP);
						c.onlyIfOverlaps = true;
						c.overwrite = true;
						c.overwritePower = 1;
						if (noWaterInTheBed) return null;
						return c;
					} else
					{
						Cube c = null;
						if (edge1)
						{
							System.out.println("!!!!!ROCKSIDE WEST!!!!!");
							c = new Cube (this,RIVER_ROCKSIDE_WEST,x,y,z,surface.steepDirection);
							
						} else
						if (edge2)
						{
							System.out.println("!!!!!ROCKSIDE EAST!!!!!");
							c = new Cube (this,RIVER_ROCKSIDE_EAST,x,y,z,surface.steepDirection);							
						} 
						if (bottom)
						{
							Cube c2 = new Cube (this,surface.steepDirection==SurfaceHeightAndType.NOT_STEEP?RIVER_ROCKSIDE_BOTTOM:RIVER_ROCKSIDE_BOTTOM_STEEP,x,y,z,surface.steepDirection);
							if (c!=null)
								c = new Cube(c,c2,x,y,z,surface.steepDirection);
							else
								c = c2;
						}
						c.waterCube = true;
						return c;
						
					}
					
				}
			}
		}
		else
		{
			if ( FastMath.abs(startSide-endSide) == 2 ) {
				
				int checkX = realMiddleX + origoX*magnification;
				if (x>=checkX-width1 && x<=checkX+width2)
				{
					boolean edge1 = false, edge2 = false;
					if (x==checkX-width1)
					{
						edge1 = true;
					}
					if (x==checkX+width2)
					{
						edge2 = true;
					}
					Cube c = null;
					if (geoCube.steepDirection==J3DCore.NORTH) {
						if (!edge1 && !edge2) {
							if (!noWaterInTheBed)
							{
								c = new Cube (this,RIVER_WATERFALL_SOUTH,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,EMPTY,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						if (edge1)
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,RIVER_WATERFALL_SOUTH_EDGE_WEST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,RIVER_WATERFALL_SOUTH_EDGE_WEST_DRIED,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,RIVER_WATERFALL_SOUTH_EDGE_EAST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,RIVER_WATERFALL_SOUTH_EDGE_EAST_DRIED,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							}
						}
					}
					if (geoCube.steepDirection==J3DCore.EAST) {
						c = new Cube (this,RIVER_WATERFALL_WEST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
					}
					if (geoCube.steepDirection==J3DCore.SOUTH) {
						if (!edge1 && !edge2) {
							if (!noWaterInTheBed) {
								c = new Cube (this,RIVER_WATERFALL_NORTH,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,EMPTY,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						if (edge1)
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,RIVER_WATERFALL_NORTH_EDGE_WEST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,RIVER_WATERFALL_NORTH_EDGE_WEST_DRIED,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,RIVER_WATERFALL_NORTH_EDGE_EAST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							} else 
							{
								c = new Cube (this,RIVER_WATERFALL_NORTH_EDGE_EAST_DRIED,x,y,z,SurfaceHeightAndType.NOT_STEEP);
							}
						}
					}
					if (geoCube.steepDirection==J3DCore.WEST) {
						c = new Cube (this,RIVER_WATERFALL_EAST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
					}
					System.out.println("STEEP WATER!!!");
					return c;
				}
			}
		}
		return new Cube(this,EMPTY,x,y,z,SurfaceHeightAndType.NOT_STEEP);
	}



	@Override
	public Cube getCube(int x,int y,int z)
	{
		return null;
	}

	@Override
	public boolean isWaterPoint(int x, int y, int z) {
		// replace coordinates based on startSide
		// TODO
		int widthMod1 = (int) ( ((z%(curveLength*2)>=curveLength)?-1:1)*(curvedness)*((z%curveLength)-(curvedness/2)) );
		int width1 = width+widthMod1;
		int width2 = width-widthMod1;
		if ( FastMath.abs(startSide-endSide) == 2 ) {
			
			int checkX = realMiddleX + origoX*magnification;
			if (x>=checkX-width1 && x<=checkX+width2)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int getDepth(int x, int y, int z) {
		return depth;
	}
	
	

}
