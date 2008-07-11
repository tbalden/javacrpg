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
package org.jcrpg.world.ai.abs.state;

import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.time.Time;

public class StateEffectInitParams {
	
	public Class<? extends StateEffect> type;
	public int basePower;
	public int durationType;
	public int baseDuration;
	
	public StateEffect getOne(Time worldMeanTime, int power, int durationMultiplier, EntityMemberInstance target)
	{
		StateEffect e = null;
		try {
			e = type.newInstance();
			e.initStateEffect(worldMeanTime.getTimeInRound(), worldMeanTime, power, durationMultiplier, target.memberState);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return e;
	}

}
