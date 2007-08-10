/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.ai.flora;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.time.Time;

/**
 * Base class for Flora generation for a type of place
 * @author pali
 *
 */
public class FloraGenerator {

	/**
	 * Key is <BELT STATIC ID + " " + LEVEL STATIC ID>
	 */
	public HashMap<String, FloraListElement[]> floraBeltLevelMap = new HashMap<String, FloraListElement[]>();
	
	public HashMap<String, ArrayList<FloraListElement>[]> cache = new HashMap<String, ArrayList<FloraListElement>[]>();
	
	public FloraCube generate(int worldX, int worldY, int worldZ, CubeClimateConditions conditions, Time time, boolean onSteep)
	{
		if (conditions==null) return new FloraCube();
		String beltLevelKey = conditions.getPartialBeltLevelKey();
		FloraListElement[] possibleFlora = floraBeltLevelMap.get(beltLevelKey);
		if (possibleFlora==null) return new FloraCube();
		FloraCube c = new FloraCube();
		
		ArrayList<FloraListElement>[] arrayLists = cache.get(beltLevelKey+onSteep);
		
		ArrayList<FloraListElement> groundFlora;
		ArrayList<FloraListElement> middleFlora;
		ArrayList<FloraListElement> topFlora;
		if (arrayLists!=null)
		{
			groundFlora = arrayLists[0];
			middleFlora = arrayLists[1];
			topFlora = arrayLists[2];
		} else
		{
			groundFlora = new ArrayList<FloraListElement>();
			middleFlora = new ArrayList<FloraListElement>();
			topFlora = new ArrayList<FloraListElement>();			

			for (FloraListElement element : possibleFlora) {
				
				// if on steep true, and plant doesnt grow on steep, continue without adding possible flora
				if (onSteep && !element.flora.growsOnSteep) continue;
				
				if (!element.alwaysPresent) // this can be cached to the choosed ones' cache, 
					//always presents are always added not from cache!
				{
					if (arrayLists==null) {
						if (element.flora.floraPosition==Flora.POSITION_GROUND)
						{
							groundFlora.add(element);
						} else if (element.flora.floraPosition==Flora.POSITION_MIDDLE)
						{
							middleFlora.add(element);
						} else
						{
							topFlora.add(element);
						}
					}
				}
			}

		}
		
		for (FloraListElement element : possibleFlora) {
			// if on steep true, and plant doesnt grow on steep, continue without adding possible flora
			if (onSteep && !element.flora.growsOnSteep) continue;
			if (element.alwaysPresent)
			{
				c.descriptions.add(element.flora.getFloraDescription(conditions.getPartialSeasonDaytimelKey()));
			}
		}

		chooseFlora(worldX, worldY, worldZ, conditions, time, c, groundFlora);
		chooseFlora(worldX, worldY, worldZ, conditions, time, c, middleFlora);
		chooseFlora(worldX, worldY, worldZ, conditions, time, c, topFlora);
		
		if (arrayLists==null)
		{
			arrayLists = new ArrayList[3];
			arrayLists[0] = groundFlora;
			arrayLists[1] = middleFlora;
			arrayLists[2] = topFlora;
			cache.put(beltLevelKey+onSteep, arrayLists);
		}
		
		return c;
	}
	/**
	 * Chooses one flora plant from possible flora on one kind of level based on hashing likeness percentage. The hashing is made different with counter <code>i</code>.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param conditions
	 * @param time
	 * @param c
	 * @param elements
	 */
	private void chooseFlora(int worldX, int worldY, int worldZ, CubeClimateConditions conditions, Time time, FloraCube c, ArrayList<FloraListElement> elements)
	{
		int i = 0;
		for (FloraListElement element : elements) {
			int likeness=element.likenessToGrow;
			int h = HashUtil.mixPer1000(worldX+i, worldY+i, worldZ+i);
			i++;
			if  ( h < likeness ) 
			{
				c.descriptions.add(element.flora.getFloraDescription(conditions.getPartialSeasonDaytimelKey()));
				break;
			}
			
		}
	}
	

	public void addFlora(String beltId, String levelId,FloraListElement[] flora)
	{
		floraBeltLevelMap.put(beltId+" "+levelId, flora);
	}

}
