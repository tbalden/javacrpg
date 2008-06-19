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

import org.jcrpg.world.ai.abs.skill.SkillInstance;

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
					return true;
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
					objList.add(o);
				}
			}
		}
		return objList;
		
	}

}