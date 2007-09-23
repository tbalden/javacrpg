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
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;

import com.jme.math.FastMath;

public class River extends Water{

	public static final String TYPE_RIVER = "RIVER";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_RIVER+"_WATER");
	public static final Swimming SUBTYPE_WATERFALL = new Swimming(TYPE_RIVER+"_WATERFALL");
	public static final SideSubType SUBTYPE_INTERSECT = new Climbing(TYPE_RIVER+"_WATERFALL_INTERSECT");

	static Side[] WATER = {new Side(TYPE_RIVER,SUBTYPE_WATER)};
	static Side[] WATERFALL = {new Side(TYPE_RIVER,SUBTYPE_WATERFALL)};
	static Side[] INTERSECT = {new Side(TYPE_RIVER,SUBTYPE_INTERSECT)};

	static Side[][] RIVER_WATER = new Side[][] { null, null, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_NORTH = new Side[][] { WATER, null, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_SOUTH = new Side[][] { null, null, WATER,null,null,WATER };
	static Side[][] RIVER_WATERFALL_WEST = new Side[][] { null, null, null,WATER,null,WATER };
	static Side[][] RIVER_WATERFALL_EAST = new Side[][] { null, WATER, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_NORTH_EDGE_WEST = new Side[][] { WATERFALL, null, INTERSECT,null,null,WATER };
	static Side[][] RIVER_WATERFALL_NORTH_EDGE_EAST = new Side[][] { WATERFALL, null, null,INTERSECT,null,WATER };
	
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_WEST = new Side[][] { null, INTERSECT, WATERFALL,null,null,WATER };
	static Side[][] RIVER_WATERFALL_SOUTH_EDGE_EAST = new Side[][] { INTERSECT, null, WATERFALL,null,null,WATER };
	
	static Side[][] RIVER_WATERFALL_WEST_EDGE_NORTH = new Side[][] { INTERSECT, null, null,WATERFALL,null,WATER };
	static Side[][] rIVER_WATERFALL_WEST_EDGE_SOUTH = new Side[][] { null, null, INTERSECT,WATERFALL,null,WATER };
	
	static Side[][] RIVER_WATERFALL_EAST_EDGE_NORTH = new Side[][] { INTERSECT, WATERFALL, null,null,null,WATER };
	static Side[][] RIVER_WATERFALL_EAST_EDGE_SOUTH = new Side[][] { null, WATERFALL, INTERSECT,null,null,WATER };
	//
	public int curvedness = 1;
	//
	public int width = 2;
	//
	public int depth = 2;
	// where the river begins
	public int startSide = 0;
	public int endSide = 2;

	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	int realMiddleX, realMiddleZ;
	private int worldGroundLevel;
	
	public River(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		realMiddleX = sizeX*magnification/2;
		realMiddleZ = sizeZ*magnification/2;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}
	
	
	
	@Override
	public Cube getWaterCube(int x, int y, int z, Cube geoCube) {
		// TODO Auto-generated method stub
		if (geoCube.steepDirection==SurfaceHeightAndType.NOT_STEEP) 
		{
			if ( FastMath.abs(startSide-endSide) == 2 ) 
			{
				
				int checkX = realMiddleX + origoX*magnification;
				if (x>=checkX-width && x<=checkX+width)
				{
					Cube c = new Cube (this,RIVER_WATER,x,y,z,SurfaceHeightAndType.NOT_STEEP);
					c.onlyIfOverlaps = true;
					c.overwrite = true;
					c.overwritePower = 1;
					return c;
				}
			}
		}
		else
		{
			if ( FastMath.abs(startSide-endSide) == 2 ) {
				
				int checkX = realMiddleX + origoX*magnification;
				if (x>=checkX-width && x<=checkX+width)
				{
					boolean edge1 = false, edge2 = false;
					if (x==checkX-width)
					{
						edge1 = true;
					}
					if (x==checkX+width)
					{
						edge2 = true;
					}
					Cube c = null;
					if (geoCube.steepDirection==J3DCore.NORTH) {
						if (!edge1 && !edge2) {
							c = new Cube (this,RIVER_WATERFALL_SOUTH,x,y,z,SurfaceHeightAndType.NOT_STEEP);
						} else
						if (edge1)
						{
							c = new Cube (this,RIVER_WATERFALL_SOUTH_EDGE_WEST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
						} else
						{
							c = new Cube (this,RIVER_WATERFALL_SOUTH_EDGE_EAST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
						}
					}
					if (geoCube.steepDirection==J3DCore.EAST) {
						c = new Cube (this,RIVER_WATERFALL_WEST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
					}
					if (geoCube.steepDirection==J3DCore.SOUTH) {
						if (!edge1 && !edge2) {
							c = new Cube (this,RIVER_WATERFALL_NORTH,x,y,z,SurfaceHeightAndType.NOT_STEEP);
						} else
						if (edge1)
						{
							c = new Cube (this,RIVER_WATERFALL_NORTH_EDGE_WEST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
						} else
						{
							c = new Cube (this,RIVER_WATERFALL_NORTH_EDGE_EAST,x,y,z,SurfaceHeightAndType.NOT_STEEP);
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
		return null;
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
		if ( FastMath.abs(startSide-endSide) == 2 ) {
			
			int checkX = realMiddleX + origoX*magnification;
			if (x>=checkX-width && x<=checkX+width)
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
