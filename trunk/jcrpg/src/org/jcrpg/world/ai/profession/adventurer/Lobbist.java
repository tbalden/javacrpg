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

package org.jcrpg.world.ai.profession.adventurer;

import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.mental.Ecology;
import org.jcrpg.world.ai.abs.skill.mental.Languages;
import org.jcrpg.world.ai.abs.skill.social.Politics;
import org.jcrpg.world.ai.profession.HumanoidProfessional;

public class Lobbist extends HumanoidProfessional {

	public Lobbist()
	{
		super();
		attrMinLevels.minimumLevels.put(FantasyAttributes.CHARISMA, 13);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PSYCHE, 13);
		addMajorSkill(Politics.class);
		addMinorSkill(Languages.class);
		addMinorSkill(Ecology.class);
	}
}
