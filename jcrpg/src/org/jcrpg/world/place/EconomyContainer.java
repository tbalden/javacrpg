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
	
	public TreeMap<String, Economic> economics;
	
	/**
	 * saves history of population element additions in time order.
	 */
	public ArrayList<EconomyHistoryElement> populationReloadHistory = new ArrayList<EconomyHistoryElement>(); 
	
	public EconomyContainer(World w)
	{
		treeLocator = new TreeLocator(w);
		economics = new TreeMap<String, Economic>();
	}

	/**
	 * Should always call it when a groupedEconomy is updated (addition,ruin,remove etc.).
	 * @param economic The economic unit.
	 * @param type Event type.
	 */
	public void recordHistory(Economic economic, short type)
	{
		populationReloadHistory.add(new EconomyHistoryElement(economic.id,type));
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
	
	public void addEconomy(Economic e)
	{
		treeLocator.addEconomic(e);
		economics.put(e.id, e);
	}

	public void onLoad(World w)
	{
		treeLocator = new TreeLocator(w);
		for (EconomyHistoryElement e:populationReloadHistory)
		{
			Economic ec = economics.get(e.id);
			if (ec instanceof Population)
			{
				((Population)ec).replayHistoryEvent(e);
				treeLocator.removeAllOfAnObject(ec);
				treeLocator.addEconomic(ec);
			}
		}
	}

	public void clearAll()
	{
		economics.clear();
	}
	
}
