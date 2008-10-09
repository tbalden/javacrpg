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
package org.jcrpg.world.ai;

import org.jcrpg.game.GameLogicConstants;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.EntityMember.SkillPreferenceHint;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.skill.SkillContainer;

public class GeneratedMemberInstance extends EntityMemberInstance {


	/**
	 * Generated attributes for the level of member.
	 */
	private Attributes generatedAttributes = null;
	
	/**
	 * Generated skill levels for the level of member.
	 */
	private SkillContainer generatedSkills = new SkillContainer();
	
	public GeneratedMemberInstance(EntityFragment parent,
			EntityInstance instance, EntityMember description, int numericId, int level) {
		super(parent, instance, description, numericId);
		memberState.level = level;
		generatedSkills = description.memberSkills.copy();
		levelUp(level);
	}

	@Override
	public SkillContainer getSkills() {
		return generatedSkills;
	}

	@Override
	public Attributes getAttributesVanilla() {
		// TODO merging generated instance attribute levels 
		Attributes base = super.getAttributesVanilla();
		if (generatedAttributes!=null) base.appendAttributes(generatedAttributes);
		return base;
	}
	
	/**
	 * Do the leveling up for a number of levels (attributes,skills etc.).
	 * @param levels number of levels.
	 */
	public void levelUp(int levels)
	{
		for (int i=0; i<levels; i++)
		{
			levelUp();
		}
	}
	
	/**
	 * Level up 1 level.
	 */
	public void levelUp()
	{
		int attributePointsLeft = GameLogicConstants.ATTRIBUTE_POINTS_TO_USE_ON_LEVELING;
		int skillPointLeft = GameLogicConstants.SKILL_POINTS_TO_USE_ON_LEVELING;
		AttributeRatios rDesc = description.getLevelingAttributeRatioHint(this);
		AttributeRatios rProf = null;
		try {
			J3DCore.getInstance().gameState.getCharCreationRules().profInstances.get(description.professions.get(0)).getLevelingAttributeRatioHint(this);
		}catch (Exception ex)
		{	
		}
		Attributes a = rDesc.calculateLevelingAttributes(getNumericId()+memberState.level, rProf!=null?attributePointsLeft/2:attributePointsLeft,getAttributesVanilla().getClass());
		getAttributesVanilla().appendAttributes(a);
		if (rProf!=null)
		{
			a = rProf.calculateLevelingAttributes(getNumericId()+memberState.level+1, attributePointsLeft/2, getAttributesVanilla().getClass());
			getAttributesVanilla().appendAttributes(a);
		}
		
		// TODO skill hints - profession should get info about what kind of Skills are preferred
		// from the EntityMember description, and the profession should return which skills to increase
		// and how much. CURRENTLY it's much simpler - check levelUpSkills
		SkillPreferenceHint sHint = description.getLevelingSkillPreferenceHint(this);
		getSkills().levelUpSkills(this, sHint, skillPointLeft);
		
		updateAfterLeveling();
	}
	

}
