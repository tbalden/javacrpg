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

import java.util.HashMap;

import org.jcrpg.world.ai.EntityDescription;
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
	
	public static int MAJOR = SkillLearnModifier.MAJOR;
	public static int MINOR = SkillLearnModifier.MAJOR;
	public static int GENDER_MALE = EntityDescription.GENDER_MALE;
	public static int GENDER_FEMALE = EntityDescription.GENDER_FEMALE;

	public HashMap<Class<? extends SkillBase>, Integer> additionalLearntSkills = new HashMap<Class<? extends SkillBase>,Integer>();
	public SkillLearnModifier skillLearnModifier = new SkillLearnModifier();
	public AttributeMinLevels attrMinLevels = new AttributeMinLevels();
	
	public int genderNeed = EntityDescription.GENDER_BOTH;
	
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
	
	public boolean validForGender(int gender)
	{
		if (genderNeed==EntityDescription.GENDER_BOTH)
		{
			return true;
		} else
		{
			if (genderNeed==gender) 
				return true;
		}
		return false;
	}
	
	public void addSkill(Class<? extends SkillBase> skill, int level, int multiplier)
	{
		additionalLearntSkills.put(skill, level);
		skillLearnModifier.multipliers.put(skill, multiplier);
	}
	
	public void addMinorSkill(Class<? extends SkillBase> skill)
	{
		addSkill(skill, 6, MINOR);
	}
	public void addMajorSkill(Class<? extends SkillBase> skill)
	{
		addSkill(skill, 10, MAJOR);
	}
	
}
