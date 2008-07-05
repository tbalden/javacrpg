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
import org.jcrpg.world.ai.abs.skill.mental.MagicalLore;
import org.jcrpg.world.ai.abs.skill.mental.Mythology;
import org.jcrpg.world.ai.profession.HumanoidProfessional;
import org.jcrpg.world.object.armor.artifact.CrockHide;
import org.jcrpg.world.object.combat.blade.artifact.WolfTooth;
import org.jcrpg.world.object.combat.staffwand.QuarterStaff;

public class Mage extends HumanoidProfessional {
	public Mage()
	{
		super();
		generationNewInstanceObjects.add(CrockHide.class);
		generationNewInstanceObjects.add(WolfTooth.class);
		generationNewInstanceObjects.add(QuarterStaff.class);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PSYCHE, 14);
		attrMinLevels.minimumLevels.put(FantasyAttributes.CONCENTRATION, 12);
		addMajorSkill(Elementarism.class);
		addMinorSkill(Alchemy.class);
		addMajorSkill(MagicalLore.class);
		addMinorSkill(Mythology.class);
	}

}
