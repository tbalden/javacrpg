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
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.body.MammalBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.tropical.Tropical;

public class PolarBears extends AnimalEntityDescription {
	
	public static AudioDescription bearAudio = new AudioDescription();
	static {
		bearAudio.ENCOUNTER = new String[]{"polarbear1"};
	}
	
	public static StrongAnimalMale POLARBEAR_TYPE_MALE = new StrongAnimalMale("POLARBEAR_MALE",MammalBody.class,bearAudio);
	public static MildAnimalFemale POLARBEAR_TYPE_FEMALE = new MildAnimalFemale("POLARBEAR_FEMALE",MammalBody.class,bearAudio);
	public static WeakAnimalChild POLARBEAR_TYPE_CHILD = new WeakAnimalChild("POLARBEAR_CHILD",MammalBody.class,bearAudio);
	
	public static MovingModel polarbear = new MovingModel("models/fauna/polarbear_tex.obj",null,null,null,false);;//new MovingModel("./data/models/fauna/gorilla/gorilla.md5mesh","./data/models/fauna/gorilla/gorilla_steady.md5anim",null,null,false);
	public static RenderedMovingUnit polarbear_unit = new RenderedMovingUnit(new Model[]{polarbear});
	
	
	static
	{
		
	}
	@Override
	public String getEntityIconPic() {
		return "bear";
	}

	public PolarBears()
	{
		climates.add(Tropical.class);
		climates.add(Arctic.class);
		//geographies.add(Forest.class);
		behaviors.add(Aggressive.class);
		genderType = GENDER_BOTH;
		indoorDweller = false;
		//setAverageGroupSizeAndDeviation(5, 2);
		startingSkills.add(new SkillInstance(BiteFight.class,20));
		addGroupingRuleMember(POLARBEAR_TYPE_MALE);
		addGroupingRuleMember(POLARBEAR_TYPE_FEMALE);
		addGroupingRuleMember(POLARBEAR_TYPE_CHILD);
	}


}
