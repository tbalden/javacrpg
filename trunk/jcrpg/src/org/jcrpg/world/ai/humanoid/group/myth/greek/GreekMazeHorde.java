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

package org.jcrpg.world.ai.humanoid.group.myth.greek;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.myth.greek.member.EyeBat;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.economic.ground.RawStreetGround;
import org.jcrpg.world.place.economic.population.DungeonDistrict;
import org.jcrpg.world.place.economic.residence.dungeon.SimpleDungeonPart;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;

public class GreekMazeHorde extends HumanoidEntityDescription {

	public static AudioDescription eyeBatAudio = new AudioDescription();
	static {
		/*batEyeAudio.ENCOUNTER = new String[]{"boarman/boarman_thug"};
		batEyeAudio.PAIN = new String[]{"boarman/boar_pain"};
		batEyeAudio.DEATH= new String[]{"boarman/boarman_thug"};
		batEyeAudio.ATTACK = new String[]{"boarman/boarman_thug"};
		batEyeAudio.ENVIRONMENTAL = new String[]{"boarman/boarman_env1","boarman/boarman_env2"};*/
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static EyeBat EYEBAT = new EyeBat("EYEBAT",eyeBatAudio);

	public static MovingModel eyeBat = new MovingModel("models/monster/eyebat/eyebat.obj",null,null,null,false);
	public static RenderedMovingUnit eyeBat_unit = new RenderedMovingUnit(new Model[]{eyeBat});

	public GreekMazeHorde()
	{
		//iconPic = "boarman";
		
		economyTemplate.addPopulationType(Plain.class, DungeonDistrict.class);
		economyTemplate.addResidenceType(Plain.class, SimpleDungeonPart.class);
		economyTemplate.addPopulationType(Forest.class, DungeonDistrict.class);
		economyTemplate.addResidenceType(Forest.class, SimpleDungeonPart.class);
		economyTemplate.addEcoGroundType(Plain.class, RawStreetGround.class);
		economyTemplate.addEcoGroundType(Forest.class, RawStreetGround.class);
	
		climates.add(Tropical.class);
		climates.add(Continental.class);
		geographies.add(Forest.class);
		geographies.add(Plain.class);
		geographies.add(Mountain.class);
		//geographies.add(Cave.class);
		
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		
		setAverageGroupSizeAndDeviation(3, 2);
		
		addGroupingRuleMember(EYEBAT);
	}
	

	
}
