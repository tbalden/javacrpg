/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jcrpg.world.place.economic;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.GroupedBoundaries;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.World;

public abstract class Population extends Economic{
	
	public transient ArrayList<Residence> residenceList = new ArrayList<Residence>();
	public transient ArrayList<EconomicGround> groundList = new ArrayList<EconomicGround>();
	
	public int blockStartX,blockStartZ;
	
	public Town town = null;
	
	public String foundationName = "Anonymous District";
	
	public AbstractInfrastructure infrastructure = null;

	public Population()
	{
		super(null, null, null, null,null,null);
		placeNeedsToBeEnteredForEncounter = true;
	}
	
	public void clear()
	{
		((World)getRoot()).economyContainer.treeLocator.removeAllOfAnObject(this); // we should remove this from treelocator to avoid messing up surface levels of geo.
		for (Residence r:residenceList)
		{
			if (r.getOwnerMember()!=null && r.getOwnerMember().getGeneratedOwnInfrastructures()!=null)
			{
				r.getOwnerMember().getGeneratedOwnInfrastructures().remove(r);
			}
			if (r.getOwnerMember()!=null && r.getOwnerMember().getGeneratedOwnInfrastructures()==null)
			{
				if (J3DCore.LOGGING) Jcrpg.LOGGER.warning("ECONOMIC GENERATED INFRASTUCTURE's SUPPOSED OWNER HAS NO LIST FOR GENERATED INFRASTRUCTURES!");
			}
		}
		residenceList.clear();
		for (EconomicGround r:groundList)
		{
			if (r.getOwnerMember()!=null && r.getOwnerMember().getGeneratedOwnInfrastructures()!=null)
			{
				r.getOwnerMember().getGeneratedOwnInfrastructures().remove(r);
			}
			if (r.getOwnerMember()!=null && r.getOwnerMember().getGeneratedOwnInfrastructures()==null)
			{
				if (J3DCore.LOGGING) Jcrpg.LOGGER.warning("ECONOMIC GENERATED INFRASTUCTURE's SUPPOSED OWNER HAS NO LIST FOR GENERATED INFRASTRUCTURES!");
			}
		}
		groundList.clear();
		boundaries.clear();
		boundaries = new GroupedBoundaries((World)parent,this);
	}
	
	@Override
	public void onLoad()
	{
		residenceList = new ArrayList<Residence>();
		groundList = new ArrayList<EconomicGround>();
		boundaries = new GroupedBoundaries((World)parent,this);
		infrastructure.onLoad();
		infrastructure.setLoadingState(true);
		update();
		infrastructure.setLoadingState(false);
	}
	
	
	public Population(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner, int blockStartX, int blockStartZ) {
		super(id, soilGeo, parent, loc,null,owner);
		this.blockStartX = blockStartX;
		this.blockStartZ = blockStartZ;
		boundaries = new GroupedBoundaries(parent,this);
		placeNeedsToBeEnteredForEncounter = true;
	}
	
	/**
	 * Adds a residential building to the list and to the boundaries.
	 * @param residence
	 */
	public void addResidence(Residence residence)
	{
		residenceList.add(residence);
		((GroupedBoundaries)boundaries).addBoundary(residence.getBoundaries());
	}
	/**
	 * Adds a eco ground to the list and to the boundaries.
	 * @param ecoGround
	 */
	public void addEcoGround(EconomicGround ecoGround)
	{
		groundList.add(ecoGround);
		((GroupedBoundaries)boundaries).addBoundary(ecoGround.getBoundaries());
	}
	
	/**
	 * recalculating things based on instance's groupedboundaries,
	 * also updateLocationAndSize later. TODO this.
	 */
	public void recalculate()
	{
		((GroupedBoundaries)boundaries).recalculateLimits();
		// updating limits
		origoX = boundaries.limitXMin;
		sizeX = boundaries.limitXMax - boundaries.limitXMin;
		origoY = boundaries.limitYMin;
		sizeY = boundaries.limitYMax - boundaries.limitYMin;
		origoZ = boundaries.limitZMin;
		sizeZ = boundaries.limitZMax - boundaries.limitZMin;
		sizeX++;
		sizeY++;
		sizeZ++;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ORIGO X = "+origoX + " / "+sizeX);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ORIGO Y = "+origoY + " / "+sizeY);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ORIGO Z = "+origoZ + " / "+sizeZ);
		//updateLocationAndSize();
	}
	
	// TODO write a quick fitter function to build up a population structure quickl.
	// TODO based on economic (house) heights add steps to the population
	
	/**
	 * Return true if treelocator of economyContainer should be updated.
	 */
	public boolean update() {
		infrastructure.update();
		recalculate();
		return true;
	}

	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		// going through the possible residences...
		for (Economic e:residenceList)
		{
			// if inside..
			if (e.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				// return cube.
				return e.getCube(key, worldX, worldY, worldZ, farView);
			}
		}
		for (Economic e:groundList)
		{
			// if inside..
			if (e.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				// return cube.
				return e.getCube(key, worldX, worldY, worldZ, farView);
			}
		}
		// no cube here...
		return null;
	}

	/**
	 * Returns an economic if there is one in the population in the given position.
	 * @param key
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param farView
	 * @return
	 */
	public Economic getEconomicAtPosition(long key, int worldX, int worldY, int worldZ, boolean farView) {
		// going through the possible residences...
		for (Economic e:residenceList)
		{
			// if inside..
			if (e.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				// return cube.
				return e;
			}
		}
		for (Economic e:groundList)
		{
			// if inside..
			if (e.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				// return cube.
				return e;
			}
		}
		// no cube here...
		return null;
	}

	
	
	public abstract Population getInstance(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner, int blockStartX, int blockStartZ);

	/**
	 * If this returns true, population cannot be any bigger, an entity instance must part 
	 * and find new population place.
	 * @return
	 */
	public boolean isPopulationFull()
	{
		// TODO
		return false;
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
	
}
