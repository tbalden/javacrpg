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

import java.util.ArrayList;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.abs.Behavior;
import org.jcrpg.world.ai.abs.behavior.Peaceful;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.ai.fauna.modifier.WeakAnimalChild;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.geography.Forest;

public class GorillaHorde extends AnimalEntityDescription {
	
	public static StrongAnimalMale GORILLA_TYPE_MALE = new StrongAnimalMale("GORILLA_MALE");
	public static MildAnimalFemale GORILLA_TYPE_FEMALE = new MildAnimalFemale("GORILLA_FEMALE");
	public static WeakAnimalChild GORILLA_TYPE_CHILD = new WeakAnimalChild("GORILLA_CHILD");
	
	public static MovingModel gorilla = new MovingModel("./data/models/fauna/gorilla/gorilla.md5mesh","./data/models/fauna/gorilla/gorilla.md5anim",null,null,false);
	public static RenderedMovingUnit gorilla_unit = new RenderedMovingUnit(new Model[]{gorilla});
	
	public static ArrayList<Class <? extends EntityDescription>> foodEntities = new ArrayList<Class <? extends EntityDescription>>();
	public static ArrayList<Class <? extends ClimateBelt>> climates = new ArrayList<Class <? extends ClimateBelt>>();
	public static ArrayList<Condition> conditions = new ArrayList<Condition>();
	public static ArrayList<Class <? extends Geography>> geographies = new ArrayList<Class <? extends Geography>>();

	public static ArrayList<Class <? extends Behavior>> behaviors = new ArrayList<Class<? extends Behavior>>();
	
	static
	{
		
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Peaceful.class);
	}
	
	public GorillaHorde()
	{
		genderType = GENDER_BOTH;
		addGroupingRuleMember(GORILLA_TYPE_MALE);
	}


}
