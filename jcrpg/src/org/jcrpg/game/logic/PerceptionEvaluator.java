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

package org.jcrpg.game.logic;

import java.util.ArrayList;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EncounterUnit;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.mental.Architecture;
import org.jcrpg.world.ai.abs.skill.physical.outdoor.Tracking;

public class PerceptionEvaluator {
	
	
	public static int likenessLevelOfPerception(EntityMemberInstance member, EncounterUnit fragment)
	{
		boolean internal = member.getParentFragment().enteredPopulation!=null;
		Class <? extends SkillBase> perceptionSkill = null;
		if (internal)
		{
			perceptionSkill = Architecture.class;
		} else
		{
			perceptionSkill = Tracking.class;
		}
		boolean active = !member.getParentFragment().fragmentState.isCamping && member.behaviorSkill!=null && member.behaviorSkill.getClass().equals(perceptionSkill);
		return likenessLevelOfPerception(member.getSkills(), active, perceptionSkill, fragment);
		
	}
	
	public static int likenessLevelOfPerception(EntityFragment source, EncounterUnit fragment)
	{
		boolean internal = source.enteredPopulation!=null;
		Class <? extends SkillBase> perceptionSkill = null;
		if (internal)
		{
			perceptionSkill = Architecture.class;
		} else
		{
			perceptionSkill = Tracking.class;
		}
		boolean active = false;
		return likenessLevelOfPerception(source.instance.skills, active, perceptionSkill, fragment);
		
	}
	

	/**
	 * Skill Level (if active 3x) minus the fragment's ActiveContraSkill level.
	 * @param member
	 * @param fragment
	 * @return
	 */
	public static int likenessLevelOfPerception(SkillContainer skills, boolean activeSourceSkill, Class <? extends SkillBase> perceptionSkill, EncounterUnit fragment)
	{
		int level = skills.getSkillLevel(perceptionSkill,null);
		if (!activeSourceSkill) level /=3;
		
		int sLevel = 0;
		SkillInstance i = skills.skills.get(perceptionSkill);
		
		//if (i!=null)
		{
			
			ArrayList<Class<? extends SkillBase>> contraSkills = null;
			try {
				contraSkills = perceptionSkill.newInstance().getContraSkillTypes();
			} catch (Exception ex){ex.printStackTrace();}
			for (Class<? extends SkillBase> contraSkill: contraSkills)
			{
				// maxing out skill level
				int cLevel = fragment.getFragment().getActiveBehaviorSkillLevel(contraSkill);
				if (cLevel>sLevel) sLevel = cLevel;
			}
		}
		return level - sLevel;
	}

	
	public static float success(int seed, int karma, int level)
	{
		return (HashUtil.mixPercentage(seed, 1, 2) + (karma /20f)+ (level))/100f;
	}
	
	public static int likenessLevelOfIdentification(EntityMemberInstance member, EncounterUnit fragment)
	{
		Class <? extends SkillBase> identificationSkill = fragment.getDescription().getSkillForIdentification();
		boolean active = member.behaviorSkill!=null && member.behaviorSkill.getClass().equals(identificationSkill);
		return likenessLevelOfIdentification(member.getSkills(), active, identificationSkill, fragment);
	}

	public static int likenessLevelOfIdentification(EntityFragment source, EncounterUnit fragment)
	{
		Class <? extends SkillBase> identificationSkill = fragment.getDescription().getSkillForIdentification();
		boolean active = false;//source.instance.skills.behaviorSkill!=null && member.behaviorSkill.getClass().equals(identificationSkill);
		return likenessLevelOfIdentification(source.instance.skills, active, identificationSkill, fragment);
	}

	public static int likenessLevelOfIdentification(SkillContainer skills, boolean activeSourceSkill, Class <? extends SkillBase> identificationSkill, EncounterUnit fragment)
	{
		int level = skills.getSkillLevel(identificationSkill,null);
		if (!activeSourceSkill) level /=3;
		
		int sLevel = 0;
		{
			ArrayList<Class<? extends SkillBase>> contraSkills = null;
			try {
				contraSkills = identificationSkill.newInstance().getContraSkillTypes();
			} catch (Exception ex){ex.printStackTrace();}
			for (Class<? extends SkillBase> contraSkill: contraSkills)
			{
				// maxing out skill level
				int cLevel = fragment.getFragment().getActiveBehaviorSkillLevel(contraSkill);
				if (cLevel>sLevel) sLevel = cLevel;
			}
		}
		return level - sLevel;
	}
	
}
