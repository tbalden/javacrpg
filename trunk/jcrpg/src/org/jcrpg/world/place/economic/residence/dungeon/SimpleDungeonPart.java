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
import org.jcrpg.space.sidetype.NotPassable;
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
	
	/**
	 * is this dungeon used in encounter standing engine? if so, returned cubes will be of an open area.
	 */
	public boolean encounterScenarioMode = false;

	public SimpleDungeonPart(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner)
			throws Exception {
		super(id, soilGeo, parent, loc, sizeX, sizeY, sizeZ, origoX, origoY, origoZ,
				groundLevel, homeBoundaries, owner);
		internalPartSizeX = sizeX-outerEdgeSize*2;
		internalPartSizeZ = sizeZ-outerEdgeSize*2;
		encounterScenarioMode = false;
		audioDescriptor.ENVIRONMENTAL = new String[] {"maze_forebode1","maze_forebode2"};
	}
	int outerEdgeSize = 4;
	int internalPartSizeX = 0;
	int internalPartSizeZ = 0;
	int entrancePosition = 10;

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
		return 3;
	}
	
	@Override
	public boolean overrideGeoHeight() {
		// TODO Auto-generated method stub
		//
		return true;
	}


	public static final String TYPE_DUNGEON = "DUNGEON";
	public static final SideSubType SUBTYPE_WALL = new NotPassable(TYPE_DUNGEON+"_WALL",true);
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_DUNGEON+"_GROUND",true);
	public static final SideSubType SUBTYPE_GROUND_ELEVATED = new GroundSubType(TYPE_DUNGEON+"_GROUND_ELEVATED",true);
	public static final SideSubType SUBTYPE_4_COLUMNS = new GroundSubType(TYPE_DUNGEON+"_4COLUMNS",true);
	public static final SideSubType SUBTYPE_2_COLUMNS = new SideSubType(TYPE_DUNGEON+"_2COLUMNS",true);
	public static final SideSubType SUBTYPE_EXTERNAL_DOOR = new SideSubType(TYPE_DUNGEON+"_EXTERNAL_DOOR");

	static Side[] WALL = {new Side(TYPE_DUNGEON,SUBTYPE_WALL)};
	static Side[] GROUND = {new Side(TYPE_DUNGEON,SUBTYPE_GROUND)};
	static Side[] PILLAR_WALL = {new Side(TYPE_DUNGEON,SUBTYPE_2_COLUMNS)};
	static Side[] GROUND_ELEVATED = {new Side(TYPE_DUNGEON,SUBTYPE_GROUND_ELEVATED)};
	static Side[] GROUND_ENTRANCE_COLUMNS = {new Side(TYPE_DUNGEON,SUBTYPE_4_COLUMNS), new Side(TYPE_DUNGEON,SUBTYPE_GROUND)};
	static Side[] EXTERNAL_DOOR = new Side[]{new Side(TYPE_DUNGEON,SUBTYPE_EXTERNAL_DOOR)};
	
	static 
	{
		SUBTYPE_WALL.colorBytes= new byte[]{(byte)255,(byte)225,(byte)200};
		SUBTYPE_WALL.colorOverwrite = true;
		SUBTYPE_GROUND.colorBytes= new byte[]{(byte)30,(byte)30,(byte)20};
		SUBTYPE_GROUND.colorOverwrite = true;
		SUBTYPE_GROUND.continuousSoundType = "maze_1";
	}
	

	static Side[][] GAP_GROUND = new Side[][] { null, null, null,null,null,GROUND };
	static Side[][] WALL_GROUND_NORTH_WEST = new Side[][] { WALL, null, null, WALL, null, GROUND };
	static Side[][] WALL_GROUND_NORTH_EAST = new Side[][] { WALL, WALL, null, null, null, GROUND };
	static Side[][] WALL_GROUND_SOUTH_EAST = new Side[][] { null, WALL, WALL, null, null, GROUND };
	static Side[][] WALL_GROUND_SOUTH_WEST = new Side[][] { null, null, WALL, WALL, null, GROUND };
	static Side[][] WALL_GROUND_NORTH_SOUTH = new Side[][] { WALL, null, WALL, null, null, GROUND };
	static Side[][] WALL_GROUND_EAST_WEST = new Side[][] { null, WALL,null , WALL, null, GROUND };
	static Side[][] WALL_GROUND_NORTH_SOUTH_ENTRANCE = new Side[][] { WALL, null, WALL, null, null, GROUND_ENTRANCE_COLUMNS };
	static Side[][] WALL_GROUND_EAST_WEST_ENTRANCE = new Side[][] { null, WALL,null , WALL, null, GROUND_ENTRANCE_COLUMNS };
    static Side[][] WALL_GROUND_NORTH = new Side[][] { WALL, null, null, null, null, GROUND };
    static Side[][] WALL_GROUND_WEST = new Side[][] { null, null, null, WALL, null, GROUND };
    static Side[][] WALL_GROUND_SOUTH = new Side[][] { null, null, WALL, null, null, GROUND };
    static Side[][] WALL_GROUND_EAST = new Side[][] { null, WALL, null, null, null, GROUND };
	static Side[][] WALL_GROUND_NORTH_WEST_PILLARS = new Side[][] { PILLAR_WALL, null, null, PILLAR_WALL, null, GROUND };
	static Side[][] WALL_GROUND_NORTH_EAST_PILLARS = new Side[][] { PILLAR_WALL, PILLAR_WALL, null, null, null, GROUND };
	static Side[][] WALL_GROUND_SOUTH_EAST_PILLARS = new Side[][] { null, PILLAR_WALL, PILLAR_WALL, null, null, GROUND };
	static Side[][] WALL_GROUND_SOUTH_WEST_PILLARS = new Side[][] { null, null, PILLAR_WALL, PILLAR_WALL, null, GROUND };
    static Side[][] WALL_GROUND_NORTH_PILLARS = new Side[][] { PILLAR_WALL, null, null, null, null, GROUND };
    static Side[][] WALL_GROUND_WEST_PILLARS = new Side[][] { null, null, null, PILLAR_WALL, null, GROUND };
    static Side[][] WALL_GROUND_SOUTH_PILLARS = new Side[][] { null, null, PILLAR_WALL, null, null, GROUND };
    static Side[][] WALL_GROUND_EAST_PILLARS = new Side[][] { null, PILLAR_WALL, null, null, null, GROUND };
    
    

    
	static Side[][] CAVE_CEILING = new Side[][] { null, null, null,null,WALL,null };
	static Side[][] WALL_CEILING_NORTH_WEST = new Side[][] { WALL, null, null, WALL, GROUND, null};
	static Side[][] WALL_CEILING_NORTH_EAST = new Side[][] { WALL, WALL, null, null, GROUND, null };
	static Side[][] WALL_CEILING_SOUTH_EAST = new Side[][] { null, WALL, WALL, null, GROUND, null };
	static Side[][] WALL_CEILING_SOUTH_WEST = new Side[][] { null, null, WALL, WALL, GROUND, null };
	static Side[][] WALL_CEILING_NORTH_SOUTH = new Side[][] { WALL, null, WALL, null, GROUND, null };
	static Side[][] WALL_CEILING_EAST_WEST = new Side[][] { null, WALL,null , WALL, GROUND, null };
    static Side[][] WALL_CEILING_NORTH = new Side[][] { WALL, null, null, null, GROUND, null};
    static Side[][] WALL_CEILING_WEST = new Side[][] { null, null, null, WALL, GROUND, null};
    static Side[][] WALL_CEILING_SOUTH = new Side[][] { null, null, WALL, null, GROUND, null};
    static Side[][] WALL_CEILING_EAST = new Side[][] { null, WALL, null, null, GROUND, null };

    static Side[][] WALL_NORTH = new Side[][] { WALL, null, null, null, null, null};
    static Side[][] WALL_WEST = new Side[][] { null, null, null, WALL, null, null};
    static Side[][] WALL_SOUTH = new Side[][] { null, null, WALL, null, null, null};
    static Side[][] WALL_EAST = new Side[][] { null, WALL, null, null, null, null };
	static Side[][] WALL_NORTH_WEST = new Side[][] { WALL, null, null, WALL, null, null};
	static Side[][] WALL_NORTH_SOUTH = new Side[][] { WALL, null, WALL, null, null, null };
	static Side[][] WALL_EAST_WEST = new Side[][] { null, WALL,null , WALL, null, null };

	static Side[][] DOOR_GROUND_NORTH_WEST = new Side[][] { EXTERNAL_DOOR, null, null, EXTERNAL_DOOR, null, GROUND };
	static Side[][] DOOR_GROUND_NORTH_EAST = new Side[][] { EXTERNAL_DOOR, EXTERNAL_DOOR, null, null, null, GROUND };
	static Side[][] DOOR_GROUND_SOUTH_EAST = new Side[][] { null, EXTERNAL_DOOR, EXTERNAL_DOOR, null, null, GROUND };
	static Side[][] DOOR_GROUND_SOUTH_WEST = new Side[][] { null, null, EXTERNAL_DOOR, EXTERNAL_DOOR, null, GROUND };
    static Side[][] DOOR_GROUND_NORTH = new Side[][] { EXTERNAL_DOOR, null, null, null, null, GROUND };
    static Side[][] DOOR_GROUND_WEST = new Side[][] { null, null, null, EXTERNAL_DOOR, null, GROUND };
    static Side[][] DOOR_GROUND_SOUTH = new Side[][] { null, null, EXTERNAL_DOOR, null, null, GROUND };
    static Side[][] DOOR_GROUND_EAST = new Side[][] { null, EXTERNAL_DOOR, null, null, null, GROUND };

	
    static Side[][] NORMAL_TOP = new Side[][] { null, null, null,null,GROUND,null };
    static Side[][] EXTERNAL_TOP = new Side[][] { null, null, null,null,null,GROUND_ELEVATED };

    

    static Cube north = new Cube(null,WALL_GROUND_NORTH,0,0,0,true,false);
	static Cube west = new Cube(null,WALL_GROUND_WEST,0,0,0,true,false);
    static Cube south = new Cube(null,WALL_GROUND_SOUTH,0,0,0,true,false);
	static Cube east = new Cube(null,WALL_GROUND_EAST,0,0,0,true,false);
	static Cube northWest = new Cube(null,WALL_GROUND_NORTH_WEST,0,0,0,true,false);
	static Cube northEast = new Cube(null,WALL_GROUND_NORTH_EAST,0,0,0,true,false);
	static Cube southWest = new Cube(null,WALL_GROUND_SOUTH_WEST,0,0,0,true,false);
	static Cube southEast = new Cube(null,WALL_GROUND_SOUTH_EAST,0,0,0,true,false);
	static Cube gap = new Cube(null,GAP_GROUND,0,0,0,true,false);
	static Cube e_northSouth = new Cube(null,WALL_GROUND_NORTH_SOUTH,0,0,0,true,false);
	static Cube e_eastWest = new Cube(null,WALL_GROUND_EAST_WEST,0,0,0,true,false);
	static Cube e_northSouth_columns = new Cube(null,WALL_GROUND_NORTH_SOUTH_ENTRANCE,0,0,0,true,false);
	static Cube e_eastWest_columns = new Cube(null,WALL_GROUND_EAST_WEST_ENTRANCE,0,0,0,true,false);
    static Cube north_columns = new Cube(null,WALL_GROUND_NORTH_PILLARS,0,0,0,true,false);
	static Cube west_columns = new Cube(null,WALL_GROUND_WEST_PILLARS,0,0,0,true,false);
    static Cube south_columns = new Cube(null,WALL_GROUND_SOUTH_PILLARS,0,0,0,true,false);
	static Cube east_columns = new Cube(null,WALL_GROUND_EAST_PILLARS,0,0,0,true,false);
	static Cube northWest_columns = new Cube(null,WALL_GROUND_NORTH_WEST_PILLARS,0,0,0,true,false);
	static Cube northEast_columns = new Cube(null,WALL_GROUND_NORTH_EAST_PILLARS,0,0,0,true,false);
	static Cube southWest_columns = new Cube(null,WALL_GROUND_SOUTH_WEST_PILLARS,0,0,0,true,false);
	static Cube southEast_columns = new Cube(null,WALL_GROUND_SOUTH_EAST_PILLARS,0,0,0,true,false);
	static Cube normal_top = new Cube(null,NORMAL_TOP,0,0,0,true,false);

	static Cube north_ceiling = new Cube(null,WALL_CEILING_NORTH,0,0,0,true,false);
	static Cube west_ceiling = new Cube(null,WALL_CEILING_WEST,0,0,0,true,false);
    static Cube south_ceiling = new Cube(null,WALL_CEILING_SOUTH,0,0,0,true,false);
	static Cube east_ceiling = new Cube(null,WALL_CEILING_EAST,0,0,0,true,false);
	static Cube northWest_ceiling = new Cube(null,WALL_CEILING_NORTH_WEST,0,0,0,true,false);
	static Cube northEast_ceiling = new Cube(null,WALL_CEILING_NORTH_EAST,0,0,0,true,false);
	static Cube southWest_ceiling = new Cube(null,WALL_CEILING_SOUTH_WEST,0,0,0,true,false);
	static Cube southEast_ceiling = new Cube(null,WALL_CEILING_SOUTH_EAST,0,0,0,true,false);
	static Cube gap_ceiling = new Cube(null,CAVE_CEILING,0,0,0,true,false);
	static Cube e_northSouth_ceiling = new Cube(null,WALL_CEILING_NORTH_SOUTH,0,0,0,true,false);
	static Cube e_eastWest_ceiling = new Cube(null,WALL_CEILING_EAST_WEST,0,0,0,true,false);
	static Cube e_northSouth_wall = new Cube(null,WALL_NORTH_SOUTH,0,0,0,true,false);
	static Cube e_eastWest_wall = new Cube(null,WALL_EAST_WEST,0,0,0,true,false);

	static Cube external_top = new Cube(null,EXTERNAL_TOP,0,0,0,true,false);
	
	static Cube edge_ground= new Cube(null,GAP_GROUND,0,0,0,true,true);

	// INTERNAL with ground 
	static Cube north_internal = new Cube(null,WALL_GROUND_NORTH,0,0,0,true,false);
	static Cube west_internal = new Cube(null,WALL_GROUND_WEST,0,0,0,true,false);
	static Cube east_internal = new Cube(null,WALL_GROUND_EAST,0,0,0,true,false);
	static Cube south_internal = new Cube(null,WALL_GROUND_SOUTH,0,0,0,true,false);
	static Cube northWest_internal = new Cube(null,WALL_GROUND_NORTH_WEST,0,0,0,true,false);
	static Cube gap_internal = new Cube(null,GAP_GROUND,0,0,0,true,false);

	// INTERNAL DOOR
	static Cube north_door_internal = new Cube(null,DOOR_GROUND_NORTH,0,0,0,true,false);
	static Cube west_door_internal = new Cube(null,DOOR_GROUND_WEST,0,0,0,true,false);
	static Cube northWest_door_internal = new Cube(null,DOOR_GROUND_NORTH_WEST,0,0,0,true,false);

	// INTERNAL with CEILING
	static Cube north_internal_ceiling = new Cube(null,WALL_CEILING_NORTH,0,0,0,true,false);
	static Cube west_internal_ceiling = new Cube(null,WALL_CEILING_WEST,0,0,0,true,false);
	static Cube south_internal_ceiling = new Cube(null,WALL_CEILING_SOUTH,0,0,0,true,false);
	static Cube east_internal_ceiling = new Cube(null,WALL_CEILING_EAST,0,0,0,true,false);
	static Cube northWest_internal_ceiling = new Cube(null,WALL_CEILING_NORTH_WEST,0,0,0,true,false);
	static Cube gap_internal_ceiling = new Cube(null,CAVE_CEILING,0,0,0,true,false);
	
	// INTERNAL No ground
	static Cube north_internal_wall = new Cube(null,WALL_NORTH,0,0,0,true,false);
	static Cube west_internal_wall = new Cube(null,WALL_WEST,0,0,0,true,false);
	static Cube northWest_internal_wall = new Cube(null,WALL_NORTH_WEST,0,0,0,true,false);
	
	static 
	{
		north.internalLight = true;
		west.internalLight = true;
		south.internalLight = true;
		east.internalLight = true;
		northWest.internalLight = true;
		northEast.internalLight = true;
		southWest.internalLight = true;
		southEast.internalLight = true;
		e_northSouth.internalLight = true;
		e_eastWest.internalLight = true;
		gap.internalLight = true;

		north_ceiling.internalLight = true;
		west_ceiling.internalLight = true;
		south_ceiling.internalLight = true;
		east_ceiling.internalLight = true;
		northWest_ceiling.internalLight = true;
		northEast_ceiling.internalLight = true;
		southWest_ceiling.internalLight = true;
		southEast_ceiling.internalLight = true;
		e_northSouth_ceiling.internalLight = true;
		e_eastWest_ceiling.internalLight = true;
		gap_ceiling.internalLight = true;

		// GROUND
		north_internal.overwrite = true;
		west_internal.overwrite = true;
		east_internal.overwrite = true;
		south_internal.overwrite = true;
		northWest_internal.overwrite = true;
		gap_internal.overwrite= true;
		north_internal.overwritePower = 2;
		west_internal.overwritePower = 2;
		east_internal.overwritePower = 2;
		south_internal.overwritePower = 2;
		northWest_internal.overwritePower = 2;
		gap_internal.overwritePower= 2;
		north_internal.internalLight = true;
		west_internal.internalLight = true;
		south_internal.internalLight = true;
		east_internal.internalLight = true;
		northWest_internal.internalLight = true;
		gap_internal.internalLight = true;
		north_internal.internalCube = true;
		west_internal.internalCube = true;
		south_internal.internalCube = true;
		east_internal.internalCube = true;
		northWest_internal.internalCube = true;
		gap_internal.internalCube = true;
		// DOOR
		north_door_internal.overwrite = true;
		west_door_internal.overwrite = true;
		northWest_door_internal.overwrite = true;
		north_door_internal.overwritePower = 2;
		west_door_internal.overwritePower = 2;
		northWest_door_internal.overwritePower = 2;
		north_door_internal.internalLight = true;
		west_door_internal.internalLight = true;
		northWest_door_internal.internalLight = true;
		north_door_internal.internalCube = true;
		west_door_internal.internalCube = true;
		northWest_door_internal.internalCube = true;

		// CEILING
		north_internal_ceiling.overwrite = true;
		west_internal_ceiling.overwrite = true;
		south_internal_ceiling.overwrite = true;
		east_internal_ceiling.overwrite = true;
		northWest_internal_ceiling.overwrite = true;
		gap_internal_ceiling.overwrite= true;
		north_internal_ceiling.overwritePower = 2;
		west_internal_ceiling.overwritePower = 2;
		south_internal_ceiling.overwritePower = 2;
		east_internal_ceiling.overwritePower = 2;
		northWest_internal_ceiling.overwritePower = 2;
		gap_internal.overwritePower= 2;
		north_internal_ceiling.internalLight = true;
		west_internal_ceiling.internalLight = true;
		south_internal_ceiling.internalLight = true;
		east_internal_ceiling.internalLight = true;
		northWest_internal_ceiling.internalLight = true;
		gap_internal_ceiling.internalLight = true;
		north_internal_ceiling.internalCube = true;
		west_internal_ceiling.internalCube = true;
		south_internal_ceiling.internalCube = true;
		east_internal_ceiling.internalCube = true;
		northWest_internal_ceiling.internalCube = true;
		gap_internal_ceiling.internalCube = true;

		// WALL
		north_internal_wall.overwrite = true;
		west_internal_wall.overwrite = true;
		northWest_internal_wall.overwrite = true;
		north_internal_wall.overwritePower = 2;
		west_internal_wall.overwritePower = 2;
		northWest_internal_wall.overwritePower = 2;
		north_internal_wall.internalLight = true;
		west_internal_wall.internalLight = true;
		northWest_internal_wall.internalLight = true;
		north_internal_wall.internalCube = true;
		west_internal_wall.internalCube = true;
		northWest_internal_wall.internalCube = true;

	}
	
	

	@Override
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		if (labyrinthData==null)
		{
			labyrinthData = MazeTool.getLabyrinth(origoX+origoY+origoZ, internalPartSizeX, internalPartSizeZ, false);
		}

		boolean edge = worldX==origoX || worldX>=origoX+sizeX-1;
		edge = edge || worldZ==origoZ || worldZ>=origoZ+sizeZ-1;

		if (worldY==origoY || worldY == origoY+1 ||worldY==origoY+2)
		{
			int relativeX = worldX-origoX-outerEdgeSize;
			int relativeZ = worldZ-origoZ-outerEdgeSize;
			boolean externalX = false;
			boolean externalXMin = false;
			boolean externalXMax = false;
			boolean externalZ = false;
			boolean externalZMin = false;
			boolean externalZMax = false;
			boolean transPartX = false;
			boolean transPartZ = false;
			boolean entranceX = false;
			boolean entranceZ = false;
			boolean outerEdgeOfTransXMin = false;
			boolean outerEdgeOfTransXMax = false;
			boolean outerEdgeOfTransZMin = false;
			boolean outerEdgeOfTransZMax = false;
			boolean edgeOfTransXMin = false;
			boolean edgeOfTransXMax = false;
			boolean edgeOfTransZMin = false;
			boolean edgeOfTransZMax = false;
			boolean inGeneratedPart = true;
			if (relativeX<0 || relativeX>=internalPartSizeX) 
			{
				inGeneratedPart = false;
				if (relativeX==-1)
				{
					edgeOfTransXMin = true;
				} else
				if (relativeX==internalPartSizeX)
				{
					edgeOfTransXMax = true;
				}
				if (relativeX==-2)
				{
					outerEdgeOfTransXMin = true;
				} else
				if (relativeX==internalPartSizeX+1)
				{
					outerEdgeOfTransXMax = true;
				}
				transPartX = true;
			}
			if (relativeZ<0 || relativeZ>=internalPartSizeZ) 
			{
				inGeneratedPart = false;
				if (relativeZ==-1)
				{
					edgeOfTransZMin = true;
				} else
				if (relativeZ==internalPartSizeZ)
				{
					edgeOfTransZMax = true;
				}
				if (relativeZ==-2)
				{
					outerEdgeOfTransZMin = true;
				} else
				if (relativeZ==internalPartSizeZ+1)
				{
					outerEdgeOfTransZMax = true;
				}
				transPartZ = true;
			}
			if (worldZ-origoZ==entrancePosition)
			{
				entranceX = true;
			}
			if (worldX-origoX==entrancePosition)
			{
				entranceZ = true;
			}
			if (worldX==origoX+1)
			{
				externalX = true;
				externalXMin = true;
			}
			if (worldX==origoX+sizeX-2)
			{
				externalX = true;
				externalXMax = true;
			}
			if (worldZ==origoZ+1)
			{
				externalZ = true;
				externalZMin = true;
			}
			if (worldZ==origoZ+sizeZ-2)
			{
				externalZ = true;
				externalZMax = true;
			}
			
			if (worldY==origoY+2)
			{
				if (edge) return null;
				boolean openArea = false;
				
				if (inGeneratedPart && (labyrinthData[relativeX][relativeZ] & MazeTool.OPEN_PART)>0)
				{
					openArea = true;
				}
				if (!openArea || !inGeneratedPart)
				{
					if (externalX || externalZ) return null;
					return external_top;
				} else
				{
					return null;
				}
			}
			
		
			if (worldY==origoY)
			{
				if (encounterScenarioMode) return gap_internal;
				
				if (edge) return edge_ground;
				boolean horWall = false;
				boolean verWall = false;
				boolean horDoor = false;
				boolean verDoor = false;
				
				if (externalXMin && externalZMin)
				{
					return southWest_columns;
				}
				if (externalXMin && externalZMax)
				{
					return northWest_columns;
				}
				if (externalXMax && externalZMin)
				{
					return southEast_columns;
				}
				if (externalXMax && externalZMax)
				{
					return northEast_columns;
				}

				if ((externalX ||transPartX ) && entranceX)
				{
					if (externalX) return e_northSouth_columns;
					return e_northSouth;
				}
				if (externalXMin)
				{
					return west_columns;
				}
				if (externalXMax)
				{
					return east_columns;
				}

				if ((externalZ || transPartZ) && entranceZ)
				{
					if (externalZ) return e_eastWest_columns;
					return e_eastWest;
				}
				if (externalZMin)
				{
					return south_columns;
				}
				if (externalZMax)
				{
					return north_columns;
				}

				if (outerEdgeOfTransXMin)
				{
					return east;
				}
				if (outerEdgeOfTransXMax)
				{
					return west;
				}

				if (outerEdgeOfTransZMin)
				{
					return north;
				}
				if (outerEdgeOfTransZMax)
				{
					return south;
				}

				// this part MUST come after outerEdge if parts
				if (edgeOfTransXMin)
				{
					return east_internal;
				}
				if (edgeOfTransXMax)
				{
					return west_internal;
				}

				if (edgeOfTransZMin)
				{
					return north_internal;
				}
				if (edgeOfTransZMax)
				{
					return south_internal;
				}
				
				
				if (transPartX || transPartZ)
				{
					return gap;
				}
				
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.WALL_HORI)>0)
				{
					horWall = true;
				}
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.WALL_VERT)>0)
				{
					verWall = true;
				}
				
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.DOOR_HORI)>0)
				{
					horDoor = true;
				}
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.DOOR_VERT)>0)
				{
					verDoor = true;
				}
				
				if (horWall&&verWall) 
				{
					return northWest_internal;
				}
				if (horWall) 
				{
					return north_internal;
				}
				if (verWall) 
				{
					return west_internal;
				}
				if (horDoor&&verDoor) 
				{
					return northWest_door_internal;
				}
				if (horDoor) 
				{
					return north_door_internal;
				}
				if (verDoor) 
				{
					return west_door_internal;
				}
				return gap_internal;
			}
	
			if (worldY==origoY+1)
			{
				if (encounterScenarioMode) return gap_internal_ceiling;

				if (edge) return null;
				boolean horWall = false;
				boolean verWall = false;
				boolean horDoor = false;
				boolean verDoor = false;
				
				if (externalXMin && externalZMin)
				{
					return normal_top;//southWest_ceiling;
				}
				if (externalXMin && externalZMax)
				{
					return normal_top;//northWest_ceiling;
				}
				if (externalXMax && externalZMin)
				{
					return normal_top;//southEast_ceiling;
				}
				if (externalXMax && externalZMax)
				{
					return normal_top;//northEast_ceiling;
				}
				if ((externalX ||transPartX ) && entranceX)
				{
					if (externalX) return e_northSouth_wall;
					return e_northSouth_ceiling;
				}
				if (externalXMin)
				{
					return normal_top;//west_ceiling;
				}
				if (externalXMax)
				{
					return normal_top;//east_ceiling;
				}

				if ((externalZ || transPartZ) && entranceZ)
				{
					if (externalZ) return e_eastWest_wall;
					return e_eastWest_ceiling;
				}
				if (externalZMin)
				{
					return normal_top;//south_ceiling;
				}
				if (externalZMax)
				{
					return normal_top;//north_ceiling;
				}

				if (outerEdgeOfTransXMin)
				{
					return east_ceiling;
				}
				if (outerEdgeOfTransXMax)
				{
					return west_ceiling;
				}
				if (outerEdgeOfTransZMin)
				{
					return north_ceiling;
				}
				if (outerEdgeOfTransZMax)
				{
					return south_ceiling;
				}

				// this part MUST come after outerEdge if parts
				if (edgeOfTransXMin)
				{
					return east_internal_ceiling;
				}
				if (edgeOfTransXMax)
				{
					return west_internal_ceiling;
				}
				if (edgeOfTransZMin)
				{
					return north_internal_ceiling;
				}
				if (edgeOfTransZMax)
				{
					return south_internal_ceiling;
				}
			
				
				if (transPartX || transPartZ)
				{
					return gap_ceiling;
				}
				
				boolean openArea = false;
				
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.OPEN_PART)>0)
				{
					openArea = true;
				}

				if ((labyrinthData[relativeX][relativeZ] & MazeTool.WALL_HORI)>0)
				{
					horWall = true;
				}
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.WALL_VERT)>0)
				{
					verWall = true;
				}
				
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.DOOR_HORI)>0)
				{
					horDoor = true;
				}
				if ((labyrinthData[relativeX][relativeZ] & MazeTool.DOOR_VERT)>0)
				{
					verDoor = true;
				}
				
				if (!openArea)
				{
					if (horWall&&verWall || horDoor&&verDoor || horWall && verDoor || verWall && horDoor) 
					{
						return northWest_internal_ceiling;
					}
					if (horWall||horDoor) 
					{
						return north_internal_ceiling;
					}
					if (verWall||verDoor) 
					{
						return west_internal_ceiling;
					}
					
					return gap_internal_ceiling;
				} else
				{
					if (horWall&&verWall || horDoor&&verDoor || horWall && verDoor || verWall && horDoor) 
					{
						return northWest_internal_wall;
					}
					if (horWall||horDoor) 
					{
						return north_internal_wall;
					}
					if (verWall||verDoor) 
					{
						return west_internal_wall;
					}
					return null;	
				}
				
			}
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

	@Override
	public boolean denyOtherEnvironmentSounds() {
		
		return true;
	}
}
