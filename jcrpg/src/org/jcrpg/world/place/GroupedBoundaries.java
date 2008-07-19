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

package org.jcrpg.world.place;

import java.util.ArrayList;

/**
 * Boundary to contain several boundaries quickened with a treelocator for the subboundaries.
 * @author illes
 *
 */
public class GroupedBoundaries extends Boundaries {

	transient TreeLocator locator;
	
	Object parent = null;
	
	public GroupedBoundaries(World world,Object parent) {
		super(1);
		this.parent = parent;
		limitXMin = -1;
		limitXMax = -1;
		limitYMin = -1;
		limitYMax = -1;
		limitZMin = -1;
		limitZMax = -1;
		locator = new TreeLocator(world);
	}
	
	public void addBoundary(Boundaries b)
	{
		if (limitXMin==-1)
		{
			// reset minimums/maximums
			limitXMin = b.limitXMin;
			limitYMin = b.limitYMin;
			limitZMin = b.limitZMin;
			limitXMax = b.limitXMax;
			limitYMax = b.limitYMax;
			limitZMax = b.limitZMax;
		} else
		{
			// testing minimums
			if (limitXMin>b.limitXMin)
			{
				limitXMin = b.limitXMin;
			}
			if (limitYMin>b.limitYMin)
			{
				limitYMin = b.limitYMin;
			}
			if (limitZMin>b.limitZMin)
			{
				limitZMin = b.limitZMin;
			}
			
			// testing maximums
			if (limitXMax<b.limitXMax)
			{
				limitXMax = b.limitXMax;
			}
			if (limitYMax<b.limitYMax)
			{
				limitYMax = b.limitYMax;
			}
			if (limitZMax<b.limitZMax)
			{
				limitZMax = b.limitZMax;
			}
		}	
		
		locator.addBoundary(b);
	}
	
	/**
	 * Removing a boundary from this group.
	 * @param b boundaries to remove
	 * @param recalculate
	 */
	public void removeBoundary(Boundaries b, boolean recalculate)
	{
		locator.removeBoundary(b);
	}
	
	public void recalculateLimits()
	{
		// TODO
	}
	
	public void updateBoundary(Boundaries b)
	{
		// TODO
	}

	@Override
	public boolean isInside(int absouluteX, int absoluteY, int absoluteZ) {
		if (limitXMin==-1) return false;
		ArrayList<Object> es = locator.getElements(absouluteX, absoluteY, absoluteZ);
		if (es!=null)
		{
			for (Object o:es)
			{
				if (((Boundaries)o).isInside(absouluteX, absoluteY, absoluteZ)) 
				{
					/*if (((Population)parent).soilGeo instanceof Cave)
					{
						if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("!!!! NEAR CAVE POPULATION");
					}*/
					return true;
				}
			}
		}
		return false;
	}
	

	@Override
	public void clear()
	{
		locator.clear();
		parent = null;
	}

}
