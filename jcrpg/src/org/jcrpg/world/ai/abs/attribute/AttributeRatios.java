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

package org.jcrpg.world.ai.abs.attribute;

import java.util.HashMap;

import org.jcrpg.util.HashUtil;

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
	
	public float getAttributeRatio(String attr)
	{
		Float f = attributeRatios.get(attr);
		if (f==null) return 0;
		return f;
	}
	
	public Attributes calculateLevelingAttributes(int seed, int points, Class<? extends Attributes> attr)
	{
		float sum = 0;
		int count = 0;
		for (String key : attributeRatios.keySet())
		{
			Float f = attributeRatios.get(key);
			if (f!=null)
			{
				sum+=f;
				count++;
			}
		}
		Attributes a = null;
		try 
		{
			a = attr.newInstance();
		} catch (Exception e)
		{}
		while (points>0)
		{
			count ++;
			for (String key : attributeRatios.keySet())
			{
				Float f = attributeRatios.get(key);
				if (f!=null)
				{
					f = f/sum;
					int i = (int)(f * 1000);
					if (i>HashUtil.mixPer1000(seed, points, count))
					{
						a.setAttribute(key, a.getAttribute(key) + 1);
						System.out.println("INCREASING ATTRIBUTES "+key);
						points--;
					}
				}
			}
		}
		return a;
	}
	
	
	public static int getAttribute(String attr, Attributes base, AttributeRatios modifier)
	{
		Float ratio = modifier.attributeRatios.get(attr);
		if (ratio==null) ratio = 1f;
		Integer attribute = base.attributes.get(attr);
		if (attribute==null) attribute = 5; //TODO is this right?
		return (int)(attribute*ratio);
	}
	
	public static Attributes getAttributes(String attr, Attributes base, AttributeRatios modifier)
	{
		Attributes ret = null;
		try {ret = base.getClass().newInstance();} catch (Exception ex){ex.printStackTrace();}
		for (String a:base.attributes.keySet())
		{
			ret.setAttribute(a, getAttribute(attr,base,modifier));
		}
		return ret;
	}
}
