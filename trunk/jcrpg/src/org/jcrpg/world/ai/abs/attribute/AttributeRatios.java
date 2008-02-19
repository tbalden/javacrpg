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
public class AttributeRatios {

	public HashMap<String, Float> attributeRatios = new HashMap<String, Float>();
	
	
	public AttributeRatios() {
	}
	
	public void setAttributeRatio(String attr,float value)
	{
		attributeRatios.put(attr, value);
	}
	
	public static int getAttribute(String attr, Attributes base, AttributeRatios modifier)
	{
		return (int)(base.attributes.get(attr)*modifier.attributeRatios.get(attr));
	}
	
	public static Attributes getAttributes(String attr, Attributes base, AttributeRatios modifier)
	{
		FantasyAttributes ret = new FantasyAttributes();
		for (String a:base.attributes.keySet())
		{
			ret.setAttribute(a, (int)(base.attributes.get(attr)*modifier.attributeRatios.get(attr)));
		}
		return ret;
	}
}
