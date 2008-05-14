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

import org.jcrpg.space.Cube;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.GroupedBoundaries;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.World;

public abstract class Population extends Economic{
	
	public transient ArrayList<Residence> residenceList = new ArrayList<Residence>();
	public transient ArrayList<EconomicGround> groundList = new ArrayList<EconomicGround>();
	
	public AbstractInfrastructure infrastructure = null;

	public Population()
	{
		super(null, null, null, null,null,null);
	}
	
	@Override
	public void onLoad()
	{
		residenceList = new ArrayList<Residence>();
		groundList = new ArrayList<EconomicGround>();
		boundaries = new GroupedBoundaries((World)parent,this);
		update();
	}
	
	
	public Population(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner) {
		super(id, soilGeo, parent, loc,null,owner);
		boundaries = new GroupedBoundaries(parent,this);
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
		System.out.println("ORIGO X = "+origoX + " / "+sizeX);
		System.out.println("ORIGO Y = "+origoY + " / "+sizeY);
		System.out.println("ORIGO Z = "+origoZ + " / "+sizeZ);
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
	
	
	
	public abstract Population getInstance(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner);
	
	/**
	 * Owners are asked if a homeless instance can join the population or not. 
	 * @param candidate
	 * @return
	 */
	public boolean canJoinPopulation(EntityInstance candidate)
	{
		// TODO
		return false;
	}

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
	
}
