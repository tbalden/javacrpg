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
package org.jcrpg.game.logic;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.EncounterLogic.TurnActTurnState;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.util.HashUtil;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.HelperSkill;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.abs.state.StateEffectInitParams;
import org.jcrpg.world.ai.body.BodyPart;
import org.jcrpg.world.object.Ammunition;
import org.jcrpg.world.object.Armor;
import org.jcrpg.world.object.BonusObject;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.Weapon;

import com.jme.renderer.ColorRGBA;

/**
 * The class for evaluation of turn act things - success failure, impact.
 * @author illes
 */
public class EvaluatorBase {
	
	
	private static int calculateAttributePower(ArrayList<String> names, Attributes attributes)
	{
		int divider = names.size();
		if (divider == 0) return 50;
		int sum = 0;
		for (String attr:names)
		{
			int i = attributes.getAttribute(attr);
			sum+=i;
		}
		sum = sum / divider;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("CONTRA ATTR VALUE = "+sum);
		return sum;
	}
	
	private static int calculateResistencePower(ArrayList<String> names, Resistances resistances)
	{
		int divider = names.size();
		if (divider == 0) return 50;
		
		int sum = 0;
		for (String resistance:names)
		{
			int i = resistances.getResistance(resistance);
			sum+=i;
		}
		sum = sum / divider;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("CONTRA RES VALUE = "+sum);
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
		int result = level;// + ATTACK_BASE_VALUE;
		result = (int)(result * ( objMultiplicator ));
		return result;
	}
	
	//public static final int ATTACK_BASE_VALUE = 0;
	public static final int DEFENSE_BASE_VALUE = 50;
	
	public static class EvaluatedData
	{

		int seed;
		
		int XPmultiplier = 1;
		
		TurnActTurnState state = null;

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
		
