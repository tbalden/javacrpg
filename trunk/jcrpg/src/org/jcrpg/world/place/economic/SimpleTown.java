package org.jcrpg.world.place.economic;

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.infrastructure.DefaultInfrastructure;

public class SimpleTown extends Population {

	public SimpleTown() {
		super();
	}

	public SimpleTown(String id, Geography soilGeo, World parent,
			PlaceLocator loc, EntityInstance owner) {
		super(id, soilGeo, parent, loc, owner);
		infrastructure = new DefaultInfrastructure(this);
	}

	@Override
	public Population getInstance(String id, Geography soilGeo, World parent,
			PlaceLocator loc, EntityInstance owner) {
		return new SimpleTown(id,soilGeo,parent,loc,owner);
	}

}
