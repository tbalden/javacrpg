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

import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.magical.Alchemy;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.abs.skill.physical.LocksAndTraps;
import org.jcrpg.world.object.craft.TrapAndLock;

public class UnlockEvaluator {
	
	public static class UnlockEvaluationInfo
	{
		public int skillLevel;
		public int spellLevel;
		public boolean additionalSkillOkay = false;
		public int additionalSkillLevel;
		public int chanceOfSkill;
		public int chanceOfSpell;
		public int chanceOfForce;
	}
	
	public static UnlockEvaluationInfo getEvaluationInfo(EntityFragment fragment, TrapAndLock lock)
	{
		UnlockEvaluationInfo info = new UnlockEvaluationInfo();
		
		int bestSkillLevel = 0;
		int bestAdditionalSkillLevel = 0;
		int bestSpellLevel = 0;

		int sumSkillLevel = 0;
		int sumAdditionalSkillLevel = 0;
		int sumSpellLevel = 0;
		int members = 0;
		int sumOfStrength = 0;
		
		Class<? extends SkillBase>  b = lock.getAdditionalDisarmSkill();
		if (b==null) info.additionalSkillOkay = true;
		
		for (PersistentMemberInstance pMI:fragment.getFollowingMembers())
		{
			if (!pMI.isDead())
			{
				members++;
				int skillLevel = pMI.getSkillLevel(LocksAndTraps.class);
				int spellLevel1 = pMI.getSkillLevel(Elementarism.class);
				int spellLevel2 = pMI.getSkillLevel(Alchemy.class);
				int spellLevel = Math.max(spellLevel1, spellLevel2);
				int strength = pMI.getAttributes().getAttribute(FantasyAttributes.STRENGTH);
				sumOfStrength+=strength;
				if (bestSkillLevel<skillLevel) bestSkillLevel = skillLevel;
				if (bestSpellLevel<spellLevel) bestSpellLevel = spellLevel;
				if (b!=null)
				{
					int additionalLevel = pMI.getSkillLevel(b);
					if (additionalLevel>0) info.additionalSkillOkay = true;
					if (bestAdditionalSkillLevel<additionalLevel) bestAdditionalSkillLevel = additionalLevel;
					sumAdditionalSkillLevel+=additionalLevel;
				}
				sumSkillLevel+=skillLevel;
				sumSpellLevel+=spellLevel;
			}
		}
		if (members>0)
		{
			sumSkillLevel/=members*2;
			sumSpellLevel/=members*2;
			sumAdditionalSkillLevel/=members*2;
			sumOfStrength/=members;
		}
		
		info.skillLevel = bestSkillLevel + sumSkillLevel;
		info.spellLevel = bestSpellLevel + sumSpellLevel;
		info.additionalSkillLevel = bestAdditionalSkillLevel + sumAdditionalSkillLevel;
		
		int level = lock.level * 10;
		int strengthLevel = lock.strength * 10;
		info.chanceOfSkill = (int)((bestSkillLevel*1f/level*1f)*100);
		info.chanceOfSpell = (int)((bestSpellLevel*1f/level*1f)*100);
		info.chanceOfForce = (int)((sumOfStrength*1f/strengthLevel*1f)*100);
		
		return info;
	}
	
	// TODO evaluation / return impact function

}
