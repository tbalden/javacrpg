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

import org.jcrpg.game.PlayerTurnLogic;
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

	
	@Override
	public void liveOneTurn(Collection<PreEncounterInfo> nearbyEntities) {
		if (this.equals(J3DCore.getInstance().gameState.player))
		{
			J3DCore.getInstance().gameState.playerTurnLogic.newTurn(nearbyEntities,Ecology.PHASE_INTERCEPTION,true);
		}
	}
	
	public ArrayList<EntityMemberInstance> orderedParty = new ArrayList<EntityMemberInstance>();
	
	public PartyInstance(EntityDescription description, World w, Ecology ecology, String id, int numberOfMembers,
			int startX, int startY, int startZ) {
		super(description, w, ecology, id, numberOfMembers, startX, startY, startZ);
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
		EntityMemberInstance mI = new EntityMemberInstance(member);
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
		EntityMemberInstance mI = new EntityMemberInstance(m);
		fixMembers.put(m.id, mI);
		numberOfMembers++;
	}
	public void removePartyMemberInstance(PartyMember m)
	{
		fixMembers.remove(m.id);
		numberOfMembers--;
	}
	
	
}
