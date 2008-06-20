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

import org.jcrpg.game.logic.Impact.ImpactUnit;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

public class EncounterUnitData
{
	public boolean isGroupId = false;
	public EncounterUnit subUnit;
	public EncounterUnit parent;
	public int groupId = -1;
	public String name;
	public int currentLine = 0;
	
	public VisibleLifeForm visibleForm = null;
	
	public ArrayList<EntityMemberInstance> generatedMembers = new ArrayList<EntityMemberInstance>();
	
	public EncounterUnitData(EncounterUnit parent, EncounterUnit subUnit)
	{
		this.parent = parent;
		isGroupId = false;
		this.subUnit = subUnit;
		name = parent.getName()+" : " + subUnit.getName();
		groupId = 0;
		
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
	
	/**
	 * Returns hint for enc phase lineup.
	 * @return
	 */
	public int getEncPhasePriority(EncounterInfo info)
	{
		return getUnit().getEncPhasePriority(info);
	}
	/**
	 * Returns hint for enc phase lineup.
	 * @return
	 */
	public int getTurnActPhasePriority(EncounterInfo info)
	{
		// TODO voting etc...
		return getUnit().getEncPhasePriority(info);
	}
	
	public EncounterUnit getUnit()
	{
		if (isGroupId)
		{
			return parent;
		}
		return subUnit;
	}
	
	public int getSize()
	{
		if (isGroupId)
		{
			return parent.getGroupSize(groupId);
		} else
		{
			return subUnit.getGroupSize(0);
		}
	}
	
	public VisibleLifeForm setupVisibleLifeForm()
	{
		if (isGroupId)
		{
			if (parent.getGroupSize(groupId)==0) return null;
			visibleForm = parent.getOne(groupId);
			visibleForm.encounterUnitData = this;
		} else
		{
			
			visibleForm = subUnit.getOne(0);
			visibleForm.encounterUnitData = this;
		}
		return visibleForm;
	}
	
	public int getRelationLevel(EncounterUnitData unit)
	{
		return getUnit().getRelationLevel(unit.getUnit());
	}
	public int getRelationLevel(EncounterUnit unit)
	{
		return getUnit().getRelationLevel(unit);
	}
	
	public void appendNewMembers(ArrayList<EntityMemberInstance> members)
	{
		generatedMembers.addAll(members);
	}
	public String getName() {
		return name;
	}
	/**
	 * Update name text upon new round in turn act phase.
	 */
	public void updateNameInTurnActPhase()
	{
		if (isGroupId)
		{
			EntityMember m = parent.getGroupType(groupId);
			name = generatedMembers.size()+" "+ (m==null?parent.getName():m.getName()) + " (" + groupId+ ")";	
		}
	}
	
	public void applyImpactUnit(ImpactUnit unit)
	{
		if (isGroupId)
		{
			if (generatedMembers.size()>0)
			{
				int groupSize = unit.effectedGroupSize;
				// TODO randomize selection of member.
				for (int i=0; i<groupSize; i++) {
					if (i<generatedMembers.size()) 
					{
						generatedMembers.get(i).applyImpactUnit(unit);
					}
				}
			}
		} else
		{
			if (subUnit instanceof EntityMemberInstance)
			{
				((EntityMemberInstance)subUnit).applyImpactUnit(unit);
			}
		}
	}
	
	
}
