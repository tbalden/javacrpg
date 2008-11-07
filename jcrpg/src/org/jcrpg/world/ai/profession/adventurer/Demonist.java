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
import org.jcrpg.world.ai.abs.skill.magical.CelestialMagic;
import org.jcrpg.world.ai.abs.skill.magical.Demonology;
import org.jcrpg.world.ai.abs.skill.mental.Mythology;
import org.jcrpg.world.ai.profession.HumanoidProfessional;
import org.jcrpg.world.object.combat.blade.Dagger;

public class Demonist extends HumanoidProfessional {
	public Demonist()
	{
		super();
		generationNewInstanceObjects.add(Dagger.class);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PIETY, 12);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PSYCHE, 14);
		addMajorSkill(Demonology.class);
		addMinorSkill(CelestialMagic.class);
		addMajorSkill(Mythology.class);
	}

}
