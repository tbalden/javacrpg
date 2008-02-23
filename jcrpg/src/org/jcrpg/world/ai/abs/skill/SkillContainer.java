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

package org.jcrpg.world.ai.abs.skill;

import java.util.Collection;
import java.util.HashMap;

public class SkillContainer {
	
	public HashMap<Class<? extends SkillBase>,SkillInstance> skills = new HashMap<Class<? extends SkillBase>, SkillInstance>();
	
	public void addSkill(SkillInstance instance)
	{
		if (!skills.containsKey(instance.skill))
		{
			skills.put(instance.skill, instance);
		}
	}
	public void setSkillValue(Class<? extends SkillBase> skill, int level)
	{
		if (skills.get(skill)!=null)
		{
			skills.get(skill).level = level;
		} else
		{
			skills.put(skill, new SkillInstance(skill,level));
		}
	}
	public void addSkills(Collection<SkillInstance> instances)
	{
		if (instances!=null)
		for (SkillInstance skill:instances)
		{
			addSkill(skill);
		}
	}

	public int getSkillLevel(Class<? extends SkillBase> skillType, SkillContainer modifier)
	{
		if (modifier==null)
		{
			return (skills.get(skillType)!=null?skills.get(skillType).level:0);
		}
		return (skills.get(skillType)!=null?skills.get(skillType).level:0)+(modifier.skills.get(skillType)!=null?modifier.skills.get(skillType).level:0);
		
	}

}
