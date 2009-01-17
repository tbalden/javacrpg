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
package org.jcrpg.world.ai.wealth;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.ObjList;
import org.jcrpg.world.object.RawMaterial;
import org.jcrpg.world.object.craft.TrapAndLock;

public class EntityCommonWealth {
	
	public EntityInstance owner;
	
	public EntityCommonWealth(EntityInstance owner)
	{
		this.owner = owner;
	}

	public int money = 0;
	
	public HashMap<Class<? extends RawMaterial>, Integer> rawMaterials= new HashMap<Class <? extends RawMaterial>, Integer>();
	
	public HashMap<Class<? extends Obj>, Integer> objects = new HashMap<Class<? extends Obj>, Integer>();
	
	public HashMap<Class, Integer> availabilityHelper = new HashMap<Class, Integer>();
	
	
	/**
	 * Returns a Java Object's inherited/specified Class types
	 * @param o
	 * @return
	 */
	public ArrayList<Class> getClassTypes(Obj o)
	{
		ArrayList<Class> ret = new ArrayList<Class>();
		Class[] i = o.getClass().getInterfaces();
		if (i!=null) 
		{
			for (Class c:i)
			{
				ret.add(c);
			}
		}
		Class c = o.getClass();
		while (c.getSuperclass()!=null && o.getClass().getSuperclass()!=Obj.class)
		{
			ret.add(o.getClass().getSuperclass());
			c = o.getClass().getSuperclass();
		}
		return ret;
	}
	
	/**
	 * Iterates a list of classes with a given quantity and modifies the availability for each
	 * class type.
	 * @param list
	 * @param quantity
	 */
	public void classifyObjectTypes(ArrayList<Class> list,int quantity)
	{
		for (Class l:list)
		{
			Integer i = availabilityHelper.get(l);
			if (i==null)
			{
				i = quantity;
			} else
			{
				i+=quantity;
			}
			if (i<=0)
			{
				availabilityHelper.remove(l);
			} else
			{
				availabilityHelper.put(l, i);
			}
		}
	}
	
	private void handleObjectQuantity(Class<? extends Obj> item, int quantity)
	{
		Integer i = objects.get(item);
		if (i==null)
		{
			i = quantity;
		} else
		{
			i+=quantity;
		}
		if (i<=0)
		{
			objects.remove(item);
		}
	}
	
	public void addObject(Class<? extends Obj> item, int quantity)
	{
		Obj o = ObjList.getInstance(item);
		ArrayList<Class> list = getClassTypes(o);
		classifyObjectTypes(list,quantity);
		handleObjectQuantity(item, quantity);
	}

	public void removeObject(Class<? extends Obj> item, int quantity)
	{
		Obj o = ObjList.getInstance(item);
		ArrayList<Class> list = getClassTypes(o);
		classifyObjectTypes(list,-1*quantity);
		handleObjectQuantity(item,-1*quantity);
	}
	
	public TrapAndLock getTrapIfAvailable()
	{
		Integer i = availabilityHelper.get(TrapAndLock.class);
		if (i==null || i==0)
		{
			return null;
		}
		ArrayList<TrapAndLock> sortedTraps = new ArrayList<TrapAndLock>();
		for (Class<? extends Obj> o : objects.keySet())
		{
			Obj oo = ObjList.getInstance(o);
			if (oo instanceof TrapAndLock)
			{
				sortedTraps.add((TrapAndLock)oo);
			}
		}
		if (sortedTraps.size()==0) return null;
		int seed = J3DCore.getInstance().gameState.engine.getNumberOfTurn()+
			J3DCore.getInstance().gameState.engine.getWorldMeanTime().getTimeInInt();
		seed+=owner.getNumericId();
		int id = HashUtil.mix(seed, 0, 0) % sortedTraps.size();
		return sortedTraps.get(id);
	}
	
}
