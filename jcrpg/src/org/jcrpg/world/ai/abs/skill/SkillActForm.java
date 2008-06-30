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
package org.jcrpg.world.ai.abs.skill;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.state.EntityMemberState;

/**
 * Base class for act forms of a specific skill. Like spells, social acts, hide modes etc.
 * @author illes
 *
 */
public abstract class SkillActForm
{
	public SkillBase skill;
	public String id;
	
	/**
	 * Describes what kind of effect does it have in it core: destructive or constructive, from
	 *  negative -10 to positive +10.
	 */
	public int atomicEffect = 0;
	
	public String animationType = MovingModelAnimDescription.ANIM_IDLE;
	
	public static final int TARGETTYPE_NONE = -1;
	public static final int TARGETTYPE_LIVING_MEMBER = 0;
	public static final int TARGETTYPE_LIVING_FRAGMENT = 1;
	public static final int TARGETTYPE_LIVING_ENTITY = 1;
	public static final int TARGETTYPE_LIVING_ALL = 2;
	
	public static final int EFFECTED_POINT_HEALTH = 0;
	public static final int EFFECTED_POINT_STAMINA = 1;
	public static final int EFFECTED_POINT_MORALE = 2;
	public static final int EFFECTED_POINT_SANITY = 3;
	public static final int EFFECTED_POINT_MANA = 4;
	
	/**
	 * What effect on target it has if any...
	 */
	public HashMap<Integer,Integer> effectTypesAndLevels = new HashMap<Integer, Integer>();
	
	/**
	 * Using the skillform costs...
	 */
	public HashMap<Integer,Integer> usedPointsAndLevels = new HashMap<Integer, Integer>();
	
	/**
	 * Attributes that strengthen defense against this skill.
	 */
	public ArrayList<String> contraAttributes = new ArrayList<String>();
	/**
	 * Resistencies that strengthen defense against this skill.
	 */
	public ArrayList<String> contraResistencies = new ArrayList<String>();
	
	
	/**
	 * The effected target that can be chosen for the skill.
	 */
	public int targetType = TARGETTYPE_NONE;
	
	/**
	 * Determines what skill level is needed for this act form.
	 */
	public int skillRequirementLevel = 0;
	public SkillActForm(SkillBase skill)
	{
		this.skill = skill;
	}
	
	/**
	 * Get sound of skillactform use.
	 * @return
	 */
	public abstract String getSound();
	
	/**
	 * Returns (3d) effect description for skill use.
	 * @return
	 */
	public EffectProgram getEffectProgram()
	{
		return null;
	}
	
	public String getName()
	{
		return Language.v("skillActForm."+this.getClass().getSimpleName());
	}
	
	public boolean canBeDoneByMember(EntityMemberInstance instance)
	{
		EntityMemberState state = instance.memberState;
		Integer neededHealthPoint = usedPointsAndLevels.get(EFFECTED_POINT_HEALTH);
		Integer neededStaminaPoint = usedPointsAndLevels.get(EFFECTED_POINT_STAMINA);
		Integer neededSanityPoint = usedPointsAndLevels.get(EFFECTED_POINT_SANITY);
		Integer neededMoralePoint = usedPointsAndLevels.get(EFFECTED_POINT_MORALE);
		Integer neededManaPoint = usedPointsAndLevels.get(EFFECTED_POINT_MANA);
		
		if (neededHealthPoint!=null)
		{
			if (neededHealthPoint+state.healthPoint<0) return false;
		}
		if (neededStaminaPoint!=null)
		{
			if (neededStaminaPoint+state.staminaPoint<0) return false;
		}
		if (neededSanityPoint!=null)
		{
			if (neededSanityPoint+state.sanityPoint<0) return false;
		}
		if (neededMoralePoint!=null)
		{
			if (neededMoralePoint+state.moralePoint<0) return false;
		}
		if (neededManaPoint!=null)
		{
			if (neededManaPoint+state.manaPoint<0) return false;
		}
		
		return true;
	}
	
}
