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

import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.PersistentMemberInstance;

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
	
	public EntityInstance entity;
	public PersistentMemberInstance member;
	public int groupId = -1;
	
	/**
	 * size that should be visible as a billboarded quad number above the unit when forGroup=true.
	 */
	public int size = 1;
	
	public boolean forGroup = false;
	
	public boolean notRendered = false;
	
	/**
	 * Temporary render data for callbacks.
	 */
	public RenderedMovingUnit unit;
	
	/**
	 * Starting point coords for initial render.
	 */
	public int worldX, worldY, worldZ;
	
	public boolean onSteep = false;
	
	/**
	 * Which this life form is targeted at (as hostile or friendly).
	 */
	public VisibleLifeForm targetForm = null;

	public VisibleLifeForm(String uniqueId, EntityMember type, EntityInstance entity, PersistentMemberInstance member) {
		super();
		this.uniqueId = uniqueId;
		this.type = type;
		this.entity = entity;
		this.member = member;
		forGroup = false;
		size = 1;
	}
	public VisibleLifeForm(String uniqueId, EntityMember type, EntityInstance entity, int groupId) {
		super();
		this.uniqueId = uniqueId;
		this.type = type;
		this.entity = entity;
		this.groupId = groupId;
		forGroup = true;
		size = entity.getGroupSizes()[groupId];
	}
}
