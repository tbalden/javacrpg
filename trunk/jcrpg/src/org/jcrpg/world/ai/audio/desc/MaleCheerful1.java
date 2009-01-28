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

public class MaleCheerful1 extends AudioDescription {

	public MaleCheerful1()
	{
		formattedName = "Cheerful1";
		String base = "humanoid/male_cheerful1/";
		ATTACK = new String[]{base+"Bernie-Attack-1",base+"Bernie-Attack-2",base+"Bernie-Attack-3"};
		PAIN = new String[]{base+"Bernie-Pain-1",base+"Bernie-Pain-2",base+"Bernie-Pain-3",base+"Bernie-Pain-4",base+"Bernie-Pain-5"};
		JOY = new String[]{base+"Bernie-HealJoy-1",base+"Bernie-HealJoy-2",base+"Bernie-HealJoy-3",base+"Bernie-HealJoy-4"};
		DEATH = new String[]{base+"Bernie-Death-1",base+"Bernie-Death-2",base+"Bernie-Death-3",base+"Bernie-Death-4",base+"Bernie-Death-5"};
		ENCOUNTER = new String[]{base+"Bernie-Greet-1",base+"Bernie-Greet-2",base+"Bernie-Greet-3",base+"Bernie-Greet-4",base+"Bernie-Greet-5"}; 
	}
	
}
