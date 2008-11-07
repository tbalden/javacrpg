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
import org.jcrpg.world.ai.abs.skill.martial.LargeBlades;
import org.jcrpg.world.ai.abs.skill.martial.MaceAndFlail;
import org.jcrpg.world.ai.abs.skill.martial.MediumBlades;
import org.jcrpg.world.ai.abs.skill.martial.ShortBlades;
import org.jcrpg.world.ai.abs.skill.martial.StaffsAndWands;
import org.jcrpg.world.ai.abs.skill.martial.Wrestling;
import org.jcrpg.world.ai.abs.skill.physical.Climbing;
import org.jcrpg.world.ai.abs.skill.physical.Swimming;
import org.jcrpg.world.ai.profession.HumanoidProfessional;
import org.jcrpg.world.object.armor.LeatherArmor;
import org.jcrpg.world.object.combat.blade.Dagger;
import org.jcrpg.world.object.combat.blade.LongSword;
import org.jcrpg.world.object.combat.staffwand.QuarterStaff;

public class Warrior extends HumanoidProfessional{

	public Warrior()
	{
		super();
		generationNewInstanceObjects.add(LongSword.class);
		generationNewInstanceObjects.add(Dagger.class);
		generationNewInstanceObjects.add(QuarterStaff.class);
		generationNewInstanceObjects.add(LeatherArmor.class);
		attrMinLevels.minimumLevels.put(FantasyAttributes.STRENGTH, 16);
		attrMinLevels.minimumLevels.put(FantasyAttributes.CONSTITUTION, 10);
		attrMinLevels.minimumLevels.put(FantasyAttributes.SPEED, 10);
		addMajorSkill(Wrestling.class);
		addMajorSkill(LargeBlades.class);
		addMajorSkill(MediumBlades.class);
		addMinorSkill(ShortBlades.class);
		addMajorSkill(MaceAndFlail.class);
		addMinorSkill(StaffsAndWands.class);
		addMinorSkill(Swimming.class);
		addMinorSkill(Climbing.class);
	}
}
