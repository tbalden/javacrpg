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

package org.jcrpg.world.ai.fauna.mammals.wolf;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.body.MammalBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalMale;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.place.geography.Plain;

public class WolfPack extends AnimalEntityDescription {

	public static AudioDescription audio = new AudioDescription();
	static {
		audio.ENCOUNTER = new String[]{"wolf_1"};
		audio.ENVIRONMENTAL = new String[]{"wolf_env1"};
	}

	public static NormalAnimalMale WOLF_TYPE_MALE = new NormalAnimalMale("WOLF_MALE",MammalBody.class,audio);
	public static NormalAnimalMale WOLF_TYPE_FEMALE = new NormalAnimalMale("WOLF_FEMALE",MammalBody.class,audio);

	public static MovingModel wolf = new MovingModel("models/fauna/wolf.obj",null,null,null,false);
	public static RenderedMovingUnit wolf_unit = new RenderedMovingUnit(new Model[]{wolf});

	
	static
	{
	}

	public WolfPack() {
		iconPic = "wolf";
		
		climates.add(Continental.class);
		climates.add(Arctic.class);
		foodEntities.add(Warthogs.class);
		geographies.add(Plain.class);
		startingSkills.add(new SkillInstance(Tracking.class,20));
		startingSkills.add(new SkillInstance(BiteFight.class,20));
		behaviors.add(Aggressive.class);
		indoorDweller = false;
		genderType = GENDER_BOTH;
		addGroupingRuleMember(WOLF_TYPE_MALE);
		addGroupingRuleMember(WOLF_TYPE_FEMALE);
	}


}
