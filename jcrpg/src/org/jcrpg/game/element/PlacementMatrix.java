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

import org.jcrpg.world.ai.EncounterUnitData;

/**
 * Abstract placement matrix.
 * The arrays here contain up to 4 lines (0 to 3 front/middle/back/inactive) that can contain
 * any number of UnitData objects in their lists. It will be displayed ahead (matrixAhead) or behind
 * (matrixBehind), all of them ordered according firstly to line (0 -> 3) and secondly list order's
 * order, so every unit will be placed in 3d, but place in 3d will depend on available (walkable) tile.
 * So this is an abstract order not 3d, and all units should be displayed if possible at the given
 * geography topology. First line units go first, second line units go next etc.
 * @author illes
 */
@SuppressWarnings("unchecked")
public class PlacementMatrix {

	/**
	 * array of array - 4 sized Front Middle Back line and all other not in active lines units.
	 */
	public ArrayList<EncounterUnitData>[] matrixAhead = new ArrayList[4];
	/**
	 * array of array - 4 sized Front Middle Back line and all other not in active lines units.
	 */
	public ArrayList<EncounterUnitData>[] matrixBehind = new ArrayList[4];
	
	
	EncounterUnitData getAhead(int count,int line)
	{
		try {
			return matrixAhead[line].get(count);
		} catch (Exception e)
		{
			return null;
		}
	}
	EncounterUnitData getBehind(int count,int line)
	{
		try {
			return matrixAhead[line].get(count);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	public void addAhead(EncounterUnitData data, int line)
	{
		ArrayList<EncounterUnitData> list = matrixAhead[line];
		if (list==null)
		{
			matrixAhead[line] = new ArrayList<EncounterUnitData>();
		}
		list.add(data);
	}
	
	public void addBehind(EncounterUnitData data, int line)
	{
		ArrayList<EncounterUnitData> list = matrixBehind[line];
		if (list==null)
		{
			matrixBehind[line] = new ArrayList<EncounterUnitData>();
		}
		list.add(data);
	}
	
}
