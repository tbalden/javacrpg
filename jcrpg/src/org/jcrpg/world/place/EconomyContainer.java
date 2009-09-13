/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 

package org.jcrpg.world.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Town;
import org.jcrpg.world.place.economic.residence.RoadShrine;
import org.jcrpg.world.place.economic.road.RoadNetwork;
import org.jcrpg.world.time.Time;

public class EconomyContainer {

	public transient TreeLocator treeLocator = null;
	
	public TreeMap<String, Economic> economics;
	
	public RoadNetwork roadNetwork;
	
	
	/**
	 * Virtual container for groups of Populations that build up Towns
	 */
	public ArrayList<Town> towns = new ArrayList<Town>();
	public World w;
	public EconomyContainer(World w)
	{
		this.w = w;
		treeLocator = new TreeLocator(w);
		economics = new TreeMap<String, Economic>();
		try {
			roadNetwork = new RoadNetwork("roadNetwork", this,w.getSeaLevel(1),w.magnification, w.sizeX, w.sizeY, w.sizeZ, 0, w.getSeaLevel(w.magnification)-1, 0);
		} catch (Exception ex)
		{
			Logger.getLogger("EconomyContainer").fine("!!! roadnetwork failure "+ex);
			ex.printStackTrace();
		}
	}

	public ArrayList<Economic> getEconomicsInColumn(int worldX,int worldZ)
	{
		ArrayList<Object> economicsList = treeLocator.getElements(worldX, 0, worldZ);
		if (economicsList==null) return null;
		ArrayList<Economic> list = new ArrayList<Economic>();
		for (Object o:economicsList)
		{
			list.add((Economic)o);
		}
		return list;
	}
	
	/**
	 * Economic cube getter. Null if no economic there.
	 * @param key
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param farView
	 * @return economic's cube, or null.
	 */
	public Cube getEconomicCube(long key, int worldX, int worldY, int worldZ,boolean farView, Time time)
	{
		ArrayList<Object> economicsList = treeLocator.getElements(worldX, worldY, worldZ);
		if (economicsList!=null) 
		{
			//if (economicsList.size()>1) if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("######## economiclist POPULATION is "+economicsList.size());
			for (Object o:economicsList)
			{
				Economic eco = (Economic)o;
				if (eco.getBoundaries().isInside(worldX, worldY, worldZ)) {
					if (key==-1)
					{
						key = Boundaries.getKey(worldX, worldY, worldZ);
					}
					Cube c = eco.getCube(key, worldX, worldY, worldZ, farView);
					if (eco instanceof RoadShrine)
					{
						System.out.println("RENDERING CUBE OF ROADSHRINE... RESULT = "+eco+" - "+worldX+"/"+worldY+"/"+worldZ+" - "+c);
					}
					/*if (c!=null && c.internalEconomicUnitForFloraQuery!=null && c.internalEconomicUnitForFloraQuery instanceof EconomicGround)
					{
						System.out.println(c.canContainFlora);
					}*/
					
					// storage object lookup...
					if (c!=null)
					{
						if (c.containingInternalEconomicUnit!=null)
						{
							HashMap<Long, int[]> storages = c.containingInternalEconomicUnit.getStorageObjectPlaces();
							if (storages!=null)
							{
								if (storages.containsKey(key))
								{
									Cube cS = c.containingInternalEconomicUnit.getStorageObjectCube(0);
									//if (cS!=null) System.out.println("___ "+cS);
									if (c!=null && cS!=null)
									{
										c.merge(cS, worldX, worldY, worldZ, c.steepDirection, true);
									}
								}
							}
						}
					}
						
					if (c!=null && c.canContainFlora)
					{
						CubeClimateConditions ccc = w.getClimate().getCubeClimate(time, worldX, worldY, worldZ, c.internalCube);
						Cube floraCube = c.containingInternalEconomicUnit==null?(eco.needsFlora?eco.getFloraCube(worldX, worldY, worldZ, ccc, time, false):null):(c.containingInternalEconomicUnit.needsFlora?c.containingInternalEconomicUnit.getFloraCube(worldX, worldY, worldZ, ccc, time, false):null);
						if (floraCube!=null)
						{
							//System.out.println("ECO WITH FLORA "+eco.getClass());							
							c.merge(floraCube, worldX, worldY, worldZ, c.steepDirection);
						}
					}
					if (c!=null)
						return c;
				}
			} 

		}
		if (roadNetwork!=null && roadNetwork.getBoundaries()!=null && w!=null)
		{
			
			if (roadNetwork.getBoundaries().isInside(worldX, w.getSeaLevel(1), worldZ))
			{
				
				Cube c = roadNetwork.getCube(key, worldX, worldY, worldZ, farView);
				CubeClimateConditions ccc = null;
				if (c!=null)
				{
					// Setting climate id for climate dependent side types.
					ccc = w.getClimate().getCubeClimate(time, worldX, worldY, worldZ, c.internalCube);
					c.climateId = ccc.belt.STATIC_ID;
				}
				if (c!=null && c.canContainFlora)
				{
					Cube floraCube = c.containingInternalEconomicUnit==null?(roadNetwork.needsFlora?roadNetwork.getFloraCube(worldX, worldY, worldZ, ccc, time, false):null):(c.containingInternalEconomicUnit.needsFlora?c.containingInternalEconomicUnit.getFloraCube(worldX, worldY, worldZ, ccc, time, false):null);
					if (floraCube!=null)
					{
						//System.out.println("ECO WITH FLORA "+eco.getClass());							
						c.merge(floraCube, worldX, worldY, worldZ, c.steepDirection);
					}
				}
				return c;
			}
		}
		return null;
		
	}
	/**
	 * If size or location changes this must be called to update treelocator.
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param zMin
	 * @param zMax
	 * @param e
	 */
	public void updateEconomy(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, Economic e)
	{
		treeLocator.updateEconomic(xMin, xMax, yMin, yMax, zMin, zMax, e);
	}
	
