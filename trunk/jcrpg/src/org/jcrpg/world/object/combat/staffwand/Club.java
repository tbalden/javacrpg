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
package org.jcrpg.world.object.combat.staffwand;

import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.StaffsAndWands;
import org.jcrpg.world.object.Obj;
import org.jcrpg.world.object.Weapon;

public class Club extends Obj implements Weapon {
	
	public Club()
	{
		icon = "weapon/quarterstaff.png";
		requirementSkillAndLevel = new SkillInstance(StaffsAndWands.class,0);
	}

	public float getAttackMultiplicator() {
		return 1.0f;
	}

	public float getDefenseMultiplicator() {
		return 1.0f;
	}

	public int getMaxDamage() {
		return 7;
	}

	public int getSpeed() {
		return 7;
	}

	public String getHitSound() {		
		return "weapon/wooden_hit";
	}
	
	public String getMissSound() {		
		return null;
	}
	
}