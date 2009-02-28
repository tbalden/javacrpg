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

package org.jcrpg.world.ai.fauna.mammals.bear;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.body.MammalBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;

public class BrownBearFamily extends AnimalEntityDescription {
	
	public static AudioDescription bearAudio = new AudioDescription();
	static {
		bearAudio.ENCOUNTER = new String[]{"grizzly_1"};
		bearAudio.ENVIRONMENTAL = new String[]{"brownbear_env1"};
	}
	
	public static StrongAnimalMale BROWNBEAR_TYPE_MALE = new StrongAnimalMale("BROWNBEAR_MALE",MammalBody.class,bearAudio);
	public static MildAnimalFemale BROWNBEAR_TYPE_FEMALE = new MildAnimalFemale("BROWNBEAR_FEMALE",MammalBody.class,bearAudio);
	public static WeakAnimalChild BROWNBEAR_TYPE_CHILD = new WeakAnimalChild("BROWNBEAR_CHILD",MammalBody.class,bearAudio);
	
	public static MovingModel brownbear = new MovingModel("models/fauna/warbear_tex.obj",null,null,null,false);;//new MovingModel("./data/models/fauna/gorilla/gorilla.md5mesh","./data/models/fauna/gorilla/gorilla_steady.md5anim",null,null,false);
	public static RenderedMovingUnit brownbear_unit = new RenderedMovingUnit(new Model[]{brownbear});
	
	static
	{
		
	}
	
	public BrownBearFamily()
	{
		iconPic = "bear";
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		//setAverageGroupSizeAndDeviation(5, 2);
		startingSkills.add(new SkillInstance(BiteFight.class,20));

		addGroupingRuleMember(BROWNBEAR_TYPE_MALE);
		addGroupingRuleMember(BROWNBEAR_TYPE_FEMALE);
		addGroupingRuleMember(BROWNBEAR_TYPE_CHILD);
	}


}
