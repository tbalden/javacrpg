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

package org.jcrpg.world.ai.fauna.mammals.warthog;

import java.util.ArrayList;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.abs.Behavior;
import org.jcrpg.world.ai.abs.behavior.Escapist;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Forest;

public class Warthogs extends AnimalEntityDescription {

	public static String WARTHOG_TYPE_MALE = "WARTHOG_MALE";
	public static String WARTHOG_TYPE_FEMALE = "WARTHOG_FEMALE";

	public static MovingModel wolf = new MovingModel("models/fauna/warthog_model.obj",null,null,null,false);
	public static RenderedMovingUnit warthog_unit = new RenderedMovingUnit(new Model[]{wolf});

	public static ArrayList<Class <? extends EntityDescription>> foodEntities = new ArrayList<Class <? extends EntityDescription>>();
	public static ArrayList<Class <? extends ClimateBelt>> climates = new ArrayList<Class <? extends ClimateBelt>>();
	public static ArrayList<Condition> conditions = new ArrayList<Condition>();
	public static ArrayList<Class <? extends Geography>> geographies = new ArrayList<Class <? extends Geography>>();
	
	public static ArrayList<Class <? extends Behavior>> behaviors = new ArrayList<Class<? extends Behavior>>();
	
	static
	{
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Escapist.class);
	}

	public Warthogs(World w, Ecology ecology, String id, int numberOfMembers, int startX,
			int startY, int startZ) {
		super(w, ecology, id, numberOfMembers, startX, startY, startZ);
		roamingBoundary.setRadiusInRealCubes(numberOfMembers*2);
		genderType = GENDER_BOTH;
	}

	@Override
	public VisibleLifeForm getOne() {
		nextVisibleSequence();
		return new VisibleLifeForm(this.getClass().getName()+visibleSequence,WARTHOG_TYPE_MALE,this,null);
	}

}
