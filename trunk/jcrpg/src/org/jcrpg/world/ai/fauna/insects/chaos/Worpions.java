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

package org.jcrpg.world.ai.fauna.insects.chaos;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.body.SinglePartBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.sub.Cave;

public class Worpions  extends AnimalEntityDescription {
	
	public static AudioDescription spiderAudio = new AudioDescription();
	static {
		//gorillaAudio.ENCOUNTER = new String[]{"gorilla"};
		//gorillaAudio.ENVIRONMENTAL = new String[]{"gorilla_environment"};
	}
	
	public static StrongAnimalMale WORPION_TYPE = new StrongAnimalMale("WORPION",SinglePartBody.class, spiderAudio);
	
	public static MovingModel worpion = new MovingModel("models/monster/worpion/worpion.obj",null,null,null,false);;//new MovingModel("./data/models/fauna/gorilla/gorilla.md5mesh","./data/models/fauna/gorilla/gorilla_steady.md5anim",null,null,false);
	public static RenderedMovingUnit worpion_unit = new RenderedMovingUnit(new Model[]{worpion});
	
	
	static
	{
		
	}
	
	public Worpions()
	{
		//iconPic = "spider";
		climates.add(Tropical.class);
		climates.add(Continental.class);
		geographies.add(Cave.class);
		//geographies.add(Mountain.class);
		geographies.add(Forest.class);
		behaviors.add(Aggressive.class);
		genderType = GENDER_NEUTRAL;
		indoorDweller = true;
		//setAverageGroupSizeAndDeviation(5, 2);
		startingSkills.add(new SkillInstance(BiteFight.class,20));
		addGroupingRuleMember(WORPION_TYPE);
	}



}
