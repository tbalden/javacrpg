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
import org.jcrpg.world.ai.abs.skill.magical.Alchemy;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.abs.skill.magical.Mentalism;
import org.jcrpg.world.ai.abs.skill.mental.HigherMusic;
import org.jcrpg.world.ai.abs.skill.mental.Mythology;
import org.jcrpg.world.ai.abs.skill.physical.HideAndSneak;
import org.jcrpg.world.ai.abs.skill.physical.LocksAndTraps;
import org.jcrpg.world.ai.profession.HumanoidProfessional;
import org.jcrpg.world.object.combat.blade.Dagger;
import org.jcrpg.world.object.magical.music.LuteOfDreams;

public class Bard extends HumanoidProfessional {
	public Bard()
	{
		super();
		generationNewInstanceObjects.add(Dagger.class);
		generationNewInstanceObjects.add(LuteOfDreams.class);
		attrMinLevels.minimumLevels.put(FantasyAttributes.CHARISMA, 14);
		attrMinLevels.minimumLevels.put(FantasyAttributes.CONCENTRATION, 12);
		attrMinLevels.minimumLevels.put(FantasyAttributes.PIETY, 10);
		addMinorSkill(Alchemy.class);
		addMinorSkill(Elementarism.class);
		addMinorSkill(Mentalism.class);
		addSkill(HigherMusic.class);
		addMajorSkill(Mythology.class);
		addMinorSkill(HideAndSneak.class);
		addMinorSkill(LocksAndTraps.class);
	}

}
