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
package org.jcrpg.world.object.combat.bow;

import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.Bows;


public class ShortBow extends Bow {
	
	public ShortBow()
	{
		super();
		icon = "weapon/shortbow.png";
		requirementSkillAndLevel = new SkillInstance(Bows.class,0);
	}

	public float getAttackMultiplicator() {
		return 1.2f;
	}

	public float getDefenseMultiplicator() {
		return 0.1f;
	}

	public String getHitSound() {
		return "weapon/arrow_hit";
	}

	public int getMaxDamage() {
		return 5;
	}

	public String getMissSound() {
		return null;
	}

	public int getSpeed() {
		return 5;
	}

	public String getDamageTypeResistance() {
		return FantasyResistances.RESIST_PIERCE;
	}

}
