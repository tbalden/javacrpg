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

package org.jcrpg.world.ai.player;

import java.util.ArrayList;
import java.util.Collection;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.place.World;

public class PartyInstance extends EntityInstance {

	
	public boolean noticeFriendly = true;
	public boolean noticeNeutral = false;
	public boolean noticeHostile = true;
	
	ArrayList<PreEncounterInfo> infos = new ArrayList<PreEncounterInfo>();
	@Override
	public boolean liveOneTurn(Collection<PreEncounterInfo> nearbyEntities) {
		if (this.equals(J3DCore.getInstance().gameState.player))
		{
			infos.clear();
			// ! filtering actives -> statically used PreEncounterInfo instances need a copy for thread safe use!
			int listSize = 0;
			for (PreEncounterInfo i:nearbyEntities)
			{
				if (!i.active) continue;
				int fullSize = 0;
				for (EntityInstance entityInstance:i.encountered.keySet())
				{
					int[] groupIds = i.encounteredGroupIds.get(entityInstance);
					if (groupIds.length==0) {
						System.out.println("NO GROUPID IN ARRAY: "+entityInstance.description+" - "+entityInstance.numberOfMembers);
					}
					for (int in:groupIds) {
						int size = entityInstance.getGroupSizes()[in];
						if (size==0) System.out.println("SIZE ZERO: "+entityInstance.description);
						fullSize+=size;
					}
				}
				if (fullSize>0) {
					infos.add(i.copy());
					listSize++;
				}
			}
			if (listSize>0) // only if groups can be encountered should we trigger newturn
			{
				J3DCore.getInstance().gameState.playerTurnLogic.newTurn(infos,Ecology.PHASE_INTERCEPTION,true);
				return true; // interrupt ecology!
			}
			else
			{
				J3DCore.getInstance().gameState.engine.turnFinishedForPlayer();
			}
			return false; // don't interrupt ecology
		}
		return false;
	}
	
	public ArrayList<EntityMemberInstance> orderedParty = new ArrayList<EntityMemberInstance>();
	
	public PartyInstance(EntityDescription description, World w, Ecology ecology, int numericId, String id, int numberOfMembers,
			int startX, int startY, int startZ) {
		super(description, w, ecology, numericId, id, numberOfMembers, startX, startY, startZ);
	}

	public void addPartyMember(EntityMemberInstance m)
	{
		if (m.description instanceof MemberPerson) {
			MemberPerson desc = (MemberPerson)m.description;
			fixMembers.put(desc.foreName+orderedParty.size(), m);
			orderedParty.add(m);
			numberOfMembers++;
		}
		
	}
	
	public void addPartyMemberInstance(String id, String foreName, String sureName, String picId, AudioDescription audio)
	{
		PartyMember member = new PartyMember(id,audio);
		EntityMemberInstance mI = new EntityMemberInstance(member, EntityMemberInstance.getNextNumbericId());
		fixMembers.put(id, mI);
		orderedParty.add(mI);
		numberOfMembers++;
	}
	public void removePartyMemberInstance(String id)
	{
		fixMembers.remove(id);
		numberOfMembers--;
	}
	
	public void addPartyMemberInstance(PartyMember m)
	{
		EntityMemberInstance mI = new EntityMemberInstance(m, EntityMemberInstance.getNextNumbericId());
		fixMembers.put(m.id, mI);
		numberOfMembers++;
	}
	public void removePartyMemberInstance(PartyMember m)
	{
		fixMembers.remove(m.id);
		numberOfMembers--;
	}
	
	
}
