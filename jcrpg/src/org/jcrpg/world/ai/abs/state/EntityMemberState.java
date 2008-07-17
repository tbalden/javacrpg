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
package org.jcrpg.world.ai.abs.state;

import java.util.ArrayList;

import org.jcrpg.game.GameLogicConstants;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.time.Time;

public class EntityMemberState {
	
	public static int DEFAULT_HEALTH_POINT = 10;
	public static int DEFAULT_STAMINA_POINT = 10;
	public static int DEFAULT_MORALE_POINT = 10;
	public static int DEFAULT_MANA_POINT = 0;
	public static int DEFAULT_SANITY_POINT = 10;
	
	public int maxHealthPoint = DEFAULT_HEALTH_POINT;
	public int maxStaminaPoint = DEFAULT_STAMINA_POINT;
	public int maxMoralePoint = DEFAULT_MORALE_POINT;
	public int maxSanityPoint = DEFAULT_SANITY_POINT;
	public int maxManaPoint = DEFAULT_MANA_POINT;

	public int origMaxHealthPoint = DEFAULT_HEALTH_POINT;
	public int origMaxStaminaPoint = DEFAULT_STAMINA_POINT;
	public int origMaxMoralePoint = DEFAULT_MORALE_POINT;
	public int origMaxSanityPoint = DEFAULT_SANITY_POINT;
	public int origMaxManaPoint = DEFAULT_MANA_POINT;
	
	public int healthPoint = DEFAULT_HEALTH_POINT;
	public int staminaPoint = DEFAULT_STAMINA_POINT;
	public int moralePoint = DEFAULT_MORALE_POINT;
	public int sanityPoint = DEFAULT_SANITY_POINT;
	public int manaPoint = DEFAULT_MANA_POINT;
	
	public int level = 1;
	public int experiencePoint = 0;
	public static final int LEVELING_XP = 10; // the XP needed for new level of the member
	
	public static final int ZERO_HEALTH = 0;
	public static final int ZERO_STAMINA = 1;
	public static final int ZERO_MORALE = 2;
	public static final int ZERO_SANITY = 3;
	public static final int ZERO_MANA = 4;
	
	EntityMemberInstance instance;
	
	private ArrayList<StateEffect> effects = new ArrayList<StateEffect>();
	
	public EntityMemberState(EntityMemberInstance i)
	{
		instance = i;
	}
	
	// set this to true if you want to prevent persistentMembers from point changes.
	boolean cheatNPC = false;
	/**
	 * Applies impact unit and return zero reached point type list.
	 * @param unit
	 * @return
	 */
	public ArrayList<Integer> applyImpactUnit(ImpactUnit unit)
	{
		if (unit.getHealthPoint()<0) updateEffectsUponAttackImpact();
		if (!cheatNPC || !(instance instanceof PersistentMemberInstance))
		{
			System.out.println("applyImpactUnit "+instance.description.getName());
			System.out.println("HEALTHPOINT BEFORE : "+healthPoint);
			System.out.println("IMPACT : "+unit.getHealthPoint());
			healthPoint=Math.min(healthPoint+unit.getHealthPoint(),maxHealthPoint);
			System.out.println("HEALTHPOINT AFTER : "+healthPoint);
			System.out.println("MANA IMPACT : "+unit.getManaPoint());
			System.out.println("SANITY IMPACT : "+unit.getSanityPoint());
			System.out.println("MORALE IMPACT : "+unit.getMoralePoint());
			staminaPoint=Math.min(staminaPoint+unit.getStaminaPoint(),maxStaminaPoint);
			moralePoint=Math.min(moralePoint+unit.getMoralePoint(),maxMoralePoint);
			sanityPoint=Math.min(sanityPoint+unit.getSanityPoint(),maxSanityPoint);
			System.out.println("SANITY POINT AFTER : "+sanityPoint);
			manaPoint=Math.min(manaPoint+unit.getManaPoint(),maxManaPoint);
		}
		for (StateEffect effect:unit.stateEffects)
		{
			addEffect(effect);
		}
		return getZeroPointTypes();
	}
	