	/**
	 * returns the X,Z of the population center in this zone.
	 * @param worldX
	 * @param worldZ
	 * @return [centerX, centerZ, origoX, origoZ]
	 */
	public int[] getPopulationCoordinatesInZone(int worldX, int worldZ, int populationGridSize)
	{
		
		int x= (((worldX/populationGridSize))*populationGridSize)+populationGridSize/2;
		int z= (((worldZ/populationGridSize))*populationGridSize)+populationGridSize/2;
		return new int[]{x,z,(worldX/populationGridSize)*populationGridSize,((worldZ/populationGridSize))*populationGridSize};
	}
	
	/**
	 * Returns if a given population zone is occupied in the given geography.
	 * @param geo
	 * @param popX
	 * @param popZ
	 * @return the Population if present, or null if no population.
	 */
	public Population isOccupied(Geography geo, int popX, int popZ)
	{
		ArrayList<Object> list = treeLocator.getElements(popX, geo.worldGroundLevel, popZ);
		// TODO treelocator is not return correctly all the populations! 
		//System.out.println("P NOT FOUND");
		if (list != null)
		for (Object o:list)
		{
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("P FOUND: "+o);
			//System.out.println("P FOUND: "+o);
			if (o instanceof Population) {
				Population p = (Population)o;
				//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("P FOUND: IS INSIDE?"+p.soilGeo+" -- "+geo);
				//System.out.println("P FOUND: IS INSIDE?"+p.soilGeo+" -- "+geo+ " "+popX+" : "+popZ);
				if (p.soilGeo == geo && p.owner.homeBoundary.posX==popX && p.owner.homeBoundary.posZ==popZ ) return p;
				//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("NOT...");
				
			}
		}
		return null;
	}
	
	
	public void addPopulation(Economic e)
	{
		treeLocator.addEconomic(e);
		economics.put(e.id, e);
	}
	
