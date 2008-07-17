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
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.InterceptionSkill;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.SkillBase.NoActForm;
import org.jcrpg.world.ai.abs.state.EntityMemberState;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.object.EntityObjInventory;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.ObjList;

public class EntityMemberInstance {

	
	/**
	 * The "genetic" heritage of the instance.
	 */
	public EntityMember description = null;
	
	/**
	 * Used only in encounter, filled by EncounterInfo.
	 */
	public transient EncounterUnitData encounterData = null;
	
	public int numericId = -1;
	
	/**
	 * The different points and such of the memberInstance.
	 */
	public EntityMemberState memberState = new EntityMemberState(this);
	/**
	 * The fragment with which the member is roaming.
	 */
	public EntityFragment parentFragment = null;
	
	/**
	 * The skill that the given instance is using for his behavior of living around at the current turn.
	 */
	public InterceptionSkill behaviorSkill = null;
	
	public EntityInstance instance = null;

	public EntityMemberInstance(EntityFragment parent, EntityInstance instance, EntityMember description, int numericId) {
		super();
		this.description = description;
		this.parentFragment = parent;
		this.instance = instance;
		this.numericId = numericId;
		try {
			for (Class<?extends Obj> o:J3DCore.getInstance().gameState.charCreationRules.getProfession(description.professions.get(0)).generationNewInstanceObjects)
			{
				inventory.add(new ObjInstance(ObjList.getInstance(o)));
				System.out.println("ADDING ITEM : "+o);
			}
		} catch (Exception ex)
		{
			//Jcrpg.LOGGER.fine(ex.toString());
		}
		updateAfterLeveling();
		memberState.maximizeAtStart();
	}
	
	private transient ArrayList<InterceptionSkill> tempList = new ArrayList<InterceptionSkill>();
	
	/**
	 * Returns currently used interception skills of member.
	 * @return
	 */
	public ArrayList<InterceptionSkill> getUsedInterceptionSkills()
	{
		if (tempList==null) tempList = new ArrayList<InterceptionSkill>();
		tempList.clear();
		tempList.add(behaviorSkill);
		return tempList;
	}

	/**
	 * The inventory.
	 */
	public EntityObjInventory inventory = new EntityObjInventory(this);
	
	/**
	 * Decides what member wants to do in this turn, what skill, what target, object etc.
	 * @param selfData
	 * @param info
	 * @return
	 */
	public TurnActMemberChoice makeTurnActChoice(EncounterUnitData selfData, EncounterInfo info)
	{
		return description.getTurnActMemberChoice(selfData, info, this);
	}

	public int getNumericId() {
		return numericId;
	}
	
	/**
	 * Applies impact unit and return zero reached point type list.
	 * @param unit
	 * @return
	 */
	public ArrayList<Integer> applyImpactUnit(ImpactUnit unit)
	{
		ArrayList<Integer> result = memberState.applyImpactUnit(unit);
		if (result.contains(EntityMemberState.ZERO_HEALTH))
		{
			encounterData.generatedMembers.remove(this);
			this.parentFragment.decreaseSize();
		} else
		if (memberState.isNeutralized())
		{
			encounterData.generatedMembers.remove(this);
		} else
		{
			memberState.increaseExperience(unit.experiencePoint);
		}
		if (parentFragment!=null) parentFragment.notifyImpactResult(this,result, unit);
		return result;
	}
	
	public void notifyEffectChange(ArrayList<StateEffect> added, ArrayList<StateEffect> removed)
	{
		if (parentFragment!=null) parentFragment.notifyEffectChange(this,added,removed);
	}
	
