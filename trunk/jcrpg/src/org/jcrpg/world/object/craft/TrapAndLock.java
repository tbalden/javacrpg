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

package org.jcrpg.world.object.craft;

import org.jcrpg.game.logic.Impact;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.game.logic.UnlockEvaluator;
import org.jcrpg.game.logic.UnlockEvaluator.TrapDisarmResult;
import org.jcrpg.game.logic.UnlockEvaluator.UnlockAction;
import org.jcrpg.game.logic.UnlockEvaluator.UnlockEvaluationInfo;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.object.BonusObject;

public abstract class TrapAndLock extends Craft implements BonusObject {

	public TrapAndLock(int level, int strength)
	{
		this.level = level;
		this.strength = strength;
		if (level<=0) this.level = 1; 
	}

	public Attributes getAttributeValues() {
		return null;
	}

	public Resistances getResistanceValues() {
		return null;
	}

	public boolean isBodyPartBonusOnly() {
		return false;
	}

	public boolean isCursed() {
		return false;
	}

	public boolean isDestructive() {
		return true;
	}

	public int level;
	public int strength;
	
	/**
	 * Trap level. 
	 * @return
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Helper skill to disarm it besides normal thief skills. If such is given,
	 * it helps, and otherwise the subject cannot unlock it if noone has such skill.
	 * @return
	 */
	public abstract Class<? extends SkillBase> getAdditionalDisarmSkill();
	

	public boolean tryIdentification(UnlockEvaluationInfo info, UnlockAction actionType)
	{
		TrapDisarmResult result = UnlockEvaluator.evaluate(info, actionType);
		return result.success;
	}

	public TrapDisarmResult tryDisarming(UnlockEvaluationInfo info, UnlockAction actionType)
	{
		TrapDisarmResult result = UnlockEvaluator.evaluate(info, actionType);
		if (!result.success || actionType==UnlockAction.UNLOCK_ACTION_TYPE_FORCE)
		{
			// TODO impact calculation - extract from EvaluatorBase!
			//ArrayList<E> getSkillActFormBonusEffectTypes();
			result.impact = new Impact();
			for (EntityMemberInstance i:info.fragment.getFollowingMembers())
			{
				ImpactUnit u = new ImpactUnit();
				u.orderedImpactPoints[0] = -2;
				result.impact.targetImpact.put(i,u);
			}
			
		}
		return result;
	}
	
}
