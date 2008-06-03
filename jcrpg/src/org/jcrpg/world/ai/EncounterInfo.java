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
import java.util.HashMap;

import org.jcrpg.world.ai.EntityFragments.EntityFragment;

/**
 * Class containing a possible encounter's data. 
 * @author illes
 */
public class EncounterInfo {

	public boolean active = false;
	/**
	 * The initiator group that faces the encounter.
	 */
	public EntityInstance subject;
	public EntityFragment subjectFragment;
	/**
	 * Encountered instances and their common radius ratios and middle point data.
	 * Never forget to append the subject's data to these, encountered entities need
	 * this data for easy selection of initiator for acts.
	 */
	public HashMap<EntityFragment, int[][]> encountered = new HashMap<EntityFragment, int[][]>();
	/**
	 * encountered instances' subgroupIds facing the possible encounter.
	 * Never forget to append the subject's data to these, encountered entities need
	 * this data for easy selection of initiator for acts.
	 */
	public HashMap<EntityFragment, int[]> encounteredGroupIds = new HashMap<EntityFragment, int[]>();
	/**
	 * Own group ids for a given fragment.
	 * You shouldn't put subject data into THIS one! all other hashmaps need subject data.
	 */
	public HashMap<EntityFragment, int[]> encounteredFragmentsAndOwnGroupIds = new HashMap<EntityFragment, int[]>();
	/**
	 * The subgroups of the initiator group which face the encountered.  
	 */
	public ArrayList<Integer> ownGroupIds = new ArrayList<Integer>();
	
	//public  
	
	
	public EncounterInfo(EntityFragment subjectFragment) {
		super();
		this.subject = subjectFragment.instance;
		this.subjectFragment = subjectFragment;
	}
	
	public EncounterInfo copy()
	{
		EncounterInfo r = new EncounterInfo(subjectFragment);
		r.active = active;
		r.encountered.putAll(encountered);
		r.encounteredGroupIds.putAll(encounteredGroupIds);
		r.ownGroupIds.addAll(ownGroupIds);
		return r;
	}
	
	/**
	 * Makes a copy of the info filtering for the fragment and groupid.
	 * @param f Fragment.
	 * @param groupId GroupId.
	 * @return new EncounterInfo.
	 */
	public EncounterInfo copyForFragmentAndGroupId(EntityFragment f)
	{
		EncounterInfo r = new EncounterInfo(subjectFragment);
		r.active = active;
		r.encountered.put(f, encountered.get(f));
		r.encounteredGroupIds.put(f, encounteredGroupIds.get(f));
		// copy data of self fragment too, it is necessary for making the encountered able to select
		// the initiator as target for its acts.
		r.encountered.put(subjectFragment, encountered.get(subjectFragment));
		r.encounteredGroupIds.put(subjectFragment, encounteredGroupIds.get(subjectFragment));
		int[] gIds = encounteredFragmentsAndOwnGroupIds.get(f);
		for (int i=0; i<gIds.length; i++)
		{
			if (ownGroupIds.contains(gIds[i])) continue;
			r.ownGroupIds.add(gIds[i]); 
		}
		return r;
	}
	
	/**
	 * Stores member instances for Player involved encounters.
	 */
	public HashMap<Integer, ArrayList<EntityMemberInstance>> generatedGroups;
	
	public void setGroupMemberInstances(int groupId, ArrayList<EntityMemberInstance> members)
	{
		if (generatedGroups == null) generatedGroups = new HashMap<Integer, ArrayList<EntityMemberInstance>>();
		generatedGroups.put(groupId, members);
	}
	
	public void appendOwnGroupIds(EntityFragment target, int[] groupIds)
	{
		for (int i=0; i<groupIds.length; i++)
		{
			if (ownGroupIds.contains(groupIds[i])) continue;
			ownGroupIds.add(groupIds[i]);
		}
		encounteredFragmentsAndOwnGroupIds.put(target, groupIds);		
	}
	
	
}
