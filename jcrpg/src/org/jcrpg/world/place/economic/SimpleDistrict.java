package org.jcrpg.world.place.economic;

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.infrastructure.GrownInfrastructure;

public class SimpleDistrict extends Population {

	public SimpleDistrict() {
		super();
	}

	public SimpleDistrict(String id, Geography soilGeo, World parent,
			PlaceLocator loc, EntityInstance owner, int blockStartX, int blockStartZ, int centerX, int centerZ) {
		super(id, soilGeo, parent, loc, owner,blockStartX,blockStartZ,centerX,centerZ);
		infrastructure = new GrownInfrastructure(this);//DefaultInfrastructure(this);
	}

	@Override
	public Population getInstance(String id, Geography soilGeo, World parent,
			PlaceLocator loc, EntityInstance owner,int blockStartX, int blockStartZ,int centerX, int centerZ) {
		return new SimpleDistrict(id,soilGeo,parent,loc,owner,blockStartX,blockStartZ,centerX,centerZ);
	}

}
