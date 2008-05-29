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
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.abs.skill.mental.Ecology;
import org.jcrpg.world.ai.abs.skill.mental.MagicalLore;
import org.jcrpg.world.ai.profession.HumanoidProfessional;
import org.jcrpg.world.object.combat.staffwand.QuarterStaff;

public class Alchemist extends HumanoidProfessional {
	public Alchemist()
	{
		super();
		characterGenerationNewPartyObjects.add(QuarterStaff.class);
		attrMinLevels.addLevel(FantasyAttributes.PSYCHE, 12);
		attrMinLevels.addLevel(FantasyAttributes.CONCENTRATION, 14);
		addMajorSkill(Alchemy.class);
		addMinorSkill(Elementarism.class);
		addMajorSkill(Ecology.class);
		addMinorSkill(MagicalLore.class);
	}

}