	/**
	 * Use it in encounter mode only! It adds the ground to the locator and the list of economics.
	 * @param e
	 */
	public void addGround(EconomicGround e)
	{
		treeLocator.addEconomic(e);
		economics.put(e.id, e);
	}

	public void onLoad(World w)
	{
		treeLocator = new TreeLocator(w);
		for (Economic ec:economics.values())
		{
			ec.onLoad();
			treeLocator.addEconomic(ec);
		}
		if (roadNetwork==null)
		{
			try {
				roadNetwork = new RoadNetwork("roadNetwork", this,w.getSeaLevel(1),w.magnification, w.sizeX, w.sizeY, w.sizeZ, 0, w.getSeaLevel(w.magnification)-1, 0);
			} catch (Exception ex)
			{
				Logger.getLogger("EconomyContainer").fine("!!! roadnetwork failure "+ex);
				ex.printStackTrace();
			}
		}
		roadNetwork.updateRoads(treeLocator);
	}
	
	/**
	 * Every X turns an economy update is done based on population's groups changes etc. TODO
	 */
	public void doEconomyUpdate()
	{
		for (Economic ec:economics.values())
		{
			if (ec instanceof Population)
			{
				if (((Population)ec).update())
				{
					//treeLocator.removeAllOfAnObject(ec);
					treeLocator.addEconomic(ec);
				}
			}
		}
		roadNetwork.updateRoads(treeLocator);
	}
	
	public void checkDistrictToTownIntegration(Population p)
	{
		Population[] fP = getFriendlyNeighborDistricts(p);
		if (fP.length>0)
		{
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("INTEGRATING TOWN");
			// TODO decisions which Town to join.
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest(fP[0].blockStartX+" "+fP[0].blockStartZ+" "+fP[0].town);
			fP[0].town.subPopulations.add(p);
			p.town = fP[0].town;
		} else
		{
			Town t = new Town();
			t.subPopulations.add(p);
			p.town = t;
			p.owner.description.nameTown(t);
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("NEW TOWN "+ p.blockStartX + " "+p.blockStartZ+" "+t.foundationName);
			towns.add(t);
		}
	}
	
	public Population[] getFriendlyNeighborDistricts(Population p)
	{
		int x = p.blockStartX;
		int z = p.blockStartZ;
		
		int xMinus = x-p.blockSize;
		int zMinus = z-p.blockSize;
		int xPlus = x+p.blockSize;
		int zPlus = z+p.blockSize;
		World world = (World)p.getRoot();
		xMinus = world.shrinkToWorld(xMinus);
		zMinus = world.shrinkToWorld(zMinus);
		xPlus = world.shrinkToWorld(xPlus);
		zPlus = world.shrinkToWorld(zPlus);		
		
		HashSet<Population> found = new HashSet<Population>();
		for (Economic eco:economics.values())
		{
			if (eco==p) continue;
			if (eco instanceof Population)
			{
				Population p2 = (Population)eco; 
				int pX = p2.blockStartX;
				int pZ = p2.blockStartZ;
				if (pX==xMinus && pZ==z || 
						pX==x && pZ==zMinus ||
						pX==xPlus && pZ==z ||
						pX==x && pZ==zPlus
				) 
				{
					if (p.owner.wouldMergeWithOther(p2.owner))
					{
						found.add((Population)eco);	
					}
				}
			}
		}
		return found.toArray(new Population[0]);
	}
	
	public Population getPopulationAt(int worldX, int worldY, int worldZ)
	{
		ArrayList<Object> list = treeLocator.getElements(worldX, worldY, worldZ);
		if (list!=null)
		for (Object l:list)
		{
			if (l instanceof Population)
			{
				if ( ((Population)l).boundaries.isInside(worldX, worldY, worldZ) ) return (Population)l;
			}
		}
		return null;
	}
	

	public void clearAll()
	{
		treeLocator.clear();
		economics.clear();
	}
	
	
	
	
}
