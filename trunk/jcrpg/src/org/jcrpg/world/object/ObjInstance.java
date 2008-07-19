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
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.time.Time;

/**
 * An instance of an object type.
 * @author illes
 *
 */
public class ObjInstance {

	public Obj description;
	
	public int numericId = 0;
	
	public int numberOfTotalUses = 0;
	
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
			if (i.getAttachableToType()==description.getClass())
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
	public Class getAttachableToType()
	{
		return description.getAttachableToType();
	}

	HashMap<BonusSkillActFormDesc, ArrayList<Time>> lastUsedBonusFormTimes = null;
	
	/**
	 * Returns the list of currently usable bonus skill act forms.
	 * @return
	 */
	public ArrayList<BonusSkillActFormDesc> currentlyUsableBonusSkillActForms()
	{
		if (description instanceof BonusObject)
		{
			ArrayList<BonusSkillActFormDesc> ret = new ArrayList<BonusSkillActFormDesc>();
			BonusObject b = ((BonusObject)description);
			ArrayList<BonusSkillActFormDesc> list = b.getSkillActFormBonusEffectTypes();
			if (list!=null)
			for (BonusSkillActFormDesc desc:list)
			{
				boolean add = false;
				if (lastUsedBonusFormTimes==null)
				{
					if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("#_#_#_#_#_#_$$$$ not used yet");
					add = true;
				} else
				{
					ArrayList<Time> t = lastUsedBonusFormTimes.get(desc);
					if (t==null)
					{
						if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("#_#_#_#_#_#_$$$$ not used yet, no time");
						add = true;
					} else
					{
						if (t.size()<desc.maxUsePerReplenish)
						{
							if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("#_#_#_#_#_#_$$$$ used but not over max use "+desc.maxUsePerReplenish);
							add = true;
						} else
						{
							if (desc.maxUsePerReplenish==0)
							{
								if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("#_#_#_#_#_#_$$$$ ## max use "+desc.maxUsePerReplenish);
								t.remove(0);
								add = true;
							} else
							{
								if (desc.isUsableNow(t.get(0), J3DCore.getInstance().gameState.engine.getWorldMeanTime()))
								{
									if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("#_#_#_#_#_#_$$$$ CHECK COMPLETE, OVER TIME ## "+desc.maxUsePerReplenish);
									t.remove(0);
									add = true;
								} else
								{
									if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("#_#_#_#_#_#_---- FAILED< not adding");
								}
							}
							
						}
					}
				}
				if (add)
					ret.add(desc);
			}
			return ret;
		} 
		return null;			
	}
	
	public ArrayList<BonusSkillActFormDesc> getLastUseBonusActForms()
	{
		return lastUseBonusActForms;
	}
	
	public ArrayList<BonusSkillActFormDesc> lastUseBonusActForms;
	
	/**
	 * 
	 * @return true if its all used up
	 */
	public boolean useOnce()
	{
		if (description instanceof BonusObject)
		{
			// storing last use for currentlyUsable bonus skills.
			if (lastUsedBonusFormTimes==null)
			{
				lastUsedBonusFormTimes = new HashMap<BonusSkillActFormDesc, ArrayList<Time>>(); 
					
			} 
			lastUseBonusActForms = currentlyUsableBonusSkillActForms();
			for (BonusSkillActFormDesc d:lastUseBonusActForms)
			{
				ArrayList<Time> t = lastUsedBonusFormTimes.get(d);
				if (t==null)
				{
					t = new ArrayList<Time>();
					lastUsedBonusFormTimes.put(d, t);
				}
				t.add(new Time(J3DCore.getInstance().gameState.engine.getWorldMeanTime()));
			}
		}
		numberOfTotalUses++;
		if (numberOfTotalUses==description.maxNumberOfUsage())
			return true;
		return false;
	}
	
}
