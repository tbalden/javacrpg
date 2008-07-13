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

import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.skill.SkillContainer;

public class GeneratedMemberInstance extends EntityMemberInstance {


	/**
	 * Generated attributes for the level of member.
	 */
	private Attributes generatedAttributes = null;
	
	/**
	 * Generated skill levels for the level of member.
	 */
	private SkillContainer generatedSkills = new SkillContainer();
	
	public GeneratedMemberInstance(EntityFragment parent,
			EntityInstance instance, EntityMember description, int numericId, int level) {
		super(parent, instance, description, numericId);
		memberState.level = level;
		// TODO generate skill/attribute level additions for the level
		generatedSkills = description.memberSkills.copy();
	}

	@Override
	public SkillContainer getSkills() {
		return generatedSkills;
	}

	@Override
	public Attributes getAttributesVanilla() {
		// TODO merging generated instance attribute levels 
		Attributes base = super.getAttributesVanilla();
		if (generatedAttributes!=null) base.appendAttributes(generatedAttributes);
		return base;
	}
	
	

}
