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
package org.jcrpg.game.logic;

import org.jcrpg.game.EncounterLogic.TurnActTurnState;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.object.Ammunition;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.Weapon;

public class EvaluatorBase {
	

	
	
	
	private static int calculateContraAttributeValue(SkillActForm form, EntityMemberInstance defender)
	{
		int divider = form.contraAttributes.size();
		if (divider == 0) return 50;
		Attributes attributes = defender.getAttributes();
		int sum = 0;
		for (String attr:form.contraAttributes)
		{
			int i = attributes.getAttribute(attr);
			sum+=i;
		}
		sum = sum / divider;
		System.out.println("CONTRA ATTR VALUE = "+sum);
		return sum;
	}
	
	private static int calculateContraResistenceValue(SkillActForm form, EntityMemberInstance defender)
	{
		int divider = form.contraResistencies.size();
		if (divider == 0) return 50;
		Resistances attributes = defender.getResistances();
		int sum = 0;
		for (String resistance:form.contraResistencies)
		{
			int i = attributes.getResistance(resistance);
			sum+=i;
		}
		sum = sum / divider;
		System.out.println("CONTRA ATTR VALUE = "+sum);
		return sum;
	}
	
	private static int calculateResultForSkillUse(int seed, TurnActMemberChoice choice, boolean defense, ObjInstance dependencyObj)
	{
		float objMultiplicator = 1f;
		if (choice.usedObject!=null)
		{
			if (choice.usedObject.description instanceof Weapon)
			{
				Weapon w = (Weapon)choice.usedObject.description;
				if (defense)
				{
					objMultiplicator = w.getDefenseMultiplicator();
				} else
				{
					objMultiplicator = w.getAttackMultiplicator();
					if (dependencyObj!=null)
					{
						if (dependencyObj.description instanceof Ammunition)
						{
							objMultiplicator *= ((Ammunition)dependencyObj.description).getAttackMultiplier();
						}
					}
				}
			}
		}
		int level = choice.skill.level;
		int result = level;
		result = (int)(result * ( objMultiplicator ));
		return result;
		
	}
	
	public static final int DEFENSE_BASE_VALUE = 20;

	public static Impact evaluateActFormSuccessImpact(int seed, TurnActMemberChoice choice, TurnActTurnState state)
	{
		System.out.println("evaluateActFormSuccessImpact "+choice.member.description.getName()+" "+(choice.skillActForm!=null?choice.skillActForm.getName():"?"));
		Impact i = new Impact();
		if (choice.skillActForm!=null)
		{
			//if (choice.skillActForm)
			
			boolean success = false;
			float impact = 0.5f;

			ObjInstance dependencyObj = null;
			if (choice.skillActForm.skill.needsInventoryItem)
			{
				if (choice.usedObject.description.needsAttachmentDependencyForSkill())
				{
					dependencyObj = choice.member.inventory.getOneInstanceOfTypesAndRemove(choice.usedObject.getAttachedDependencies());
				}
				choice.member.inventory.getOneInstanceOfTypeAndRemove(choice.usedObject.description);
			}
		
			// calculate attack...
			int result = calculateResultForSkillUse(seed, choice,false,dependencyObj); 
			int maxHundredPlus = HashUtil.mixPercentage(seed, choice.member.getNumericId()+choice.member.instance.getNumericId(), 0); // random factor
			result+=maxHundredPlus;
			
			// calculate defense
			int contraResult = DEFENSE_BASE_VALUE; // default defense value
			TurnActMemberChoice contraChoice = state.memberChoices.get(choice.targetMember);
			if (contraChoice!=null && contraChoice.skillActForm!=null)
			{
				contraResult += calculateResultForSkillUse(seed, choice,true,null);
			}
			int contraMaxHundredPlus =
				(
						calculateContraAttributeValue(choice.skillActForm, choice.targetMember)/2
				+
						calculateContraResistenceValue(choice.skillActForm, choice.targetMember))/2
				;
				
			//contraMaxHundredPlus+=HashUtil.mixPercentage(seed, choice.targetMember.getNumericId(),choice.targetMember.instance.getNumericId(), 0); // random factor
			contraResult+=contraMaxHundredPlus;

			System.out.println("EVAULATED RESULT = "+result + " ? "+contraResult);
			if (result>contraResult)
			{
				success = true;
			}
			if (success)
			{
				i.success = true;
				ImpactUnit u = new ImpactUnit();
				for (Integer effectType:choice.skillActForm.effectTypesAndLevels.keySet())
				{
					u.orderedImpactPoints[effectType] = (int)(impact * choice.skillActForm.effectTypesAndLevels.get(effectType));
					System.out.println("* EFFECT " +impact+ " i " + effectType+ " t "+u.orderedImpactPoints[effectType]);
				}
				i.targetImpact.put(choice.targetMember, u);
				// TODO calculate impact effect based on targetmember's skills / spell effects/ armor etc.
				// calculate ammunition / weapon magical effects etc.
			}
			ImpactUnit u = i.actCost;
			for (Integer effectType:choice.skillActForm.usedPointsAndLevels.keySet())
			{
				u.orderedImpactPoints[effectType] = (int)(impact * choice.skillActForm.usedPointsAndLevels.get(effectType));
				System.out.println("* SELF EFFECT " + u.orderedImpactPoints[effectType]);
			}
		}
		return i;
	}
	
	public static float[] evaluateActFormTimesWithSpeed(int seed, TurnActMemberChoice choice)
	{
		return evaluateActFormTimesWithSpeed(seed, choice.member,choice.skill,choice.skillActForm,choice.usedObject);
	}

	/**
	 * Returns all times of an act represented by it's speed time.
	 * @param seed
	 * @param instance
	 * @param skill
	 * @param form
	 * @param obj
	 * @return
	 */
	public static float[] evaluateActFormTimesWithSpeed(int seed, EntityMemberInstance instance, SkillInstance skill, SkillActForm form, InventoryListElement obj)
	{
		
		float speed = AttributeRatios.getAttribute(FantasyAttributes.SPEED, instance.instance.attributes, instance.description.commonAttributeRatios);
		
		float stamina = Math.max(0.1f,instance.memberState.staminaPoint/instance.memberState.maxStaminaPoint);
		stamina*=2f;

		float baseFloat = 0.2f;
		if (skill!=null) 
		{
			float level = skill.level/100f;
			
			float objectSpeed = 0.5f;
			if (obj!=null && obj.description instanceof Weapon)
			{
				objectSpeed = ((Weapon)obj.description).getSpeed()/10f;
			}
			baseFloat*=level*objectSpeed;
			
		}
		float plus = HashUtil.mixPercentage(seed, instance.getNumericId()+instance.instance.getNumericId(), 0)/100f; // random factor
		
		return new float[]{100f/(10f*speed*baseFloat*stamina + 2*plus)};
		// TODO multi time events (quick attacks etc.)
	}
	
}
