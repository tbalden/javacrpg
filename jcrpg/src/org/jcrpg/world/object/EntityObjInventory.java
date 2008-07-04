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

import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.body.BodyPart;

public class EntityObjInventory {
	/**
	 * Inventory.
	 */
	private ArrayList<ObjInstance> inventory = new ArrayList<ObjInstance>();
	/**
	 * Objects that are currently equipped.
	 */
	private ArrayList<ObjInstance> equipped = new ArrayList<ObjInstance>();
	
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
	
	/**
	 * Returns objects in inventory that are currently usable for the given skill instance - even checking needed attachments
	 * if it's needed.
	 * @param skill
	 * @return
	 */
	public ArrayList<InventoryListElement> getObjectsForSkillInInventory(SkillInstance skill)
	{
		return getObjectsForSkillInInventory(skill, -1);
	}
	/**
	 * Returns objects filtering for skill/attachment and lineup too.
	 * @param skill
	 * @param targetLineUpDistance
	 * @return
	 */
	public ArrayList<InventoryListElement> getObjectsForSkillInInventory(SkillInstance skill,int targetLineUpDistance)
	{
		return getObjectsForSkill(inventory, skill, targetLineUpDistance);
	}
	/**
	 * Returns objects in equipped inventory that are currently usable for the given skill instance - even checking needed attachments
	 * if it's needed.
	 * @param skill
	 * @return
	 */
	public ArrayList<InventoryListElement> getObjectsForSkillInEquipped(SkillInstance skill)
	{
		return getObjectsForSkillInEquipped(skill, -1);
	}
	/**
	 * Returns objects filtering for skill/attachment and lineup too.
	 * @param skill
	 * @param targetLineUpDistance
	 * @return
	 */
	public ArrayList<InventoryListElement> getObjectsForSkillInEquipped(SkillInstance skill, int targetLineUpDistance)
	{
		return getObjectsForSkill(equipped, skill, targetLineUpDistance);
	}
	
	/**
	 * Returns objects that are usable with the skill - checking attachment is present (if needed).
	 * @param list
	 * @param skill
	 * @return
	 */
	private ArrayList<InventoryListElement> getObjectsForSkill(ArrayList<ObjInstance> list, SkillInstance skill, int targetLineUpDistance)
	{
		HashMap<Obj, InventoryListElement> gatherer = new HashMap<Obj, InventoryListElement>();
		
		ArrayList<InventoryListElement> objList = new ArrayList<InventoryListElement>();
		for (ObjInstance o:list)
		{
			if (o.description.requirementSkillAndLevel==null) continue;
			if (o.description.requirementSkillAndLevel.skill == skill.skill)
			{
				if (o.description.requirementSkillAndLevel.level<=skill.level)
				{
					if (targetLineUpDistance>=0)
					{
						// checking lineup
						if (o.description.getUseRangeInLineup()!=Obj.NO_RANGE &&
								o.description.getUseRangeInLineup()<targetLineUpDistance)
							continue;
					}
					
					if (o.needsAttachmentDependencyForSkill())
					{
						if (o.getAttachedDependencies()!=null)
						{
							if (hasOneOfTypes(o.getAttachedDependencies()))
							{
								InventoryListElement l = null;
								if (o.description.isGroupable()) {
									l = gatherer.get(o.description);
								}
								if (l==null)
								{
									l = new InventoryListElement(this,o.description);
									gatherer.put(o.description, l);
									objList.add(l);
								}
								l.objects.add(o);
							}
						}
					} else
					{
						InventoryListElement l = null;
						if (o.description.isGroupable()) {
							l = gatherer.get(o.description);
						}
						if (l==null)
						{
							l = new InventoryListElement(this,o.description);
							gatherer.put(o.description, l);
							objList.add(l);
						}
						l.objects.add(o);
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
			if (toRemove.useOnce())
				inventory.remove(toRemove);
		}
		return toRemove;
	}

	public ObjInstance getOneInstanceOfTypeAndRemove(Obj type)
	{
		
		if (type==null) return null;
		
		ObjInstance toRemove = null;
		for (ObjInstance i:inventory)
		{
			if (i.description == type)
			{
				toRemove = i;
			}
		}
		if (toRemove!=null)
		{
			if (toRemove.useOnce())
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
	
	public void remove(ObjInstance object)
	{
		inventory.remove(object);
		equipped.remove(object);
	}
	public void add(ObjInstance object)
	{
		if (inventory.contains(object)) return;
		inventory.add(object);
	}
	
	public ArrayList<ObjInstance> getInventory() {
		return inventory;
	}
	public ArrayList<ObjInstance> getEquipped() {
		return equipped;
	}

}
