/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */                                                           

package org.jcrpg.world.ai.fauna;

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;

public class PerceptedVisibleForm extends VisibleLifeForm {

	public EntityFragment fragment;
	
	public PerceptedVisibleForm(String uniqueId, EntityMember type, EntityInstance entity, EntityFragment fragment,PersistentMemberInstance member) {
		super(uniqueId,type,entity,member);
		this.fragment = fragment;
		
	}
	public PerceptedVisibleForm(String uniqueId, EntityMember type,
			EntityInstance entity, EntityFragment fragment, int groupId) {
		super(uniqueId, type, entity, groupId);
		this.fragment = fragment;
	}

	String id = null;
	public String getIdentifier()
	{
		if (id==null)
		{
			id = fragment.getNumericId()+" " +(member==null?"null":member.getNumericId())+ " "+type.getName()+" "+groupId;
		}
		return id;
	}
	
}