		public EvaluatedData(int seed, TurnActTurnState state, TurnActMemberChoice choice, BonusSkillActFormDesc bonuseActForm, ObjInstance usedObjectInstance, ObjInstance dependencyObjectInstance)
		{
			this.seed = seed;
			this.state = state;
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
					if (choice.target.getAllLivingMember()!=null)
					{
						targetMembers.addAll(choice.target.getAllLivingMember());
						XPmultiplier = choice.target.getUnit().getLevel();
					}
				} else
				{
					if (choice.target.subUnit instanceof EntityMemberInstance)
					{
						targetMembers.add((EntityMemberInstance)choice.target.subUnit);
						XPmultiplier = ((EntityMemberInstance)choice.target.subUnit).memberState.level;
					}
				}
			} else
			{
				if (choice.targetMember!=null)
				{
					if (choice.targetMember.isNotPresent() && choice.targetMember.encounterData.isGroupId)
					{
						// chose another one
						choice.targetMember = choice.targetMember.encounterData.getFirstLivingMember();
					}
					if (!choice.targetMember.isNotPresent())
					{
						targetMembers.add(choice.targetMember);
						XPmultiplier = choice.targetMember.memberState.level;
					}
				}
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
				targetData.add(new EvaluatedSkillActFormTargetData(seed, choice,contraChoice,contraObjectInstance));
			}
			
		}
		
		public Impact evaluate()
		{
			
			// calculating sourcePower
			int sourcePower = sourceData.calculateSourcePower();
			int maxHundredPlus = HashUtil.mixPercentage(seed, sourceData.source.getNumericId()+sourceData.source.instance.getNumericId(), 0); // random factor
			sourcePower+=maxHundredPlus;
			
			Impact i = new Impact();
			SkillActForm skillActForm = sourceData.form;
			for (EvaluatedSkillActFormTargetData data:targetData) {
				int targetPower = DEFENSE_BASE_VALUE;
				float impact = 1f;
				HashMap<String, Integer> damages = sourceData.getPossibleDamagesPerResistance();
				ArrayList<String> additionalContraResistances = new ArrayList<String>();
				if (damages!=null)
				{
					additionalContraResistances.addAll(damages.keySet());
				}
				if (!sourceData.sourceChoice.isConstructive())
				{
					if (sourceData.sourceChoice.skill!=null && data.targetChoice.skill!=null)
					{
						// if the target's skill is not a contra skill for attacker's skill, then use level 0...
						if (!sourceData.sourceChoice.skill.getSkill().getContraSkillTypes().contains(data.targetChoice.skill))
						{
							data.levelOfSkill = 0;
						}
					}
					int[] targetPowerAndRes =  data.calculateTargetPowerAndResistance(additionalContraResistances);
					targetPower = targetPowerAndRes[0];
					int targetRes = targetPowerAndRes[1];
					
					int resistanceRoll = HashUtil.mixPer1000(seed, data.target.getNumericId()+data.target.instance.getNumericId(), 0)%120; // random factor
					
					float resistanceMul = 1f;
					if (targetRes<=resistanceRoll)
					{
						// full damage
					} else
					{
						// resistance roll is lesser than target resistance, modify damage
						resistanceMul = (resistanceRoll/targetRes);
					}
					
					impact = (sourcePower-targetPower)/100f * resistanceMul;
					String resString = ""+resistanceMul;
					if (resString.length()>4)
					{
						resString = resString.substring(0,4);
					}
					i.messages.add(new TextEntry(data.target.description.getName() + ": Resistance Mul ("+targetRes+" ? "+resistanceRoll+") "+resString,ColorRGBA.darkGray) );
				} else
				{
					impact = 1f;
				}
				if (skillActForm.isBodyPartTargetted())
				{
					i.messages.add(new TextEntry(data.target.description.getName() + ": "+data.randomTargetBodyPart.getName(), ColorRGBA.black));
				}
				if (sourcePower>targetPower)
				{
					String impactString = ""+impact;
					if (impactString.length()>4)
					{
						impactString = impactString.substring(0,4);
					}
					
					i.messages.add(new TextEntry(data.target.description.getName() + ": Success! Impact: "+impactString+ " Def/Att: "+targetPower+"/"+sourcePower,ColorRGBA.red));
					// success
					// calculate resistance impact decrease etc. TODO
					ImpactUnit u = new ImpactUnit();
					String effectText = "";

					// gathering/instantiating state effects for skill act form
					ArrayList<StateEffect> effects = new ArrayList<StateEffect>();
					if (sourceData.form!=null)
					{
						for (StateEffectInitParams params:sourceData.form.stateEffectsAndLevels)
						{
							StateEffect e = params.getOne(J3DCore.getInstance().gameState.engine.getWorldMeanTime(), sourcePower-targetPower, 1, data.target);
							i.messages.add(new TextEntry(data.target.description.getName() + " " + e.getAdditionText(),ColorRGBA.magenta));
							effects.add(e);
						}
					}
					u.stateEffects = effects;
					
					for (String key:damages.keySet())
					{
						if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("EvaluatorBase.evaluate: ## DAMAGE: "+key+" "+damages.get(key));
					}
					for (Integer effectType:skillActForm.effectTypesAndLevels.keySet())
					{
						int baseDamage = 1; 
						float criticalHitMultiplicator = 1f;
						if (effectType == SkillActForm.EFFECTED_POINT_HEALTH)
						{
							if (damages.size()>0) baseDamage = 0;
							for (Integer hpDamage:damages.values())
							{
								// TODO resistance string lookup, decrease
								baseDamage+=hpDamage;
							}
							if (baseDamage>0)
							{
								// negative hitpoint effect, check for armor...
								if (data.armor!=null)
								{
									int decrease =data.armor.getHitPointImpactDecrease();
									baseDamage-=decrease;
								}
							}
							if (baseDamage<0) baseDamage = 0;
							if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("##!! BASEDAMAGE = "+baseDamage);
							
							if (data.randomTargetBodyPart!=null)
							{
								// critical multiplicator calculation
								criticalHitMultiplicator = data.randomTargetBodyPart.getCriticalityOfInjury()/10f;
								if (criticalHitMultiplicator>1.2f)
									i.messages.add(new TextEntry("*CRITICAL BODYPART HIT*",ColorRGBA.black));
							}
							
						}
						u.orderedImpactPoints[effectType] = (int)(impact * (baseDamage * 1f) * (skillActForm.effectTypesAndLevels.get(effectType)/2f) * criticalHitMultiplicator);
						effectText+=""+Language.v("effects.short."+effectType)+" = "+u.orderedImpactPoints[effectType]+" ";
					}
					i.messages.add(new TextEntry("Effect: "+effectText,ColorRGBA.black));
					
					addTargetImpactUnit(data.target, u);
					
					// gather sounds
					if (u.isEffectiveSuccess())
					{
						i.success = true;
						if (sourceData.sourceChoice.isDestructive())
						{
							// gather sounds
							String sound = data.target.getSound(AudioDescription.T_PAIN);
							if (sound!=null)
								i.soundsToPlay.add(sound);
						} 
						if (sourceData.sourceChoice.isConstructive())
						{
							// gather sounds
							String sound = data.target.getSound(AudioDescription.T_JOY);
							if (sound!=null)
								i.soundsToPlay.add(sound);
						}
					}
				} else
				{
					i.messages.add(new TextEntry(data.target.description.getName() + ": Failure! Def/Att: "+targetPower+"/"+sourcePower,ColorRGBA.black));
				}
			}
			
			
			ImpactUnit cost = new ImpactUnit();
			for (Integer effectType:skillActForm.usedPointsAndLevels.keySet())
			{
				cost.orderedImpactPoints[effectType] = (int)(skillActForm.usedPointsAndLevels.get(effectType));
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("* SELF EFFECT " + cost.orderedImpactPoints[effectType]);
			}
			
			i.actCost = cost;
			i.targetImpact = resultImpacts;
			// calculating XP for the full act (not per target!)
			if (i.success) i.actCost.experiencePoint = i.actCost.experiencePoint * XPmultiplier;
			i.messages.add(new TextEntry("XP Gain: "+i.actCost.experiencePoint ,ColorRGBA.black));
			return i;
		}
		
		
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
				baseUnit.append(unit,true);
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
		
		public HashMap<String, Integer> getPossibleDamagesPerResistance()
		{
			HashMap<String, Integer> ret = new HashMap<String, Integer>();
			if (objInstance!=null)
			{
				if (objInstance.description instanceof Weapon)
				{
					Weapon w = (Weapon)objInstance.description;
					ret.put(w.getDamageTypeResistance(),w.getMaxDamage());
				}
			}
			if (dependencyObj!=null)
			{
				if (dependencyObj.description instanceof Ammunition)
				{
					Ammunition a = (Ammunition)(dependencyObj.description);
					Integer res = ret.get(a.getDamageTypeResistance());
					if (res!=null)
					{
						res = (int)(res*a.getDamageMultiplier());
						ret.put(a.getDamageTypeResistance(), res);
					}
					if (a.getAmmunitionExtraDamage()!=null)
					{
						for (String resType : a.getAmmunitionExtraDamage().keySet())
						{
							Integer i = a.getAmmunitionExtraDamage().get(resType);
							Integer o = ret.get(resType);
							if (o!=null)
							{
								i+=o;
							}
							ret.put(resType, i);
						}
					}
				}
			}
			return ret;
		}
		
		public void addCostImpactUnit(ImpactUnit unit)
		{
			resultCost.append(unit,true);
		}
		
		public int calculateSourcePower()
		{
			// ATTACK POINT = 
			//
			//    (SKILL TYPE POWER) +
			//      ( PRO ATTRIBUTES AVERAGE ) / 2 )      + Random 0-100
			//
			// (0 - 100) + (0 - 50) = 0-150  ->  0 - 250
			

			int base = calculatePowerForSkillUse(levelOfSkill, false, objInstance, dependencyObj);
			if (form!=null)
				base += calculateAttributePower(form.proAttributes, sourceAttributes)/2;
			return base;
		}
		
		public boolean isBodyPartTargetted()
		{
			if (sourceChoice!=null && sourceChoice.skillActForm!=null)
			{
				return sourceChoice.skillActForm.isBodyPartTargetted();
			}
			return false;
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
		BodyPart randomTargetBodyPart;
		Armor armor = null;

		public EvaluatedSkillActFormTargetData(int seed, TurnActMemberChoice sourceChoice, TurnActMemberChoice choice, ObjInstance objInstance)
		{
			this.targetChoice = choice;
			this.objInstance = objInstance;
			this.target = choice.member;
			this.form = choice.skillActForm;
			this.sourceForm = sourceChoice.skillActForm;
			targetAttributes = choice.member.getAttributes();
			targetResistances = choice.member.getResistances();
			
			if (sourceForm!=null && sourceForm.isBodyPartTargetted())
			{
				int skillLevelForTargetting = 0;
				SkillInstance helperInstance = sourceChoice.member.getSkills().getHighestLevelHelperSkill(sourceChoice.skill.skill, HelperSkill.TAG_CRITICAL_HIT);
				if (helperInstance!=null)
				{
					skillLevelForTargetting = helperInstance.level;
				}
				randomTargetBodyPart = getBodyPartTargetted(seed, skillLevelForTargetting);
			}
			if (randomTargetBodyPart!=null) {
				Attributes bonusAttributesForBodyPart = target.inventory.getEquipmentAttributeValues(randomTargetBodyPart.getClass());
				Resistances bonusResistancesForBodyPart = target.inventory.getEquipmentResistanceValues(randomTargetBodyPart.getClass());
				armor = target.inventory.getEquippedArmor(randomTargetBodyPart.getClass());
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
		
		public int[] calculateTargetPowerAndResistance(ArrayList<String> additionalContraResistances)
		{
			
			// DEFENSE POINT = 
			//
			//    BASE VALUE (50) + ARMOR VALUE + (CONTRA SKILL TYPE POWER / 2) +
			//      ( CONTRA ATTRIBUTES AVERAGE )
			//
			//    50 + (0 - 100) + (0 - 50) + (0 - 100) = 50 - 250
			
			int base = DEFENSE_BASE_VALUE;
			
			if (armor!=null)
			{
				base+=armor.getDefenseValue();
			}
			
			if (form!=null)
			{
				base += calculatePowerForSkillUse(levelOfSkill, true, objInstance, null) / 2;	
			} else
			
			if (additionalContraResistances==null)
			{
				additionalContraResistances = sourceForm.contraResistencies;
			}
			else
			{
				additionalContraResistances.addAll(sourceForm.contraResistencies);
			}
			
			int contraMaxHundredPlus =
				(
						calculateAttributePower(sourceForm.contraAttributes, targetAttributes))
				;
			
			return new int[]{base+contraMaxHundredPlus,	calculateResistencePower(additionalContraResistances, targetResistances)};
		}
		
		public BodyPart getBodyPartTargetted(int seed, int targettingCriticalLevel)
		{
			if (J3DCore.LOGGING) Jcrpg.LOGGER.fine("getBodyPartTargetted "+ target+" - "+target.description.getName()+" "+target.description.getBodyType());
			return target.description.getBodyType().getBodyPart(seed, target, targettingCriticalLevel);
		}
		
		
	}
	
	
	public static Impact evaluateActFormSuccessImpact(int seed, TurnActMemberChoice choice, TurnActTurnState state)
	{
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("evaluateActFormSuccessImpact "+choice.member.description.getName()+" "+(choice.skillActForm!=null?choice.skillActForm.getName():"?"));
		
		if (choice.doUse || choice.skillActForm!=null)
		{
			ArrayList<BonusSkillActFormDesc> bonusActForms = new ArrayList<BonusSkillActFormDesc>();
			ObjInstance objInstance = null;
			ObjInstance dependencyObjInstance = null;
			if (choice.doUse || choice.skillActForm.skill.needsInventoryItem)
			{
				if (choice.usedObject.description.needsAttachmentDependencyForSkill())
				{
					// handling of dependency object and gathering of its bonuses
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
				// get object's bonus skill act form list...
				ArrayList<BonusSkillActFormDesc> objBonusList = objInstance.getLastUseBonusActForms();
				// merge dependency / used object bonus list...
				if (objBonusList!=null)
				{
					if (bonusActForms!=null)
						bonusActForms.addAll(objBonusList);
					else
						bonusActForms = objBonusList;
				}
			}

			// the simple act form
			Impact impact = new Impact();
			if (!choice.doUse)
			{
				EvaluatedData evaluated = new EvaluatedData(seed,state,choice,null,objInstance,dependencyObjInstance);
				impact = evaluated.evaluate();
			}
			impact.additionalEffectsToPlay = bonusActForms;
			if (bonusActForms!=null)
			for (BonusSkillActFormDesc bonus:bonusActForms)
			{
				EvaluatedData evaluatedBonus = new EvaluatedData(seed,state,choice,bonus,objInstance,dependencyObjInstance);
				Impact plusImpact = evaluatedBonus.evaluate();
				impact.append(plusImpact,false,false); // appending impacts but not XP and cost (this is artifact use, no such needed)!
				if (choice.doUse && impact.success==false)
				{
					impact.success = plusImpact.success;
				}
			}

			return impact;
			
		}
		Impact i = new Impact();
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
