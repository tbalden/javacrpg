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

package org.jcrpg.threed.scene.config;

import java.util.HashMap;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;

/**
 * Mapping for moving life forms to renderend moving units.
 * @author illes
 */
public class MovingTypeModels {
	
	public static final String NON_INSTANCE = "-NONE-";
	
	HashMap<String, Integer> hmMobIdToModelId = new HashMap<String, Integer>();
	HashMap<Integer, RenderedMovingUnit> hmModelIdToRenderedMovingUnit = new HashMap<Integer, RenderedMovingUnit>();

	public MovingTypeModels()
	{
		fillMap();
	}
	
	public void fillMap()
	{
		hmMobIdToModelId.put(GorillaHorde.GORILLA_TYPE_MALE,0);

		MovingModel gorilla = new MovingModel("models/fauna/gorilla_texture.obj",null,null,false);
		//MovingModel gorilla = new MovingModel("data/models/fauna/gorilla.dae",null,null,false);
		RenderedMovingUnit gorilla_unit = new RenderedMovingUnit(NON_INSTANCE,0,0,0,new Model[]{gorilla});
		hmModelIdToRenderedMovingUnit.put(0, gorilla_unit);
	}
	
	public RenderedMovingUnit getRenderedUnit(String id)
	{
		Integer iid = hmMobIdToModelId.get(id);
		if (iid==null) return null;
		return hmModelIdToRenderedMovingUnit.get(iid);
	}
}
