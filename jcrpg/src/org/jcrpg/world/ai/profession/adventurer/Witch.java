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

package org.jcrpg.world.ai.profession.adventurer;

import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.magical.Alchemy;
import org.jcrpg.world.ai.abs.skill.magical.Demonology;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.abs.skill.magical.Mentalism;
import org.jcrpg.world.ai.abs.skill.mental.Mythology;
import org.jcrpg.world.ai.abs.skill.social.Cheating;
import org.jcrpg.world.ai.profession.HumanoidProfessional;

public class Witch extends HumanoidProfessional {
	public Witch()
	{
		super();
		genderNeed = GENDER_FEMALE;
		attrMinLevels.minimumLevels.put(FantasyAttributes.CHARISMA, 13);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PSYCHE, 13);
		addMinorSkill(Demonology.class);
		addMinorSkill(Alchemy.class);
		addMinorSkill(Elementarism.class);
		addMinorSkill(Mentalism.class);
		addMinorSkill(Cheating.class);
		addMinorSkill(Mythology.class);
	}

}
