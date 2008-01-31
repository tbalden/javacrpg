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
import java.util.Collection;

import org.jcrpg.util.HashUtil;

public class GroupingRule {

	public int averageSize = 2;
	/**
	 * + /  - deviation from average size
	 */
	public int sizeDeviation = 1;
	
	Collection<GroupingMemberProps> possibleMembers = new ArrayList<GroupingMemberProps>();
	public Collection<EntityMemberInstance> getGroup(int size)
	{
		int counter = 0;
		ArrayList<EntityMemberInstance> members = new ArrayList<EntityMemberInstance>();
		for (GroupingMemberProps prop :possibleMembers)
		{
			members.add(new EntityMemberInstance(prop.memberType));
			counter++;
			if (counter==size) break;
		}
		return members;
	}
	
	/**
	 * This should return always the same ordered group sizes regardless of number of members of instance. 
	 * @param instance
	 * @return
	 */
	public int[][] getGroupSizes(EntityInstance instance)
	{
		int parts = instance.numberOfMembers/averageSize;
		int[][] ret = new int[parts][];
		for (int i=0; i<parts; i++)
		{
			int rand = HashUtil.mixPercentage(instance.id.hashCode(), 0, 0);
			int dev = (((int)((100f/rand)*sizeDeviation))*2)-sizeDeviation;
			ret[i] = new int[averageSize+sizeDeviation];
		}
		return ret;
	}
}
