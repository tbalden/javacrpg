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

package org.jcrpg.world.ai.fauna.mammals.gorilla;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.body.MammalBody;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;

public class GorillaHorde extends AnimalEntityDescription {
	
	public static AudioDescription gorillaAudio = new AudioDescription();
	static {
		gorillaAudio.ENCOUNTER = new String[]{"gorilla"};
		gorillaAudio.ENVIRONMENTAL = new String[]{"gorilla_environment"};
	}
	
	public static StrongAnimalMale GORILLA_TYPE_MALE = new StrongAnimalMale("GORILLA_MALE",MammalBody.class,gorillaAudio);
	public static MildAnimalFemale GORILLA_TYPE_FEMALE = new MildAnimalFemale("GORILLA_FEMALE",MammalBody.class,gorillaAudio);
	public static WeakAnimalChild GORILLA_TYPE_CHILD = new WeakAnimalChild("GORILLA_CHILD",MammalBody.class,gorillaAudio);
	
	public static MovingModel gorilla = null;
	static 
	{
		MovingModelAnimDescription desc = new MovingModelAnimDescription(new String[]{"./data/models/fauna/gorilla/gorilla_steady.md5anim","./data/models/fauna/gorilla/gorilla.md5anim"});
		desc.WALK = "./data/models/fauna/gorilla/gorilla_steady.md5anim";
		desc.ATTACK_UPPER = "./data/models/fauna/gorilla/gorilla.md5anim";
		desc.ATTACK_LOWER = "./data/models/fauna/gorilla/gorilla.md5anim";
		desc.PAIN = "./data/models/fauna/gorilla/gorilla.md5anim";
		desc.DEFEND_UPPER = "./data/models/fauna/gorilla/gorilla.md5anim";
		desc.DEFEND_LOWER = "./data/models/fauna/gorilla/gorilla.md5anim";
		desc.DEATH_NORMAL = "./data/models/fauna/gorilla/gorilla.md5anim";
		gorilla = new MovingModel("./data/models/fauna/gorilla/gorilla.md5mesh",desc,null,null,false);
		gorilla.genericScale = 0.8f;
	}
	
	public static RenderedMovingUnit gorilla_unit = new RenderedMovingUnit(new Model[]{gorilla});
	
	
	static
	{
		
	}
	
	public GorillaHorde()
	{
		iconPic = "gorilla";
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Peaceful.class);
		genderType = GENDER_BOTH;
		indoorDweller = false;
		setAverageGroupSizeAndDeviation(5, 2);
		addGroupingRuleMember(GORILLA_TYPE_MALE);
		addGroupingRuleMember(GORILLA_TYPE_FEMALE);
		addGroupingRuleMember(GORILLA_TYPE_CHILD);
	}


}