	public ArrayList<Integer> tmpZeroPointTypes = new ArrayList<Integer>();
	public ArrayList<Integer> getZeroPointTypes()
	{
		tmpZeroPointTypes.clear();
		if (healthPoint<=0) tmpZeroPointTypes.add(ZERO_HEALTH);
		if (staminaPoint<=0) tmpZeroPointTypes.add(ZERO_STAMINA);
		if (moralePoint<=0) tmpZeroPointTypes.add(ZERO_MORALE);
		if (sanityPoint<=0) tmpZeroPointTypes.add(ZERO_SANITY);
		if (manaPoint<=0) tmpZeroPointTypes.add(ZERO_MANA);		
		return tmpZeroPointTypes;
	}

	public boolean isDead()
	{
		if (healthPoint<=0) return true;
		return false;
	}
	public boolean isExhausted()
	{
		if (staminaPoint<=0) return true;
		return false;
	}

	public boolean isNeutralized()
	{
		if (moralePoint<=0) return true;
		return false;
	}
	
	public boolean isInsane()
	{
		if (sanityPoint<=0) return true;
		return false;
	}
	
	public boolean checkLevelingAvailable()
	{
		if (experiencePoint/LEVELING_XP!=level-1)
		{
			level++;
			return true;
		}
		return false;
	}
	
	public void increaseExperience(int value)
	{
		experiencePoint+=value;
	}
	
	/**
	 * Recalculates maximum levels for an instance's attribute multipliers.
	 * @param finalize If finalize actual points are increased with the difference to new maximum.
	 */
	public void recalculateMaximums(boolean finalize)
	{
		Attributes attributes = instance.getAttributes();
		for (int i=ZERO_HEALTH; i<=ZERO_MANA; i++) {
			float m = attributes.getAttributePointMultiplier(i);
			int point = (int)(level * m);
			System.out.println("MULTIPLIER "+i+" "+m+ " "+point);
			if (i==ZERO_HEALTH)
			{
				if (finalize) {
					healthPoint+= point-origMaxHealthPoint;
					origMaxHealthPoint = point;
				}
				maxHealthPoint = point;
			} else
			if (i==ZERO_STAMINA)
			{
				if (finalize) {
					staminaPoint+= point-origMaxStaminaPoint;
					origMaxStaminaPoint = point;
				}
				maxStaminaPoint = point;
			} else
			if (i==ZERO_MORALE)
			{
				if (finalize) {
					moralePoint+= point-origMaxMoralePoint;
					origMaxMoralePoint = point;
				}
				maxMoralePoint = point;
			} else
			if (i==ZERO_SANITY)
			{
				if (finalize) {
					sanityPoint+= point-origMaxSanityPoint;
					origMaxSanityPoint = point;
					
				}
				maxSanityPoint = point;
			} else
			if (i==ZERO_MANA)
			{
				if (finalize) {
					manaPoint+= point-origMaxManaPoint;
					origMaxManaPoint = point;
				}
				maxManaPoint = point;
			}
		}
		shrinkToMax();
	}
	
	public void replenishInOneRound()
	{
		int replenishHealthPoint = Math.max(1,maxHealthPoint/GameLogicConstants.REPLENISH_ROUNDS_SLOW);
		int replenishStaminaPoint = Math.max(1,maxStaminaPoint/GameLogicConstants.REPLENISH_ROUNDS);
		int replenishMoralePoint = Math.max(1,maxMoralePoint/GameLogicConstants.REPLENISH_ROUNDS);
		int replenishSanityPoint = Math.max(1,maxSanityPoint/GameLogicConstants.REPLENISH_ROUNDS_SLOW);
		int replenishManaPoint = Math.max(1,maxManaPoint/GameLogicConstants.REPLENISH_ROUNDS_SLOW);
		
		healthPoint = Math.min(healthPoint+replenishHealthPoint, maxHealthPoint);
		staminaPoint = Math.min(staminaPoint+replenishStaminaPoint, maxStaminaPoint);
		moralePoint = Math.min(moralePoint+replenishMoralePoint, maxMoralePoint);
		sanityPoint = Math.min(sanityPoint+replenishSanityPoint, maxSanityPoint);
		manaPoint = Math.min(manaPoint+replenishManaPoint, maxManaPoint);
		
	}
	
