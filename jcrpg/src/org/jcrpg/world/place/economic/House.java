/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.world.place.economic;

import java.util.ArrayList;

import org.jcrpg.audio.AudioServer;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class House extends Residence {

	public static final String TYPE_HOUSE = "HOUSE";
	public static final SideSubType SUBTYPE_WALL = new NotPassable(TYPE_HOUSE+"_WALL");
	public static final SideSubType SUBTYPE_INTERNAL_GROUND = new GroundSubType(TYPE_HOUSE+"_INTERNAL_GROUND");
	public static final SideSubType SUBTYPE_EXTERNAL_GROUND = new GroundSubType(TYPE_HOUSE+"_EXTERNAL_GROUND");
	public static final SideSubType SUBTYPE_INTERNAL_CEILING = new NotPassable(TYPE_HOUSE+"_INTERNAL_CEILING");
	public static final SideSubType SUBTYPE_EXTERNAL_DOOR = new SideSubType(TYPE_HOUSE+"_EXTERNAL_DOOR");
	public static final SideSubType SUBTYPE_WINDOW = new NotPassable(TYPE_HOUSE+"_WINDOW");
	public static final SideSubType SUBTYPE_BOOKCASE = new NotPassable(TYPE_HOUSE+"_BK");
	public static final SideSubType SUBTYPE_STAIRS = new Climbing(TYPE_HOUSE+"_STAIRS");

	static
	{
		SUBTYPE_INTERNAL_GROUND.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_EXTERNAL_GROUND.audioStepType = AudioServer.STEP_STONE;
	}
	
	static Side[] EXTERNAL_DOOR = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_DOOR)};
	static Side[] WINDOW = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_WINDOW)};
	static Side[] STAIRS = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_STAIRS)};
	
	static Side[][] WALL_NORTH = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null, null,null,null,null };
	static Side[][] WALL_EAST = new Side[][] { null, {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null,null };
	static Side[][] WALL_SOUTH = new Side[][] { null, null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null };
	static Side[][] WALL_WEST = new Side[][] { null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null };
	static Side[][] WALL_GROUND_NORTH = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL),new Side(TYPE_HOUSE,SUBTYPE_BOOKCASE)}, null, null ,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] WALL_GROUND_EAST = new Side[][] { null, {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] WALL_GROUND_SOUTH = new Side[][] { null, null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] WALL_GROUND_WEST = new Side[][] { null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] DOOR_GROUND_NORTH = new Side[][] { EXTERNAL_DOOR, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] DOOR_GROUND_EAST = new Side[][] { null, EXTERNAL_DOOR, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] DOOR_GROUND_SOUTH = new Side[][] { null, null,EXTERNAL_DOOR, null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] DOOR_GROUND_WEST = new Side[][] { null, null,null,EXTERNAL_DOOR, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };

	static Side[][] WINDOW_NORTH = new Side[][] { WINDOW, null, null,null,null,null };
	static Side[][] WINDOW_EAST = new Side[][] { null, WINDOW, null,null,null,null };
	static Side[][] WINDOW_SOUTH = new Side[][] { null, null,WINDOW, null,null,null };
	static Side[][] WINDOW_WEST = new Side[][] { null, null,null,WINDOW, null,null };
	static Side[][] WINDOW_GROUND_NORTH = new Side[][] { WINDOW, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] WINDOW_GROUND_EAST = new Side[][] { null, WINDOW, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] WINDOW_GROUND_SOUTH = new Side[][] { null, null,WINDOW, null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] WINDOW_GROUND_WEST = new Side[][] { null, null,null,WINDOW, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	
	static Side[][] INTERNAL = new Side[][] { null, null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_CEILING)},{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
	static Side[][] INTERNAL_STEPS_NORTH = new Side[][] { STAIRS, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
	static Side[][] INTERNAL_STEPS_SOUTH = new Side[][] { null, null, STAIRS,null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
	static Side[][] INTERNAL_STEPS_WEST = new Side[][] { null, null, null,STAIRS,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
	static Side[][] INTERNAL_STEPS_EAST = new Side[][] { null, STAIRS, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
	static Side[][] EXTERNAL = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND),new Side(TYPE_GEO,SUBTYPE_GROUND)} };
	
	
	//public int sizeX, sizeY, sizeZ;
	//public int origoX, origoY, origoZ;
	
	public House()
	{
		super();
	}
	
	/**
	 * Simple Stone House
	 * @param id
	 * @param loc
	 * @param sizeX Minimum is 4.
	 * @param sizeY Minimum is 1.
	 * @param sizeZ Minimum is 4.
	 * @param origoX
	 * @param origoY
	 * @param origoZ
	 * @throws Exception
	 */
	public House(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner) throws Exception {
		super(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		
		if (sizeX<4|| sizeZ<4|| sizeY<1) throw new Exception("House below minimum size"+getParameteredKey());

		
		if (searchLoadParameteredArea()) return;
		
		for (int y= 0; y<sizeY; y++)
		{
			for (int x= 1; x<sizeX-1; x++)
			{
				for (int z= 1; z<sizeZ-1; z++)
				{
					
					{
						if (y%2==0) {
							if (sizeY-1!=y)
							if (x == 1 && z == 2)
							{
								addStoredCube(x, y, z, new Cube(this,INTERNAL_STEPS_SOUTH,x,y,z,true,y==groundLevel));
								continue;
							}
							if (y > 0 && x == 2 && z == 2)
							{
								continue;
							}
						} else
						{
							if (sizeY-1!=y)
							if (x == 2 && z == 2)
							{
								addStoredCube(x, y, z, new Cube(this,INTERNAL_STEPS_NORTH,x,y,z,true,y==groundLevel));
							}
							if (y > 0 && x == 1 && z == 2)
							{
								// no ground needed above the stairs
								continue;
							}
						}
					}
					addStoredCube(x, y, z, new Cube(this,INTERNAL,x,y,z,true,y==groundLevel));
					
				}
				
			}
		}
		for (int y=0; y<sizeY; y++) {
			for (int x=1; x<sizeX-1; x++)
			{
				int z = 0;
				Side[][] s = y==groundLevel?WALL_GROUND_NORTH:WALL_NORTH; 
				if (x%3==2) 
				{
					s = y==groundLevel?WINDOW_GROUND_NORTH:WINDOW_NORTH; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z,y==groundLevel,y==groundLevel));
			}
			for (int x=1; x<sizeX-1; x++)
			{
				int z = sizeZ-1;
				Side[][] s = y==groundLevel?WALL_GROUND_SOUTH:WALL_SOUTH;
				if (x%3==2) 
				{
					s = y==groundLevel?WINDOW_GROUND_SOUTH:WINDOW_SOUTH; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z,y==groundLevel,y==groundLevel));
			}
			for (int z=1; z<sizeZ-1; z++)
			{
				int x = 0;
				Side[][] s = y==groundLevel?WALL_GROUND_EAST:WALL_EAST; 
				if (z%3==2) 
				{
					s = y==groundLevel?WINDOW_GROUND_EAST:WINDOW_EAST; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z,y==groundLevel,y==groundLevel));
			}
			for (int z=1; z<sizeZ-1; z++)
			{
				int x = sizeX-1;
				Side[][] s = y==groundLevel?WALL_GROUND_WEST:WALL_WEST; 
				if (z%3==2) 
				{
					s = y==groundLevel?WINDOW_GROUND_WEST:WINDOW_WEST; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z,y==groundLevel,y==groundLevel));
			}
		}
		addStoredCube(0, groundLevel, 0, new Cube(this,EXTERNAL,0,0,0,true,true));
		addStoredCube(sizeX-1, groundLevel, 0, new Cube(this,EXTERNAL,0,0,0,true,true));
		addStoredCube(0, groundLevel, 0+sizeZ-1, new Cube(this,EXTERNAL,0,0,0,true,true));
		addStoredCube(sizeX-1, groundLevel, 0+sizeZ-1, new Cube(this,EXTERNAL,0,0,0,true,true));
		addStoredCube(sizeX-1,groundLevel,1,new Cube(this,DOOR_GROUND_WEST,0,0,0,true,true));
		storeParameteredArea();
		
	}
	
	public String getParameteredKey()
	{
		return this.getClass().getName()+" "+sizeX+" "+sizeY+" "+sizeZ+" "+groundLevel;//+" "+origoX+" "+origoY+" "+origoZ;
	}

	@Override
	public boolean generateModel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loadModelFromFile() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		Cube o = getStoredCube(worldX-origoX, worldY-origoY, worldZ-origoZ);
		if (o==null) return null;
		Cube c = o.copy(this);
		c.x = worldX;
		c.y = worldY;
		c.z = worldZ;
		
		if (o.canContainFlora) {
			float[] kind =super.getCubeKind(key, worldX, worldY, worldZ, farView);		
			if (kind!=null) {
				c.cornerHeights=kind;
				c.middleHeight = (kind[0]+kind[1]+kind[2]+kind[3])/4f;
			}
		}
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("__ HOUSE CUBE");
		return c;
		
	}
	
	
	

	@Override
	public Residence getInstance(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)
	{
		try {
			return new House(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	private ArrayList<int[]> tmpSettlePlaces = new ArrayList<int[]>();

	@Override
	public ArrayList<int[]> getPossibleSettlePlaces() {
		tmpSettlePlaces.clear();
		tmpSettlePlaces.add(new int[]{origoX+sizeX/2, origoY, origoZ+sizeZ/2});
		return tmpSettlePlaces;
	}
	
	
}
