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

import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Condition;
import org.jcrpg.world.place.World;

/**
 * Represents a description for an animal/pack of animals depending on the nature of an animal species.
 * @author illes
 *
 */
public abstract class AnimalEntityDescription {

	public String ANIMAL_NONE_TYPE = "NONE";
	
	public static final int GENDER_NEUTRAL = 0;
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;
	public static final int GENDER_BOTH = 3;
	
	public static int visibleSequence = 0;
	public static Object mutex = new Object();
	public int numberOfMembers = 1;
	public int genderType = GENDER_NEUTRAL;
	
	public DistanceBasedBoundary boundary = null;
	
	public abstract ArrayList<String> getFoodEntities();
	public abstract ArrayList<Class <? extends ClimateBelt>> getClimates();
	public abstract ArrayList<Condition> getConditions();
	
	/**
	 * Unique id in the worlds.
	 */
	public String id;
	
	public AnimalEntityDescription(World w, String id, int numberOfMembers, int startX, int startY, int startZ) {
		super();
		this.id = id;
		this.numberOfMembers = numberOfMembers;
		boundary = new DistanceBasedBoundary(w,startX,startY,startZ,0);
	}

	public VisibleLifeForm getOne()
	{
		nextVisibleSequence();
		return new VisibleLifeForm(this.getClass().getName()+visibleSequence,ANIMAL_NONE_TYPE);
	}
	
	public int nextVisibleSequence()
	{
		synchronized (mutex) {
			visibleSequence++;
		}
		return visibleSequence;
	}
	
	public void liveOneTurn()
	{
		System.out.println("LIVE ONE TURN "+this.getClass()+" "+id);
	}
}
