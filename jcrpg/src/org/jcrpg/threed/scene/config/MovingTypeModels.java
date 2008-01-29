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

import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.ai.fauna.mammals.wolf.WolfPack;

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
		int counter = 0;
		
		hmMobIdToModelId.put(GorillaHorde.GORILLA_TYPE_MALE,counter);
		hmModelIdToRenderedMovingUnit.put(counter, GorillaHorde.gorilla_unit);
		counter++;
		
		hmMobIdToModelId.put(WolfPack.WOLF_TYPE_MALE,counter);
		hmModelIdToRenderedMovingUnit.put(counter, WolfPack.wolf_unit);
		counter++;

		hmMobIdToModelId.put(Warthogs.WARTHOG_TYPE_MALE,counter);
		hmModelIdToRenderedMovingUnit.put(counter, Warthogs.warthog_unit);
		counter++;
		
	}
	
	public RenderedMovingUnit getRenderedUnit(String id)
	{
		Integer iid = hmMobIdToModelId.get(id);
		if (iid==null) return null;
		return hmModelIdToRenderedMovingUnit.get(iid);
	}
}