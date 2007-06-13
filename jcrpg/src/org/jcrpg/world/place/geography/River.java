package org.jcrpg.world.place.geography;

import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class River extends Place{

	public static final String TYPE_RIVER = "RIVER";
	public static final String SUBTYPE_WATER = TYPE_RIVER+"_WATER";
	
	public River(String id, PlaceLocator loc) {
		super(id, loc);
		// TODO Auto-generated constructor stub
	}

}
