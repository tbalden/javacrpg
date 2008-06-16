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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeMap;

import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnitData;

/**
 * Lineup information for the Encounter phase.
 * @author illes
 *
 */
public class EncounterPhaseLineup {

	public TreeMap<Integer, ArrayList<EncounterUnitData>> orderedList = new TreeMap<Integer, ArrayList<EncounterUnitData>>();
	
	private EncounterInfo info;
	
	public EncounterPhaseLineup(EncounterInfo info)
	{
		this.info = info;
	}
	public void addUnit(EncounterUnitData unit, int priority)
	{
		System.out.println("########### ADDING UNIT: "+unit.parent.getName()+" - "+priority);
		ArrayList<EncounterUnitData> list = orderedList.get(priority);
		if (list==null)
		{
			list = new ArrayList<EncounterUnitData>();
			orderedList.put(priority, list);
		}
		list.add(unit);
	}
	
}
