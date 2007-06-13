package org.jcrpg.world.place.economic;

import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class House extends Economic {

	public House(String id, PlaceLocator loc) {
		super(id, loc);
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
	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
