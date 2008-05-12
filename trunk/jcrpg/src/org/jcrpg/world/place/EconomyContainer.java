/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.world.place;

import java.util.ArrayList;
import java.util.TreeMap;

import org.jcrpg.space.Cube;
import org.jcrpg.world.place.economic.Population;

public class EconomyContainer {

	public transient TreeLocator treeLocator = null;
	
	public int populationGridSize;
	
	public TreeMap<String, Economic> economics;
	
	public EconomyContainer(World w)
	{
		populationGridSize = w.magnification; // TODO, use generation geography grid size instead?
		treeLocator = new TreeLocator(w);
		economics = new TreeMap<String, Economic>();
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
	public Cube getEconomicCube(long key, int worldX, int worldY, int worldZ,boolean farView)
	{
		ArrayList<Object> economicsList = treeLocator.getElements(worldX, worldY, worldZ);
		if (economicsList!=null) 
		{
			//if (economicsList.size()>1) System.out.println("######## economiclist POPULATION is "+economicsList.size());
			for (Object o:economicsList)
			{
				Economic eco = (Economic)o;
				if (eco.getBoundaries().isInside(worldX, worldY, worldZ)) {
					return eco.getCube(key, worldX, worldY, worldZ, farView);
				}
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
	 * @return
	 */
	public int[] getPopulationCoordinatesInZone(int worldX, int worldZ)
	{
		
		int x= ((worldX/populationGridSize)*populationGridSize)+populationGridSize/2;
		int z= ((worldZ/populationGridSize)*populationGridSize)+populationGridSize/2;
		return new int[]{x,z};
	}
	
	/**
	 * Returns if a given population zone is occupied in the given geography.
	 * @param geo
	 * @param popX
	 * @param popZ
	 * @return
	 */
	public boolean isOccupied(Geography geo, int popX, int popZ)
	{
		ArrayList<Object> list = treeLocator.getElements(popX, geo.worldGroundLevel, popZ);
		if (list != null)
		for (Object o:list)
		{
			if (o instanceof Population) {
				Population p = (Population)o;
				if (p.soilGeo == geo && p.boundaries.isInside(popX, p.groundLevel, popZ)) return true;
			}
		}
		return false;
	}
	
	public void addPopulation(Economic e)
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
	}

	public void clearAll()
	{
		economics.clear();
	}
	
}
