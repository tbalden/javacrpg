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

import org.jcrpg.world.ai.abs.skill.magical.Alchemy;
import org.jcrpg.world.ai.abs.skill.magical.CelestialMagic;
import org.jcrpg.world.ai.abs.skill.magical.Demonology;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.abs.skill.magical.Mentalism;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.abs.skill.martial.Bows;
import org.jcrpg.world.ai.abs.skill.martial.Crossbows;
import org.jcrpg.world.ai.abs.skill.martial.HammerAndAxe;
import org.jcrpg.world.ai.abs.skill.martial.HandsAndFeet;
import org.jcrpg.world.ai.abs.skill.martial.LargeBlades;
import org.jcrpg.world.ai.abs.skill.martial.MaceAndFlail;
import org.jcrpg.world.ai.abs.skill.martial.MediumBlades;
import org.jcrpg.world.ai.abs.skill.martial.Polearms;
import org.jcrpg.world.ai.abs.skill.martial.ShortBlades;
import org.jcrpg.world.ai.abs.skill.martial.StaffsAndWands;
import org.jcrpg.world.ai.abs.skill.martial.Throwing;
import org.jcrpg.world.ai.abs.skill.martial.Wrestling;
import org.jcrpg.world.ai.abs.skill.mental.Architecture;
import org.jcrpg.world.ai.abs.skill.mental.Ecology;
import org.jcrpg.world.ai.abs.skill.mental.Herbalism;
import org.jcrpg.world.ai.abs.skill.mental.Languages;
import org.jcrpg.world.ai.abs.skill.mental.MagicalLore;
import org.jcrpg.world.ai.abs.skill.mental.Mapmaking;
import org.jcrpg.world.ai.abs.skill.mental.Mythology;
import org.jcrpg.world.ai.abs.skill.mental.methodology.QuickPlanning;
import org.jcrpg.world.ai.abs.skill.mental.methodology.Strategy;
import org.jcrpg.world.ai.abs.skill.physical.Agriculture;
import org.jcrpg.world.ai.abs.skill.physical.AnimalHandling;
import org.jcrpg.world.ai.abs.skill.physical.Climbing;
import org.jcrpg.world.ai.abs.skill.physical.Disguise;
import org.jcrpg.world.ai.abs.skill.physical.HideAndSneak;
import org.jcrpg.world.ai.abs.skill.physical.LocksAndTraps;
import org.jcrpg.world.ai.abs.skill.physical.MartialTrance;
import org.jcrpg.world.ai.abs.skill.physical.PickPocket;
import org.jcrpg.world.ai.abs.skill.physical.Prospecting;
import org.jcrpg.world.ai.abs.skill.physical.SecureAndEscort;
import org.jcrpg.world.ai.abs.skill.physical.StrikeNerves;
import org.jcrpg.world.ai.abs.skill.physical.Survival;
import org.jcrpg.world.ai.abs.skill.physical.Swimming;
import org.jcrpg.world.ai.abs.skill.physical.Tumbling;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;
import org.jcrpg.world.ai.abs.skill.social.Chatter;
import org.jcrpg.world.ai.abs.skill.social.Cheating;
import org.jcrpg.world.ai.abs.skill.social.Diplomacy;
import org.jcrpg.world.ai.abs.skill.social.Laws;
import org.jcrpg.world.ai.abs.skill.social.Politics;
import org.jcrpg.world.ai.abs.skill.social.Reasoning;
import org.jcrpg.world.ai.abs.skill.social.Trade;

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
	
	public static ArrayList<Class> skillTypeInterfaces = new ArrayList<Class>();
	
	
	public static HashMap<String, ArrayList<Class<? extends SkillBase>>> groupedSkills = new HashMap<String, ArrayList<Class<? extends SkillBase>>>();
	public static HashMap<Class<? extends SkillBase>,String> skillsGroup = new HashMap<Class<? extends SkillBase>,String>();
	public static ArrayList<String> orderedGroups = new ArrayList<String>();
	public static HashMap<Class<? extends SkillBase>, SkillBase> skillBaseInstances = new HashMap<Class<? extends SkillBase>, SkillBase>();
	
	public static ArrayList<SkillActForm> negativeSkillActForms = new ArrayList<SkillActForm>();
	public static ArrayList<SkillActForm> neutralSkillActForms = new ArrayList<SkillActForm>();
	public static ArrayList<SkillActForm> positiveSkillActForms = new ArrayList<SkillActForm>();
	
	static {
		
		skillTypeInterfaces.add(InterceptionSkill.class);
		skillTypeInterfaces.add(ObjectSkill.class);
		skillTypeInterfaces.add(HelperSkill.class);
		skillTypeInterfaces.add(EncounterSkill.class);
		skillTypeInterfaces.add(TurnActSkill.class);
		skillTypeInterfaces.add(WorkSkill.class);
		
		orderedGroups.add(GROUP_MARTIAL);
		addSkillToGroup(GROUP_MARTIAL, new BiteFight());
		addSkillToGroup(GROUP_MARTIAL, new Wrestling());
		addSkillToGroup(GROUP_MARTIAL, new ShortBlades());
		addSkillToGroup(GROUP_MARTIAL, new MediumBlades());
		addSkillToGroup(GROUP_MARTIAL, new LargeBlades());
		addSkillToGroup(GROUP_MARTIAL, new Bows());
		addSkillToGroup(GROUP_MARTIAL, new Crossbows());
		addSkillToGroup(GROUP_MARTIAL, new HandsAndFeet());
		addSkillToGroup(GROUP_MARTIAL, new StaffsAndWands());
		addSkillToGroup(GROUP_MARTIAL, new Throwing());
		addSkillToGroup(GROUP_MARTIAL, new Polearms());
		addSkillToGroup(GROUP_MARTIAL, new MaceAndFlail());
		addSkillToGroup(GROUP_MARTIAL, new HammerAndAxe());
		
		orderedGroups.add(GROUP_SOCIAL);
		addSkillToGroup(GROUP_SOCIAL, new Chatter());
		addSkillToGroup(GROUP_SOCIAL, new Diplomacy());
		addSkillToGroup(GROUP_SOCIAL, new Politics());
		addSkillToGroup(GROUP_SOCIAL, new Laws());
		addSkillToGroup(GROUP_SOCIAL, new Cheating());
		addSkillToGroup(GROUP_SOCIAL, new Reasoning());
		addSkillToGroup(GROUP_SOCIAL, new Trade());

		orderedGroups.add(GROUP_PHYSICAL);
		addSkillToGroup(GROUP_PHYSICAL, new Agriculture());
		addSkillToGroup(GROUP_PHYSICAL, new Tracking());
		addSkillToGroup(GROUP_PHYSICAL, new LocksAndTraps());
		addSkillToGroup(GROUP_PHYSICAL, new PickPocket());
		addSkillToGroup(GROUP_PHYSICAL, new Swimming());
		addSkillToGroup(GROUP_PHYSICAL, new Climbing());
		addSkillToGroup(GROUP_PHYSICAL, new HideAndSneak());
		addSkillToGroup(GROUP_PHYSICAL, new Tumbling());
		addSkillToGroup(GROUP_PHYSICAL, new MartialTrance());
		addSkillToGroup(GROUP_PHYSICAL, new StrikeNerves());
		addSkillToGroup(GROUP_PHYSICAL, new Disguise());
		addSkillToGroup(GROUP_PHYSICAL, new AnimalHandling());
		addSkillToGroup(GROUP_PHYSICAL, new Survival());
		addSkillToGroup(GROUP_PHYSICAL, new Prospecting());
		addSkillToGroup(GROUP_PHYSICAL, new SecureAndEscort());
		
		orderedGroups.add(GROUP_MENTAL);
		addSkillToGroup(GROUP_MENTAL, new Architecture());
		addSkillToGroup(GROUP_MENTAL, new Herbalism());
		addSkillToGroup(GROUP_MENTAL, new QuickPlanning());
		addSkillToGroup(GROUP_MENTAL, new Strategy());
		addSkillToGroup(GROUP_MENTAL, new Languages());
		addSkillToGroup(GROUP_MENTAL, new Mythology());
		addSkillToGroup(GROUP_MENTAL, new Ecology());
		addSkillToGroup(GROUP_MENTAL, new MagicalLore());
		addSkillToGroup(GROUP_MENTAL, new Mapmaking());
		
		orderedGroups.add(GROUP_MAGICAL);
		addSkillToGroup(GROUP_MAGICAL, new Demonology());
		addSkillToGroup(GROUP_MAGICAL, new CelestialMagic());
		addSkillToGroup(GROUP_MAGICAL, new Alchemy());
		addSkillToGroup(GROUP_MAGICAL, new Elementarism());
		addSkillToGroup(GROUP_MAGICAL, new Mentalism());
		
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
		
		for (SkillActForm form: skill.actForms)
		{
			if (form.atomicEffect<0)
			{
				negativeSkillActForms.add(form);
			} else
			if (form.atomicEffect==0)
			{
				neutralSkillActForms.add(form);
			} else
			if (form.atomicEffect>0)
			{
				positiveSkillActForms.add(form);
			}
		}
		
	}
	
}
