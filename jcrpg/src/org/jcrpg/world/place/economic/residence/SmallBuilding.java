/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world.place.economic.residence;

import java.util.ArrayList;

import org.jcrpg.audio.AudioServer;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.scene.config.SideTypeModels;
import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.side.RenderedSide;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.economic.Residence;

public abstract class SmallBuilding extends Residence {

	public String TYPE_HOUSE = null;
	public  SideSubType SUBTYPE_WALL;
	public  SideSubType SUBTYPE_INTERNAL_GROUND;
	public  SideSubType SUBTYPE_EXTERNAL_GROUND;
	public  SideSubType SUBTYPE_INTERNAL_CEILING;
	public  SideSubType SUBTYPE_EXTERNAL_DOOR;
	public  SideSubType SUBTYPE_WINDOW;
	public  SideSubType SUBTYPE_BOOKCASE;
	public  SideSubType SUBTYPE_STAIRS;

	Side[] EXTERNAL_DOOR;
	Side[] WINDOW;
	Side[] STAIRS;
	public Side EXTERNAL_GROUND_SIDE;
	
	Side[][] WALL_NORTH;
	Side[][] WALL_EAST;
	Side[][] WALL_SOUTH;
	Side[][] WALL_WEST;
	protected Side[][] WALL_GROUND_NORTH;
	protected Side[][] WALL_GROUND_EAST ;
	protected Side[][] WALL_GROUND_SOUTH;
	protected Side[][] WALL_GROUND_WEST ;
	Side[][] DOOR_GROUND_NORTH;
	Side[][] DOOR_GROUND_EAST ;
	Side[][] DOOR_GROUND_SOUTH;
	Side[][] DOOR_GROUND_WEST;
	Side[][] OPEN_GROUND_WEST;

	Side[][] WINDOW_NORTH ;
	Side[][] WINDOW_EAST;
	Side[][] WINDOW_SOUTH;
	Side[][] WINDOW_WEST;
	Side[][] WINDOW_GROUND_NORTH;
	Side[][] WINDOW_GROUND_EAST;
	Side[][] WINDOW_GROUND_SOUTH;
	Side[][] WINDOW_GROUND_WEST;
	
	protected Side[][] INTERNAL;
	Side[][] INTERNAL_STEPS_NORTH ;
	Side[][] INTERNAL_STEPS_SOUTH;
	Side[][] INTERNAL_STEPS_WEST ;
	Side[][] INTERNAL_STEPS_EAST;
	protected Side[][] EXTERNAL;
	
	
	//public int sizeX, sizeY, sizeZ;
	//public int origoX, origoY, origoZ;
	
	public String modelName;
	public String[] textures;
	
	boolean doubleEntrance = false;
	boolean useSeparateGroundModels = true;
	
	public SmallBuilding(String TYPE, String modelName, String[] textures, boolean doubleEntrance, boolean useSeparateGround)
	{
		super();
		useSeparateGroundModels = useSeparateGround;
		TYPE_HOUSE = TYPE;
		this.modelName = modelName;
		this.doubleEntrance = doubleEntrance;
		this.textures = textures;
		init();
	}
	
