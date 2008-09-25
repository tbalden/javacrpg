package org.jcrpg.world.place.economic.population;

import java.util.ArrayList;

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.InfrastructureBlockChecker;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.checker.WaterChecker;
import org.jcrpg.world.place.economic.infrastructure.BigBlockInfrastructure;

/**
 * For the monstaz labyrinths - large block populations.
 * @author illes
 *
 */
public class DungeonDistrict extends Population {


	@Override
	public boolean isGeographyAreaUsable() {
		for (Water w:((World)soilGeo.getRoot()).waters.values())
		{
			if (w.isWaterBlock(centerX, w.worldGroundLevel, centerZ))
				return false;
		}
		return super.isGeographyAreaUsable();
	}

	public DungeonDistrict() {
		super();
	}

	public DungeonDistrict(String id, Geography soilGeo, World parent,
			PlaceLocator loc, EntityInstance owner, int blockStartX, int blockStartZ, int centerX, int centerZ) {
		super(id, soilGeo, parent, loc, owner,blockStartX,blockStartZ,centerX,centerZ);
		infrastructure = new BigBlockInfrastructure(this);//DefaultInfrastructure(this);
	}

	@Override
	public Population getInstance(String id, Geography soilGeo, World parent,
			PlaceLocator loc, EntityInstance owner,int blockStartX, int blockStartZ,int centerX, int centerZ) {
		return new DungeonDistrict(id,soilGeo,parent,loc,owner,blockStartX,blockStartZ,centerX,centerZ);
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

	@Override
	public boolean needsFullBlockSizedSubelement() {
		return true;
	}

	
}
