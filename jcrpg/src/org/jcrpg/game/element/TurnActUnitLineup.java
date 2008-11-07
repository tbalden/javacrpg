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
package org.jcrpg.game.element;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnitData;

/**
 * Class for storing one sides' line-up in rows in a turn act phase.
 * @author illes
 *
 */
public class TurnActUnitLineup {

	public ArrayList<ArrayList<EncounterUnitData>> lines = new ArrayList<ArrayList<EncounterUnitData>>();
	
	public HashMap<EncounterUnitData, Integer> unitToLineMap = new HashMap<EncounterUnitData, Integer>();
	
	EncounterInfo info = null;
	int unitsPerLine = 2;
	public TurnActUnitLineup(EncounterInfo info, int unitsPerLine)
	{
		this.info = info;
		this.unitsPerLine = unitsPerLine;
	}
	
	//public static final int UNITS_PER_LINE = 2;
	
	public ArrayList<EncounterUnitData> getList(int line)
	{
		if (lines.size()>line)
		{
		} else
		for (int i=0; i<=line; i++)
		{
			lines.add(new ArrayList<EncounterUnitData>());
		}
		return lines.get(line);
	}
	
	public ArrayList<EncounterUnitData> getAllUnits()
	{
		ArrayList<EncounterUnitData> fullList = new ArrayList<EncounterUnitData>();
		for (int i=0; i<=2; i++)
		{
			fullList.addAll(getList(i));
		}
		return fullList;
	}
	
	public void addUnitPushing(EncounterUnitData unit, int line)
	{
		ArrayList<EncounterUnitData> l = getList(line);
		if (l==null)
		{
			l = new ArrayList<EncounterUnitData>();
		}
		l.add(unit);
		unit.setCurrentLine(line);
		unit.turnActLineup = this;
		
		unitToLineMap.put(unit, line);
		if (l.size()>unitsPerLine)
		{
			EncounterUnitData pushed = getUnitToPush(l);
			l.remove(pushed);
			unitToLineMap.keySet().remove(pushed);
			addUnitPushing(pushed, line+1);
		}
	}
	
	public EncounterUnitData getUnitToPush(ArrayList<EncounterUnitData> list)
	{
		int worstPoint = 9999;
		int worstUnitCount = 0;
		int count = 0;
		for (EncounterUnitData d:list) 
		{
			int priorityPoint = d.getEncPhasePriority(info);
			if (priorityPoint<worstPoint)
			{
				worstPoint = priorityPoint;
				worstUnitCount = count; 
			}
			count++;
		}
		return list.get(worstUnitCount);
	}
	
}
