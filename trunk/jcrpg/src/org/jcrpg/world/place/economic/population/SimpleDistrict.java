package org.jcrpg.world.place.economic.population;

import java.util.ArrayList;

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.InfrastructureBlockChecker;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.checker.WaterChecker;
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

	public static ArrayList<InfrastructureBlockChecker> checkers = new ArrayList<InfrastructureBlockChecker>();
	static
	{
		checkers.add(new WaterChecker());
	}
	
	@Override
	public ArrayList<InfrastructureBlockChecker> getBlockCheckers() {
		// TODO Auto-generated method stub
		return checkers;
	}

}
