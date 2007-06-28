package org.jcrpg.world.place.economic;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.NotPassable;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class House extends Economic {

	public static final String TYPE_HOUSE = "HOUSE";
	public static final SideSubType SUBTYPE_WALL = new NotPassable(TYPE_HOUSE+"_WALL");
	public static final SideSubType SUBTYPE_INTERNAL_GROUND = new SideSubType(TYPE_HOUSE+"_INTERNAL_GROUND");
	public static final SideSubType SUBTYPE_EXTERNAL_GROUND = new SideSubType(TYPE_HOUSE+"_EXTERNAL_GROUND");
	public static final SideSubType SUBTYPE_INTERNAL_CEILING = new SideSubType(TYPE_HOUSE+"_INTERNAL_CEILING");
	public static final SideSubType SUBTYPE_EXTERNAL_DOOR = new SideSubType(TYPE_HOUSE+"_EXTERNAL_DOOR");
	public static final SideSubType SUBTYPE_WINDOW = new NotPassable(TYPE_HOUSE+"_WINDOW");

	
	static Side[] EXTERNAL_DOOR = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_DOOR)};
	static Side[] WINDOW = new Side[]{new Side(TYPE_HOUSE,SUBTYPE_WINDOW)};
	
	static Side[][] WALL_NORTH = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null, null,null,null,null };
	static Side[][] WALL_EAST = new Side[][] { null, {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null,null };
	static Side[][] WALL_SOUTH = new Side[][] { null, null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null,null };
	static Side[][] WALL_WEST = new Side[][] { null, null,null,{new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null,null };
	static Side[][] WALL_GROUND_NORTH = new Side[][] { {new Side(TYPE_HOUSE,SUBTYPE_WALL)}, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
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
	static Side[][] EXTERNAL = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	
	
	public int sizeX, sizeY, sizeZ;
	public int origoX, origoY, origoZ;
	
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
	public House(String id, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id,parent, loc);
		
		if (sizeX<4|| sizeZ<4|| sizeY<1) throw new Exception("House below minimum size"+getParameteredKey());

		this.origoX = origoX;this.origoY = origoY;this.origoZ = origoZ;
		this.sizeX = sizeX;this.sizeY = sizeY;this.sizeZ = sizeZ;
		boundaries = BoundaryUtils.createCubicBoundaries(1, sizeX, sizeY, sizeZ, origoX, origoY, origoZ);
		
		if (searchLoadParameteredArea()) return;
		
		for (int y= 0; y<sizeY; y++)
		{
			for (int x= 1; x<sizeX-1; x++)
			{
				for (int z= 1; z<sizeZ-1; z++)
				{
					addStoredCube(x, y, z, new Cube(this,INTERNAL,x,y,z));
				}
				
			}
		}
		for (int y=0; y<sizeY; y++) {
			for (int x=1; x<sizeX-1; x++)
			{
				int z = 0;
				Side[][] s = y==0?WALL_GROUND_NORTH:WALL_NORTH; 
				if (x%3==2) 
				{
					s = y==0?WINDOW_GROUND_NORTH:WINDOW_NORTH; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z));
			}
			for (int x=1; x<sizeX-1; x++)
			{
				int z = sizeZ-1;
				Side[][] s = y==0?WALL_GROUND_SOUTH:WALL_SOUTH;
				if (x%3==2) 
				{
					s = y==0?WINDOW_GROUND_SOUTH:WINDOW_SOUTH; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z));
			}
			for (int z=1; z<sizeZ-1; z++)
			{
				int x = 0;
				Side[][] s = y==0?WALL_GROUND_EAST:WALL_EAST; 
				if (z%3==2) 
				{
					s = y==0?WINDOW_GROUND_EAST:WINDOW_EAST; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z));
			}
			for (int z=1; z<sizeZ-1; z++)
			{
				int x = sizeX-1;
				Side[][] s = y==0?WALL_GROUND_WEST:WALL_WEST; 
				if (z%3==2) 
				{
					s = y==0?WINDOW_GROUND_WEST:WINDOW_WEST; 
				}
				addStoredCube(x, y, z, new Cube(this,s,x,y,z));
			}
		}
		addStoredCube(0, 0, 0, new Cube(this,EXTERNAL,0,0,0));
		addStoredCube(sizeX-1, 0, 0, new Cube(this,EXTERNAL,0,0,0));
		addStoredCube(0, 0, 0+sizeZ-1, new Cube(this,EXTERNAL,0,0,0));
		addStoredCube(sizeX-1, 0, 0+sizeZ-1, new Cube(this,EXTERNAL,0,0,0));
		addStoredCube(sizeX-1,0,1,new Cube(this,DOOR_GROUND_WEST,0,0,0));
		storeParameteredArea();
	}
	
	public String getParameteredKey()
	{
		return this.getClass().getName()+" "+sizeX+" "+sizeY+" "+sizeZ;//+" "+origoX+" "+origoY+" "+origoZ;
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
	public Cube getCube(int worldX, int worldY, int worldZ) {
		Cube o = getStoredCube(worldX-origoX, worldY-origoY, worldZ-origoZ);
		if (o==null) return null;
		Cube c = o.copy(this);
		c.x = worldX;
		c.y = worldY;
		c.z = worldZ;
		return c;
		
	}

}
