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

package org.jcrpg.world.ai.abs.attribute;

import java.util.HashMap;

import org.jcrpg.game.GameLogicConstants;

public abstract class Resistances {

	public HashMap<String, Integer> resistances = new HashMap<String, Integer>();
	/**
	 * For tuning attributes in a common way for group entities. Shouldn't be used with genuine NPCs or player characters.
	 */
	public HashMap<String, Float> resRatios = new HashMap<String, Float>();

	public static String[] resistanceName = new String[0];

	public void setResistance(String attr,int value)
	{
		resistances.put(attr, value);
	}
	public int getResistance(String attr)
	{
		return resistances.get(attr);
	}
	
	public static int getResistance(String attr, Resistances base, Resistances modifier)
	{
		return base.resistances.get(attr)+modifier.resistances.get(attr);
	}
	
	public static Resistances getResistances(Resistances base, Resistances modifier)
	{
		Resistances ret = null;
		try { ret = base.getClass().newInstance(); } catch (Exception ex) {}
		
		for (String a:base.resistances.keySet())
		{
			Integer attrVal = base.resistances.get(a);
			if (attrVal==null) attrVal = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
			Integer attrValPlus = modifier.resistances.get(a);
			if (attrValPlus==null) attrValPlus = 0;
			ret.setResistance(a, attrVal+attrValPlus);
		}
		return ret;
	}
	public static Resistances getResistances(Resistances base, ResistanceRatios modifier)
	{
		Resistances ret = null;
		try { ret = base.getClass().newInstance(); } catch (Exception ex) {}
		
		for (String a:base.resistances.keySet())
		{
			Float ratio = modifier.resistanceRatios.get(a);
			if (ratio==null) ratio = 1f;
			Integer attrVal = base.resistances.get(a);
			if (attrVal==null) attrVal = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
			ret.setResistance(a, (int)(attrVal*ratio));
		}
		return ret;
	}
	
	public abstract String getShortestName(String attr);
}
