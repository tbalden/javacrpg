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

package org.jcrpg.world.ai.humanoid.group.anatipion;

import java.util.HashMap;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.ui.map.BlockPattern;
import org.jcrpg.ui.map.IconReader;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.yeti.member.YetiMaleHunter;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.ground.RawTreadGround;
import org.jcrpg.world.place.economic.ground.StoneBaseSquareGround;
import org.jcrpg.world.place.economic.population.SimpleDistrict;
import org.jcrpg.world.place.economic.residence.BrickHouse;
import org.jcrpg.world.place.economic.residence.SandIgloo;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Plain;

import com.jme.renderer.ColorRGBA;

public class AnatipionCatchers extends HumanoidEntityDescription {

	public static AudioDescription humanMaleAudio = new AudioDescription();
	public static AudioDescription humanFemaleAudio = new AudioDescription();	
	static {
		humanMaleAudio.ENVIRONMENTAL = new String[]{"human_env1", "human_env2"};
		humanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static YetiMaleHunter ANATIPION_MALE_CATCHER = new YetiMaleHunter("ANATIPION_CATCHER",humanMaleAudio);
	/*public static HumanMalePeasant HUMAN_MALE_PEASANT = new HumanMalePeasant("HUMAN_MALE_PEASANT",humanMaleAudio);
	public static HumanMaleSmith HUMAN_MALE_SMITH = new HumanMaleSmith("HUMAN_MALE_SMITH",humanMaleAudio);
	public static HumanFemaleHousewife HUMAN_FEMALE_HOUSEWIFE= new HumanFemaleHousewife("HUMAN_FEMALE_HOUSEWIFE",humanFemaleAudio);
*/

	public static MovingModel anatipionMale = new MovingModel("models/humanoid/anatipion/anatipion1.obj",null,null,null,false);

	static {
		MovingModelAnimDescription desc = new MovingModelAnimDescription();
		desc.IDLE = "./data/models/humanoid/anatipion/anat1_idle.md5anim";		
		desc.IDLE_COMBAT = "./data/models/humanoid/anatipion/anat1_idle.md5anim";
		desc.WALK = "./data/models/humanoid/anatipion/anat1_idle.md5anim";
		desc.ATTACK_LOWER = "./data/models/humanoid/anatipion/anat1_attack.md5anim";
		desc.ATTACK_UPPER = "./data/models/humanoid/anatipion/anat1_attack.md5anim";
		desc.DEFEND_LOWER = "./data/models/humanoid/anatipion/anat1_hit.md5anim";
		desc.DEFEND_UPPER = "./data/models/humanoid/anatipion/anat1_hit.md5anim";
		desc.PAIN = "./data/models/humanoid/anatipion/anat1_hit.md5anim";
		desc.DEATH_NORMAL = "./data/models/humanoid/anatipion/anat1_hit.md5anim";
		desc.DEAD = "./data/models/humanoid/anatipion/anat1_hit.md5anim";
		anatipionMale = new MovingModel("./data/models/humanoid/anatipion/anat1.md5mesh",desc,null,null,false);
		anatipionMale.genericScale = 0.5f;
		anatipionMale.disposition = new float[] {0f,+1f,-1f};
	}

	public static RenderedMovingUnit anatipionMale_unit = new RenderedMovingUnit(new Model[]{anatipionMale});

	/*public static MovingModel humanFemale = new MovingModel("models/humanoid/human/human_female_1.obj",null,null,null,false);
	public static RenderedMovingUnit humanFemale_unit = new RenderedMovingUnit(new Model[]{humanFemale});
*/
	public AnatipionCatchers()
	{
		
		economyTemplate.addPopulationType(Plain.class, SimpleDistrict.class);
		economyTemplate.addPopulationType(Forest.class, SimpleDistrict.class);
		//economyTemplate.addPopulationType(Mountain.class, SimpleDistrict.class);
		economyTemplate.addResidenceType(Plain.class, BrickHouse.class);
		economyTemplate.addResidenceType(Forest.class, BrickHouse.class);
		economyTemplate.addResidenceType(Plain.class, SandIgloo.class);
		economyTemplate.addResidenceType(Forest.class, SandIgloo.class);
		//economyTemplate.addResidenceType(Mountain.class, Igloo.class);
		economyTemplate.addEcoGroundType(Plain.class, StoneBaseSquareGround.class);
		economyTemplate.addEcoGroundType(Forest.class, StoneBaseSquareGround.class);
		//economyTemplate.addEcoGroundType(Mountain.class, RawStreetGround.class);
		//climates.add(Tropical.class);
		//climates.add(Continental.class);
		climates.add(Desert.class);
		geographies.add(Forest.class);
		geographies.add(Plain.class);
		//geographies.add(Mountain.class);
		//geographies.add(Cave.class);
		
		behaviors.add(Aggressive.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		
		setAverageGroupSizeAndDeviation(3, 2);
		
		addGroupingRuleMember(ANATIPION_MALE_CATCHER);
		//addGroupingRuleMember(HUMAN_FEMALE_HOUSEWIFE,300,5,10);
		//addGroupingRuleMember(HUMAN_MALE_PEASANT);
		//addGroupingRuleMember(HUMAN_MALE_SMITH);
	}

	@Override
	public String getEntityIconPic() {
		return "anatipion";
	}

	static byte[] populationColor = new byte[] {(byte)240,(byte)240,(byte)240};
	
	@Override
	public byte[] getPopulationMapColor() {
		return populationColor;
	}
	public static ColorRGBA[][] CITY_COLORED = IconReader.readMapIconFile("./data/ui/mapicons/sandigloo.ico");

	public static boolean[][] CITY = new boolean[][] 
			  	                                    {
			  		{ true, false, false, false, false,false, true, false, false },
			  		{ false, true, true, true, true,false, true, false, false },
			  		{ false, true, false, false, true,true, true, false, false },
			  		{ false, true, false, true, true,true, false, false, false },
			  		{ false, true, true, true, true,true, false, false, false },
			  		{ false, true, true, true, true,false, false, false, false },
			  		{ true, false, false, false, true,true, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false },
			  		{ false, false, false, false, false,false, false, false, false }
			  	                                    }
			  	;

	static HashMap<Class<? extends Population>, BlockPattern> patternMap = new HashMap<Class<? extends Population>, BlockPattern>();
	static 
	{
		BlockPattern patternSimpleDistrict = new BlockPattern();
		patternSimpleDistrict.PATTERN = CITY;
		patternSimpleDistrict.COLORED_PATTERN = CITY_COLORED;
		patternMap.put(SimpleDistrict.class, patternSimpleDistrict);
	}
	
	@Override
	public HashMap<Class<? extends Population>, BlockPattern> getPopulationPatternMap() {
		return patternMap;
	}
	
	

	
}