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

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.Weapon;

public class EvaluatorBase {

	/**
	 * Returns all times of an act represented by it's speed time.
	 * @param seed
	 * @param instance
	 * @param skill
	 * @param form
	 * @param obj
	 * @return
	 */
	public static float[] evaluateActFormTimesWithSpeed(int seed, EntityMemberInstance instance, SkillInstance skill, SkillActForm form, ObjInstance obj)
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
