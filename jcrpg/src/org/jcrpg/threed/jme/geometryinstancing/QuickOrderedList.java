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
package org.jcrpg.threed.jme.geometryinstancing;

import java.util.ArrayList;

public class QuickOrderedList {

	private ArrayList<Integer> listOrdering = new ArrayList<Integer>();
	private ArrayList<Object> list = new ArrayList<Object>();
	
	public static long timeCounter = 0;
	
	public int getNearestIndex(int orderingValue)
	{
		return getNearestIndexPriv(orderingValue, 0, listOrdering.size()-1);
	}
	private int getNearestIndexPriv(int orderingValue,int from, int to)
	{
		if (from==to) return from;
		int center = (from+to)/2;
		int val = listOrdering.get(center);
		if (val==orderingValue) return center;
		if (val<orderingValue)
		{
			if (from+1==to)
			{
				return to;
			}
			return getNearestIndexPriv(orderingValue,center,to);
		} 
		if (from+1==to)
		{
			return from;
		}
		return getNearestIndexPriv(orderingValue,from,center);
	}
	
	public void addElement(int orderingValue, Object value)
	{
		long time = System.currentTimeMillis(); 
		int index = 0;
		if (list.size()==0)
		{
			
		} else
		{
			index = getNearestIndex(orderingValue);
		}
		listOrdering.add(index, orderingValue);		
		list.add(index, value);
		timeCounter+=System.currentTimeMillis()-time;
	}

	
	public void removeElement(Object value)
	{
		int index = 0;
		if (list.size()==0)
		{
			
		} else
		{
			index = list.indexOf(value);
			listOrdering.remove(index);
			list.remove(index);
		}
	}
	
	public Object removeElementWithEqualOrBiggerOrderingValue(int orderingValue)
	{
		long time = System.currentTimeMillis(); 
		int index = 0;
		if (list.size()==0)
		{
			timeCounter+=System.currentTimeMillis()-time;
			return null;
		} else
		{
			index = getNearestIndex(orderingValue);
		}
		int order = listOrdering.get(index);
		if (order>=orderingValue)
		{
			listOrdering.remove(index);
			timeCounter+=System.currentTimeMillis()-time;
			return list.remove(index);
		} 
		// no such element
		timeCounter+=System.currentTimeMillis()-time;
		return null;
		
	}

	public Object removeElementWithEqualOrderingValue(int orderingValue)
	{
		long time = System.currentTimeMillis(); 
		int index = 0;
		if (list.size()==0)
		{
			timeCounter+=System.currentTimeMillis()-time;
			return null;
		} else
		{
			index = getNearestIndex(orderingValue);
		}
		int order = listOrdering.get(index);
		if (order==orderingValue)
		{
			listOrdering.remove(index);
			timeCounter+=System.currentTimeMillis()-time;
			return list.remove(index);
		} 
		// no such element
		timeCounter+=System.currentTimeMillis()-time;
		return null;
		
	}

}