	public void maximizeAtStart()
	{
		System.out.println("MAX:"+maxHealthPoint+" "+maxStaminaPoint);
		healthPoint = maxHealthPoint;
		staminaPoint = maxStaminaPoint;
		sanityPoint = maxSanityPoint;
		moralePoint = maxMoralePoint;
		manaPoint = maxManaPoint;
	}
	
	public void shrinkToMax()
	{
		healthPoint = Math.min(healthPoint,maxHealthPoint);
		staminaPoint = Math.min(staminaPoint,maxStaminaPoint);
		sanityPoint = Math.min(sanityPoint,maxSanityPoint);
		moralePoint = Math.min(moralePoint,maxMoralePoint);
		manaPoint = Math.min(manaPoint,maxManaPoint);
	}
	
	public void addEffect(StateEffect effect)
	{
		System.out.println("ADDING EFFECT "+instance.description.getName()+" "+effect.getClass().getSimpleName());
		for (StateEffect old:effects)
		{
			if (old.getClass()==effect.getClass())
			{
				System.out.println("Replaced Effect");
				effects.remove(old);
				break;
			}
		}
		effects.add(effect);
		
		ArrayList<StateEffect> list = new ArrayList<StateEffect>();
		list.add(effect);
		recalculateMaximums(true);
		callBackEffectChange(list,null);
	}
	
	
	public void updateEffectsUponAttackImpact()
	{
		ArrayList<StateEffect> removable = new ArrayList<StateEffect>();
		for (StateEffect effect:effects)
		{
			if (effect.updateBeingAttacked())
			{
				System.out.println("REMOVING EFFECT "+instance.description.getName()+" "+effect.getClass().getSimpleName());
				removable.add(effect);
			}
		}
		effects.removeAll(removable);
		recalculateMaximums(true);
		callBackEffectChange(null,removable);
	}
	
	public void callBackEffectChange(ArrayList<StateEffect> added, ArrayList<StateEffect> removed)
	{
		instance.notifyEffectChange(added,removed);
	}
	
	public void updateEffectsAfterSkillActForm(SkillActForm form, int powerLevel)
	{
		ArrayList<StateEffect> removable = new ArrayList<StateEffect>();
		for (StateEffect effect:effects)
		{
			if (effect.updateEffect(form, powerLevel))
			{
				System.out.println("REMOVING EFFECT "+instance.description.getName()+" "+effect.getClass().getSimpleName());
				removable.add(effect);
			}
		}
		effects.removeAll(removable);
		recalculateMaximums(true);
		callBackEffectChange(null,removable);
	}
	public void updateEffects(int seed, int round, Time time)
	{
		ArrayList<StateEffect> removable = new ArrayList<StateEffect>();
		for (StateEffect effect:effects)
		{
			if (effect.update(seed, round, time))
			{
				System.out.println("REMOVING EFFECT "+instance.description.getName()+" "+effect.getClass().getSimpleName());
				removable.add(effect);
			}
		}
		effects.removeAll(removable);
		recalculateMaximums(true);
		callBackEffectChange(null,removable);
	}
	
	public boolean isItDoableWithEffects(SkillActForm form)
	{
		for (StateEffect e:effects)
		{
			if (form==null)
			{
				if (!e.canDoUse()) return false;
			} else
			{
				if (!e.canDoActForm(form)) return false;
			}
		}
		return true;
	}
	public String getBlockingEffectText(SkillActForm form)
	{
		for (StateEffect e:effects)
		{
			if (form==null)
			{
				if (!e.canDoUse()) return e.getInabilityText();
			} else
			{
				if (!e.canDoActForm(form)) return e.getInabilityText();
			}
		}
		return "";
	}
	
	public ArrayList<StateEffect> getStateEffects()
	{
		return effects;
	}
	
	public Attributes getStateEffectAttributesSum()
	{
		Attributes a = null;
		for (StateEffect e:effects)
		{
			Attributes c = e.getCurrentAttributes();
			if (c==null) continue;
			if (a==null)
			{
				a = c;
			} else
			{
				a.appendAttributes(c);
			}
		}
		return a;
	}
	public Resistances getStateEffectResistancesSum()
	{
		Resistances a = null;
		for (StateEffect e:effects)
		{
			Resistances c = e.getCurrentResistances();
			if (c==null) continue;
			if (a==null)
			{
				a = c;
			} else
			{
				a.appendResistances(c);
			}
		}
		return a;
	}
	

}
