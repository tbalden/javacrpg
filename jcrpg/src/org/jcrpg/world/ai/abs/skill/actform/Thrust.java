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
package org.jcrpg.world.ai.abs.skill.actform;

import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;

public class Thrust extends SkillActForm
{
	public Thrust(SkillBase skill, float multiplier) {
		super(skill);
		isBodyPartTargetted = true;
		animationType = MovingModelAnimDescription.ANIM_ATTACK_LOWER;
		atomicEffect = (int)(-5*multiplier);
		targetType = TARGETTYPE_LIVING_MEMBER;
		effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, -(int)(2*multiplier));
		effectTypesAndLevels.put(EFFECTED_POINT_STAMINA,-(int)(1*multiplier));
		usedPointsAndLevels.put(EFFECTED_POINT_STAMINA, -(int)(2*multiplier));
		contraAttributes.add(FantasyAttributes.CONCENTRATION);
		contraAttributes.add(FantasyAttributes.SPEED);
	}

	@Override
	public String getSound() {
		return "thrust";
	}
	
	
}

