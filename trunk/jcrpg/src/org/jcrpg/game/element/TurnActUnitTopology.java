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
package org.jcrpg.game.element;

import java.util.ArrayList;

import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnitData;

/**
 * includes the two turn act phase lineups (friendly lineup, enemy lineup).
 * @author illes
 *
 */
public class TurnActUnitTopology {
	
	private TurnActUnitLineup friendlyLineup = null;
	private TurnActUnitLineup enemyLineup = null;
	
	private EncounterInfo info;
	
	public TurnActUnitTopology(EncounterInfo info)
	{
		friendlyLineup = new TurnActUnitLineup(info);
		enemyLineup = new TurnActUnitLineup(info);
		this.info = info;
	}

	public TurnActUnitLineup getFriendlyLineup() {
		return friendlyLineup;
	}

	public TurnActUnitLineup getEnemyLineup() {
		return enemyLineup;
	}
	
	public void addUnitPushing(EncounterUnitData unit, int line)
	{
		if (unit.friendly)
		{
			getFriendlyLineup().addUnitPushing(unit, line);
		} else
		{
			getEnemyLineup().addUnitPushing(unit, line);
		}
	}
	
	/**
	 * Removes units from the linups.
	 * @param toRemove
	 */
	public void removeUnits(ArrayList<EncounterUnitData> toRemove)
	{
		removeUnitsFromLineup(friendlyLineup, toRemove);
		removeUnitsFromLineup(enemyLineup, toRemove);
	}
	
	public static void removeUnitsFromLineup(TurnActUnitLineup lineup,ArrayList<EncounterUnitData> toRemove)
	{
		lineup.unitToLineMap.keySet().removeAll(toRemove);
		for (ArrayList<EncounterUnitData> unitDataList:lineup.lines)
		{
			if (unitDataList!=null) unitDataList.removeAll(toRemove);
		}
	}

}
