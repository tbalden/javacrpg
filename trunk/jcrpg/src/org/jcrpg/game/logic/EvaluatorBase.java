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

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.game.EncounterLogic.TurnActTurnState;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.body.BodyPart;
import org.jcrpg.world.object.Ammunition;
import org.jcrpg.world.object.BonusObject;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.Weapon;

public class EvaluatorBase {
	
	
	private static int calculateContraAttributePower(SkillActForm form, Attributes attributes)
	{
		int divider = form.contraAttributes.size();
		if (divider == 0) return 50;
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
	
	private static int calculateContraResistencePower(SkillActForm form, Resistances resistances)
	{
		int divider = form.contraResistencies.size();
		if (divider == 0) return 50;
		
		int sum = 0;
		for (String resistance:form.contraResistencies)
		{
			int i = resistances.getResistance(resistance);
			sum+=i;
		}
		sum = sum / divider;
		System.out.println("CONTRA ATTR VALUE = "+sum);
		return sum;
	}

	
	private static int calculatePowerForSkillUse( int level, boolean defense, ObjInstance usedObject, ObjInstance dependencyObj)
	{
		float objMultiplicator = 1f;
		if (usedObject!=null)
		{
			if (usedObject.description instanceof Weapon)
			{
				Weapon w = (Weapon)usedObject.description;
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
		int result = level;
		result = (int)(result * ( objMultiplicator ));
		return result;
	}
	
	public static final int DEFENSE_BASE_VALUE = 20;
	
	public static class EvaluatedData
	{

		int seed;
		public EvaluatedData(int seed, TurnActTurnState state, TurnActMemberChoice choice, BonusSkillActFormDesc bonuseActForm, ObjInstance usedObjectInstance, ObjInstance dependencyObjectInstance)
		{
			this.seed = seed;
			if (bonuseActForm!=null)
			{
				sourceData = new EvaluatedSkillActFormSourceData(choice,bonuseActForm,usedObjectInstance,dependencyObjectInstance);
			} else
			{
				sourceData = new EvaluatedSkillActFormSourceData(choice,usedObjectInstance,dependencyObjectInstance);	
			}
			
			targetData = new ArrayList<EvaluatedSkillActFormTargetData>();
			ArrayList<EntityMemberInstance> targetMembers = new ArrayList<EntityMemberInstance>();
			// collecting target members...
			if (sourceData.needsGroup)
			{
				
				if (choice.target.isGroupId)
				{
					if (choice.target.generatedMembers!=null)
						targetMembers.addAll(choice.target.generatedMembers);
				} else
				{
					if (choice.target.subUnit instanceof EntityMemberInstance)
					{
						targetMembers.add((EntityMemberInstance)choice.target.subUnit);
					}
				}
			} else
			{
				if (choice.targetMember!=null)
					targetMembers.add(choice.targetMember);
			}
			// creating target evaluation data
			for (EntityMemberInstance i:targetMembers)
			{
				TurnActMemberChoice contraChoice = state.memberChoices.get(i);
				if (contraChoice==null)
				{
					contraChoice = new TurnActMemberChoice();
					contraChoice.member = i;
				}
				ObjInstance contraObjectInstance = null;
				if (contraChoice.usedObject!=null)
				{ 	// TODO probably better 'get object' usage?
					contraObjectInstance = contraChoice.usedObject.objects.get(0);
				}
				// TODO body part calc
				targetData.add(new EvaluatedSkillActFormTargetData(choice.skillActForm,contraChoice,null,contraObjectInstance));
			}
			
		}
		
		public Impact evaluate()
		{
			
			int sourcePower = sourceData.calculateSourcePower();
			int maxHundredPlus = HashUtil.mixPercentage(seed, sourceData.source.getNumericId()+sourceData.source.instance.getNumericId(), 0); // random factor
			sourcePower+=maxHundredPlus;
			for (EvaluatedSkillActFormTargetData data:targetData) {
				int targetPower = 50;
				float impact = 0.5f;
				if (!sourceData.sourceChoice.isConstructive())
				{
					targetPower =  data.calculateTargetPower();
					impact = (sourcePower-targetPower)/100f; 
				} else
				{
					impact = 1f;
				}
				SkillActForm skillActForm = sourceData.form;
				Impact i = new Impact();
				J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Impact: "+impact+ " TP/SP: "+targetPower+" / "+sourcePower);
				if (sourcePower>targetPower)
				{
					// success
					i.success = true;
					// calculate resistance impact decrease etc. TODO
					ImpactUnit u = new ImpactUnit();
					for (Integer effectType:skillActForm.effectTypesAndLevels.keySet())
					{
						u.orderedImpactPoints[effectType] = (int)(impact * skillActForm.effectTypesAndLevels.get(effectType));
						System.out.println("* EFFECT " +impact+ " i " + effectType+ " t "+u.orderedImpactPoints[effectType]);
					}
					addTargetImpactUnit(data.target, u);
				}
				ImpactUnit u = new ImpactUnit();
				for (Integer effectType:skillActForm.usedPointsAndLevels.keySet())
				{
					u.orderedImpactPoints[effectType] = (int)(impact * skillActForm.usedPointsAndLevels.get(effectType));
					System.out.println("* SELF EFFECT " + u.orderedImpactPoints[effectType]);
				}
				// calculate XP TODO
				i.actCost = u;
				i.targetImpact = resultImpacts;
				return i;
			}
			
			return null;
		}
		
		/**
		 * The source's skill act form use data
		 */
		public EvaluatedSkillActFormSourceData sourceData;
		
		/**
		 * Target's defense related values for all targets.
		 */
		public ArrayList<EvaluatedSkillActFormTargetData> targetData = new ArrayList<EvaluatedSkillActFormTargetData>();
		
		
		
		/**
		 * hashmap for storing impact units for targets.
		 */
		public HashMap<EntityMemberInstance, ImpactUnit> resultImpacts = new HashMap<EntityMemberInstance, ImpactUnit>();
		
		/**
		 * Upon calculation this should be used if the act form use was successful.  
		 * @param instance
		 * @param unit
		 */
		public void addTargetImpactUnit(EntityMemberInstance instance, ImpactUnit unit)
		{
			ImpactUnit baseUnit = resultImpacts.get(instance);
			if (baseUnit==null)
			{
				resultImpacts.put(instance, unit);
			} else
			{
				baseUnit.append(unit);
			}			
		}
	}
	
	public static class EvaluatedSkillActFormSourceData
	{
		public EntityMemberInstance source;
		public SkillActForm form;
		public int levelOfSkill;
		public TurnActMemberChoice sourceChoice;

		public boolean needsGroup = false;
		
		public Attributes sourceAttributes;
		
		public ImpactUnit resultCost = new ImpactUnit();

		ObjInstance objInstance = null;
		ObjInstance dependencyObj = null;

		public EvaluatedSkillActFormSourceData(TurnActMemberChoice choice, ObjInstance usedObjectInstance, ObjInstance dependencyObjInstance)
		{
			this.sourceChoice = choice;
			this.source = choice.member;
			this.form = choice.skillActForm;
			this.objInstance = usedObjectInstance;
			this.dependencyObj = dependencyObjInstance;
			
			if (form.targetType!=SkillActForm.TARGETTYPE_LIVING_MEMBER) needsGroup = true;
			
			Attributes attributes = choice.member.getAttributes();
			sourceAttributes = attributes;
			levelOfSkill = choice.skill.level;
			if (choice.usedObject!=null && choice.usedObject.description instanceof BonusObject)
			{
				BonusObject bo = (BonusObject)(choice.usedObject.description);
				bo.getAttributeValues();
				sourceAttributes.appendAttributes(bo.getAttributeValues());
			}
		}
		public EvaluatedSkillActFormSourceData(TurnActMemberChoice choice, BonusSkillActFormDesc form, ObjInstance usedObjectInstance, ObjInstance dependencyObjInstance)
		{
			this.sourceChoice = choice;
			this.source = choice.member;
			this.form = form.form;
			this.objInstance = usedObjectInstance;
			this.dependencyObj = dependencyObjInstance;
			
			levelOfSkill = form.skillLevel;
			sourceAttributes = choice.member.getAttributesVanilla();
			if (choice.usedObject!=null && choice.usedObject.description instanceof BonusObject)
			{
				BonusObject bo = (BonusObject)(choice.usedObject.description);
				bo.getAttributeValues();
				sourceAttributes.appendAttributes(bo.getAttributeValues());
			}
		}
		
		public void addCostImpactUnit(ImpactUnit unit)
		{
			resultCost.append(unit);
		}
		
		public int calculateSourcePower()
		{
			return calculatePowerForSkillUse(levelOfSkill, false, objInstance, dependencyObj);
		}
	}
	
	public static class EvaluatedSkillActFormTargetData
	{
		public EntityMemberInstance target;
		public SkillActForm form;
		public SkillActForm sourceForm;
		public int levelOfSkill;

		public Attributes targetAttributes;
		public Resistances targetResistances;

		ObjInstance objInstance = null;
		TurnActMemberChoice targetChoice = null;

		public EvaluatedSkillActFormTargetData(SkillActForm sourceForm, TurnActMemberChoice choice, BodyPart randomTargetBodyPart, ObjInstance objInstance)
		{
			this.targetChoice = choice;
			this.objInstance = objInstance;
			this.target = choice.member;
			this.form = choice.skillActForm;
			this.sourceForm = sourceForm;
			targetAttributes = choice.member.getAttributes();
			targetResistances = choice.member.getResistances();
			if (randomTargetBodyPart!=null) {
				Attributes bonusAttributesForBodyPart = target.inventory.getEquipmentAttributeValues(randomTargetBodyPart.getClass());
				Resistances bonusResistancesForBodyPart = target.inventory.getEquipmentResistanceValues(randomTargetBodyPart.getClass());
				targetAttributes.appendAttributes(bonusAttributesForBodyPart);
				targetResistances.appendResistances(bonusResistancesForBodyPart);
			}
			if (choice.usedObject!=null && choice.usedObject.description instanceof BonusObject)
			{
				BonusObject bo = (BonusObject)(choice.usedObject.description);
				bo.getAttributeValues();
				targetAttributes.appendAttributes(bo.getAttributeValues());
				targetResistances.appendResistances(bo.getResistanceValues());
			}
			levelOfSkill = choice.skill==null?0:choice.skill.level;
		}
		
		public int calculateTargetPower()
		{
			int base = DEFENSE_BASE_VALUE;
			
			if (form!=null)
			{
				base += calculatePowerForSkillUse(levelOfSkill, false, objInstance, null);	
			} else
			{
				// using object, resting, double defense value.
				if (targetChoice.doNothing) {
					base += DEFENSE_BASE_VALUE;
				} else
				{
					base += DEFENSE_BASE_VALUE/2;
				}
			}
			
			int contraMaxHundredPlus =
				(
						calculateContraAttributePower(sourceForm, targetAttributes)/2
				+
						calculateContraResistencePower(sourceForm, targetResistances))/2
				;
			
			return base+contraMaxHundredPlus;
			
			
		}
	}
	
	
	public static Impact evaluateActFormSuccessImpact(int seed, TurnActMemberChoice choice, TurnActTurnState state)
	{
		System.out.println("evaluateActFormSuccessImpact "+choice.member.description.getName()+" "+(choice.skillActForm!=null?choice.skillActForm.getName():"?"));
		Impact i = new Impact();
		
		if (choice.skillActForm!=null)
		{
			ArrayList<BonusSkillActFormDesc> bonusActForms = new ArrayList<BonusSkillActFormDesc>();
			ObjInstance objInstance = null;
			ObjInstance dependencyObjInstance = null;
			if (choice.skillActForm.skill.needsInventoryItem)
			{
				if (choice.usedObject.description.needsAttachmentDependencyForSkill())
				{
					dependencyObjInstance = choice.member.inventory.getOneInstanceOfTypesAndRemove(choice.usedObject.getAttachedDependencies());
					ArrayList<BonusSkillActFormDesc> depBonusActForms = dependencyObjInstance.getLastUseBonusActForms();  
					if (depBonusActForms!=null)
						bonusActForms = depBonusActForms; 
				}
				if (choice.usedObject.description.isGroupable()) {
					objInstance = choice.member.inventory.getOneInstanceOfTypeAndRemove(choice.usedObject.description);
				} else
				{
					objInstance = choice.usedObject.objects.get(0);
					choice.member.inventory.useOnceAndRemove(objInstance);
				}
				ArrayList<BonusSkillActFormDesc> objBonusList = objInstance.getLastUseBonusActForms();
				if (objBonusList!=null)
				{
					if (bonusActForms!=null)
						bonusActForms.addAll(objBonusList);
					else
						bonusActForms = objBonusList;
				}
			}

			// the simple act form
			EvaluatedData evaluated = new EvaluatedData(seed,state,choice,null,objInstance,dependencyObjInstance);
			Impact impact = evaluated.evaluate();
			impact.additionalEffectsToPlay = bonusActForms;
			if (bonusActForms!=null)
			for (BonusSkillActFormDesc bonus:bonusActForms)
			{
				EvaluatedData evaluatedBonus = new EvaluatedData(seed,state,choice,bonus,objInstance,dependencyObjInstance);
				Impact plusImpact = evaluatedBonus.evaluate();
				impact.append(plusImpact,false,false); // appending impacts but not XP and cost (this is artifact use, no such needed)!
			}

			return impact;
			
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
