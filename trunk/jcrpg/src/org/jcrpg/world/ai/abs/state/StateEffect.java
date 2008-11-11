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
package org.jcrpg.world.ai.abs.state;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.time.Time;

/**
 * A member's state created by a spell or skill like sleep/berserk/poisoned.
 * Override methods in extension, and fields in constructor - use impactForTurn/attributes/resistances/icon etc.
 * @author illes
 *
 */
public abstract class StateEffect {

	public static final int DURATION_TYPE_TURN_ACT_ROUNDS = 0;
	public static final int DURATION_TYPE_NORMAL_TIME = 1;
	
	public int durationType = DURATION_TYPE_TURN_ACT_ROUNDS;
	
	public int startRound = -1;
	public Time startTime = null;
	
	public int lastUpdateRound;
	public int lastUpdateTime;
	
	public EntityMemberState targetState;
	public int powerLevel = -1;
	public int durationMultiplier = 0;
	
	public int normalDuration = 0;
	
	/**
	 * Skills that can cancel effect
	 */
	public ArrayList<Class<? extends SkillBase>> saverSkills = new ArrayList<Class<? extends SkillBase>>();
	/**
	 * The resistance types that can cancel effect.
	 */
	public ArrayList<String> saverResistances = new ArrayList<String>();
	
	public Attributes effectAttributes = null;
	public Resistances effectResistances = null;
	
	public StateEffect()
	{
		
	}
	
	public void initStateEffect(int startRound, Time startTime, int duration, int powerLevel, int durationMultiplier, EntityMemberState targetState)
	{
		this.startRound = startRound;
		this.startTime = startTime;
		this.targetState = targetState;
		this.powerLevel = powerLevel;
		this.durationMultiplier = durationMultiplier;
		
		normalDuration = duration;
		
		effectAttributes = getBaseAttributes();
		effectResistances = getBaseResistances();
		
	}
	
	public String getLeftDuration(int round, Time time)
	{
		if (durationType==DURATION_TYPE_TURN_ACT_ROUNDS)
		{
			return ""+(normalDuration-(round-startRound))+" t.";
		} else
		{
			return ""+(normalDuration-startTime.diffSeconds(time))+" s."; 
		}
	}
	
	
	public boolean update(int seed, int round, Time time)
	{
		ImpactUnit i = null; 
		boolean ending = false;
		if (durationType==DURATION_TYPE_TURN_ACT_ROUNDS)
		{
			if (round!=lastUpdateRound) {
				lastUpdateRound = round;
				ending = updateEffect(seed);
				if (round-startRound>normalDuration) ending = true;
				if (!ending) i = impactForTurn();
				if (i!=null)
					targetState.applyImpactUnit(i);
			}
		}
		if (durationType==DURATION_TYPE_NORMAL_TIME)
		{
			if (lastUpdateTime!=time.getTimeInInt()) {
				ending = updateEffect(seed);
				if (startTime.diffSeconds(time)>normalDuration) ending = true;
				
				ImpactUnit i2 = null;
				if (!ending) i2 = impactForTime();
				if (i!=null) 
				{
					i.append(i2,false);
				} else
				{
					i = i2;
				}
				
				if (i!=null)
					targetState.applyImpactUnit(i);
			}
		}
		
		return ending;		
	}
	
	/**
	 * Updates for new time slice, adding effects etc.
	 * @return true if effect ended.
	 */
	private boolean updateEffect(int seed)
	{
		return checkResistances(seed);
	}
	
	private boolean checkResistances(int seed)
	{
		Resistances r = targetState.instance.getResistances();
		int divider = saverResistances.size();
		
		if (divider==0) return false;
		
		int sum = 0;
		for (String res:saverResistances)
		{
			sum+=r.getResistance(res);
		}
		sum = sum / divider;
		int random = HashUtil.mixPercentage(1, 2, seed+targetState.instance.getNumericId()+targetState.instance.instance.getNumericId());
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("EFFECT RESISTANCE => "+random+" < "+sum);
		if (random<sum) return true;
		return false;
	}
	
	/**
	 * Update when some act form is being done on member - like dispel etc.
	 * @param form
	 * @param powerLevel
	 * @return
	 */
	public boolean updateEffect(SkillActForm form, int powerLevel)
	{
		return false;
	}
	
	/**
	 * Should be called when member is attacked/hit - for awaking from sleep etc.
	 * @return
	 */
	public boolean updateBeingAttacked()
	{
		return false;
	}

	/**
	 * Effect should determine if something can be done by effected member.
	 * @param form
	 * @return
	 */
	public abstract boolean canDoActForm(SkillActForm form);
	
	/**
	 * Tells if Use object is doable with this.
	 * @return
	 */
	public abstract boolean canDoUse();
	
	/**
	 * impact for a given turn. With this you can add point modifications / turn like burning flames -> -HP.
	 * @return
	 */
	public abstract ImpactUnit impactForTurn();
	
	/**
	 * Impact for a new time period. With this you can add point modifications / time period, like burning flames -> -HP.
	 * @return
	 */
	public abstract ImpactUnit impactForTime();
	
	public abstract org.jcrpg.world.ai.abs.attribute.Attributes getBaseAttributes();
	public abstract Resistances getBaseResistances();
	
	/**
	 * Path to icon image of effect.
	 * @return the path.
	 */
	public abstract String getIcon();
	
	public String getIconFilePath()
	{
		return "./data/icons/states/"+getIcon();
	}
	
	
	public String getName()
	{
		return Language.v("stateEffect."+this.getClass().getSimpleName());
	}

	public String getAdditionText()
	{
		return Language.v("stateEffect.sentence."+this.getClass().getSimpleName());
	}

	public String getInabilityText()
	{
		return Language.v("stateEffect.inability."+this.getClass().getSimpleName());
	}

	public String getRemovalText()
	{
		return Language.v("stateEffect.removal."+this.getClass().getSimpleName());
	}

	public Attributes getCurrentAttributes()
	{
		// TODO update them with power level
		return effectAttributes;
	}
	public Resistances getCurrentResistances()
	{
		// TODO update them with power level
		return effectResistances;
	}

}
