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

/**
 * Class that can be used for an entity member to shade the common Attributes of a group
 * to a detailed level for an entity member type.
 * @author pali
 *
 */
public class ResistanceRatios {

	public HashMap<String, Float> resistanceRatios = new HashMap<String, Float>();
	
	
	public ResistanceRatios() {
	}
	
	public void setResistanceRatio(String attr,float value)
	{
		resistanceRatios.put(attr, value);
	}
	
	public static int getResistance(String res, Resistances base, ResistanceRatios modifier)
	{
		Float ratio = modifier.resistanceRatios.get(res);
		if (ratio==null) ratio = 1f;
		Integer attribute = base.resistances.get(res);
		if (attribute==null) attribute = 5; //TODO is this right?
		return (int)(attribute*ratio);
	}
	
	public static Resistances getAttributes(String res, Resistances base, ResistanceRatios modifier)
	{
		FantasyResistances ret = new FantasyResistances(false);
		for (String a:base.resistances.keySet())
		{
			ret.setResistance(a, getResistance(res,base,modifier));
		}
		return ret;
	}
}
