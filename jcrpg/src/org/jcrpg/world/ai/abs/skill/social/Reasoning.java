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

package org.jcrpg.world.ai.abs.skill.social;

import org.jcrpg.world.ai.abs.skill.EncounterSkill;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;

public class Reasoning extends SkillBase implements EncounterSkill, TurnActSkill {
	
	public class LogicalSolutions extends SkillActForm
	{
		public LogicalSolutions(SkillBase skill) {
			super(skill);
			skillRequirementLevel = 0;
		}

		@Override
		public String getSound() {
			return null;
		}
	}
	
	public Reasoning()
	{
		actForms.add(new LogicalSolutions(this));
	}

}
