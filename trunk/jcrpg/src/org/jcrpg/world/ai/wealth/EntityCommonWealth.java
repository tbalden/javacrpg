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
package org.jcrpg.world.ai.wealth;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.ObjList;
import org.jcrpg.world.object.RawMaterial;

public class EntityCommonWealth {

	public int money = 0;
	
	public HashMap<Class<? extends RawMaterial>, Integer> rawMaterials= new HashMap<Class <? extends RawMaterial>, Integer>();
	
	public HashMap<Class<? extends Obj>, Integer> objects = new HashMap<Class<? extends Obj>, Integer>();
	
	public HashMap<Class, Integer> availabilityHelper = new HashMap<Class, Integer>();
	
	
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
	
}
