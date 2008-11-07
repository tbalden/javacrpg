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

package org.jcrpg.world.ai.abs.skill.martial;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;

public class Wrestling extends SkillBase implements TurnActSkill  {
	public int getUseRangeInLineup() {
		return 0;
	}

	public class KillerGrip extends SkillActForm
	{
		public KillerGrip(SkillBase skill) {
			super(skill);
			isBodyPartTargetted = true;
			skillRequirementLevel = 0;
			atomicEffect = -10;
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, -5);
			usedPointsAndLevels.put(EFFECTED_POINT_STAMINA, -3);
		}

		@Override
		public String getSound() {
			return null;
		}
	}

	public class Detain extends SkillActForm
	{

		public Detain(SkillBase skill) {
			super(skill);
			isBodyPartTargetted = true;
			skillRequirementLevel = 0;
			atomicEffect = 0;
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_STAMINA, -5);
			usedPointsAndLevels.put(EFFECTED_POINT_STAMINA, -4);
		}

		@Override
		public String getSound() {
			return null;
		}
	}

	public Wrestling()
	{
		actForms.add(new KillerGrip(this));
		actForms.add(new Detain(this));
	}
	@Override
	public ArrayList<Class<? extends SkillBase>> getContraSkillTypes() {
		return SkillGroups.contraCloseCombatSkills;
	}

}
