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
package org.jcrpg.game.element;

import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.world.ai.EncounterUnitData;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.object.BonusObject;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.Obj;

/**
 * A member's decision about what to do in a given turn act turn.
 * @author illes
 */
public class TurnActMemberChoice {
	
	/**
	 * The actor.
	 */
	public EntityMemberInstance member;
	
	public boolean doNothing = false;
	public boolean doUse = false;
	public boolean doEscape = false;
	
	/** 
	 * Used skill.
	 */
	public SkillInstance skill;
	/**
	 * Act form.
	 */
	public SkillActForm skillActForm;
	/**
	 * Target group/unit.
	 */
	public EncounterUnitData target;
	/**
	 * May be null if group target skillActForm.
	 */
	public EntityMemberInstance targetMember;
	
	/**
	 * Object used for act form.
	 */
	public InventoryListElement usedObject;
	
	/**
	 * Return init message for the time when choice execution is being started.
	 * @return
	 */
	public String getInitMessage()
	{
		if (doNothing)
		{
			if (doEscape)
			{
				return member.description.getName() + " trying to escape!";
			}
			return member.description.getName() + " doing nothing."; 
		}
		return member.description.getName() + " -> "+(targetMember!=null?targetMember.description.getName():target!=null?target.getName():"?")+" : "+(doUse?"uses":(skillActForm!=null?skillActForm.getClass().getSimpleName():"?"))+" "+(usedObject!=null?usedObject.getSingleName():"")+".";
	}
	
	/**
	 * Tells if choice is with destruction will. 
	 * @return
	 */
	public boolean isDestructive()
	{
		if (skillActForm!=null)
		{
			return skillActForm.atomicEffect<0;
		} else
		{
			if (doUse && usedObject!=null)
			{
				if (usedObject.description instanceof BonusObject)
				{
					return ((BonusObject)usedObject.description).isDestructive();
				}
			}
		}
		return false;
	}
	/**
	 * Tells if choice is with constructive will.
	 * @return
	 */
	public boolean isConstructive()
	{
		if (skillActForm!=null)
		{
			return skillActForm.atomicEffect>0;
		} else
		{
			if (doUse && usedObject!=null)
			{
				if (usedObject.description instanceof BonusObject)
				{
					return !((BonusObject)usedObject.description).isDestructive();
				}
			}
		}
		return false;
	}
	
	public EffectProgram getEffectProgram()
	{
		EffectProgram program = null;
		if (skillActForm!=null)
		{
			program = skillActForm.getEffectProgram();
		}
		
		if (usedObject!=null) {
			
			Obj i = null;
			if (usedObject.description.needsAttachmentDependencyForSkill())
			{
				i = member.inventory.getPossibleNextOneType(usedObject.getAttachedDependencies());
			}
			EffectProgram program3 = usedObject.description.getEffectProgram();
			if (program3!=null) program = program3;
			if (i!=null)
			{
				EffectProgram program2 = i.getEffectProgram();
				if (program2!=null) program = program2;
			}
			
		}
		return program;
	
	}
	
	/**
	 * Tells if there should be an effect played on target after this choice done. (pain etc.)
	 * @return
	 */
	public boolean isWithImpact()
	{
		return doUse && usedObject.description instanceof BonusObject||skillActForm!=null;
	}
	
	@Override
	public String toString()
	{
		return "DoUse: "+doUse+" DoNothing: "+doNothing+" DoEscape: "+doEscape+"\n"+" ActForm "+(skillActForm==null?"null":skillActForm.getName())+" Skill: "+(skill==null?"null":skill.getSkill().getName())+" Object"+(usedObject==null?"null":usedObject.getName())+"\n"+"Member: "+(member==null?"null":(member.description.getName()+" EncData="+member.encounterData))+" TargetMember: "+(targetMember==null?"null":targetMember.description.getName())+" TargetEncData: "+(target==null?"null":(target.getName()+"/"+(target.description!=null?target.description.getName():"target no desc"))); 
	}

}
