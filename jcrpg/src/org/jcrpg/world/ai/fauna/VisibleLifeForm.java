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

package org.jcrpg.world.ai.fauna;

import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityMember;

/**
 * Different entities/groups when met visibly should show their appearance through this base class or its extension
 * for the J3DMobEngine.
 * @author illes
 */
public class VisibleLifeForm {

	/**
	 * Marks uniquely an instance of Visible lifeform
	 */
	public String uniqueId;
	/**
	 * Generic type of the life form - for 3d display type
	 */
	public EntityMember type;
	
	public EntityDescription entity;
	public EntityMember member;

	public VisibleLifeForm(String uniqueId, EntityMember type, EntityDescription entity, EntityMember member) {
		super();
		this.uniqueId = uniqueId;
		this.type = type;
		this.entity = entity;
		this.member = member;
	}
}
