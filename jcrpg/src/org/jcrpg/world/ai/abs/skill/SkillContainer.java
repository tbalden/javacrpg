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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.world.ai.Ecology;

public class SkillContainer {
	
	public HashMap<Class<? extends SkillBase>,SkillInstance> skills = new HashMap<Class<? extends SkillBase>, SkillInstance>();
	public HashMap<Class, HashSet<Class<? extends SkillBase>>> skillTypeGroupedBases = new HashMap<Class, HashSet<Class<? extends SkillBase>>>();
	
	public void addSkill(SkillInstance instance)
	{
		if (!skills.containsKey(instance.skill))
		{
			skills.put(instance.skill, instance);
			Class[] ifaces = instance.skill.getInterfaces();
			
			for (Class c:ifaces)
			{
				if (SkillGroups.skillTypeInterfaces.contains(c)) 
				{
					HashSet<Class<? extends SkillBase>> set = skillTypeGroupedBases.get(c);
					if (set==null)
					{
						set = new HashSet<Class<? extends SkillBase>>();
						skillTypeGroupedBases.put(c, set);
					}
					set.add(instance.skill);
				}
			}
		}
	}
	public void setSkillValue(Class<? extends SkillBase> skill, int level)
	{
		if (skills.get(skill)!=null)
		{
			skills.get(skill).level = level;
		} else
		{
			addSkill(new SkillInstance(skill,level));
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
	
	public Collection<Class<? extends SkillBase>> getSkillsOfType(int phase)
	{
		if (phase == Ecology.PHASE_INTERCEPTION)
		{
			return getSkillsOfType(InterceptionSkill.class);
		} else
		if (phase == Ecology.PHASE_ENCOUNTER)
		{
			return getSkillsOfType(EncounterSkill.class);
		} else
		if (phase == Ecology.PHASE_TURNACT_COMBAT)
		{
			return getSkillsOfType(TurnActSkill.class);
		} else
		if (phase == Ecology.PHASE_TURNACT_SOCIAL_RIVALRY)
		{
			Collection<Class<?extends SkillBase>> skills = getSkillsOfType(TurnActSkill.class);
			Collection<Class<?extends SkillBase>> filtered = new ArrayList<Class<?extends SkillBase>>();
			for (Class<?extends SkillBase> s:skills)
			{
				if (SkillGroups.groupedSkills.get(SkillGroups.GROUP_SOCIAL).contains(s))
				{
					filtered.add(s);
				}
			}
			return filtered;
		}
		return null;
		
	}
	
	public Collection<Class<? extends SkillBase>> getSkillsOfType(Class type)
	{
		if (skillTypeGroupedBases==null) // workaround for old exported chars
		{
			HashMap<Class<? extends SkillBase>, SkillInstance> oldSkills = skills;
			skills = new HashMap<Class<? extends SkillBase>, SkillInstance>();
			skillTypeGroupedBases = new HashMap<Class, HashSet<Class<? extends SkillBase>>>();
			for (SkillInstance i:oldSkills.values())
			{
				addSkill(i);
			}
			skills = oldSkills;
		}
		return skillTypeGroupedBases.get(type);
	}
	
	public void updateSkillActForms()
	{
		for (SkillInstance i:skills.values())
		{
			i.updateAvailableActForms();
		}
	}
	
	public SkillContainer copy()
	{
		SkillContainer c = new SkillContainer();
		for (SkillInstance s:skills.values())
		{
			c.addSkill(s.copy());
		}
		return c;
	}

}
