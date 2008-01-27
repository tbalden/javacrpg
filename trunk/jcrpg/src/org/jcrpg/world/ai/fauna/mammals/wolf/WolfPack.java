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

import java.util.ArrayList;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.abs.Behavior;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.physical.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Plain;

public class WolfPack extends AnimalEntityDescription {

	public static String WOLF_TYPE_MALE = "WOLF_MALE";
	public static String WOLF_TYPE_FEMALE = "WOLF_FEMALE";

	public static MovingModel wolf = new MovingModel("models/fauna/wolf.obj",null,null,null,false);
	public static RenderedMovingUnit wolf_unit = new RenderedMovingUnit(new Model[]{wolf});

	public static ArrayList<Class <? extends EntityDescription>> foodEntities = new ArrayList<Class <? extends EntityDescription>>();
	public static ArrayList<Class <? extends ClimateBelt>> climates = new ArrayList<Class <? extends ClimateBelt>>();
	public static ArrayList<Condition> conditions = new ArrayList<Condition>();
	public static ArrayList<Class <? extends Geography>> geographies = new ArrayList<Class <? extends Geography>>();

	public static ArrayList<SkillInstance> startingSkills = new ArrayList<SkillInstance>();
	public static ArrayList<Class <? extends Behavior>> behaviors = new ArrayList<Class<? extends Behavior>>();
	
	static
	{
		foodEntities.add(Warthogs.class);
		geographies.add(Plain.class);
		startingSkills.add(new SkillInstance(Tracking.class,20));
		startingSkills.add(new SkillInstance(BiteFight.class,20));
		behaviors.add(Aggressive.class);
	}

	public WolfPack(World w, Ecology ecology, String id, int numberOfMembers, int startX,
			int startY, int startZ) {
		super(w, ecology, id, numberOfMembers, startX, startY, startZ);
		roamingBoundary.setRadiusInRealCubes(numberOfMembers*2);
		genderType = GENDER_BOTH;
	}

	@Override
	public VisibleLifeForm getOne() {
		nextVisibleSequence();
		return new VisibleLifeForm(this.getClass().getName()+visibleSequence,WOLF_TYPE_MALE,this, null);
	}

	
	

}
