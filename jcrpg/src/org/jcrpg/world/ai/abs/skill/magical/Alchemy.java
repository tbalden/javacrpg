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

package org.jcrpg.world.ai.abs.skill.magical;

import org.jcrpg.threed.jme.program.impl.FumeCloud;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.skill.WorkSkill;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.abs.state.StateEffectInitParams;
import org.jcrpg.world.ai.abs.state.effect.Sleep;

public class Alchemy extends SkillBase implements TurnActSkill, WorkSkill {

	public class FumesOfTwilight extends SkillActForm
	{

		public FumesOfTwilight(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(-2);
			targetType = TARGETTYPE_LIVING_GROUP;
			
			StateEffectInitParams sleepParam = new StateEffectInitParams();
			sleepParam.baseDuration=2;
			sleepParam.basePower=1;
			sleepParam.durationType=StateEffect.DURATION_TYPE_TURN_ACT_ROUNDS;
			sleepParam.type = Sleep.class;
			stateEffectsAndLevels.add(sleepParam);
			
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(5));
			proAttributes.add(FantasyAttributes.PSYCHE);
			contraAttributes.add(FantasyAttributes.CONCENTRATION);
			contraAttributes.add(FantasyAttributes.PSYCHE);
			contraResistencies.add(FantasyResistances.RESIST_CHEMICAL);
		}

		@Override
		public String getSound() {
			return "chime";
		}
		
		EffectProgram p = new EffectProgram(FumeCloud.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return p;
		}
		
	}
	
	public Alchemy()
	{
		actForms.add(new FumesOfTwilight(this));
	}

	public int getUseRangeInLineup() {
		return -1;
	}

}
