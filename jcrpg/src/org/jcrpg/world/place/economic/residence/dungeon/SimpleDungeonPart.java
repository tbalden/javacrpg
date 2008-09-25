/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.world.place.economic.residence.dungeon;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.Residence;
import org.jcrpg.world.place.economic.residence.WoodenHouse;

public class SimpleDungeonPart extends WoodenHouse {



	public SimpleDungeonPart() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SimpleDungeonPart(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner)
			throws Exception {
		super(id, soilGeo, parent, loc, sizeX, sizeY, sizeZ, origoX, origoY, origoZ,
				groundLevel, homeBoundaries, owner);
	}

	@Override
	public Residence getInstance(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner) {
		try {
			return new SimpleDungeonPart(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	public byte[][] labyrinthData;

	@Override
	public int getMinimumHeight() {
		return 1;
	}
	
	@Override
	public boolean overrideGeoHeight() {
		// TODO Auto-generated method stub
		//
		return true;
	}


	public static final String TYPE_CAVE = "CAVE";
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_CAVE+"_GROUND",true);
	public static final SideSubType SUBTYPE_GROUND_ELEVATED = new GroundSubType(TYPE_CAVE+"_GROUND_ELEVATED",true);
	static Side[] GROUND = {new Side(TYPE_CAVE,SUBTYPE_GROUND)};
	static Side[] GROUND_ELEVATED = {new Side(TYPE_CAVE,SUBTYPE_GROUND_ELEVATED)};
	
	static Side[][] CAVE_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] WALL_GROUND_NORTH_WEST = new Side[][] { GROUND, null, null, GROUND, null, GROUND };
    static Side[][] WALL_GROUND_NORTH = new Side[][] { GROUND, null, null, null, null, GROUND };
    static Side[][] WALL_GROUND_WEST = new Side[][] { null, null, null, GROUND, null, GROUND };
    static Side[][] WALL_GROUND_SOUTH = new Side[][] { null, null, GROUND, null, null, GROUND };
    static Side[][] WALL_GROUND_EAST = new Side[][] { null, GROUND, null, null, null, GROUND };

	static Side[][] CAVE_CEILING = new Side[][] { null, null, null,null,GROUND,null };
	static Side[][] WALL_CEILING_NORTH_WEST = new Side[][] { GROUND, null, null, GROUND, GROUND, null};
    static Side[][] WALL_CEILING_NORTH = new Side[][] { GROUND, null, null, null, GROUND, null};
    static Side[][] WALL_CEILING_WEST = new Side[][] { null, null, null, GROUND, GROUND, null};
    static Side[][] WALL_CEILING_SOUTH = new Side[][] { null, null, GROUND, null, GROUND, null};
    static Side[][] WALL_CEILING_EAST = new Side[][] { null, GROUND, null, null, GROUND, null };

    static Side[][] EXTERNAL_TOP = new Side[][] { null, null, null,null,null,GROUND_ELEVATED };


    static Cube north = new Cube(null,WALL_GROUND_NORTH,0,0,0,true,false);
	static Cube west = new Cube(null,WALL_GROUND_WEST,0,0,0,true,false);
    static Cube south = new Cube(null,WALL_GROUND_SOUTH,0,0,0,true,false);
	static Cube east = new Cube(null,WALL_GROUND_EAST,0,0,0,true,false);
	static Cube northWest = new Cube(null,WALL_GROUND_NORTH_WEST,0,0,0,true,false);
	static Cube gap = new Cube(null,CAVE_GROUND,0,0,0,true,false);

	static Cube north_ceiling = new Cube(null,WALL_CEILING_NORTH,0,0,0,true,false);
	static Cube west_ceiling = new Cube(null,WALL_CEILING_WEST,0,0,0,true,false);
    static Cube south_ceiling = new Cube(null,WALL_CEILING_SOUTH,0,0,0,true,false);
	static Cube east_ceiling = new Cube(null,WALL_CEILING_EAST,0,0,0,true,false);
	static Cube northWest_ceiling = new Cube(null,WALL_CEILING_NORTH_WEST,0,0,0,true,false);
	static Cube gap_ceiling = new Cube(null,CAVE_CEILING,0,0,0,true,false);

	static Cube external_top = new Cube(null,EXTERNAL_TOP,0,0,0,true,false);
	
	static Cube edge_ground= new Cube(null,EXTERNAL,0,0,0,true,true);

	static Cube north_internal = new Cube(null,WALL_GROUND_NORTH,0,0,0,true,false);
	static Cube west_internal = new Cube(null,WALL_GROUND_WEST,0,0,0,true,false);
	static Cube northWest_internal = new Cube(null,WALL_GROUND_NORTH_WEST,0,0,0,true,false);
	static Cube gap_internal = new Cube(null,CAVE_GROUND,0,0,0,true,false);

	static Cube north_internal_ceiling = new Cube(null,WALL_CEILING_NORTH,0,0,0,true,false);
	static Cube west_internal_ceiling = new Cube(null,WALL_CEILING_WEST,0,0,0,true,false);
	static Cube northWest_internal_ceiling = new Cube(null,WALL_CEILING_NORTH_WEST,0,0,0,true,false);
	static Cube gap_internal_ceiling = new Cube(null,CAVE_CEILING,0,0,0,true,false);
	
	static 
	{
		north.internalLight = true;
		west.internalLight = true;
		south.internalLight = true;
		east.internalLight = true;
		northWest.internalLight = true;
		gap.internalLight = true;
		north_ceiling.internalLight = true;
		west_ceiling.internalLight = true;
		south_ceiling.internalLight = true;
		east_ceiling.internalLight = true;
		northWest_ceiling.internalLight = true;
		gap_ceiling.internalLight = true;

		north_internal.overwrite = true;
		west_internal.overwrite = true;
		northWest_internal.overwrite = true;
		gap_internal.overwrite= true;
		north_internal.overwritePower = 2;
		west_internal.overwritePower = 2;
		northWest_internal.overwritePower = 2;
		gap_internal.overwritePower= 2;

		north_internal.internalLight = true;
		west_internal.internalLight = true;
		northWest_internal.internalLight = true;
		gap_internal.internalLight = true;

		north_internal.internalCube = true;
		west_internal.internalCube = true;
		northWest_internal.internalCube = true;
		gap_internal.internalCube = true;

		north_internal_ceiling.overwrite = true;
		west_internal_ceiling.overwrite = true;
		northWest_internal_ceiling.overwrite = true;
		gap_internal_ceiling.overwrite= true;
		north_internal_ceiling.overwritePower = 2;
		west_internal_ceiling.overwritePower = 2;
		northWest_internal_ceiling.overwritePower = 2;
		gap_internal.overwritePower= 2;

		north_internal_ceiling.internalLight = true;
		west_internal_ceiling.internalLight = true;
		northWest_internal_ceiling.internalLight = true;
		gap_internal_ceiling.internalLight = true;

		north_internal_ceiling.internalCube = true;
		west_internal_ceiling.internalCube = true;
		northWest_internal_ceiling.internalCube = true;
		gap_internal_ceiling.internalCube = true;
	
	}
	
	

	@Override
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		if (labyrinthData==null)
		{
			labyrinthData = MazeTool.getLabyrinth(origoX+origoY+origoZ, sizeX+1, sizeZ+1, null);
		}

		boolean edge = worldX==origoX || worldX>=origoX+sizeX-1;
		edge = edge || worldZ==origoZ || worldZ>=origoZ+sizeZ-1;

		if (worldY==origoY+2)
		{
			if (edge) return null;
			return external_top;
		}
		if (worldY==origoY)
		//if (kind==K_NORMAL_GROUND)
		{
			if (edge) return edge_ground;
			boolean horWall = false;
			boolean verWall = false;
			if ((labyrinthData[worldX-origoX][worldZ-origoZ] & MazeTool.WALL_HORI)>0)
			{
				horWall = true;
			}
			if ((labyrinthData[worldX-origoX][worldZ-origoZ] & MazeTool.WALL_VERT)>0)
			{
				verWall = true;
			}
			
			boolean external = worldX==origoX+1 || worldX>=origoX+sizeX-2;
			external = external || worldZ==origoZ+1 || worldZ>=origoZ+sizeZ-2;
			
			if (external)
			{
				if (worldX==origoX+1)
				{
					return west;
				}
				if (worldX==origoX+sizeX-2)
				{
					return east;
				}
				if (worldZ==origoZ+1)
				{
					return south;
				}
				if (worldZ==origoZ+sizeZ-2)
				{
					return north;
				}
			}
			
			if (horWall&&verWall) 
			{
				return external?northWest:northWest_internal;
			}
			if (horWall) 
			{
				return external?north:north_internal;
			}
			if (verWall) 
			{
				return external?west:west_internal;
			}
			return external?gap:gap_internal;
		}

		if (worldY==origoY+1)
			//if (kind==K_NORMAL_GROUND)
			{
				if (edge) return null;
				boolean horWall = false;
				boolean verWall = false;
				if ((labyrinthData[worldX-origoX][worldZ-origoZ] & MazeTool.WALL_HORI)>0)
				{
					horWall = true;
				}
				if ((labyrinthData[worldX-origoX][worldZ-origoZ] & MazeTool.WALL_VERT)>0)
				{
					verWall = true;
				}
				
				boolean external = worldX==origoX+1 || worldX>=origoX+sizeX-2;
				external = external || worldZ==origoZ+1 || worldZ>=origoZ+sizeZ-2;
				
				if (external)
				{
					if (worldX==origoX+1)
					{
						return west_ceiling;
					}
					if (worldX==origoX+sizeX-2)
					{
						return east_ceiling;
					}
					if (worldZ==origoZ+1)
					{
						return south_ceiling;
					}
					if (worldZ==origoZ+sizeZ-2)
					{
						return north_ceiling;
					}
				}
				
				if (horWall&&verWall) 
				{
					return external?northWest_ceiling:northWest_internal_ceiling;
				}
				if (horWall) 
				{
					return external?north_ceiling:north_internal_ceiling;
				}
				if (verWall) 
				{
					return external?west_ceiling:west_internal_ceiling;
				}
				return external?gap_ceiling:gap_internal_ceiling;
			}

		return null;
	}

	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		float[] kind = getCubeKind(key, worldX, worldY, worldZ, farView);
		Cube c = getCubeObject((int)kind[4], worldX, worldY, worldZ, farView);
		if (c==null) return c;
		c = c.copy(this);
		c.x = worldX;
		c.y = worldY;
		c.z = worldZ;
		c.cornerHeights = kind;
		c.middleHeight = (kind[0]+kind[1]+kind[2]+kind[3])/4f;
		c.angleRatio = Math.max( Math.abs(kind[0]-kind[2]) , Math.max( Math.abs(kind[1]-kind[3]) , Math.max( Math.abs(kind[0]-kind[1]) , Math.abs(kind[2]-kind[3]))));
		c.geoCubeKind = (int)kind[4];
		c.pointHeightFloat = kind[9];
		c.pointHeightInt = (int)kind[9];
		return c;
	}
	@Override
	public float[] getCubeKind(long key, int worldX, int worldY, int worldZ, boolean farView) {
		// let's check for waters here...
		boolean water = false;
		for (Water geo:((World)getRoot()).waters.values())
		{
			{
				if (geo.getBoundaries().isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					if (geo.isWaterPoint(worldX, geo.worldGroundLevel, worldZ, farView)) water = true;
				}
			}
		}
		float[] retKind = null;
		if (soilGeo!=null && soilGeo.getBoundaries().isInside(worldX, worldY, worldZ))
		{
			retKind = soilGeo.getCubeKind(key, worldX,worldY,worldZ, farView);
		} else
		{
			retKind = super.getCubeKindOutside(key, worldX, worldY, worldZ, farView);
		}
		if (water && retKind[4]==K_NORMAL_GROUND)
		{
			retKind[4] = K_WATER_GROUND;
		}
		return retKind;
		
	}

	@Override
	protected float getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView) {
		// use the height defined by the geography here...
		if (soilGeo.getBoundaries().isInside(worldX, soilGeo.worldGroundLevel, worldZ))
		{
			int[] values = soilGeo.calculateTransformedCoordinates(worldX, soilGeo.worldGroundLevel, worldZ);
			return soilGeo.getPointHeight(values[3], values[5], values[0], values[2],worldX,worldZ, farView);
		}
		return getPointHeightOutside(worldX, worldZ, farView);
	}

	
	@Override
	public boolean isFullBlockSized() {
		return true;
	}

}
