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

package org.jcrpg.world.ai.abs.skill.mental.methodology;

import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.abs.state.StateEffectInitParams;
import org.jcrpg.world.ai.abs.state.effect.Haste;

public class Strategy extends SkillBase implements TurnActSkill {
	
	public class QuickArrangement extends SkillActForm
	{
		public QuickArrangement(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = 5;
			skillRequirementLevel = 0;
			targetType = TARGETTYPE_LIVING_GROUP;
			StateEffectInitParams quickParam = new StateEffectInitParams();
			quickParam.baseDuration=2;
			quickParam.basePower=1;
			quickParam.durationType=StateEffect.DURATION_TYPE_TURN_ACT_ROUNDS;
			quickParam.type = Haste.class;
			stateEffectsAndLevels.add(quickParam);
			
			proAttributes.add(FantasyAttributes.PSYCHE);
			usedPointsAndLevels.put(EFFECTED_POINT_SANITY, -5);
		}

		@Override
		public String getSound() {
			return "heal";
		}
	}
	
	public int getUseRangeInLineup() {
		return -1;
	}
	
	public Strategy()
	{
		actForms.add(new QuickArrangement(this));
	}

}
