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

package org.jcrpg.world.ai.fauna.reptile.lizard.green;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.body.LizardBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.NormalAnimalMale;
import org.jcrpg.world.ai.profession.MonsterNormal;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Plain;

public class Lizards extends AnimalEntityDescription {

	public static AudioDescription audio = new AudioDescription();
	static {
		audio.ENCOUNTER = new String[]{"lizard/encounter"};
		audio.ATTACK = new String[]{"lizard/short"};
		audio.PAIN = new String[]{"lizard/short"};
		audio.DEATH = new String[]{"lizard/short"};
	}

	public static NormalAnimalMale GREENLIZARD_TYPE_MALE = new NormalAnimalMale("GREENLIZ_MALE",LizardBody.class,audio);
	public static NormalAnimalFemale GREENLIZARD_TYPE_FEMALE = new NormalAnimalFemale("GREENLIZ_MALE",LizardBody.class,audio);
	//public static WeakAnimalChild FOX_TYPE_CHILD = new WeakAnimalChild("FOX_CHILD",LizardBody.class,audio);

	static 
	{
		GREENLIZARD_TYPE_FEMALE.addProfessionInitially(new MonsterNormal());
		GREENLIZARD_TYPE_MALE.addProfessionInitially(new MonsterNormal());
	}
    public static MovingModel greenLizard = new MovingModel("models/fauna/lizard_exp.obj",null,null,null,false);
	//public static MovingModel greenLizard = new MovingModel("models/monster/eyebat/eyebat.obj",null,null,null,false);
	public static RenderedMovingUnit greenLizard_unit = new RenderedMovingUnit(new Model[]{greenLizard});

	
	static
	{
	}

	public Lizards() {
		//iconPic = "fox";
		climates.add(Tropical.class);
		climates.add(Continental.class);	
		//climates.add(Arctic.class);
		geographies.add(Plain.class);
		
		startingSkills.add(new SkillInstance(Tracking.class,20));
		startingSkills.add(new SkillInstance(BiteFight.class,10));
		behaviors.add(Aggressive.class);
		indoorDweller = true;
		genderType = GENDER_BOTH;
		addGroupingRuleMember(GREENLIZARD_TYPE_MALE);
		addGroupingRuleMember(GREENLIZARD_TYPE_FEMALE);
	}


}
