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

package org.jcrpg.world.ai.abs.skill.magical;

import org.jcrpg.threed.jme.program.impl.BlackFire;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;

public class Demonology extends SkillBase implements TurnActSkill {
	public int getUseRangeInLineup() {
		return -1;
	}

	public class SummonDemon extends SkillActForm
	{
		public SummonDemon(SkillBase skill) {
			super(skill);
			skillRequirementLevel = 0;
		}

		@Override
		public String getSound() {
			return null;
		}
	}
	
	
	public class DemonBreath extends SkillActForm
	{
		
		public DemonBreath(SkillBase skill) {
			super(skill);
			animationType = MovingModelAnimDescription.ANIM_CAST;
			atomicEffect = (int)(-5);
			targetType = TARGETTYPE_LIVING_MEMBER;
			effectTypesAndLevels.put(EFFECTED_POINT_HEALTH, -(int)(20));
			effectTypesAndLevels.put(EFFECTED_POINT_SANITY, -(int)(20));
			usedPointsAndLevels.put(EFFECTED_POINT_MANA, -(int)(5));
			proAttributes.add(FantasyAttributes.PSYCHE);
			contraAttributes.add(FantasyAttributes.PIETY);
			contraAttributes.add(FantasyAttributes.PSYCHE);
			contraResistencies.add(FantasyResistances.RESIST_EVIL);
		}

		@Override
		public String getSound() {
			return "fireburn";
		}
		//SimpleModel effectProgramModel = new SimpleModel("models/item/ammo/Arrow1.3ds",null,false);

		EffectProgram p = new EffectProgram(BlackFire.class);
		
		@Override
		public EffectProgram getEffectProgram() {
			return p;
		}
		
	}
	
	public Demonology()
	{
		//actForms.add(new SummonDemon(this));
		actForms.add(new DemonBreath(this));
	}

}
