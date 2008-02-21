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

package org.jcrpg.world.ai.profession;

import java.util.HashSet;

import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillBase;

/**
 * A being can learn a profession with certain attributes in a school.
 * It will give a momentum to succeed in certain skill learning and
 * can add a bunch of new skills that can be learnt by the professional.
 * Any entity member can have a profession if the attributes are okay. 
 * @author pali
 *
 */
public class Profession {

	public HashSet<Class<? extends SkillBase>> additionalLearntSkills = new HashSet<Class<? extends SkillBase>>();
	public SkillLearnModifier skillLearnModifier = new SkillLearnModifier();
	public AttributeMinLevels attrMinLevels = new AttributeMinLevels();
	
	public boolean isQualifiedEnough(Attributes attr)
	{
		for (String name : FantasyAttributes.attributeName)
		{
			Integer value = attrMinLevels.minimumLevels.get(name);
			if (value!=null)
			{
				int iV = value;
				Integer value2 = attr.getAttribute(name);
				if (value2 == null) return false;
				int iV2 = value2;
				if (iV2<iV) return false;
			}
		}
		return true;
	}
	
	
}
