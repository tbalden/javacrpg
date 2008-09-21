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
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanFemale;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanMaleArcher;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanMaleMage;
import org.jcrpg.world.ai.humanoid.group.boarman.member.BoarmanMaleThug;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.economic.ground.PavedStorageAreaGround;
import org.jcrpg.world.place.economic.ground.RawStreetGround;
import org.jcrpg.world.place.economic.population.SimpleDistrict;
import org.jcrpg.world.place.economic.residence.WoodenHouse;
import org.jcrpg.world.place.economic.residence.dungeon.SimpleDungeonPart;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;

public class BoarmanTribe extends HumanoidEntityDescription {

	public static AudioDescription boarmanMaleAudio = new AudioDescription();
	public static AudioDescription boarmanFemaleAudio = new AudioDescription();	
	static {
		boarmanMaleAudio.ENCOUNTER = new String[]{"boarman/boarman_thug"};
		boarmanMaleAudio.PAIN = new String[]{"boarman/boar_pain"};
		boarmanMaleAudio.DEATH= new String[]{"boarman/boarman_thug"};
		boarmanMaleAudio.ATTACK = new String[]{"boarman/boarman_thug"};
		boarmanMaleAudio.ENVIRONMENTAL = new String[]{"boarman/boarman_env1","boarman/boarman_env2"};
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static BoarmanMaleThug BOARMAN_MALE_THUG = new BoarmanMaleThug("BOARMAN_MALE_THUG",boarmanMaleAudio);
	public static BoarmanMaleArcher BOARMAN_MALE_ARCHER = new BoarmanMaleArcher("BOARMAN_MALE_ARCHER",boarmanMaleAudio);
	public static BoarmanMaleMage BOARMAN_MALE_MAGE = new BoarmanMaleMage("BOARMAN_MALE_MAGE",boarmanMaleAudio);
	public static BoarmanFemale BOARMAN_FEMALE = new BoarmanFemale("BOARMAN_FEMALE",boarmanFemaleAudio);

	public static MovingModel boarmanMale = null;
	public static MovingModel boarmanMaleMage = null;
	public static MovingModel boarmanFemale = null;
	static {
		MovingModelAnimDescription desc = new MovingModelAnimDescription();
		desc.IDLE = "./data/models/humanoid/boarman/boarman_idle_long.md5anim";		
		desc.IDLE_COMBAT = "./data/models/humanoid/boarman/boarman_idle.md5anim";
		desc.WALK = "./data/models/humanoid/boarman/boarman_walk.md5anim";
		desc.ATTACK_LOWER = "./data/models/humanoid/boarman/boarman_attack.md5anim";
		desc.ATTACK_UPPER = "./data/models/humanoid/boarman/boarman_attack.md5anim";
		desc.DEFEND_LOWER = "./data/models/humanoid/boarman/boarman_defense.md5anim";
		desc.DEFEND_UPPER = "./data/models/humanoid/boarman/boarman_defense.md5anim";
		desc.PAIN = "./data/models/humanoid/boarman/boarman_pain.md5anim";
		desc.DEATH_NORMAL = "./data/models/humanoid/boarman/boarman_death.md5anim";
		desc.DEAD = "./data/models/humanoid/boarman/boarman_dead.md5anim";
		boarmanMale = new MovingModel("./data/models/humanoid/boarman/boarman.md5mesh",desc,null,null,false);
		boarmanMale.genericScale = 1.1f;
		boarmanFemale = boarmanMale;

		desc = new MovingModelAnimDescription();
		desc.IDLE = "./data/models/humanoid/boarman/mage/boarmage_idle.md5anim";		
		desc.IDLE_COMBAT = "./data/models/humanoid/boarman/mage/boarmage_combatidle.md5anim";
		desc.WALK = "./data/models/humanoid/boarman/mage/boarmage_idle.md5anim";
		desc.ATTACK_LOWER = "./data/models/humanoid/boarman/mage/boarmage_attack.md5anim";
		desc.ATTACK_UPPER = "./data/models/humanoid/boarman/mage/boarmage_attack.md5anim";
		desc.CAST = "./data/models/humanoid/boarman/mage/boarmage_cast.md5anim";
		desc.DEFEND_LOWER = "./data/models/humanoid/boarman/mage/boarmage_defense.md5anim";
		desc.DEFEND_UPPER = "./data/models/humanoid/boarman/mage/boarmage_defense.md5anim";
		desc.PAIN = "./data/models/humanoid/boarman/mage/boarmage_pain.md5anim";
		desc.DEATH_NORMAL = "./data/models/humanoid/boarman/mage/boarmage_death.md5anim";
		desc.DEAD = "./data/models/humanoid/boarman/mage/boarmage_dead.md5anim";
		boarmanMaleMage = new MovingModel("./data/models/humanoid/boarman/mage/boarmage.md5mesh",desc,null,null,false);
	
	}
	public static RenderedMovingUnit boarmanMale_unit = new RenderedMovingUnit(new Model[]{boarmanMale});
	
	public static RenderedMovingUnit boarmanMaleMage_unit = new RenderedMovingUnit(new Model[]{boarmanMaleMage});

	public static RenderedMovingUnit boarmanFemale_unit = new RenderedMovingUnit(new Model[]{boarmanFemale});


	public BoarmanTribe()
	{
		iconPic = "boarman";
		economyTemplate.addPopulationType(Plain.class, SimpleDistrict.class);
		economyTemplate.addPopulationType(Forest.class, SimpleDistrict.class);
		economyTemplate.addPopulationType(Mountain.class, SimpleDistrict.class);
		economyTemplate.addResidenceType(Plain.class, WoodenHouse.class);
		economyTemplate.addResidenceType(Forest.class, WoodenHouse.class);
		economyTemplate.addResidenceType(Mountain.class, WoodenHouse.class);
		//economyTemplate.addResidenceType(Mountain.class, SimpleDungeonPart.class);
		economyTemplate.addEcoGroundType(Plain.class, RawStreetGround.class);
		economyTemplate.addEcoGroundType(Plain.class, PavedStorageAreaGround.class);
		economyTemplate.addEcoGroundType(Forest.class, RawStreetGround.class);
		economyTemplate.addEcoGroundType(Forest.class, PavedStorageAreaGround.class);
		economyTemplate.addEcoGroundType(Mountain.class, RawStreetGround.class);
		economyTemplate.addEcoGroundType(Mountain.class, PavedStorageAreaGround.class);
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
		
		addGroupingRuleMember(BOARMAN_MALE_THUG);
		addGroupingRuleMember(BOARMAN_MALE_MAGE);
	}
	

	
}
