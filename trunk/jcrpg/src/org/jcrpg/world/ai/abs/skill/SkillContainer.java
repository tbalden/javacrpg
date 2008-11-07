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

package org.jcrpg.world.ai.abs.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityMember.SkillPreferenceHint;
import org.jcrpg.world.ai.profession.Profession;

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

	public Collection<Class<? extends SkillBase>> getTurnActSkillsOrderedBySkillLevel(int phase, SkillContainer modifier, int targetLineUpDistance)
	{
		TreeMap<Integer, Class<? extends SkillBase>> order = new TreeMap<Integer, Class<? extends SkillBase>>();
		
		Collection<Class<?extends SkillBase>> list = getSkillsOfType(Ecology.PHASE_TURNACT_COMBAT);
		if (list!=null)
		for (Class<?extends SkillBase> s:list)
		{
			SkillBase base = SkillGroups.skillBaseInstances.get(s);
			if (base instanceof TurnActSkill)
			{
				int lineupLimit = ((TurnActSkill)base).getUseRangeInLineup();
				if (targetLineUpDistance>lineupLimit+2) continue;
			}
			int level = 1000-getSkillLevel(s, modifier);
			while (order.get(level)!=null)
			{
				level++;
			}
			order.put(level, s);
		}
		return order.values();
	}

	public Collection<Class<? extends SkillBase>> getSkillsOfTypeOrderedBySkillLevel(int phase,SkillContainer modifier)
	{
		TreeMap<Integer, Class<? extends SkillBase>> order = new TreeMap<Integer, Class<? extends SkillBase>>();
		
		Collection<Class<?extends SkillBase>> list = getSkillsOfType(phase);
		for (Class<?extends SkillBase> s:list)
		{
			int level = 1000-getSkillLevel(s, modifier);
			while (order.get(level)!=null)
			{
				level++;
			}
			order.put(level, s);
		}
		return order.values();
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
	
	public SkillInstance getHighestLevelHelperSkill(Class<? extends SkillBase> skill, String tagWord)
	{
		SkillInstance highestLevel = null;
		for (Class<? extends SkillBase> s:getSkillsOfType(HelperSkill.class))
		{
			if (((HelperSkill)SkillGroups.skillBaseInstances.get(s)).helpsForTag(skill, tagWord))
			{
				SkillInstance i = skills.get(s);
				if (highestLevel==null)
				{
					highestLevel = i;
				} else
				{
					if (i.level>highestLevel.level)
					{
						highestLevel = i;
					}
				}
			}
		}
		return highestLevel;
	}
	
	public void levelUpSkills(EntityMemberInstance instance, SkillPreferenceHint hint, int points)
	{

		// no skill to increase...
		if (skills.keySet().size()==0) return; // return.

		float sum = 0;
		int count = 0;
		Profession p = null;
		try {
			J3DCore.getInstance().gameState.charCreationRules.getProfession(instance.description.professions.get(0));
		} catch (Exception ex)
		{
			
		}
		for (Class<? extends SkillBase> key : skills.keySet())
		{
			int mod = p==null?1:p.skillLearnModifier.getMultiplier(key);
			{
				// summarizing all skill multiplier 
				sum+=mod;
				count++;
			}
		}
		int seed = instance.getNumericId();
		while (points>0)
		{
			count ++;
			for (Class<? extends SkillBase> key : skills.keySet())
			{
				int mod = p==null?1:p.skillLearnModifier.getMultiplier(key);
				{
					float modF = mod*1f/sum; // calculating the ratio of a given skill in the full spectrum based on its multiplier.
					int i = (int)(modF * 1000);
					if (i>HashUtil.mixPer1000(seed+count, points, count)) // rolled value is below, so we will increase that skill...
					{
						setSkillValue(key, getSkillLevel(key, null)+(1 * mod));
						System.out.println(instance.description.getName()+" INCREASING SKILL "+key);
						points--;
					}
				}
			}
		}
	}
	

}
