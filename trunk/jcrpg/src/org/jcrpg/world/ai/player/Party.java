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

import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.place.World;

/**
 * Player's party.
 * @author pali
 *
 */
public class Party extends EntityDescription {

	public ArrayList<MemberPerson> members = new ArrayList<MemberPerson>();
	
	public Party(World w, Ecology eco, String id, Collection<MemberPerson> members, int x, int y, int z)
	{
		super(w,eco,id,members.size(),x,y, z);
		// TODO skill based set for the radius values:
		roamingBoundary.setRadiusInRealCubes(10);
		domainBoundary.setRadiusInRealCubes(1);
	}

	@Override
	public void liveOneTurn(Collection<EntityDescription> nearbyEntities) {
		super.liveOneTurn(nearbyEntities);
	}
	
}
