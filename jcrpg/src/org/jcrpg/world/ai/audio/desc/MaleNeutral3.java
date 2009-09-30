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

public class MaleNeutral3 extends AudioDescription {

	public MaleNeutral3()
	{
		formattedName = "Enervated";
		String base = "humanoid/male_neutral3/";
		ATTACK = new String[]{base+"die-01",base+"die-02",base+"ha-01",base+"ha-02",base+"ha-03",base+"ha-04",base+"ha-05",base+"ha-06",base+"takethat-01"};
		PAIN = new String[]{base+"ouch-01",base+"ouch-02",base+"ouch-03",base+"ouch-04",base+"ouch-05",base+"ouch-06"};
		JOY = new String[]{base+"yes-01",base+"yes-02",base+"yes-03"};
		DEATH = new String[]{base+"pain-03"};
		ENCOUNTER = new String[]{base+"greetings-01"};
		LEVELING = new String[] {base+"igrowstronger-01",base+"ihavelearnedmuch-01"};
		TIRED = new String[] {base+"ineedabed-01",base+"ineedabed-02"};
		BRUISED = new String[] {base+"imdying-01"};		
		
	}
	
}
