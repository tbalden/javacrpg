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
import java.util.Iterator;

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
	public class MatrixData implements Iterator<EncounterUnitData>
	{
		public ArrayList<EncounterUnitData>[] matrix = new ArrayList[4];
		public int iLineCount = 0;
		public int iLineListCount = 0;
		
		public void reset()
		{
			iLineCount = 0;
			iLineListCount = 0;
		}
		
		public boolean hasNext() {
			System.out.println("iLenCount = "+iLineCount+" - iLineListCount = "+iLineListCount);
			if (matrix[iLineCount]==null) return false;
			if (matrix[iLineCount]!=null && matrix[iLineCount].get(iLineListCount) == null) return false;
			return true;
		}
		public EncounterUnitData next() {
			EncounterUnitData d = matrix[iLineCount].get(iLineListCount);
			d.currentLine = iLineCount;
			if (matrix[iLineCount].size()>iLineListCount+1)
			{
				iLineListCount++;
			} else
			{
				if (iLineCount==3)
				{
					System.out.println("## iLenCount = "+iLineCount+" - iLineListCount = "+iLineListCount);
					return null;
					
				} else
				{
					iLineCount++;
					iLineListCount = 0;
				}
			}
			System.out.println("# iLenCount = "+iLineCount+" - iLineListCount = "+iLineListCount);
			return d;
		}
		public void remove() {
			
		}
	}
	
	/**
	 * array of array - 4 sized Front Middle Back line and all other not in active lines units.
	 */
	public MatrixData matrixAhead = new MatrixData();
	/**
	 * array of array - 4 sized Front Middle Back line and all other not in active lines units.
	 */
	public MatrixData matrixBehind = new MatrixData();
	
	
	EncounterUnitData getAhead(int count,int line)
	{
		try {
			return matrixAhead.matrix[line].get(count);
		} catch (Exception e)
		{
			return null;
		}
	}
	EncounterUnitData getBehind(int count,int line)
	{
		try {
			return matrixBehind.matrix[line].get(count);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	public void addAhead(EncounterUnitData data, int line)
	{
		if (line>3) line = 3;
		ArrayList<EncounterUnitData> list = matrixAhead.matrix[line];
		if (list==null)
		{
			matrixAhead.matrix[line] = new ArrayList<EncounterUnitData>();
			list = matrixAhead.matrix[line];
		}
		list.add(data);
		System.out.println("####____ addAhead "+data.getUnit().getName()+" "+list.size()+ " , "+line);
	}
	
	public void addBehind(EncounterUnitData data, int line)
	{
		if (line>3) line = 3;
		ArrayList<EncounterUnitData> list = matrixBehind.matrix[line];
		if (list==null)
		{
			matrixBehind.matrix[line] = new ArrayList<EncounterUnitData>();
			list = matrixBehind.matrix[line];
		}
		list.add(data);
	}
	
	
}
