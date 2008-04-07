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

package org.jcrpg.world.ai.humanoid.group.human;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.human.member.HumanFemaleHousewife;
import org.jcrpg.world.ai.humanoid.group.human.member.HumanMaleArtisan;
import org.jcrpg.world.ai.humanoid.group.human.member.HumanMalePeasant;
import org.jcrpg.world.ai.humanoid.group.human.member.HumanMaleSmith;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.Plain;

public class HumanCommoners extends HumanoidEntityDescription {

	public static AudioDescription humanMaleAudio = new AudioDescription();
	public static AudioDescription humanFemaleAudio = new AudioDescription();	
	static {
	}
	
	public static HumanMaleArtisan HUMAN_MALE_ARTISAN = new HumanMaleArtisan("HUMAN_MALE_ARTISAN",humanMaleAudio);
	public static HumanMalePeasant HUMAN_MALE_PEASANT = new HumanMalePeasant("HUMAN_MALE_PEASANT",humanMaleAudio);
	public static HumanMaleSmith HUMAN_MALE_SMITH = new HumanMaleSmith("HUMAN_MALE_SMITH",humanMaleAudio);
	public static HumanFemaleHousewife HUMAN_FEMALE_HOUSEWIFE= new HumanFemaleHousewife("HUMAN_FEMALE_HOUSEWIFE",humanFemaleAudio);

	public static MovingModel humanMale = new MovingModel("models/humanoid/human/human_male_1.obj",null,null,null,false);
	public static RenderedMovingUnit humanMale_unit = new RenderedMovingUnit(new Model[]{humanMale});

	public HumanCommoners()
	{
		climates.add(Tropical.class);
		climates.add(Continental.class);
		geographies.add(Forest.class);
		geographies.add(Plain.class);
		geographies.add(MountainNew.class);
		
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		
		setAverageGroupSizeAndDeviation(6, 2);
		
		addGroupingRuleMember(HUMAN_MALE_ARTISAN);
		addGroupingRuleMember(HUMAN_MALE_PEASANT);
		addGroupingRuleMember(HUMAN_MALE_SMITH);
		
		
	}

	
}
