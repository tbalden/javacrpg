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

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.behavior.Escapist;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.modifier.MildAnimalFemale;
import org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.geography.Forest;

public class Warthogs extends AnimalEntityDescription {

	public static AudioDescription audio = new AudioDescription();
	static {
		audio.ENCOUNTER = new String[]{"warthog_1"};
	}

	public static StrongAnimalMale WARTHOG_TYPE_MALE = new StrongAnimalMale("WARTHOG_MALE",audio);
	public static MildAnimalFemale WARTHOG_TYPE_FEMALE = new MildAnimalFemale("WARTHOG_FEMALE",audio);

	public static MovingModel wolf = new MovingModel("models/fauna/warthog_model.obj",null,null,null,false);
	public static RenderedMovingUnit warthog_unit = new RenderedMovingUnit(new Model[]{wolf});
	

	public Warthogs() {
		iconPic = "warthog";
		climates.add(Tropical.class);
		geographies.add(Forest.class);
		behaviors.add(Escapist.class);
		indoorDweller = false;
		genderType = GENDER_BOTH;
		addGroupingRuleMember(WARTHOG_TYPE_MALE);
	}


}
