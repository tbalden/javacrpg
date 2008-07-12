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

import org.jcrpg.threed.jme.program.impl.IceArrow;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.abs.state.StateEffectInitParams;
import org.jcrpg.world.ai.abs.state.effect.PhysicalResistance;

public class CelestialMagic extends SkillBase implements TurnActSkill {
	public int getUseRangeInLineup() {
		return -1;
	}

	public class ThickSkin extends SkillActForm
	{

		public ThickSkin(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(+5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			
			StateEffectInitParams elResParam = new StateEffectInitParams();
			elResParam.baseDuration=4;
			elResParam.basePower=1;
			elResParam.durationType=StateEffect.DURATION_TYPE_TURN_ACT_ROUNDS;
			elResParam.type = PhysicalResistance.class;
			stateEffectsAndLevels.add(elResParam);
			
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(8));
			proAttributes.add(FantasyAttributes.PSYCHE);
			proAttributes.add(FantasyAttributes.PIETY);
		}
		
		@Override
		public String getSound() {
			return null;
		}
		
		EffectProgram p = new EffectProgram(IceArrow.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return p;
		}

	}

	
	public class MinorHeal extends SkillActForm
	{

		public MinorHeal(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(+5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, +(int)(5));
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(5));
		}

		@Override
		public String getSound() {
			return null;
		}
		
		//EffectProgram p = new EffectProgram(FireArrow.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return null;
		}
		
	}

	public CelestialMagic()
	{
		actForms.add(new MinorHeal(this));
		actForms.add(new ThickSkin(this));
	}
	
}
