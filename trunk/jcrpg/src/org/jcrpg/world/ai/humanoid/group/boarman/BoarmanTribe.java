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

package org.jcrpg.world.ai.humanoid.group.boarman;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanFemale;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanMaleArcher;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanMaleThug;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanMaleWorker;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.economic.SimpleDistrict;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;

public class BoarmanTribe extends HumanoidEntityDescription {

	public static AudioDescription boarmanMaleAudio = new AudioDescription();
	public static AudioDescription boarmanFemaleAudio = new AudioDescription();	
	static {
		//boarmanMaleAudio.ENVIRONMENTAL = new String[]{"human_env1", "human_env2"};
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static BoarmanMaleThug BOARMAN_MALE_THUG = new BoarmanMaleThug("BOARMAN_MALE_THUG",boarmanMaleAudio);
	public static BoarmanMaleArcher BOARMAN_MALE_ARCHER = new BoarmanMaleArcher("BOARMAN_MALE_ARCHER",boarmanMaleAudio);
	public static BoarmanMaleWorker BOARMAN_MALE_WORKER = new BoarmanMaleWorker("BOARMAN_MALE_WORKER",boarmanMaleAudio);
	public static BoarmanFemale BOARMAN_FEMALE = new BoarmanFemale("BOARMAN_FEMALE",boarmanFemaleAudio);

	public static MovingModel boarmanMale = new MovingModel("./data/models/humanoid/boarman/boarman.md5mesh","./data/models/humanoid/boarman/boarman.md5anim",null,null,false);
	public static RenderedMovingUnit boarmanMale_unit = new RenderedMovingUnit(new Model[]{boarmanMale});

	public static MovingModel boarmanFemale = new MovingModel("./data/models/fauna/gorilla/boarman.md5mesh","./data/models/humanoid/boarman/boarman.md5anim",null,null,false);
	public static RenderedMovingUnit boarmanFemale_unit = new RenderedMovingUnit(new Model[]{boarmanFemale});

	public BoarmanTribe()
	{
		iconPic = "boarman";
		economyTemplate.addPopulationType(Plain.class, SimpleDistrict.class);
		economyTemplate.addPopulationType(Forest.class, SimpleDistrict.class);
		economyTemplate.addPopulationType(MountainNew.class, SimpleDistrict.class);
		economyTemplate.addResidenceType(Plain.class, House.class);
		economyTemplate.addResidenceType(Forest.class, House.class);
		economyTemplate.addResidenceType(MountainNew.class, House.class);
		economyTemplate.addEcoGroundType(Plain.class, EconomicGround.class);
		economyTemplate.addEcoGroundType(Forest.class, EconomicGround.class);
		economyTemplate.addEcoGroundType(MountainNew.class, EconomicGround.class);
		climates.add(Tropical.class);
		climates.add(Continental.class);
		geographies.add(Forest.class);
		geographies.add(Plain.class);
		geographies.add(Cave.class);
		
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		
		setAverageGroupSizeAndDeviation(6, 2);
		
		addGroupingRuleMember(BOARMAN_MALE_THUG);
	}
	

	
}
