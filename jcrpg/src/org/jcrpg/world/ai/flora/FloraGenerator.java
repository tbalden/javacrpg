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

import java.util.HashMap;

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
	
	public FloraCube generate(int worldX, int worldY, int worldZ, CubeClimateConditions conditions, Time time)
	{
		if (conditions==null) return new FloraCube();
		FloraListElement[] possibleFlora = floraBeltLevelMap.get(conditions.getPartialBeltLevelKey());
		if (possibleFlora==null) return new FloraCube();
		FloraCube c = new FloraCube();
		int id = 0;
		
		for (FloraListElement element : possibleFlora) {
			if (element.alwaysPresent)
			{
				c.descriptions.add(element.flora.getFloraDescription(conditions.getPartialSeasonDaytimelKey()));
			}
			
		}
/*
		FloraDescription ground = possibleFlora[0].flora.statesToFloraDescription.get(conditions.getPartialSeasonDaytimelKey());
		if (ground==null)
		{
			ground = possibleFlora[0].flora.defaultDescription;
		}
		c.descriptions.add(ground);
		
		if (possibleFlora.length>0)
		{
			id = (worldX+worldZ*2)%(possibleFlora.length-1)+1;
			System.out.println(" ID = "+id);
			FloraDescription d = possibleFlora[id].flora.statesToFloraDescription.get(conditions.getPartialSeasonDaytimelKey());
			if (d==null) d=possibleFlora[id].flora.defaultDescription;
			if ((worldX+worldZ)%4==0) c.descriptions.add(d);
		}*/
		return c;
	}

	public void addFlora(String beltId, String levelId,FloraListElement[] flora)
	{
		floraBeltLevelMap.put(beltId+" "+levelId, flora);
	}

}
