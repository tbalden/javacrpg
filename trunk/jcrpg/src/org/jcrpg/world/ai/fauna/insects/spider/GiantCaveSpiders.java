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

package org.jcrpg.world.ai.fauna.insects.spider;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.body.SinglePartBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;

public class GiantCaveSpiders extends AnimalEntityDescription {
	
	public static AudioDescription spiderAudio = new AudioDescription();
	static {
		//gorillaAudio.ENCOUNTER = new String[]{"gorilla"};
		//gorillaAudio.ENVIRONMENTAL = new String[]{"gorilla_environment"};
	}
	
	public static StrongAnimalMale GIANTCAVESPIDER_TYPE_MALE = new StrongAnimalMale("GIANTCAVESPIDER_MALE",SinglePartBody.class, spiderAudio);
	public static MildAnimalFemale GIANTCAVESPIDER_TYPE_FEMALE = new MildAnimalFemale("GIANTCAVESPIDER_FEMALE",SinglePartBody.class, spiderAudio);
	public static WeakAnimalChild GIANTCAVESPIDER_TYPE_CHILD = new WeakAnimalChild("GIANTCAVESPIDER_CHILD",SinglePartBody.class, spiderAudio); // TODO insect body type
	
	public static MovingModel giantcavespider = new MovingModel("models/fauna/spider.obj",null,null,null,false);;//new MovingModel("./data/models/fauna/gorilla/gorilla.md5mesh","./data/models/fauna/gorilla/gorilla_steady.md5anim",null,null,false);
	public static RenderedMovingUnit giantcavespider_unit = new RenderedMovingUnit(new Model[]{giantcavespider});
	
	
	static
	{
		
	}
	
	public GiantCaveSpiders()
	{
		iconPic = "spider";
		climates.add(Continental.class);
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = true;
		//setAverageGroupSizeAndDeviation(5, 2);
		addGroupingRuleMember(GIANTCAVESPIDER_TYPE_MALE);
		addGroupingRuleMember(GIANTCAVESPIDER_TYPE_FEMALE);
		addGroupingRuleMember(GIANTCAVESPIDER_TYPE_CHILD);
	}


}
