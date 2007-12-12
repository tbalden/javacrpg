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

package org.jcrpg.world.place.water;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.WorldSizeBitBoundaries;

public class River extends Water {

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
	
	static Side[][] RIVER_WATERFALL_WEST_EDGE_NORTH = new Side[][] { null, INTERSECT, null,WATERFALL,null,WATER };
	static Side[][] RIVER_WATERFALL_WEST_EDGE_SOUTH = new Side[][] { null, null, INTERSECT,WATERFALL,null,WATER };
	
	static Side[][] RIVER_WATERFALL_EAST_EDGE_NORTH = new Side[][] { INTERSECT, WATERFALL, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_EAST_EDGE_SOUTH = new Side[][] { null, WATERFALL, null,INTERSECT,null,WATER };

	static Side[][] RIVER_WATERFALL_NORTH_EDGE_WEST_DRIED = new Side[][] { null, null, INTERSECT,null,null,null };
	static Side[][] RIVER_WATERFALL_NORTH_EDGE_EAST_DRIED = new Side[][] { null, null, null,INTERSECT,null,null };
	
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_WEST_DRIED = new Side[][] { null, INTERSECT, null,null,null,null };
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_EAST_DRIED = new Side[][] { INTERSECT, null, null,null,null,null };
	
	static Side[][] RIVER_WATERFALL_WEST_EDGE_NORTH_DRIED = new Side[][] { null, INTERSECT, null,null,null,null };
	static Side[][] RIVER_WATERFALL_WEST_EDGE_SOUTH_DRIED = new Side[][] { null, null, INTERSECT,null,null,null };
	
	static Side[][] RIVER_WATERFALL_EAST_EDGE_NORTH_DRIED = new Side[][] { INTERSECT, null, null,null,null,null };
	static Side[][] RIVER_WATERFALL_EAST_EDGE_SOUTH_DRIED = new Side[][] { null, null, null,INTERSECT,null,null };

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
	// where the river begins...
	public int startSide = 0;
	// where it turns in the end :-)
	public int endSide = 2;
	
	public static int SIDE_SOUTH=0;
	public static int SIDE_WEST=1;
	public static int SIDE_NORTH=2;
	public static int SIDE_EAST=3;
	

	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	int realMiddleX, realMiddleZ;
	public int blockSize;
	
	public River(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int startSide, int endSide, int width, int depth, float curvedness, int curveLength, boolean fillBoundaries) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		blockSize = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		this.width = width;
		this.depth = depth;
		this.startSide = startSide;
		this.endSide = endSide;
		this.curvedness = curvedness;
		this.curveLength = curveLength;
		realMiddleX = blockSize/2;
		realMiddleZ = blockSize/2;
		if (fillBoundaries)
			setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		else
			setBoundaries(new WorldSizeBitBoundaries(magnification,(World)parent));
	}
	
	
	
