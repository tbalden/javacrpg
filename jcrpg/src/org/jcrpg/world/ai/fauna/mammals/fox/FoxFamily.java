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

package org.jcrpg.world.ai.fauna.mammals.fox;

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
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Plain;

public class FoxFamily extends AnimalEntityDescription {

	public static AudioDescription audio = new AudioDescription();
	static {
		audio.ENCOUNTER = new String[]{"redfox_1"};
	}

	public static NormalAnimalMale FOX_TYPE_MALE = new NormalAnimalMale("FOX_MALE",MammalBody.class,audio);
	public static NormalAnimalFemale FOX_TYPE_FEMALE = new NormalAnimalFemale("FOX_FEMALE",MammalBody.class,audio);
	public static WeakAnimalChild FOX_TYPE_CHILD = new WeakAnimalChild("FOX_CHILD",MammalBody.class,audio);

	public static MovingModel fox = new MovingModel("models/fauna/redfox.obj",null,null,null,false);
	public static RenderedMovingUnit fox_unit = new RenderedMovingUnit(new Model[]{fox});

	
	static
	{
	}
	@Override
	public String getEntityIconPic() {
		return "fox";
	}

	public FoxFamily() {
		climates.add(Tropical.class);
		climates.add(Continental.class);	
		//climates.add(Arctic.class);
		geographies.add(Plain.class);
		startingSkills.add(new SkillInstance(Tracking.class,20));
		startingSkills.add(new SkillInstance(BiteFight.class,10));
		behaviors.add(Aggressive.class);
		indoorDweller = true;
		genderType = GENDER_BOTH;
		addGroupingRuleMember(FOX_TYPE_MALE);
		addGroupingRuleMember(FOX_TYPE_FEMALE);
	}


}
