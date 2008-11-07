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

import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.TurnActSkill;
import org.jcrpg.world.ai.abs.skill.actform.Thrust;

/**
 * staffs/wands and clubs too.
 * @author pali
 *
 */
public class StaffsAndWands extends SkillBase implements TurnActSkill  {
	public int getUseRangeInLineup() {
		return -1;
	}

	public StaffsAndWands()
	{
		needsInventoryItem = true;
		actForms.add(new Thrust(this,1f));
	}
	@Override
	public ArrayList<Class<? extends SkillBase>> getContraSkillTypes() {
		return SkillGroups.contraCloseCombatSkills;
	}

}
