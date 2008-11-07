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

import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;

/**
 * Fighting with bites - like dog/wolf and such.
 * @author pali
 *
 */
public class Throwing extends SkillBase implements TurnActSkill  {
	public int getUseRangeInLineup() {
		return -1;
	}

	public class StraightThrow extends SkillActForm
	{

		public StraightThrow(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_THROW;
			atomicEffect = (int)(-5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, -(int)(5));
			usedPointsAndLevels.put(EFFECTED_POINT_STAMINA, -(int)(2));
			contraAttributes.add(FantasyAttributes.CONCENTRATION);
			contraAttributes.add(FantasyAttributes.SPEED);
		}

		@Override
		public String getSound() {
			return null;
		}
	}
	
	public Throwing()
	{
		needsInventoryItem = true;
		actForms.add(new StraightThrow(this));
	}
	
}
