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

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.skill.actform.Swing;
import org.jcrpg.world.ai.abs.skill.actform.Thrust;

public class LargeBlades extends SkillBase implements TurnActSkill  {
	public int getUseRangeInLineup() {
		return -1;
	}
	public class SliceInTwo extends SkillActForm
	{

		public SliceInTwo(SkillBase skill) {
			super(skill);
			isBodyPartTargetted = true;
			skillRequirementLevel = 50;
			atomicEffect = -10;
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH,-10);
			usedPointsAndLevels.put(EFFECTED_POINT_STAMINA,-10);
		}

		@Override
		public String getSound() {
			return null;
		}
	}

	public LargeBlades()
	{
		needsInventoryItem = true;
		Thrust t = new Thrust(this,1.5f);
		t.contraResistencies.add(FantasyResistances.RESIST_PIERCE);
		actForms.add(t);
		actForms.add(new Swing(this,1.5f));
	}

	@Override
	public ArrayList<Class<? extends SkillBase>> getContraSkillTypes() {
		return SkillGroups.contraCloseCombatSkills;
	}

}
