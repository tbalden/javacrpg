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

public abstract class Attributes {

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
	public int getAttribute(String attr)
	{
		return attributes.get(attr);
	}
	
	public static int getAttribute(String attr, Attributes base, Attributes modifier)
	{
		Integer attrVal = base.attributes.get(attr);
		if (attrVal==null) attrVal = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
		Integer attrValPlus = modifier.attributes.get(attr);
		if (attrValPlus==null) attrValPlus = 0;
		return attrVal+attrValPlus;
	}
	
	public static Attributes getAttributes(Attributes base, Attributes modifier)
	{
		Attributes ret = null;
		try { ret = base.getClass().newInstance(); } catch (Exception ex) {}
		
		for (String a:base.attributes.keySet())
		{
			Integer attrVal = base.attributes.get(a);
			if (attrVal==null) attrVal = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
			Integer attrValPlus = modifier.attributes.get(a);
			if (attrValPlus==null) attrValPlus = 0;
			ret.setAttribute(a, attrVal+attrValPlus);
		}
		return ret;
	}
	public static Attributes getAttributes(Attributes base, AttributeRatios modifier)
	{
		Attributes ret = null;
		try { ret = base.getClass().newInstance(); } catch (Exception ex) {}
		
		for (String a:base.attributes.keySet())
		{
			Float ratio = modifier.attributeRatios.get(a);
			if (ratio==null) ratio = 1f;
			Integer attrVal = base.attributes.get(a);
			if (attrVal==null) attrVal = GameLogicConstants.BASE_ATTRIBUTE_VALUE;
			ret.setAttribute(a, (int)(attrVal*ratio));
		}
		return ret;
	}
	
	/**
	 * Returns ratio how a given attribute set instance influences max point of pointType (health/mana etc.).
	 * @param pointType
	 * @return
	 */
	public abstract float getAttributePointMultiplier(int pointType);
	
	public abstract String getShortestName(String attr);
	
	public void appendAttributes(Attributes attributes)
	{
		if (attributes==null) return;
		for (String attr: attributes.attributes.keySet())
		{
			Integer base = this.attributes.get(attr);
			if (base==null) base = 0;
			Integer plus= attributes.attributes.get(attr);
			if (plus==null) continue;
			setAttribute(attr, base+plus);
		}
	}
	public void appendAttributeRatios(Attributes attributes)
	{
		if (attributes==null) return;
		for (String attr: attributes.attributeRatios.keySet())
		{
			Float base = this.attributeRatios.get(attr);
			if (base==null) base = 1f;
			Float mul= attributes.attributeRatios.get(attr);
			if (mul==null) continue;
			attributeRatios.put(attr, base*mul);
		}
	}
	
	public Attributes copy()
	{
		Attributes copy = null; 
		try {
			copy = this.getClass().newInstance();
			copy.appendAttributes(this);
			copy.appendAttributeRatios(this);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return copy;
	}
	
	@Override
	public String toString()
	{
		StringBuffer b = new StringBuffer("ATTRIBUTES - ");
		for (String attr:attributes.keySet())
		{
			b.append(attr+": "+getAttribute(attr)+" ");
		}
		return b.toString();
	}
	
}
