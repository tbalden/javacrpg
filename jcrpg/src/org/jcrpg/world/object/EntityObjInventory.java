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

import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.body.BodyPart;

public class EntityObjInventory {
	/**
	 * Inventory.
	 */
	public ArrayList<ObjInstance> inventory = new ArrayList<ObjInstance>();
	/**
	 * Objects that are currently equipped.
	 */
	public ArrayList<ObjInstance> equipped = new ArrayList<ObjInstance>();
	
	public boolean hasInInventoryForSkillAndLevel(SkillInstance skill)
	{
		return hasInListForSkill(inventory, skill);
	}
	public boolean hasInEquippedForSkillAndLevel(SkillInstance skill)
	{
		return hasInListForSkill(equipped, skill);
	}

	public boolean hasInListForSkill(ArrayList<ObjInstance> list, SkillInstance skill)
	{
		
		for (ObjInstance o:list)
		{
			if (o.description.requirementSkillAndLevel==null) continue;
			if (o.description.requirementSkillAndLevel.skill == skill.skill)
			{
				if (o.description.requirementSkillAndLevel.level<=skill.level)
				{
					if (o.needsAttachmentDependencyForSkill())
					{
						if (o.getAttachedDependencies()!=null)
						{
							if (hasOneOfTypes(o.getAttachedDependencies()))
							{
								return true;
							}
						}
					} else
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public ArrayList<ObjInstance> getObjectsForSkillInInventory(SkillInstance skill)
	{
		return getObjectsForSkill(inventory, skill);
	}
	public ArrayList<ObjInstance> getObjectsForSkillInEquipped(SkillInstance skill)
	{
		return getObjectsForSkill(equipped, skill);
	}
	
	public ArrayList<ObjInstance> getObjectsForSkill(ArrayList<ObjInstance> list, SkillInstance skill)
	{
		ArrayList<ObjInstance> objList = new ArrayList<ObjInstance>();
		for (ObjInstance o:list)
		{
			if (o.description.requirementSkillAndLevel==null) continue;
			if (o.description.requirementSkillAndLevel.skill == skill.skill)
			{
				if (o.description.requirementSkillAndLevel.level<=skill.level)
				{
					if (o.needsAttachmentDependencyForSkill())
					{
						if (o.getAttachedDependencies()!=null)
						{
							if (hasOneOfTypes(o.getAttachedDependencies()))
							{
								objList.add(o);
							}
						}
					} else
					{
						objList.add(o);
					}
				}
			}
		}
		return objList;
	}
	
	/**
	 * 
	 * @param possibleTypesOrdered
	 * @return if inventory contains one of the possible types then returns true.
	 */
	public boolean hasOneOfTypes(ArrayList<Obj> possibleTypesOrdered)
	{
		if (possibleTypesOrdered==null) return false;

		for (Obj type:possibleTypesOrdered)
		{
			for (ObjInstance i:inventory)
			{
				if (i.description == type)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get one instance object of the ordered type list if inventory has one, and remove it.
	 * Generally used for getting an ammunition out of the inventory.
	 * @param possibleTypesOrdered
	 * @return The object instance.
	 */
	public ObjInstance getOneInstanceOfTypesAndRemove(ArrayList<Obj> possibleTypesOrdered)
	{
		
		if (possibleTypesOrdered==null) return null;
		
		ObjInstance toRemove = null;
		for (Obj type:possibleTypesOrdered)
		{
			for (ObjInstance i:inventory)
			{
				if (i.description == type)
				{
					toRemove = i;
				}
			}
		}
		if (toRemove!=null)
		{
			inventory.remove(toRemove);
		}
		return toRemove;
	}

	/**
	 * Returns the first possible object instance which is of type in an ordered type list.
	 * @param possibleTypesOrdered
	 * @return
	 */
	public Obj getPossibleNextOneType(ArrayList<Obj> possibleTypesOrdered)
	{
		if (possibleTypesOrdered==null) return null;
			
		for (Obj type:possibleTypesOrdered)
		{
			for (ObjInstance i:inventory)
			{
				if (i.description == type)
				{
					return type;
				}
			}
		}
		return null;
	}
	
	public boolean equip(EntityMemberInstance instance, ObjInstance equipment)
	{
		if (!inventory.contains(equipment)) return false;
		
		if (!(equipment.description instanceof Equippable)) return false;
		
		ArrayList<BodyPart> parts = instance.description.getBodyType().bodyParts;
		
		Class<? extends BodyPart> part = ((Equippable)equipment.description).getEquippableBodyPart();
		System.out.println("P: "+part);
		boolean found = false;
		BodyPart bPart = null;
		for (BodyPart p:parts)
		{
			if (p.getClass()==part) 
			{
				bPart = p;
				found = true;
			}
		}
		if (!found) return false;
		
		System.out.println("P: FOUND "+part);

		int counterForEquipped = 0;
		int maxEquipped = bPart.getMaxNumberOfObjToEquip();
		for (ObjInstance eq:equipped)
		{
			if (((Equippable)eq.description).getEquippableBodyPart() == part)
			{
				counterForEquipped++;
			}
			if (counterForEquipped==maxEquipped) return false;
		}
		// TODO other checks! profession and such
		
		equipped.add(equipment);
		
		return true;
	}
	
	public boolean unequip(ObjInstance object)
	{
		if (!equipped.contains(object)) return false;
		if (object.description instanceof BonusObject)
		{
			if (((BonusObject)object.description).isCursed())
			{
				return false;
			}
		}
		equipped.remove(object);
		return true;
	}
	
	// TODO 
	public void getSumOfBonuses(BodyPart part)
	{
		// TODO summarize bonus objects for a part plus the general bonus (rings etc.)
	}

}
