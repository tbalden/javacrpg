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

package org.jcrpg.world.ai.humanoid.group.kobold;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.humanoid.group.kobold.member.KoboldMaleMiner;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.sub.Cave;

public class KoboldHorde extends HumanoidEntityDescription {

	public static AudioDescription koboldMaleAudio = new AudioDescription();
	static {
		//boarmanMaleAudio.ENCOUNTER = new String[]{"boarman/boarman_thug"};
		//boarmanMaleAudio.PAIN = new String[]{"boarman/boar_pain"};
		//boarmanMaleAudio.DEATH= new String[]{"boarman/boarman_thug"};
		//boarmanMaleAudio.ATTACK = new String[]{"boarman/boarman_thug"};
		//boarmanMaleAudio.ENVIRONMENTAL = new String[]{"human_env1", "human_env2"};
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static KoboldMaleMiner KOBOLD_MALE_MINER = new KoboldMaleMiner("KOBOLD_MALE_MINER",koboldMaleAudio);

	public static MovingModel koboldMaleMiner = null;
	static {
		MovingModelAnimDescription desc = new MovingModelAnimDescription("./data/models/humanoid/kobold/kobold_pose.md5anim");
		koboldMaleMiner = new MovingModel("./data/models/humanoid/kobold/kobold.md5mesh",desc,null,null,false);
		koboldMaleMiner.disposition = new float[] {0,-0.93f,0};
		koboldMaleMiner.genericScale = 0.5f;
	
	}
	public static RenderedMovingUnit koboldMaleMiner_unit = new RenderedMovingUnit(new Model[]{koboldMaleMiner});


	public KoboldHorde()
	{
		iconPic = "kobold";
		climates.add(Tropical.class);
		climates.add(Continental.class);
		geographies.add(Cave.class);
		geographies.add(MountainNew.class);
		
		behaviors.add(Aggressive.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		
		setAverageGroupSizeAndDeviation(4, 2);
		
		addGroupingRuleMember(KOBOLD_MALE_MINER);
	}
	
}
