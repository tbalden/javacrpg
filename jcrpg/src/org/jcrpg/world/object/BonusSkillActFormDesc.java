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
package org.jcrpg.world.object;

import org.jcrpg.world.ai.abs.skill.SkillActForm;

public class BonusSkillActFormDesc {

	public SkillActForm form;
	/**
	 * Level of the skill for the bonus skill act form use.
	 */
	public int skillLevel = 0;
	
	public static int FREQUENCY_INSTANT = 0;
	public static int FREQUENCY_MINUTE = 1;
	public static int FREQUENCY_HOUR = 1;
	public static int FREQUENCY_DAY = 2;
	
	public static int MAX_USE_UNLIMITED = 0;
	
	/**
	 * Max use of this bonus before exhaust.
	 */
	public int maxUsePerReplenish = MAX_USE_UNLIMITED;
	
	/**
	 * Frequency type
	 */
	public int replenishFrequency = FREQUENCY_INSTANT;
	
}
