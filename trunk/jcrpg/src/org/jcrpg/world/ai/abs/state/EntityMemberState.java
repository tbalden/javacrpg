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

import org.jcrpg.game.logic.ImpactUnit;

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
	
	/**
	 * Applies impact unit and return zero reached point type list.
	 * @param unit
	 * @return
	 */
	public ArrayList<Integer> applyImpactUnit(ImpactUnit unit)
	{
		healthPoint+=unit.getHealthPoint();
		staminaPoint+=unit.getStaminaPoint();
		moralePoint+=unit.getMoralePoint();
		sanityPoint+=unit.getSanityPoint();
		manaPoint+=unit.getManaPoint();
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
		System.out.println("HEALTHPOINT: "+healthPoint);
		return tmpZeroPointTypes;
	}

	public boolean isDead()
	{
		if (healthPoint<=0) return true;
		return false;
	}
	public boolean isUnconsious()
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
		if (experiencePoint/LEVELING_XP!=level)
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
	

}
