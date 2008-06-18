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

package org.jcrpg.world.ai;

import java.util.ArrayList;

import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.world.ai.abs.skill.InterceptionSkill;
import org.jcrpg.world.ai.abs.state.EntityMemberState;
import org.jcrpg.world.object.EntityObjInventory;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.ObjList;

public class EntityMemberInstance {

	
	/**
	 * The "genetic" heritage of the instance.
	 */
	public EntityMember description = null;
	
	
	
	public int numericId = -1;
	
	/**
	 * The different points and such of the memberInstance.
	 */
	public EntityMemberState memberState = new EntityMemberState();
	
	/**
	 * The skill that the given instance is using for his behavior of living around at the current turn.
	 */
	public InterceptionSkill behaviorSkill = null;
	
	public EntityInstance instance = null;

	public EntityMemberInstance(EntityInstance instance, EntityMember description, int numericId) {
		super();
		this.description = description;
		this.instance = instance;
		this.numericId = numericId;
		try {
			for (Class<?extends Obj> o:EntityMember.profInstances.get(description.professions.get(0)).generationNewInstanceObjects)
			{
				inventory.inventory.add(new ObjInstance(ObjList.objects.get(o)));
				System.out.println("ADDING ITEM : "+o);
			}
		} catch (Exception ex)
		{
			//Jcrpg.LOGGER.fine(ex.toString());
		}
	
	}
	
	private ArrayList<InterceptionSkill> tempList = new ArrayList<InterceptionSkill>();
	
	public ArrayList<InterceptionSkill> getUsedInterceptionSkills()
	{
		tempList.clear();
		tempList.add(behaviorSkill);
		return tempList;
	}

	/**
	 * The inventory.
	 */
	public EntityObjInventory inventory = new EntityObjInventory();
	
	public TurnActMemberChoice makeTurnActChoice(EncounterUnitData selfData, EncounterInfo info)
	{
		return description.getTurnActMemberChoice(selfData, info, this);
	}

	public int getNumericId() {
		return numericId;
	}
	
	
}
