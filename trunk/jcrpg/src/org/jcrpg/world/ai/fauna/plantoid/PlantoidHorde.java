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

package org.jcrpg.world.ai.fauna.plantoid;

import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.plantoid.member.Plantobite;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;

public class PlantoidHorde extends AnimalEntityDescription {
	
	@Override
	public String getEntityIconPic() {
		return "plantoid";
	}

	public PlantoidHorde()
	{
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Aggressive.class);
		genderType = GENDER_BOTH;
		indoorDweller = false;
		startingSkills.add(new SkillInstance(BiteFight.class,20));
		//setAverageGroupSizeAndDeviation(5, 2);
		addGroupingRuleMember(Plantobite.PLANTOBITE);
	}


}
