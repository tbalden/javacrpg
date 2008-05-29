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

import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;

public class HitDontCut extends SkillActForm {
	public HitDontCut(SkillBase skill, float multiplier) {
		super(skill);
		atomicEffect = 0;
		targetType = TARGETTYPE_LIVING_MEMBER;
		effectTypesAndLevels.put(EFFECTED_POINT_STAMINA,-(int)(7*multiplier));
		usedPointsAndLevels.put(EFFECTED_POINT_STAMINA, -(int)(3*multiplier));
	}

}
