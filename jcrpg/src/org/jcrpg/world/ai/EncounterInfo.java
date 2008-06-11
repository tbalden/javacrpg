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
import org.jcrpg.world.ai.player.PartyInstance;

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
	public HashMap<EncounterUnit, int[][]> encountered = new HashMap<EncounterUnit, int[][]>();
	/**
	 * encountered instances' subgroupIds facing the possible encounter.
	 * Never forget to append the subject's data to these, encountered entities need
	 * this data for easy selection of initiator for acts.
	 */
	public HashMap<EncounterUnit, int[]> encounteredGroupIds = new HashMap<EncounterUnit, int[]>();
	public HashMap<EncounterUnit, ArrayList<EncounterUnit>> encounteredSubUnits = new HashMap<EncounterUnit, ArrayList<EncounterUnit>>();
	/**
	 * Own group ids for a given fragment.
	 * You shouldn't put subject data into THIS one! all other hashmaps need subject data.
	 */
	public HashMap<EncounterUnit, int[]> encounteredUnitsAndOwnGroupIds = new HashMap<EncounterUnit, int[]>();
	public HashMap<EncounterUnit, ArrayList<EncounterUnit>> encounteredUnitsAndOwnSubUnits = new HashMap<EncounterUnit, ArrayList<EncounterUnit>>();
	/**
	 * The subgroups of the initiator group which face the encountered.  
	 */
	public ArrayList<Integer> ownGroupIds = new ArrayList<Integer>();
	
	public ArrayList<EncounterUnit> ownSubUnits = new ArrayList<EncounterUnit>();
	
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
		r.encounteredSubUnits.putAll(encounteredSubUnits);
		r.encounteredUnitsAndOwnGroupIds.putAll(encounteredUnitsAndOwnGroupIds);
		r.encounteredUnitsAndOwnSubUnits.putAll(encounteredUnitsAndOwnSubUnits);
		r.ownGroupIds.addAll(ownGroupIds);
		r.ownSubUnits.addAll(ownSubUnits);
		return r;
	}
	
	/**
	 * Makes a copy of the info filtering for the fragment and groupid.
	 * @param f Fragment.
	 * @param groupId GroupId.
	 * @return new EncounterInfo.
	 */
	public EncounterInfo copyForFragment(EntityFragment f)
	{
		EncounterInfo r = new EncounterInfo(subjectFragment);
		r.active = active;
		r.encountered.put(f, encountered.get(f));
		r.encounteredGroupIds.put(f, encounteredGroupIds.get(f));
		// copy data of self fragment too, it is necessary for making the encountered able to select
		// the initiator as target for its acts.
		r.encountered.put(subjectFragment, encountered.get(subjectFragment));
		r.encounteredSubUnits.put(subjectFragment, encounteredSubUnits.get(subjectFragment));
		r.encounteredGroupIds.put(subjectFragment, encounteredGroupIds.get(subjectFragment));
		int[] gIds = encounteredUnitsAndOwnGroupIds.get(f);
		for (int i=0; i<gIds.length; i++)
		{
			if (ownGroupIds.contains(gIds[i])) continue;
			r.ownGroupIds.add(gIds[i]); 
		}
		return r;
	}
	
	/**
	 * This should be used to filter out neutrals in a Turn Act phase.
	 * @param notThePlayer Tells if player should be left even if neutral
	 * @param player the player entity. 
	 */
	public ArrayList<EncounterUnit> filterNeutralsForSubjectBeforeTurnAct(boolean notThePlayer, PartyInstance player)
	{
		ArrayList<EncounterUnit> keysToRemove = new ArrayList<EncounterUnit>();
		for (EncounterUnit unit:encountered.keySet())
		{
			if (unit==subjectFragment) continue;
			if (notThePlayer && unit==player) continue;
			int level = unit.getRelationLevel(subjectFragment);
			if (level==EntityScaledRelationType.NEUTRAL)
			{
				keysToRemove.add(unit);
			}
		}
		encountered.keySet().removeAll(keysToRemove);
		encounteredSubUnits.keySet().removeAll(keysToRemove);
		encounteredGroupIds.keySet().removeAll(keysToRemove);
		return keysToRemove;
	}
	
	/**
	 * Stores transient member instances for Player involved encounters.
	 */
	public transient HashMap<Integer, ArrayList<EntityMemberInstance>> generatedGroups;
	
	/**
	 * Stores a set of generated members for a given groupId (transiently).
	 * @param groupId
	 * @param members
	 */
	public void setGroupMemberInstances(int groupId, ArrayList<EntityMemberInstance> members)
	{
		if (generatedGroups == null) generatedGroups = new HashMap<Integer, ArrayList<EntityMemberInstance>>();
		generatedGroups.put(groupId, members);
	}
	
	
	
	public void appendOwnGroupIds(EncounterUnit target, int[] groupIds)
	{
		for (int i=0; i<groupIds.length; i++)
		{
			if (ownGroupIds.contains(groupIds[i])) continue;
			ownGroupIds.add(groupIds[i]);
		}
		encounteredUnitsAndOwnGroupIds.put(target, groupIds);		
	}
	public void appendOwnSubUnits(EncounterUnit target, ArrayList<EncounterUnit> subUnits)
	{
		for (EncounterUnit eu: subUnits)
		{
			if (ownSubUnits.contains(eu)) continue;
			ownSubUnits.add(eu);
		}
		encounteredUnitsAndOwnSubUnits.put(target, subUnits);		
	}
	
	
	public int getGroupsAndSubUnitsCount(EncounterUnit filtered)
	{
		int allSize = 0;
		for (EncounterUnit unit:encountered.keySet())
		{
			if (filtered!=null && unit == filtered) continue;
			int[] groupIds = encounteredGroupIds.get(unit);
			if (groupIds!=null)
			for (int in:groupIds) {
				int size = unit.getGroupSize(in);
				if (size>0) allSize++;
			}
			ArrayList<EncounterUnit> subUnits = encounteredSubUnits.get(unit);
			if (subUnits!=null)
			{
				allSize+=subUnits.size();
			}
			if (unit instanceof EntityFragment)
			{
				if (((EntityFragment)unit).alwaysIncludeFollowingMembers)
				{
					ArrayList<PersistentMemberInstance> members = ((EntityFragment)unit).getFollowingMembers();
					for (PersistentMemberInstance p:members)
					{
						if (!subUnits.contains(p))
						{
							allSize++;
						}
					}
				}
			}
		}
		return allSize;
	}
	
	public class EncounterUnitData
	{
		public boolean isGroupId = false;
		public EncounterUnit subUnit;
		public EncounterUnit parent;
		public int groupId = -1;
		public String name;
		
		public EncounterUnitData(EncounterUnit parent, EncounterUnit subUnit)
		{
			this.parent = parent;
			isGroupId = false;
			this.subUnit = subUnit;
			name = parent.getName()+" : " + subUnit.getName();
			
		}
		public EncounterUnitData(EncounterUnit parent, int groupId)
		{
			isGroupId = true;
			this.parent = parent;
			this.groupId = groupId;
			int size1 = parent.getGroupSize(groupId);
			EntityMember m = parent.getGroupType(groupId);
			name = size1+" "+ (m==null?parent.getName():m.getName()) + " (" + groupId+ ")";			
		}
		
	}
	
	public ArrayList<EncounterUnitData> getEncounterUnitDataList(EncounterUnit filtered)
	{
		
		System.out.println(" + "+this);
		for (EncounterUnit u:encounteredSubUnits.keySet())
		{
			ArrayList<EncounterUnit> u2  = encounteredSubUnits.get(u);
			if (u2!=null)
			for (EncounterUnit u3:u2)
			{
				System.out.println(subjectFragment.getName()+" : "+u.getName()+" : "+u3.getName());
			}
		}
		
		ArrayList<EncounterUnitData> list = new ArrayList<EncounterUnitData>();
		for (EncounterUnit unit:encountered.keySet())
		{
			System.out.println("--"+unit.getName());
			if (filtered!=null && unit == filtered) continue;
			int[] groupIds = encounteredGroupIds.get(unit);
			if (groupIds!=null)
			for (int in:groupIds) {
				int size = unit.getGroupSize(in);
				if (size>0) 
				{
					list.add(new EncounterUnitData(unit,in));
				}
			}
			ArrayList<EncounterUnit> subUnits = encounteredSubUnits.get(unit);
			System.out.println(subUnits+" "+(subUnits==null?"":subUnits.size()));
			if (subUnits!=null)
			{
				for (EncounterUnit u:subUnits)
				{
					EncounterUnit parent = unit;
					if (unit instanceof PersistentMemberInstance)
					{
						if ( ((PersistentMemberInstance)unit).getParentFragment()!=null)
							parent = ((PersistentMemberInstance)unit).getParentFragment();
					}
					list.add(new EncounterUnitData(parent,u));
				}
			}
			if (unit instanceof EntityFragment)
			{
				if (((EntityFragment)unit).alwaysIncludeFollowingMembers)
				{
					ArrayList<PersistentMemberInstance> members = ((EntityFragment)unit).getFollowingMembers();
					for (PersistentMemberInstance p:members)
					{
						if (subUnits==null || !subUnits.contains(p))
						{
							EncounterUnit parent = unit;
							if ( ((PersistentMemberInstance)p).getParentFragment()!=null)
								parent = ((PersistentMemberInstance)p).getParentFragment();
							list.add(new EncounterUnitData(parent,p));
						}
					}
				}
			}
		}
		return list;
		
	}
	
}