	@Override
	public Cube getWaterCube(int worldX, int worldY, int worldZ, Cube geoCube, SurfaceHeightAndType surface) 
	{
		
		int x = 0,y = 0,z = 0, checkX = 0, curveZ = 0, edgeWX = 0, edgeWZ = 0;
		int steepAhead = 0, steepBack = 0, steepLeft = 0, steepRight = 0;
		int addWX = 0, addWZ = 0;
		
		
		
		Side[][] edgeRockSide1 = null;
		Side[][] edgeRockSideAheadRock = null;
		Side[][] edgeRockSideBackRock = null;
		Side[][] edgeRockSide2 = null;
		Side[][] waterfallAhead = null;
		Side[][] waterfallBack = null;
		Side[][] waterfallLeft = null;
		Side[][] waterfallRight = null;
		Side[][] waterfallAheadEdge1 = null;
		Side[][] waterfallAheadEdge2 = null;
		Side[][] waterfallBackEdge1 = null;
		Side[][] waterfallBackEdge2 = null;		
		Side[][] waterfallAheadEdge1Dry = null;
		Side[][] waterfallAheadEdge2Dry = null;
		Side[][] waterfallBackEdge1Dry = null;
		Side[][] waterfallBackEdge2Dry = null;

		Side[][] waterfallRightEdgeNext = null;
		Side[][] waterfallRightEdgePrev = null;
		Side[][] waterfallLeftEdgeNext = null;
		Side[][] waterfallLeftEdgePrev = null;
		Side[][] waterfallRightEdgeNextDry = null;
		Side[][] waterfallRightEdgePrevDry = null;
		Side[][] waterfallLeftEdgeNextDry = null;
		Side[][] waterfallLeftEdgePrevDry = null;
		
		int startSide = this.startSide;
		
		startSide = bendStartSide(startSide, endSide, worldX, worldY, worldZ);
		if (startSide==-1) return null;
			
		
		// depending on start side, set the different variables
		if (startSide%2==0) {
			addWX = 0; addWZ = 1;
			edgeRockSide1 = RIVER_ROCKSIDE_WEST;
			edgeRockSide2 = RIVER_ROCKSIDE_EAST;
			edgeRockSideAheadRock = RIVER_ROCKSIDE_NORTH;
			edgeRockSideBackRock = RIVER_ROCKSIDE_SOUTH;

			waterfallAhead = RIVER_WATERFALL_SOUTH;
			waterfallBack = RIVER_WATERFALL_NORTH;
			waterfallLeft = RIVER_WATERFALL_EAST;
			waterfallRight = RIVER_WATERFALL_WEST;

			waterfallAheadEdge1 = RIVER_WATERFALL_SOUTH_EDGE_WEST;
			waterfallAheadEdge2 = RIVER_WATERFALL_SOUTH_EDGE_EAST;
			waterfallBackEdge1 = RIVER_WATERFALL_NORTH_EDGE_WEST;
			waterfallBackEdge2 = RIVER_WATERFALL_NORTH_EDGE_EAST;

			waterfallAheadEdge1Dry = RIVER_WATERFALL_SOUTH_EDGE_WEST_DRIED;
			waterfallAheadEdge2Dry = RIVER_WATERFALL_SOUTH_EDGE_EAST_DRIED;
			waterfallBackEdge1Dry = RIVER_WATERFALL_NORTH_EDGE_WEST_DRIED;
			waterfallBackEdge2Dry = RIVER_WATERFALL_NORTH_EDGE_EAST_DRIED;

			waterfallRightEdgeNext = RIVER_WATERFALL_WEST_EDGE_SOUTH;
			waterfallRightEdgePrev = RIVER_WATERFALL_WEST_EDGE_NORTH;
			waterfallLeftEdgeNext = RIVER_WATERFALL_EAST_EDGE_SOUTH;
			waterfallLeftEdgePrev = RIVER_WATERFALL_EAST_EDGE_NORTH;
			
			waterfallRightEdgeNextDry = RIVER_WATERFALL_WEST_EDGE_SOUTH_DRIED;
			waterfallRightEdgePrevDry = RIVER_WATERFALL_WEST_EDGE_NORTH_DRIED;
			waterfallLeftEdgeNextDry = RIVER_WATERFALL_EAST_EDGE_SOUTH_DRIED;
			waterfallLeftEdgePrevDry = RIVER_WATERFALL_EAST_EDGE_NORTH_DRIED;

			x=worldX%blockSize; 
			y=worldY;
			z=worldZ%blockSize;
			edgeWX = worldX; edgeWZ = worldZ;
			curveZ = worldZ;
			checkX = realMiddleX;
			// water surface
			steepAhead = J3DCore.NORTH;
			steepBack = J3DCore.SOUTH;
			steepLeft = J3DCore.WEST;
			steepRight = J3DCore.EAST;
			
		}
		if (startSide%2==1) {
			addWX = 1; addWZ = 0;
			edgeRockSide1 = RIVER_ROCKSIDE_SOUTH;
			edgeRockSide2 = RIVER_ROCKSIDE_NORTH;
			edgeRockSideAheadRock = RIVER_ROCKSIDE_EAST;
			edgeRockSideBackRock = RIVER_ROCKSIDE_WEST;

			waterfallAhead = RIVER_WATERFALL_WEST;
			waterfallBack = RIVER_WATERFALL_EAST;
			waterfallLeft = RIVER_WATERFALL_SOUTH;
			waterfallRight = RIVER_WATERFALL_NORTH;

			waterfallAheadEdge1 = RIVER_WATERFALL_WEST_EDGE_NORTH;
			waterfallAheadEdge2 = RIVER_WATERFALL_WEST_EDGE_SOUTH;
			waterfallBackEdge1 = RIVER_WATERFALL_EAST_EDGE_NORTH;
			waterfallBackEdge2 = RIVER_WATERFALL_EAST_EDGE_SOUTH;

			waterfallAheadEdge1Dry = RIVER_WATERFALL_WEST_EDGE_NORTH_DRIED;
			waterfallAheadEdge2Dry = RIVER_WATERFALL_WEST_EDGE_SOUTH_DRIED;
			waterfallBackEdge1Dry = RIVER_WATERFALL_EAST_EDGE_NORTH_DRIED;
			waterfallBackEdge2Dry = RIVER_WATERFALL_EAST_EDGE_SOUTH_DRIED;

			waterfallLeftEdgeNext = RIVER_WATERFALL_SOUTH_EDGE_EAST;
			waterfallLeftEdgePrev = RIVER_WATERFALL_SOUTH_EDGE_WEST;
			waterfallRightEdgeNext = RIVER_WATERFALL_NORTH_EDGE_EAST;
			waterfallRightEdgePrev = RIVER_WATERFALL_NORTH_EDGE_WEST;

			waterfallLeftEdgeNextDry = RIVER_WATERFALL_SOUTH_EDGE_EAST_DRIED;
			waterfallLeftEdgePrevDry = RIVER_WATERFALL_SOUTH_EDGE_WEST_DRIED;
			waterfallRightEdgeNextDry = RIVER_WATERFALL_NORTH_EDGE_EAST_DRIED;
			waterfallRightEdgePrevDry = RIVER_WATERFALL_NORTH_EDGE_WEST_DRIED;
			
			x=worldZ%blockSize;
			y=worldY;
			z=worldX%blockSize;
			edgeWX = worldZ; edgeWZ = worldX;
			checkX = realMiddleZ;
			curveZ = worldX;
			// water surface
			steepAhead = J3DCore.EAST;
			steepBack = J3DCore.WEST;
			steepLeft = J3DCore.NORTH;
			steepRight = J3DCore.SOUTH;
		}
		  
		int widthMod1 = (int) ( ((curveZ%(curveLength*2)>=curveLength)?-1:1)*(curvedness)*((curveZ%curveLength)-(curvedness/2)) );
		int width1 = width+widthMod1;
		int width2 = width-widthMod1;
		
		
		if (y != surface.surfaceY || geoCube.steepDirection==SurfaceHeightAndType.NOT_STEEP) 
		{
				
				boolean edge1 = false, edge2 = false, bottom = false, onSurface = (surface.surfaceY==y);
				if (x==checkX-width1)
				{
					if (startSide%2==0) {
						edge1 = !isWaterPoint(worldX-1, worldY, worldZ);	
					} else
					{
						edge1 = !isWaterPoint(worldX, worldY, worldZ-1);
					}
				}
				if (x==checkX+width2)
				{
					if (startSide%2==0) {
						edge2 = !isWaterPoint(worldX+1, worldY, worldZ);	
					} else
					{
						edge2 = !isWaterPoint(worldX, worldY, worldZ+1);
					}
				}
				if (surface.surfaceY-y == depth)
				{
					bottom = true;
				}
				
				if (x>=checkX-width1 && x<=checkX+width2)
				{
					if (onSurface) 
					{
						Cube c = new Cube (this,RIVER_WATER,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
						c.onlyIfOverlaps = true;
						c.overwrite = true;
						c.overwritePower = 1;
						if (noWaterInTheBed) return null;
						return c;
					} else
					{
						boolean nextNotWater = !this.isWaterPoint(worldX+addWX, worldY, worldZ+addWZ);
						boolean prevNotWater = !this.isWaterPoint(worldX-addWX, worldY, worldZ-addWZ);
						// TODO based on next/prev no water add more rockside!
						Cube c = null;
						if (edge1)
						{
							c = new Cube (this,edgeRockSide1,worldX,worldY,worldZ,surface.steepDirection);
							
						} else
						if (edge2)
						{
							c = new Cube (this,edgeRockSide2,worldX,worldY,worldZ,surface.steepDirection);							
						} 
						if (bottom)
						{
							Cube c2 = new Cube (this,surface.steepDirection==SurfaceHeightAndType.NOT_STEEP?RIVER_ROCKSIDE_BOTTOM:RIVER_ROCKSIDE_BOTTOM_STEEP,worldX,worldY,worldZ,surface.steepDirection);
							if (c!=null)
								c = new Cube(c,c2,worldX,worldY,worldZ,surface.steepDirection);
							else
								c = c2;
						}
						if (nextNotWater) // no water next, we need rock wall ahead
						{
							Cube c2 = new Cube (this,edgeRockSideAheadRock,worldX,worldY,worldZ,surface.steepDirection);
							if (c!=null)
								c = new Cube(c,c2,worldX,worldY,worldZ,surface.steepDirection);
							else
								c = c2;
						}
						if (prevNotWater) // no water prev, we need rock wall back
						{
							Cube c2 = new Cube (this,edgeRockSideBackRock,worldX,worldY,worldZ,surface.steepDirection);
							if (c!=null)
								c = new Cube(c,c2,worldX,worldY,worldZ,surface.steepDirection);
							else
								c = c2;
						}
						c.waterCube = true;
						return c;
						
					}
					
				}
		}
		else
		{	// steepy part...
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
					if (geoCube.steepDirection==steepAhead) {
						if (!edge1 && !edge2) {
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallAhead,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,EMPTY,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						if (edge1)
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallAheadEdge1,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallAheadEdge1Dry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallAheadEdge2,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallAheadEdge2Dry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						}
					}
					if (geoCube.steepDirection==steepRight) {
						boolean nextNotWater = !this.isWaterPoint(worldX+addWX, worldY, worldZ+addWZ);
						boolean prevNotWater = !this.isWaterPoint(worldX-addWX, worldY, worldZ-addWZ);
						if (nextNotWater && prevNotWater)
						{
							if (!noWaterInTheBed) 
							{
								c = new Cube (this,waterfallRightEdgeNext,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallRightEdgeNextDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
							c = new Cube(c,new Cube (this,waterfallRightEdgePrevDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							
						} else
						if (nextNotWater)
						{
							// next Z is not water
							if (!noWaterInTheBed) 
							{
								c = new Cube (this,waterfallRightEdgeNext,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallRightEdgeNextDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);	
							}
						}
						else if (prevNotWater)
						{
							// prev Z is not water
							if (!noWaterInTheBed) 
							{
								c = new Cube (this,waterfallRightEdgePrev,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallRightEdgePrevDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						} else 
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallRight,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,EMPTY,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						}
							
					}
					if (geoCube.steepDirection==steepBack) {
						if (!edge1 && !edge2) {
							if (!noWaterInTheBed) {
								c = new Cube (this,waterfallBack,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,EMPTY,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						if (edge1)
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallBackEdge1,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallBackEdge1Dry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						} else
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallBackEdge2,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else 
							{
								c = new Cube (this,waterfallBackEdge2Dry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						}
					}
					if (geoCube.steepDirection==steepLeft) {
						boolean nextNotWater = !this.isWaterPoint(worldX+addWX, worldY, worldZ+addWZ);
						boolean prevNotWater = !this.isWaterPoint(worldX-addWX, worldY, worldZ-addWZ);
						if (nextNotWater && prevNotWater)
						{
							if (!noWaterInTheBed) 
							{
								c = new Cube (this,waterfallLeftEdgeNext,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallLeftEdgeNextDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
							c = new Cube(c,new Cube (this,waterfallLeftEdgePrevDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP),worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							
						} else
						if (nextNotWater)
						{
							// next Z is not water, this is an edge
							if (!noWaterInTheBed) 
							{
								c = new Cube (this,waterfallLeftEdgeNext,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallLeftEdgeNextDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						}
						else if (prevNotWater)
						{
							// prev Z is not water, edge
							if (!noWaterInTheBed) 
							{
								c = new Cube (this,waterfallLeftEdgePrev,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,waterfallLeftEdgePrevDry,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						} else 
						{
							if (!noWaterInTheBed)
							{
								c = new Cube (this,waterfallLeft,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							} else
							{
								c = new Cube (this,EMPTY,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
							}
						}
					}
					c.overwrite = true;
					c.overwritePower = 2;
					return c;
				}
		}
		return new Cube(this,EMPTY,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
	}



	@Override
	public Cube getCube(int x,int y,int z)
	{
		return null;
	}

	public int bendStartSide(int startSide, int endSide, int worldX, int worldY, int worldZ)
	{
		// north-south
		if (startSide==SIDE_NORTH)
		{
			if (worldZ%blockSize<=realMiddleZ+width+1)
			{
				if (endSide==SIDE_EAST)
				{
					if (worldX%blockSize>realMiddleX-width)
					{
						startSide=SIDE_WEST;
					} else
					{
						return -1;
					}
				} else
				if (endSide==SIDE_WEST)
				{
					if (worldX%blockSize<=realMiddleX+width)
					{
						startSide=SIDE_EAST;
					} else
					{
						return -1;
					}
				}
			}
		} else
		if (startSide==SIDE_SOUTH)
		{
			if (worldZ%blockSize>=realMiddleZ-width-1)
			{
				if (endSide==SIDE_EAST)
				{
					if (worldX%blockSize>realMiddleX-width)
					{
						startSide=SIDE_WEST;
					} else
					{
						return -1;
					}
				} else
				if (endSide==SIDE_WEST)
				{
					if (worldX%blockSize<=realMiddleX+width)
					{
						startSide=SIDE_EAST;
					} else
					{
						return -1;
					}
				}
			}
		} else
			
		// east-west
		if (startSide==SIDE_EAST)
		{
			if (worldX%blockSize<=realMiddleX+width+1 || worldZ%blockSize<realMiddleZ-width-1 || worldZ%blockSize>realMiddleZ+width+1)
			{
				if (endSide==SIDE_NORTH)
				{
					if (worldZ%blockSize>realMiddleZ-width)
					{
						startSide=SIDE_SOUTH;
					} else
					{
						return -1;
					}
				} else
				if (endSide==SIDE_SOUTH)
				{
					if (worldZ%blockSize<=realMiddleZ+width)
					{
						startSide=SIDE_NORTH;
					} else
					{
						return -1;
					}
				}
			}
		} else
		if (startSide==SIDE_WEST)
		{
			if (worldX%blockSize>=realMiddleX-width || worldZ%blockSize<realMiddleZ-width || worldZ%blockSize>realMiddleZ+width )
			{
				if (endSide==SIDE_NORTH)
				{
					if (worldZ%blockSize>realMiddleZ-width)
					{
						startSide=SIDE_SOUTH;
					} else
					{
						return -1;
					}
				} else
				if (endSide==SIDE_SOUTH)
				{
					if (worldZ%blockSize<=realMiddleZ+width)
					{
						startSide=SIDE_NORTH;
					} else
					{
						return -1;
					}
				}
			}
		}
		return startSide;
		
	}
	
	@Override
	public boolean isWaterPoint(int worldX, int worldY, int worldZ) {
		int x = 0,y = 0,z = 0, checkX = 0, curveZ = 0;
		
		
		int startSide = this.startSide;
		
		startSide = bendStartSide(startSide, endSide, worldX, worldY, worldZ);
		if (startSide==-1) return false;
			
		
		if (startSide%2==0) {
			x=worldX%blockSize;
			y=worldY;
			z=worldZ%blockSize;
			curveZ = worldZ;
			checkX = realMiddleX;
		}
		if (startSide%2==1) {
			x=worldZ%blockSize;
			y=worldY;
			z=worldX%blockSize;
			curveZ = worldX;
			checkX = realMiddleZ;
		}
		int widthMod1 = (int) ( ((curveZ%(curveLength*2)>=curveLength)?-1:1)*(curvedness)*((curveZ%curveLength)-(curvedness/2)) );
		int width1 = width+widthMod1;
		int width2 = width-widthMod1;
		if (x>=checkX-width1 && x<=checkX+width2)
		{
			//System.out.println("RIVER WATER");
			return true;
		}
		
		//System.out.println("!RIVER WATER "+x + " >= "+checkX+"-"+width1+" && "+x + " <= "+checkX+"+"+width2+"  ");
		return false;
	}

	@Override
	public int getDepth(int x, int y, int z) {
		return depth;
	}
	
	

}
