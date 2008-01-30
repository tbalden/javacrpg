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

public class GroupingMemberProps {
	
	public static final int UNDEFINED = -1;

	/**
	 * Likeness of the member to happen in the group.
	 */
	public int likeness = 50;
	public int maxNumberInAGroup = UNDEFINED;
	public int minNumberInAGroup = UNDEFINED;
	public EntityMember memberType;
	
	public GroupingMemberProps(int likeness, int maxNumberInAGroup, int minNumberInAGroup,
			EntityMember memberType) {
		super();
		this.likeness = likeness;
		this.maxNumberInAGroup = maxNumberInAGroup;
		this.minNumberInAGroup = minNumberInAGroup;
		this.memberType = memberType;
	}
	
	
}
