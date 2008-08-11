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
import java.util.HashSet;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;

public class GroupingRule {

	public int averageSize = 2;
	/**
	 * + /  - deviation from average size
	 */
	public int sizeDeviation = 1;
	
	public ArrayList<GroupingMemberProps> possibleMembers = new ArrayList<GroupingMemberProps>();
	
	public ArrayList<EntityMemberInstance> getGroup(int groupId, EntityFragment fragment)
	{
		ArrayList<EntityMemberInstance> members = new ArrayList<EntityMemberInstance>();
		int i=0;
		while (members.size()<fragment.instance.getGroupSizes()[groupId]) {
			members.add(new GeneratedMemberInstance(fragment, fragment.instance,fragment.instance.getGroupSizesAndTypes()[groupId].type,-1+i+groupId*100, 1));
			i++;
		}
		return members;
	}
	
	public class GroupSizeAndType
	{
		public int size;
		public EntityMember type;
	}
	
	/**
	 * This should return always the same ordered group sizes regardless of number of members of instance. 
	 * @param instance
	 * @return
	 */
	public GroupSizeAndType[] getGroupSizesAndTypes(EntityInstance instance)
	{
		int parts = instance.numberOfMembers/averageSize;
		if (parts==0) parts = 1;
		GroupSizeAndType[] ret = new GroupSizeAndType[parts];
		int count = 0;
		int sumOfLikeness = 0;
		for (GroupingMemberProps prop :possibleMembers)
		{
			sumOfLikeness+=prop.likeness;
			count++;
		}
		for (int i=0; i<parts; i++)
		{
			int randFull = HashUtil.mix(instance.id.hashCode(), i, 0);
			int rand = randFull%100;
			int dev = ((int)(((rand/100f)*sizeDeviation))*2)-sizeDeviation;
			GroupSizeAndType gst = new GroupSizeAndType();
			gst.size = averageSize+dev;
			
			if (possibleMembers.size()>0 && sumOfLikeness>0) {
				int randGroupLikeness = randFull%sumOfLikeness;
				int likenessCount = 0;
				GroupingMemberProps selected = null;
				for (GroupingMemberProps prop :possibleMembers)
				{
					selected = prop;
					likenessCount+=prop.likeness;
					if (likenessCount>=randGroupLikeness)
					{
						break;
					}
				}
				//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest(instance.fragments.fragments.get(0).getName()+" : "+selected.memberType.getName());
				gst.type = selected.memberType;
			}
			ret[i] = gst;
			//if (ret[i]==0) if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("####### "+instance.description+" ZERO SIZE");
		}
		return ret;
	}
	
	public int[] getGroupIds(EntityFragment f, int radiusRatio, int randomSeed)
	{
		int numberOfGroups = (int)(f.instance.getGroupSizes().length * 1f * radiusRatio/100f)+1;
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("getGroupIds = "+instance.description+" "+numberOfGroups);
		numberOfGroups = randomSeed%numberOfGroups; // primitive randomization for met groups
		if (numberOfGroups==0 || numberOfGroups==1 ) numberOfGroups = 1;
		if (numberOfGroups>4) numberOfGroups = 4; // TODO after removing slowness in animation node, you should remove this
		if (numberOfGroups>f.instance.getGroupSizes().length) numberOfGroups = f.instance.getGroupSizes().length;
		int[] groupIds = new int[numberOfGroups];
		HashSet<Integer> used = new HashSet<Integer>();
		for (int i=0; i<groupIds.length; i++)
		{
			Integer gId = HashUtil.mix(randomSeed,i,0)%f.instance.getGroupSizes().length;
			while (used.contains(gId))
			{
				gId++;
				gId%=f.instance.getGroupSizes().length;
			}
			groupIds[i] = gId; 
			used.add(gId);
		}
		return groupIds;
	}
	
}
