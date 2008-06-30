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

import org.jcrpg.apps.Jcrpg;

/**
 * An instance of an object type.
 * @author illes
 *
 */
public class ObjInstance {

	public Obj description;
	public int numericId = 0;
	public ObjInstance(Obj description, long id)
	{
		this.description = description;
	}
	public ObjInstance(Obj description)
	{
		this(description,getNextObjInstanceId());
	}
	
	public String getName()
	{
		return description.getName();
	}
	
	public static long sequence = 0;
	public static synchronized long getNextObjInstanceId()
	{
		return sequence++;
	}

	boolean attached = false;
	
	/**
	 * Tells if this objInstance is attached to another objInstance or not.
	 * @return is attached?
	 */
	public boolean isAttached()
	{
		return attached;
	}
	
	public boolean isAttacheable()
	{
		return description.isAttacheable();
	}
	

	
	ArrayList<Obj> attachedDependencies = null;
	public ArrayList<Obj> getAttachedDependencies()
	{
		return attachedDependencies;
	}
	
	public boolean hasAttachedDependencies()
	{
		if (attachedDependencies==null || attachedDependencies.size()==0) return false;
		return true;
	}
	
	
	public void addAttachedDependency(Obj dependency)
	{
		if (attachedDependencies==null)
		{
			attachedDependencies = new ArrayList<Obj>();
		}
		if (!attachedDependencies.contains(dependency))
		{
			attachedDependencies.add(dependency);
		}
		
	}
	
	public void addAttachedDependencies(ArrayList<Obj> dependencies)
	{
		if (attachedDependencies==null)
		{
			attachedDependencies = new ArrayList<Obj>();
		}
		for (Obj i:dependencies)
		{
			if (attachedDependencies.contains(i)) continue;
			if (i.getAttacheableToType()==description.getClass())
			{
				attachedDependencies.add(i);
			}
		}
	}
	
	public void removeAttachedDependency(Obj removed)
	{
		if (attachedDependencies!=null)
		{
			attachedDependencies.remove(removed);
		} else
		{
			Jcrpg.LOGGER.warning("TRYING TO REMOVE AN OBJECT DEPENDENCY FROM A NULL DEP LIST! "+this+" "+this.description);
		}
	}
	
	public void clearDependencies()
	{
		if (attachedDependencies!=null)
		{
			attachedDependencies.clear();
		}
	}
	
	
	public boolean needsAttachmentDependencyForSkill()
	{
		return description.needsAttachmentDependencyForSkill();
	}
	public Class getAttacheableToType()
	{
		return description.getAttacheableToType();
	}
	
}
