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

public class MaleGood1 extends AudioDescription {

	public MaleGood1()
	{
		formattedName = "Good";
		String base = "humanoid/male_good1/paladin-";
		ATTACK = new String[]{base+"attackshout1",base+"attackshout2"};
		PAIN = new String[]{base+"pain1",base+"pain2"};
		JOY = new String[]{base+"healjoy1",base+"healjoy2"};
		DEATH = new String[]{base+"death1",base+"death2"};
		ENCOUNTER = new String[]{base+"greeting1",base+"greeting2",};
		DANGER = new String[]{base+"dangersensing1",base+"dangersensing2",};
		LEVELING = new String[] {base+"leveling1",base+"leveling2"};
		TIRED = new String[] {base+"tired1",base+"tired2"};
		BRUISED = new String[] {base+"needheal1",base+"needheal2",};
		pitchModifier = 1.07f;
		
	}
	
}
