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
import org.jcrpg.world.ai.abs.skill.martial.Bows;
import org.jcrpg.world.ai.abs.skill.martial.Crossbows;
import org.jcrpg.world.ai.abs.skill.martial.MediumBlades;
import org.jcrpg.world.ai.abs.skill.martial.ShortBlades;
import org.jcrpg.world.ai.abs.skill.martial.Throwing;
import org.jcrpg.world.ai.abs.skill.physical.Climbing;
import org.jcrpg.world.ai.abs.skill.physical.LocksAndTraps;
import org.jcrpg.world.ai.abs.skill.physical.PickPocket;
import org.jcrpg.world.ai.profession.HumanoidProfessional;

public class Thief extends HumanoidProfessional {

	public Thief()
	{
		super();
		attrMinLevels.minimumLevels.put(FantasyAttributes.SPEED, 12);
		attrMinLevels.minimumLevels.put(FantasyAttributes.CONCENTRATION, 14);
		addMajorSkill(LocksAndTraps.class);
		addMajorSkill(PickPocket.class);
		addMajorSkill(Throwing.class);
		addMinorSkill(Bows.class);
		addMinorSkill(Crossbows.class);
		addMinorSkill(MediumBlades.class);
		addMinorSkill(ShortBlades.class);
		addMinorSkill(Climbing.class);
	}

}
