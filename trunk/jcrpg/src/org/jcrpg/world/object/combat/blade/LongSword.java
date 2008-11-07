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
package org.jcrpg.world.object.combat.blade;

import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.MediumBlades;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.Weapon;

public class LongSword extends Obj implements Weapon {
	
	public LongSword()
	{
		useRangeInLineup = 1;
		icon = "weapon/longsword.png";
		requirementSkillAndLevel = new SkillInstance(MediumBlades.class,0);
	}

	public float getAttackMultiplicator() {
		return 1.1f;
	}

	public float getDefenseMultiplicator() {
		return 0.9f;
	}

	public int getMaxDamage() {
		return 10;
	}

	public int getSpeed() {
		return 7;
	}

	public String getHitSound() {
		return "weapon/pierce";
	}
	public String getMissSound() {
		return "weapon/clang";
	}
	public String getDamageTypeResistance() {
		return FantasyResistances.RESIST_PIERCE;
	}
	
}