	public void init()
	{
		SUBTYPE_WALL = new NotPassable(TYPE_HOUSE+"_WALL");
		SUBTYPE_INTERNAL_GROUND = new GroundSubType(TYPE_HOUSE+"_INTERNAL_GROUND",true);
		SUBTYPE_EXTERNAL_GROUND = new GroundSubType(TYPE_HOUSE+"_EXTERNAL_GROUND",true);
		SUBTYPE_INTERNAL_CEILING = new NotPassable(TYPE_HOUSE+"_INTERNAL_CEILING",true);
		SUBTYPE_EXTERNAL_DOOR = new SideSubType(TYPE_HOUSE+"_EXTERNAL_DOOR");
		SUBTYPE_WINDOW = new NotPassable(TYPE_HOUSE+"_WINDOW");
		SUBTYPE_BOOKCASE = new NotPassable(TYPE_HOUSE+"_BK");
		SUBTYPE_STAIRS = new Climbing(TYPE_HOUSE+"_STAIRS",true);

		SUBTYPE_INTERNAL_GROUND.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_EXTERNAL_GROUND.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_EXTERNAL_GROUND.colorOverwrite = true;
		SUBTYPE_EXTERNAL_GROUND.colorBytes = new byte[] {(byte)150,(byte)150,(byte)150};
		SUBTYPE_INTERNAL_GROUND.colorOverwrite = true;
		SUBTYPE_INTERNAL_GROUND.colorBytes = new byte[] {(byte)150,(byte)150,(byte)0};

		EXTERNAL_DOOR = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_DOOR)};
		WINDOW = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_WINDOW)};
		STAIRS = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_STAIRS)};
		EXTERNAL_GROUND_SIDE = new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND);
		
		WALL_NORTH = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null, null,null,null,null };
		WALL_EAST = new Side[][] { null, {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null,null };
		WALL_SOUTH = new Side[][] { null, null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null };
		WALL_WEST = new Side[][] { null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null };
		WALL_GROUND_NORTH = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL),new Side(TYPE_HOUSE,SUBTYPE_BOOKCASE)}, null, null ,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		WALL_GROUND_EAST = new Side[][] { null, {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		WALL_GROUND_SOUTH = new Side[][] { null, null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		WALL_GROUND_WEST = new Side[][] { null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		DOOR_GROUND_NORTH = new Side[][] { EXTERNAL_DOOR, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		DOOR_GROUND_EAST = new Side[][] { null, EXTERNAL_DOOR, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		DOOR_GROUND_SOUTH = new Side[][] { null, null,EXTERNAL_DOOR, null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		DOOR_GROUND_WEST = new Side[][] { null, null,null,EXTERNAL_DOOR, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		OPEN_GROUND_WEST = new Side[][] { null, null,null,null, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };

		WINDOW_NORTH = new Side[][] { WINDOW, null, null,null,null,null };
		WINDOW_EAST = new Side[][] { null, WINDOW, null,null,null,null };
		WINDOW_SOUTH = new Side[][] { null, null,WINDOW, null,null,null };
		WINDOW_WEST = new Side[][] { null, null,null,WINDOW, null,null };
		WINDOW_GROUND_NORTH = new Side[][] { WINDOW, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		WINDOW_GROUND_EAST = new Side[][] { null, WINDOW, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		WINDOW_GROUND_SOUTH = new Side[][] { null, null,WINDOW, null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		WINDOW_GROUND_WEST = new Side[][] { null, null,null,WINDOW, null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
		
		INTERNAL = new Side[][] { null, null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_CEILING)},{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
		INTERNAL_STEPS_NORTH = new Side[][] { STAIRS, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
		INTERNAL_STEPS_SOUTH = new Side[][] { null, null, STAIRS,null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
		INTERNAL_STEPS_WEST = new Side[][] { null, null, null,STAIRS,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
		INTERNAL_STEPS_EAST = new Side[][] { null, STAIRS, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_INTERNAL_GROUND)} };
		EXTERNAL = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND),new Side(TYPE_GEO,SUBTYPE_GROUND)} };

		if (SideTypeModels.initializedObjects.contains(TYPE_HOUSE))
		{
			return;
		}
		SideTypeModels.initializedObjects.add(TYPE_HOUSE);

		J3DCore.getInstance().standingModels.addSide(SUBTYPE_STAIRS.id, SideTypeModels.EMPTY_SIDE);
		J3DCore.getInstance().standingModels.addSide(SUBTYPE_INTERNAL_CEILING.id, SideTypeModels.EMPTY_SIDE);
		if (useSeparateGroundModels)
		{
			J3DCore.getInstance().standingModels.addSide(SUBTYPE_INTERNAL_GROUND.id, new Integer(29));
			J3DCore.getInstance().standingModels.addSide(SUBTYPE_EXTERNAL_GROUND.id, new Integer(3));
		} else
		{
			J3DCore.getInstance().standingModels.addSide(SUBTYPE_INTERNAL_GROUND.id, SideTypeModels.EMPTY_SIDE);
			J3DCore.getInstance().standingModels.addSide(SUBTYPE_EXTERNAL_GROUND.id, SideTypeModels.EMPTY_SIDE);
		}
		J3DCore.getInstance().standingModels.addSide(SUBTYPE_BOOKCASE.id, SideTypeModels.EMPTY_SIDE);
		J3DCore.getInstance().standingModels.addSide(SUBTYPE_WALL.id, SideTypeModels.EMPTY_SIDE);
		J3DCore.getInstance().standingModels.addSide(SUBTYPE_WINDOW.id, SideTypeModels.EMPTY_SIDE);
		
		SimpleModel sm_hut_model = new SimpleModel(modelName, null);
		sm_hut_model.batchEnabled = true;
		if (textures!=null)
		{
			sm_hut_model.normalMapTexture = textures[0];
			sm_hut_model.heightMapTexture = textures[1];
			sm_hut_model.specMapTexture = textures[2];
		}
		
		J3DCore.getInstance().standingModels.addSide(SUBTYPE_EXTERNAL_DOOR.id, new RenderedSide(new Model[]{sm_hut_model}));
		
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
	public SmallBuilding(String TYPE, String modelName,String[] textures, boolean doubleEntrance, boolean useSeparateGround, String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner) throws Exception {
		super(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		
		if (sizeX<4|| sizeZ<4|| sizeY<getMinimumHeight()) throw new Exception("House below minimum size"+getParameteredKey());

		TYPE_HOUSE = TYPE;
		this.textures = textures;
		this.modelName = modelName;
		this.doubleEntrance = doubleEntrance;
		useSeparateGroundModels = useSeparateGround;
		init();
		
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
		System.out.println("ADDING CUBE -------------------- "+getParameteredKey());
		addStoredCube(sizeX-1,groundLevel,1,new Cube(this,DOOR_GROUND_WEST,0,0,0,true,true));
		if (doubleEntrance)
			addStoredCube(sizeX-1,groundLevel,2,new Cube(this,OPEN_GROUND_WEST,0,0,0,true,true));
		storeParameteredArea();
		
	}
	
	public String getParameteredKey()
	{
		return this.getClass().getName()+" "+sizeX+" "+sizeY+" "+sizeZ+" "+groundLevel+ " "+doubleEntrance;//+" "+origoX+" "+origoY+" "+origoZ;
	}

	@Override
	public boolean generateModel() {
		return false;
	}

	@Override
	public boolean loadModelFromFile() {
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
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("__ HOUSE CUBE");
		return c;
		
	}
	

	@Override
	public int getMinimumHeight() {
		return 1;
	}
	
	
}
