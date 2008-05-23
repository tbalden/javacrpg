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

package org.jcrpg.world.ai;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.skill.InterceptionSkill;
import org.jcrpg.world.ai.abs.state.EntityMemberState;
import org.jcrpg.world.place.Economic;

public class EntityMemberInstance {

	
	/**
	 * The "genetic" heritage of the instance.
	 */
	public EntityMember description = null;
	
	/**
	 * The existing relations' class of this member instance.
	 */
	public EntityMemberRelations personalRelations = new EntityMemberRelations();
	
	public int numericId = -1;
	
	/**
	 * Those infrastructures (buildings) owned by this member in its home population.
	 * Not the actual instances only Class types, because based on this list the buildProgram 
	 * of the Infrastructure 
	 * is modified only, so that in every sizeLevel state of the district the MemberInstance
	 * will have its infrastructure.
	 *
	 */
	public ArrayList<Class<? extends Economic>> ownedInfrastructures = null;
	
	/**
	 * The different points and such of the memberInstance.
	 */
	public EntityMemberState memberState = new EntityMemberState();
	
	/**
	 * The skill that the given instance is using for his behavior of living around at the current turn.
	 */
	public InterceptionSkill behaviorSkill = null;
	
	public EntityMemberInstance(EntityMember description, int numericId) {
		super();
		this.description = description;
		this.numericId = numericId;
	}

	public static int numericIdSequence = 0;
	
	public final static synchronized int getNextNumbericId()
	{
		return numericIdSequence++;
	}
	
}
