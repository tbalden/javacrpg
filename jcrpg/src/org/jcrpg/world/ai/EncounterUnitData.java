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

import org.jcrpg.game.logic.Impact;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

public class EncounterUnitData
{
	public boolean isGroupId = false;
	public EncounterUnit subUnit;
	public EncounterUnit parent;
	public int groupId = -1;
	public String name;
	public int currentLine = 0;
	
	/**
	 * indicates if this unit is friendly for player or not.
	 */
	public boolean friendly = false; // TODO setting of this in encounterLogic
	
	public VisibleLifeForm visibleForm = null;
	
	public ArrayList<EntityMemberInstance> generatedMembers = null;
	
	public EntityMember description = null;
	
	public EncounterUnitData(EncounterUnit parent, EncounterUnit subUnit)
	{
		this.parent = parent;
		isGroupId = false;
		this.subUnit = subUnit;
		name = parent.getName()+" : " + subUnit.getName();
		if (subUnit.getDescription() instanceof EntityMember) description = (EntityMember)subUnit.getDescription();
		groupId = 0;
		
	}
	public EncounterUnitData(EncounterUnit parent, int groupId)
	{
		isGroupId = true;
		this.parent = parent;
		this.groupId = groupId;
		int size1 = parent.getGroupSize(groupId);
		EntityMember m = parent.getGroupType(groupId);
		description = m;
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
			if (generatedMembers!=null) return generatedMembers.size();			 
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
		if (generatedMembers==null) generatedMembers = new ArrayList<EntityMemberInstance>();
		generatedMembers.addAll(members);
	}
	public String getName() {
		return name;
	}
	
	/**
	 * If unit has no more living members, this will be true.
	 */
	public boolean destroyed = false;
	/**
	 * Unit is destroyed, set things for destroy.
	 */
	public void destroyed()
	{
		destroyed = true;
		// TODO gamelogic unit clear?
	}
	
	/**
	 * Remove unit from encounter scenario.
	 */
	public void clearUnitOut()
	{
		if (!visibleForm.notRendered) {
			J3DCore.getInstance().mEngine.clearUnit(visibleForm.unit);
		}
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
		if (isRendered())
			J3DCore.getInstance().mEngine.updateUnitTextNodes(visibleForm.unit);
	}
	
	public void applyImpactUnit(Impact unit)
	{
		if (isGroupId)
		{
			if (generatedMembers!=null)
			if (generatedMembers.size()>0)
			{
				int groupSize = generatedMembers.size();
				// TODO randomize selection of member.
				for (int i=0; i<groupSize; i++) {
					if (i<generatedMembers.size()) 
					{
						EntityMemberInstance inst = generatedMembers.get(i);
						ImpactUnit u = unit.targetImpact.get(inst);
						if (u!=null)
							inst.applyImpactUnit(u);
					}
				}
			}
			if (generatedMembers!=null)
			if (generatedMembers.size()==0)
			{
				destroyed();
			}
		} else
		{
			if (subUnit instanceof EntityMemberInstance)
			{
				ImpactUnit u = unit.targetImpact.get(subUnit);
				if (u!=null)
					((EntityMemberInstance)subUnit).applyImpactUnit(u);
			}
		}
	}
	
	public EntityMemberInstance getFirstLivingMember()
	{
		if (isGroupId)
		{
			if (generatedMembers==null) return null;
			if (generatedMembers.size()==0) return null;
			return generatedMembers.get(0);
		} else
		{
			if (subUnit instanceof EntityMemberInstance)
			{
				return (EntityMemberInstance)subUnit;
			}
		}
		return null;
		
	}
	
	public boolean isRendered()
	{
		if (visibleForm==null) return false;
		return !visibleForm.notRendered;
	}
	
	/**
	 * Returns a random sound of a given type if available.
	 * @param type
	 * @return
	 */
	public String getSound(String type)
	{
		return description.getSound(type);
	}
	
	public boolean isDead()
	{
		if (isGroupId)
		{
			if (generatedMembers==null || generatedMembers.size()==0)
			{
				return true;
			}
			return false;
		} else
		{
			return ((EntityMemberInstance)subUnit).memberState.isDead();			
		}
		
	}
	
}