	/**
	 * Get attributes of the member.
	 * @return
	 */
	public Attributes getAttributes()
	{
		// TODO add spell modifiers in effect later when we have them
		
		Attributes attributes = getAttributesVanilla();
		System.out.println("Vanilla: "+attributes);
		Attributes equipmentAttributes = inventory.getEquipmentAttributeValues(null);
		attributes.appendAttributes(equipmentAttributes);
		System.out.println("Equipment: "+equipmentAttributes);
		Attributes stateEffectAttributes = memberState.getStateEffectAttributesSum();
		if (stateEffectAttributes!=null)
		{
			System.out.println("StateEffect: "+stateEffectAttributes);
			attributes.appendAttributes(stateEffectAttributes);
		}
		return attributes;
	}
	/** Get original unbonused attributes. */
	public Attributes getAttributesVanilla()
	{
		Attributes attributes = description.getAttributes(instance!=null?instance.description:null);
		return attributes;
	}

	/**
	 * Get resistances of the member.
	 * @return
	 */
	public Resistances getResistances()
	{
		Resistances base = description.getResistances(instance.description);
		if (base==null) { // workaround for old characters, can be removed later TODO
			if (description instanceof MemberPerson)
			{
				((MemberPerson)description).resistances = new FantasyResistances(false);
			}
		} // end workaround
		Resistances resistances = description.getResistances(instance.description).copy();
		resistances.appendResistances(inventory.getEquipmentResistanceValues(null));
		Resistances stateEffectResistances = memberState.getStateEffectResistancesSum();
		if (stateEffectResistances!=null)
		{
			resistances.appendResistances(stateEffectResistances);
		}
		return resistances;
	}
	
	/** Get original unbonused res. */
	public Resistances getResistancesVanilla()
	{
		Resistances resistances = description.getResistances(instance.description);
		return resistances;
	}

	/**
	 * Returns a random sound of a given type if available.
	 * @param type
	 * @return
	 */
	public String getSound(String type)
	{
		if (description.audioDescription==null) return null;
		return description.audioDescription.getSound(type);
	}
	
	/**
	 * Tells if this member is currently rendered in 3d.
	 * @return
	 */
	public boolean isRendered()
	{
		if (encounterData==null) return false;
		return encounterData.isRendered();
	}
	
	public EntityFragment getParentFragment() {
		return parentFragment;
	}

	public void setParentFragment(EntityFragment parentFragment) {
		this.parentFragment = parentFragment;
	}
	
	public int getSkillLevel(Class<? extends SkillBase> skill)
	{
		return description.memberSkills.getSkillLevel(skill, null);
	}
	public void setSkillLevel(Class<? extends SkillBase> skill, int level)
	{
		description.memberSkills.setSkillValue(skill, level);
	}
	
	
	
	public void updateAfterLeveling()
	{
		description.memberSkills.updateSkillActForms();
		memberState.recalculateMaximums(true);
	}
	
	public boolean isDead()
	{
		return memberState.isDead();
	}
	public boolean isNotPresent()
	{
		return memberState.isDead() || memberState.isNeutralized();
	}
	
	/**
	 * Returns those skill act form of a skill that for the current state of member's points can be done.
	 * @param skill
	 * @return The list os skill act forms.
	 */
	public ArrayList<Class<? extends SkillActForm>> getDoableActForms(Class<? extends SkillBase> skill)
	{
		ArrayList<Class<? extends SkillActForm>> list = new ArrayList<Class<? extends SkillActForm>>();
		SkillInstance i = description.memberSkills.skills.get(skill);
		if (i!=null)
		{
			for (Class<? extends SkillActForm> form :i.aquiredActForms)
			{
				if (form == NoActForm.class) continue;
				SkillBase b = SkillGroups.skillBaseInstances.get(i.skill);				
				if (b.getActForm(form).canBeDoneByMember(this)){
					list.add(form);
				}
			}
		}
		return list;
	}
	
	/**
	 * Tries to equip an item.
	 * @param equipment
	 * @return true if success.
	 */
	public boolean equip(ObjInstance equipment)
	{
		return inventory.equip(equipment);
	}
	
	/**
	 * Tries to unequip (if not cursed, it succeeds).
	 * @param equipment
	 * @return
	 */
	public boolean unequip(ObjInstance equipment)
	{
		return inventory.unequip(equipment);
	}
	
	public SkillContainer getSkills()
	{
		return description.memberSkills;
	}
	
}
