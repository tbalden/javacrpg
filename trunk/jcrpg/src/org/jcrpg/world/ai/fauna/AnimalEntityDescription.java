/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.ai.fauna;

import java.util.ArrayList;

import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.PositionCalculus;
import org.jcrpg.world.ai.position.NormalCalculus;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;

/**
 * Represents a description for an animal/pack of animals depending on the nature of an animal species.
 * @author illes
 *
 */
public abstract class AnimalEntityDescription extends EntityDescription {

	public static Class<? extends PositionCalculus> positionCalcType = NormalCalculus.class;
	
	static
	{
		calcTypes.put(NormalCalculus.class, new NormalCalculus());
	}
	
	public AnimalEntityDescription() {
	}

	
	public static final int GENDER_NEUTRAL = 0;
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;
	public static final int GENDER_BOTH = 3;
	
	public int genderType = GENDER_NEUTRAL;
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<Class <? extends EntityDescription>> getFoodEntities()
	{
		try {
			return (ArrayList<Class <? extends EntityDescription>>) getClass().getField("foodEntities").get(this);
		} catch (Exception ex)
		{
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public ArrayList<Class <? extends ClimateBelt>> getClimates()
	{
		try {
			return (ArrayList<Class <? extends ClimateBelt>>) getClass().getField("climates").get(this);
		} catch (Exception ex)
		{
			return null;
		}
		
	}
	@SuppressWarnings("unchecked")
	public ArrayList<Condition> getConditions()
	{
		try {
			return (ArrayList<Condition>) getClass().getField("conditions").get(this);
		} catch (Exception ex)
		{
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Class <? extends Geography>> getGeographies()
	{
		try {
			return (ArrayList<Class <? extends Geography>>) getClass().getField("geographies").get(this);
		} catch (Exception ex)
		{
			return null;
		}
		
	}

	@Override
	public boolean isPrey(EntityDescription desc) {
		
		if (getFoodEntities().contains(desc.getClass()))
			return true;
		return false;
	}
	
	

}
