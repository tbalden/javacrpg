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
import java.util.HashMap;

import org.jcrpg.world.ai.abs.skill.magical.Demonology;
import org.jcrpg.world.ai.abs.skill.mental.methodology.QuickPlanning;
import org.jcrpg.world.ai.abs.skill.physical.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.physical.martial.LargeBlades;
import org.jcrpg.world.ai.abs.skill.physical.martial.MediumBlades;
import org.jcrpg.world.ai.abs.skill.physical.martial.ShortBlades;
import org.jcrpg.world.ai.abs.skill.physical.martial.Wrestling;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.abs.skill.social.Chatter;

/**
 * Contains skill class types grouped in groupedSkills and one skill instance per class too in skillBaseInstances.
 * @author pali
 *
 */
public class SkillGroups {

	
	public static final String GROUP_MARTIAL = "martial";
	public static final String GROUP_SOCIAL = "social";
	public static final String GROUP_MENTAL = "mental";
	public static final String GROUP_PHYSICAL = "physical";
	public static final String GROUP_MAGICAL = "magical";
	
	
	public static HashMap<String, ArrayList<Class<? extends SkillBase>>> groupedSkills = new HashMap<String, ArrayList<Class<? extends SkillBase>>>();
	public static HashMap<Class<? extends SkillBase>,String> skillsGroup = new HashMap<Class<? extends SkillBase>,String>();
	public static ArrayList<String> orderedGroups = new ArrayList<String>();
	public static HashMap<Class<? extends SkillBase>, SkillBase> skillBaseInstances = new HashMap<Class<? extends SkillBase>, SkillBase>();
	static {
		orderedGroups.add(GROUP_MARTIAL);
		addSkillToGroup(GROUP_MARTIAL, new BiteFight());
		addSkillToGroup(GROUP_MARTIAL, new Wrestling());
		addSkillToGroup(GROUP_MARTIAL, new ShortBlades());
		addSkillToGroup(GROUP_MARTIAL, new MediumBlades());
		addSkillToGroup(GROUP_MARTIAL, new LargeBlades());
		
		orderedGroups.add(GROUP_SOCIAL);
		addSkillToGroup(GROUP_SOCIAL, new Chatter());

		orderedGroups.add(GROUP_PHYSICAL);
		addSkillToGroup(GROUP_PHYSICAL, new Tracking());
		
		orderedGroups.add(GROUP_MENTAL);
		addSkillToGroup(GROUP_MENTAL, new QuickPlanning());
		
		orderedGroups.add(GROUP_MAGICAL);
		addSkillToGroup(GROUP_MAGICAL, new Demonology());
		
	}
	
	public static void addSkillToGroup(String group, SkillBase skill)
	{
		if (!orderedGroups.contains(group)) 
		{
			System.out.println("WARNING! No such Skill Group!");
			return;
		}
		ArrayList<Class<? extends SkillBase>> list = groupedSkills.get(group);
		if (list == null)
		{
			list = new ArrayList<Class<? extends SkillBase>>();
			groupedSkills.put(group, list);
		}
		if (!list.contains(skill.getClass()))
		{
			list.add(skill.getClass());
			skillBaseInstances.put(skill.getClass(), skill);
			skillsGroup.put(skill.getClass(), group);
		}
	}
	
}
