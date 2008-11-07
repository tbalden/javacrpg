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

/**
 * Skill that is used automatically helping things to happen or adding help to other skills.
 * @author pali
 *
 */
public interface HelperSkill {
	
	public static final String TAG_CRITICAL_HIT = "CRITICAL_HIT";
	
	/**
	 * Tells if this helper skill does help another skill for a given 'tag' (which tells what way
	 * the help is needed).
	 * @param skill
	 * @param tagWord
	 * @return
	 */
	public boolean helpsForTag(Class<? extends SkillBase> skill, String tagWord);

}
