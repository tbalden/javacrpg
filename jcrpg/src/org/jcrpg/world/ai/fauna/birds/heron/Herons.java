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

package org.jcrpg.world.ai.fauna.birds.heron;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Escapist;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.body.SinglePartBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalMale;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;

public class Herons extends AnimalEntityDescription {

	public static AudioDescription audio = new AudioDescription();
	static {
		//audio.ENCOUNTER = new String[]{"redfox_1"};
	}

	public static NormalAnimalMale HERON_TYPE_MALE = new NormalAnimalMale("HERON_MALE",SinglePartBody.class, audio);
	public static NormalAnimalFemale HERON_TYPE_FEMALE = new NormalAnimalFemale("HERON_FEMALE",SinglePartBody.class, audio); // TODO bird body type
	//public static WeakAnimalChild FOX_TYPE_CHILD = new WeakAnimalChild("FOX_CHILD",audio);

	public static MovingModel heron = new MovingModel("models/fauna/heron.obj",null,null,null,false);
	public static RenderedMovingUnit heron_unit = new RenderedMovingUnit(new Model[]{heron});

	
	static
	{
	}

	public Herons() {
		//iconPic = "heron";
		climates.add(Tropical.class);
		climates.add(Continental.class);	
		//climates.add(Arctic.class);
		//geographies.add(Plain.class);
		startingSkills.add(new SkillInstance(Tracking.class,20));
		startingSkills.add(new SkillInstance(BiteFight.class,10));
		behaviors.add(Escapist.class);
		indoorDweller = false;
		genderType = GENDER_BOTH;
		addGroupingRuleMember(HERON_TYPE_MALE);
		addGroupingRuleMember(HERON_TYPE_FEMALE);
	}


}
