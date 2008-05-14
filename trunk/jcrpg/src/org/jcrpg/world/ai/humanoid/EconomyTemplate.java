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

package org.jcrpg.world.ai.humanoid;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;
import org.jcrpg.world.place.economic.SimpleDistrict;

/**
 * Describes a certain economy type which humanoids may build as their home domain.
 * @author pali
 *
 */
public class EconomyTemplate {
	
	
	public static HashMap<Class<? extends Economic>, Economic> economicBase = new HashMap<Class<? extends Economic>, Economic>();
	static 
	{
		economicBase.put(SimpleDistrict.class, new SimpleDistrict());
		economicBase.put(House.class, new House());
		economicBase.put(EconomicGround.class, new EconomicGround());
	}
	

	public HashMap<Class<? extends Geography>, ArrayList<Class<? extends Population>>> populationTypes = new HashMap<Class<? extends Geography>, ArrayList<Class<? extends Population>>>();
	public HashMap<Class<? extends Geography>, ArrayList<Class<? extends Residence>>> residenceTypes = new HashMap<Class<? extends Geography>, ArrayList<Class<? extends Residence>>>();
	public HashMap<Class<? extends Geography>, ArrayList<Class<? extends EconomicGround>>> groundTypes = new HashMap<Class<? extends Geography>, ArrayList<Class<? extends EconomicGround>>>();
	// TODO workGround types
	
	public EconomyTemplate()
	{
	}
	
	public void addPopulationType(Class<? extends Geography> geo, Class<? extends Population> eco)
	{
		ArrayList<Class<? extends Population>> list = populationTypes.get(geo);
		if (list == null) 
		{
			list = new ArrayList<Class<? extends Population>>();
			populationTypes.put(geo, list);
		} else
		{
			if (list.contains(eco)) return;
		}
		list.add(eco);
	}
	public void addResidenceType(Class<? extends Geography> geo, Class<? extends Residence> eco)
	{
		ArrayList<Class<? extends Residence>> list = residenceTypes.get(geo);
		if (list == null) 
		{
			list = new ArrayList<Class<? extends Residence>>();
			residenceTypes.put(geo, list);
		} else
		{
			if (list.contains(eco)) return;
		}
		list.add(eco);
	}
	
	public void addEcoGroundType(Class<? extends Geography> geo, Class<? extends EconomicGround> eco)
	{
		ArrayList<Class<? extends EconomicGround>> list = groundTypes.get(geo);
		if (list == null) 
		{
			list = new ArrayList<Class<? extends EconomicGround>>();
			groundTypes.put(geo, list);
		} else
		{
			if (list.contains(eco)) return;
		}
		list.add(eco);
	}
	
}
