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

import org.jcrpg.util.Language;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.World;

public class PersistentMemberInstance extends EntityMemberInstance implements EncounterUnit {

	/**
	 * Fixed members
	 */
	public DistanceBasedBoundary roamingBoundary = null;

	public PersistentMemberInstance(EntityMember description, World w,
			int numericId, int startX, int startY, int startZ) {
		super(description, numericId);
		if (w!=null) {
			roamingBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,description.getRoamingSize());
		}
	}

	public DistanceBasedBoundary getEncounterBoundary() {
		return roamingBoundary;
	}

	public int getNumericId() {
		return numericId;
	}

	public long getLevel() {
		return memberState.level;
	}

	public DescriptionBase getDescription() {
		return description;
	}

	public ArrayList<EntityMemberInstance> getGroup(int groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getGroupSize(int groupId) {
		return 1;
	}

	public VisibleLifeForm getOne(EntityMemberInstance member) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRelationLevel(EncounterUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		return Language.v("member."+description.getClass().getSimpleName());
	}

	public int getSize() {
		return 1;
	}
	
	static final int[] groupId = new int[]{0};
	public int[] getGroupIds(int radiusRatio, int randomSeed) {
		return groupId;
	}

}
