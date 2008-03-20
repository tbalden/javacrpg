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

package org.jcrpg.world.ai.profession;

import org.jcrpg.world.ai.abs.skill.martial.Bows;
import org.jcrpg.world.ai.abs.skill.martial.MediumBlades;
import org.jcrpg.world.ai.abs.skill.martial.ShortBlades;
import org.jcrpg.world.ai.abs.skill.martial.StaffsAndWands;
import org.jcrpg.world.ai.abs.skill.martial.Throwing;
import org.jcrpg.world.ai.abs.skill.martial.Wrestling;
import org.jcrpg.world.ai.abs.skill.mental.Ecology;
import org.jcrpg.world.ai.abs.skill.mental.Languages;
import org.jcrpg.world.ai.abs.skill.mental.Mythology;
import org.jcrpg.world.ai.abs.skill.physical.Climbing;
import org.jcrpg.world.ai.abs.skill.physical.Hiding;
import org.jcrpg.world.ai.abs.skill.physical.LocksAndTraps;
import org.jcrpg.world.ai.abs.skill.physical.Swimming;
import org.jcrpg.world.ai.abs.skill.physical.Tumbling;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.abs.skill.social.Chatter;
import org.jcrpg.world.ai.abs.skill.social.Cheating;
import org.jcrpg.world.ai.abs.skill.social.Reasoning;
import org.jcrpg.world.ai.abs.skill.social.Trade;

public class HumanoidProfessional extends Profession {

	public HumanoidProfessional()
	{
		addSkill(ShortBlades.class);
		addSkill(MediumBlades.class);
		addSkill(StaffsAndWands.class);
		addSkill(Bows.class);
		addSkill(Throwing.class);
		addSkill(Wrestling.class);
		addSkill(Climbing.class);
		addSkill(Swimming.class);
		addSkill(Tumbling.class);
		addSkill(Tracking.class);
		addSkill(Hiding.class);
		addSkill(LocksAndTraps.class);
		addSkill(Languages.class);
		addSkill(Ecology.class);
		addSkill(Mythology.class);
		addSkill(Chatter.class);
		addSkill(Cheating.class);
		addSkill(Reasoning.class);
		addSkill(Trade.class);
	}
}
