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

import org.jcrpg.threed.jme.program.impl.FireArrow;
import org.jcrpg.threed.jme.program.impl.IceArrow;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.abs.state.StateEffectInitParams;
import org.jcrpg.world.ai.abs.state.effect.ElementalResistance;

public class Elementarism extends SkillBase implements TurnActSkill {
	public int getUseRangeInLineup() {
		return -1;
	}
	public class ElementalShield extends SkillActForm
	{

		public ElementalShield(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(+5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			
			StateEffectInitParams elResParam = new StateEffectInitParams();
			elResParam.baseDuration=4;
			elResParam.basePower=1;
			elResParam.durationType=StateEffect.DURATION_TYPE_TURN_ACT_ROUNDS;
			elResParam.type = ElementalResistance.class;
			stateEffectsAndLevels.add(elResParam);
			
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(8));
			proAttributes.add(FantasyAttributes.PSYCHE);
			proAttributes.add(FantasyAttributes.CONCENTRATION);
		}
		
		@Override
		public String getSound() {
			return "heal"; // TODO better sound
		}
		
		EffectProgram p = new EffectProgram(IceArrow.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return p;
		}

	}
	
	public class BurningSparks extends SkillActForm
	{

		
		public BurningSparks(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(-5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, -(int)(40));
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(5));
			proAttributes.add(FantasyAttributes.PSYCHE);
			contraAttributes.add(FantasyAttributes.CONCENTRATION);
			contraAttributes.add(FantasyAttributes.PSYCHE);
			contraResistencies.add(FantasyResistances.RESIST_HEAT);
		}

		@Override
		public String getSound() {
			return "fireburn";
		}
		//SimpleModel effectProgramModel = new SimpleModel("models/item/ammo/Arrow1.3ds",null,false);

		EffectProgram p = new EffectProgram(FireArrow.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return p;
		}
		
	}

	public class IceBeam extends SkillActForm
	{

		public IceBeam(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(-5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, -(int)(40));
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(5));
			proAttributes.add(FantasyAttributes.PSYCHE);
			contraAttributes.add(FantasyAttributes.CONCENTRATION);
			contraAttributes.add(FantasyAttributes.PSYCHE);
			contraResistencies.add(FantasyResistances.RESIST_COLD);
		}

		@Override
		public String getSound() {
			return "icespell";
		}
		
		EffectProgram p = new EffectProgram(IceArrow.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return p;
		}
		
	}
	
	public Elementarism()
	{
		actForms.add(new BurningSparks(this));
		actForms.add(new IceBeam(this));
		actForms.add(new ElementalShield(this));
	}
	
	
}
