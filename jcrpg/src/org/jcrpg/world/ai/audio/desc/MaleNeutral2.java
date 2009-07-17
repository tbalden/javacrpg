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

package org.jcrpg.world.ai.audio.desc;

import org.jcrpg.world.ai.AudioDescription;

public class MaleNeutral2 extends AudioDescription {

	public MaleNeutral2()
	{
		formattedName = "Neutral2";
		String base = "humanoid/male_neutral2/";
		ATTACK = new String[]{base+"Attack_1",base+"Attack_2",base+"Attack_3",base+"Attack_4",base+"Attack_5",base+"Attack_6",base+"Attack_7"};
		PAIN = new String[]{base+"Hurt_1",base+"Hurt_2",base+"Hurt_3",base+"Hurt_4"};
		JOY = new String[]{base+"Hooray"};
		DEATH = new String[]{base+"Death"};
		ENCOUNTER = new String[]{base+"Greeting"};
		LEVELING = new String[] {base+"Level_up"};
		TIRED = new String[] {base+"Tired"};
		BRUISED = new String[] {base+"Need_Healing"};		
		
	}
	
}
