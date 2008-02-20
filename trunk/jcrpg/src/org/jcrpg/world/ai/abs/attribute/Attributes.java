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

public class Attributes {

	public HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	/**
	 * For tuning attributes in a common way for group entities. Shouldn't be used with genuine NPCs or player characters.
	 */
	public HashMap<String, Float> attributeRatios = new HashMap<String, Float>();

	public static String[] attributeName = new String[0];

	public void setAttribute(String attr,int value)
	{
		attributes.put(attr, value);
	}
	
	public static int getAttribute(String attr, Attributes base, Attributes modifier)
	{
		return base.attributes.get(attr)+modifier.attributes.get(attr);
	}
	
	public static Attributes getAttributes(Attributes base, Attributes modifier)
	{
		FantasyAttributes ret = new FantasyAttributes();
		for (String a:base.attributes.keySet())
		{
			ret.setAttribute(a, base.attributes.get(a)+modifier.attributes.get(a));
		}
		return ret;
	}
	public static Attributes getAttributes(Attributes base, AttributeRatios modifier)
	{
		FantasyAttributes ret = new FantasyAttributes();
		for (String a:base.attributes.keySet())
		{
			ret.setAttribute(a, (int)(base.attributes.get(a)*modifier.attributeRatios.get(a)));
		}
		return ret;
	}
}
