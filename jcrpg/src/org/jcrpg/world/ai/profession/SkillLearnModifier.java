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

package org.jcrpg.world.ai.profession;

import java.util.HashMap;

import org.jcrpg.world.ai.abs.skill.SkillBase;

public class SkillLearnModifier {
	
	public static int MAJOR = 3;
	public static int MINOR = 2;

	protected HashMap<Class <? extends SkillBase>, Integer> multipliers = new HashMap<Class<? extends SkillBase>, Integer>();
	
	public int getMultiplier(Class<? extends SkillBase> skill)
	{
		Integer r = multipliers.get(skill);
		if (r==null) return 1;
		return r;
	}
	
}
