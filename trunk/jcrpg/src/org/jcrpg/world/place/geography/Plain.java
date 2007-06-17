package org.jcrpg.world.place.geography;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.PlaceLocator;

public class Plain extends Geography{

	public static final String TYPE_PLAIN = "PLAIN";
	public static final SideSubType SUBTYPE_GRASS = new SideSubType(TYPE_PLAIN+"_GRASS");
	public static final SideSubType SUBTYPE_TREE = new SideSubType(TYPE_PLAIN+"_TREE");
	
	
	public Plain(String id, PlaceLocator loc) {
		super(id, loc);
		// TODO Auto-generated constructor stub
	}


	static Side[][] GRASS = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GRASS)} };
	static Side[][] TREE = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GRASS),new Side(TYPE_PLAIN,SUBTYPE_TREE)} };
	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		return new Cube(this, worldY==0?(worldX%10==0&&worldZ%10==0?TREE:GRASS):EMPTY,worldX,worldY,worldZ);
	}

	
}
