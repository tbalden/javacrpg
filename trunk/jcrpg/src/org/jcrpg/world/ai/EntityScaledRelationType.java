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

package org.jcrpg.world.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

public class EntityScaledRelationType {
	
	public static byte WORST_PERMANENT = 0;
	public static byte WORST = 1;
	public static byte NEUTRAL = 10;
	public static byte BEST = 19;
	public static byte BEST_PERMANENT = 20;

	public HashMap<Integer, Byte> relations = new HashMap<Integer, Byte>();
	
	public int getRelationQuality(int entity)
	{
		Byte b = relations.get(entity);
		if (b==null) return NEUTRAL;
		return b;
	}
	
	public void doNeutralization()
	{
		Set<Integer> removableKeys = new HashSet<Integer>();
		for (Entry<Integer, Byte> i:relations.entrySet())
		{
			if (i.getValue()==NEUTRAL || i.getValue()==NEUTRAL+1 || i.getValue()==NEUTRAL-1)
			{
				removableKeys.add(i.getKey());
			}
			if (i.getValue()==WORST_PERMANENT || i.getValue()==BEST_PERMANENT)
			{
				continue;
			}
			if (i.getValue()>NEUTRAL)
			{
				i.setValue((byte)(i.getValue()-1));
			} else
			if (i.getValue()<NEUTRAL)
			{
				i.setValue((byte)(i.getValue()+1));
			}
		}
	}
	
	public void increase(Integer key, byte plus)
	{
		Byte b = relations.get(key);
		if (b==null)
		{
			b = NEUTRAL;
		}
		b = (byte)(b.byteValue()+plus);
		b = (byte)Math.min(b, BEST_PERMANENT);
		relations.put(key, b);
	}

	public void decrease(Integer key, byte minus)
	{
		Byte b = relations.get(key);
		if (b==null)
		{
			b = NEUTRAL;
		}
		b = (byte)(b.byteValue()-minus);
		b = (byte)Math.min(b, WORST_PERMANENT);
		relations.put(key, b);
	}

}
