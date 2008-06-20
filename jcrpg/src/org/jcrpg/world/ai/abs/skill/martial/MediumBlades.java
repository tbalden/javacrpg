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

package org.jcrpg.world.ai.abs.skill.martial;

import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.skill.actform.HitDontCut;
import org.jcrpg.world.ai.abs.skill.actform.Swing;
import org.jcrpg.world.ai.abs.skill.actform.Thrust;

public class MediumBlades extends SkillBase implements TurnActSkill  {
	
	public class WhirlwindCut extends SkillActForm
	{

		public WhirlwindCut(SkillBase skill) {
			super(skill);
			skillRequirementLevel = 50;
			atomicEffect = -5;
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH,-5);
			usedPointsAndLevels.put(EFFECTED_POINT_STAMINA,-5);
		}

		@Override
		public String getSound() {
			return null;
		}
	}
	
	public MediumBlades()
	{
		needsInventoryItem = true;
		actForms.add(new Thrust(this,1.5f));
		actForms.add(new Swing(this,1f));
		actForms.add(new WhirlwindCut(this));
		actForms.add(new HitDontCut(this,1f));
	}

}
