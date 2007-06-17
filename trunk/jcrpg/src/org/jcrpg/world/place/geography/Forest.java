package org.jcrpg.world.place.geography;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.geography.forest.Bushes;
import org.jcrpg.world.place.geography.forest.Clearing;

public class Forest extends Geography {

	public static final String TYPE_FOREST = "FOREST";
	public static final SideSubType SUBTYPE_TREE = new SideSubType(TYPE_FOREST+"_TREE");
	public static final SideSubType SUBTYPE_GRASS = new SideSubType(TYPE_FOREST+"_GRASS");

	public HashMap<String, Clearing>clearings;
	public HashMap<String, Bushes>bushes;
	
	
	public Forest(String id, PlaceLocator loc) {
		super(id, loc);
		clearings = new HashMap<String, Clearing>();
		bushes = new HashMap<String, Bushes>();
	}

	static Side[][] TREE = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side(TYPE_FOREST,SUBTYPE_GRASS),new Side(TYPE_FOREST,SUBTYPE_TREE)} };
	static Side[][] GRASS = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side(TYPE_FOREST,SUBTYPE_GRASS)} };

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		Place[] places = getDirectSubPlacesForCoordinates(worldX, worldY, worldZ, new HashMap[]{clearings,bushes});
		for (Place place : places) {
			return place.getCube(worldX, worldY, worldZ);
		}
		return new Cube(this,worldY==0?((worldX+worldZ)%3==0?GRASS:TREE):EMPTY,worldX,worldY,worldZ);
	}
	
	

}
