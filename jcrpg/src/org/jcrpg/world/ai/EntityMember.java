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

import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.skill.SkillContainer;


/**
 * Specially described dependent member of an EntityDescription.
 * @author pali
 *
 */
public class EntityMember {
	public String visibleTypeId;
	public static SkillContainer commonSkills = new SkillContainer();
	public static AttributeRatios commonAttributeRatios = new AttributeRatios();
	public EntityMember(String visibleTypeId) {
		super();
		this.visibleTypeId = visibleTypeId;
	}
	
	public static AttributeRatios getCommonAttributes() {
		return commonAttributeRatios;
	}

	public static SkillContainer getCommonSkills() {
		return commonSkills;
	}
	
	public Attributes getAttributes(EntityDescription parent, String attr)
	{
		return Attributes.getAttributes(parent.attributes, commonAttributeRatios);
	}



}
