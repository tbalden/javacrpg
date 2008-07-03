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
package org.jcrpg.world.object;

import java.util.ArrayList;

public class InventoryListElement
{
	
	public EntityObjInventory inventory;
	
	public Obj description = null;
	
	public InventoryListElement(EntityObjInventory inventory, Obj description) 
	{
		this.inventory = inventory;
		this.description = description;
	}
	
	public ArrayList<ObjInstance> objects = new ArrayList<ObjInstance>();
	
	public String getName()
	{
		if (description.isGroupable()) {
			return ""+description.getName()+" " +objects.size();
		}
		return getSingleName();
	}

	public String getSingleName()
	{
		return ""+description.getName();
	}
	
	public ArrayList<Obj> getAttachedDependencies()
	{
		ArrayList<Obj> total = new ArrayList<Obj>();
		for (ObjInstance i:objects)
		{
			ArrayList<Obj> list = i.getAttachedDependencies();
			if (list!=null && list.size()!=0)
				total.addAll(list);
		}
		return total;
	}
	public boolean hasAttachedDependencies()
	{
		for (ObjInstance i:objects)
		{
			if (i.hasAttachedDependencies()) return true;
		}
		return false;
	}
	
}
