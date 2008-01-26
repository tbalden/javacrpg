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

import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.climate.impl.tropical.Tropical;

public class GorillaHorde extends AnimalEntityDescription {
	
	public static String GORILLA_TYPE_MALE = "GORILLA_MALE";
	public static String GORILLA_TYPE_FEMALE = "GORILLA_FEMALE";
	
	public static ArrayList<String> foodEntities = new ArrayList<String>();
	public static ArrayList<Class <? extends ClimateBelt>> climates = new ArrayList<Class <? extends ClimateBelt>>();
	public static ArrayList<Condition> conditions = new ArrayList<Condition>();
	
	static
	{
		foodEntities.add("");
		climates.add(Tropical.class);
	}
	
	public GorillaHorde(String id, int number)
	{
		super(id,number);
		genderType = GENDER_BOTH;
		
	}

	@Override
	public VisibleLifeForm getOne() {
		nextVisibleSequence();
		return new VisibleLifeForm(this.getClass().getName()+visibleSequence,GORILLA_TYPE_MALE);
	}

	@Override
	public ArrayList<Class <? extends ClimateBelt>> getClimates() {
		return climates;
	}

	@Override
	public ArrayList<Condition> getConditions() {
		return conditions;
	}

	@Override
	public ArrayList<String> getFoodEntities() {
		return foodEntities;
	}
	
	

}
