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

import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.PositionCalculus;
import org.jcrpg.world.ai.position.NormalCalculus;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.place.Geography;

/**
 * Represents a description for an animal/pack of animals depending on the nature of an animal species.
 * @author illes
 *
 */
public abstract class AnimalEntityDescription extends EntityDescription {

	public static Class<? extends PositionCalculus> positionCalcType = NormalCalculus.class;

	public ArrayList<Class <? extends EntityDescription>> foodEntities = new ArrayList<Class <? extends EntityDescription>>();
	public ArrayList<Class <? extends ClimateBelt>> climates = new ArrayList<Class <? extends ClimateBelt>>();
	public ArrayList<Condition> conditions = new ArrayList<Condition>();
	public ArrayList<Class <? extends Geography>> geographies = new ArrayList<Class <? extends Geography>>();


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
	
	
	public ArrayList<Class <? extends EntityDescription>> getFoodEntities()
	{
		return foodEntities;
	}
	public ArrayList<Class <? extends ClimateBelt>> getClimates()
	{
		return climates;
	}
	public ArrayList<Condition> getConditions()
	{
		return conditions;
	}
	
	public ArrayList<Class <? extends Geography>> getGeographies()
	{
		return geographies;
	}

	@Override
	public boolean isPrey(EntityDescription desc) {
		
		if (getFoodEntities().contains(desc.getClass()))
			return true;
		return false;
	}
	
	

}
