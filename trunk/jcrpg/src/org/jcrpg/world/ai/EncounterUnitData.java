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
	
	public int getRelationLevel(EncounterUnitData unit)
	{
		return getUnit().getRelationLevel(unit.getUnit());
	}
	public int getRelationLevel(EncounterUnit unit)
	{
		return getUnit().getRelationLevel(unit);
	}
	
}
