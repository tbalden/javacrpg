/*
 *  This file is part of JavaCRPG.
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

import java.util.HashSet;

/**
 * Cube Coordinate based area bounding
 * @author pali
 *
 */
public class Boundaries {


	/**
	 * One coordinate of this boundary is MAGNIFICATION times bigger than a normal Cube 
	 */
	public int magnification;
	/**
	 * All coordinates
	 */
	public HashSet<Long> area;

	/**
	 * The limiting coordinates
	 */
	public HashSet<Long> limits;
	
	public Boundaries(int magnification)
	{
		this.magnification = magnification;
		area = new HashSet<Long>();
		limits = new HashSet<Long>();
	}
	
	public boolean nearProbeAvailabe = false;
	
	/**
	 * Helper values for near probe. if nearProbeAv.=true these must be set!
	 */
	public int limitXMin, limitYMin, limitZMin, limitXMax, limitYMax, limitZMax;
	

	public void addLimiterCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		Long key = getKey(x*magnification, y*magnification, z*magnification);
		limits.add(key);
	}
	public void removeLimiterCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		limits.remove(getKey(x*magnification, y*magnification, z*magnification));
	}
	
	
	public void addCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		Long key = getKey(x*magnification, y*magnification, z*magnification);
		area.add(key);
	}
	public void removeCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		area.remove(getKey(x*magnification, y*magnification, z*magnification));
	}
	
	public void mergeBoundaries(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		/*
		 * The common limits can be removed, they are overlapping,
		 * not common limits are new limits.
		 */
		for (Long element : area2.getArea()) {
			if (limits.contains(element))
			{
				limits.remove(element);
			} else
			{
				// if not an internal element already, it is a new limiter
				if (!area.contains(element)) 
					limits.add(element);
			}
		};
		area.addAll(area2.getArea());
	}
	
	public void subtractBoundaries(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		for (Long element : area2.getArea()) {
			area.remove(element);
		};
		/*
		 * The common limits can be removed, they are overlapping,
		 * not common limits are new limits.
		 */
		for (Long element : area2.getArea()) {
			if (limits.contains(element))
			{
				limits.remove(element);
			} else
			{
				limits.add(element);
			}
		};
	}

	public boolean isOverlapping(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		for (Long element : area2.getArea()) {
			return area.contains(element);
		}
		return false;
	}

	public boolean isInside(int absouluteX, int absoluteY, int absoluteZ)
	{
		//if (absoluteY<0 && magnification>1) {
			//absoluteY=absoluteY-1*magnification;
		//}
		boolean ret = area.contains(getKey(absouluteX-absouluteX%magnification, absoluteY-absoluteY%magnification, absoluteZ-absoluteZ%magnification));
		//if (ret && magnification==1) System.out.println(" -- "+absouluteX+" "+absoluteZ+ " MAG: "+magnification+ " == "+ret);
		return ret;
	}
	
	public HashSet<Long> getArea()
	{
		return area;
	}
	public HashSet<Long> getLimits()
	{
		return limits;
	}
	
	public static Long getKey(int x,int y,int z){
		long s = (((long)x) << 32) + ((z) << 16) + (y);
		//System.out.println("##"+ s);
		return s;
	}
	/**
	 * Tells if this boundary overlaps the area inside a distance to a given coordinate.
	 * @param x
	 * @param y
	 * @param z
	 * @param distance
	 * @return
	 */
	public boolean isNear(int x,int y,int z, int distance)
	{
		if (!nearProbeAvailabe)
			return true;
		if (x-distance>=limitXMin)
		{
			if (x+distance<=limitXMax)
			{
				if (y-distance>=limitYMin)
				{
					if (y+distance<=limitYMax)
					{
						if (z-distance>=limitZMin)
						{
							if (z+distance<=limitZMax)
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	public void calcLimits()
	{
		limitXMin = (limitXMin/World.PROBE_DISTANCE)*World.PROBE_DISTANCE;
		limitXMax = (limitXMax/World.PROBE_DISTANCE)*World.PROBE_DISTANCE;
		if (limitXMin==limitXMax) limitXMax+=World.PROBE_DISTANCE;
		limitXMin-=World.PROBE_DISTANCE;
		limitXMax+=World.PROBE_DISTANCE;
		
		limitZMin = (limitZMin/World.PROBE_DISTANCE)*World.PROBE_DISTANCE;
		limitZMax = (limitZMax/World.PROBE_DISTANCE)*World.PROBE_DISTANCE;
		if (limitZMin==limitZMax) limitZMax+=World.PROBE_DISTANCE;
		limitZMin-=World.PROBE_DISTANCE;
		limitZMax+=World.PROBE_DISTANCE;
		
		limitYMin = (limitYMin/World.PROBE_DISTANCE)*World.PROBE_DISTANCE;
		limitYMax = (limitYMax/World.PROBE_DISTANCE)*World.PROBE_DISTANCE;
		if (limitYMin==limitYMax) limitYMax+=World.PROBE_DISTANCE;
		limitYMin-=World.PROBE_DISTANCE;
		limitYMax+=World.PROBE_DISTANCE;

		nearProbeAvailabe = true;
	}
	
	public boolean changed()
	{
		return false;
	}
	public void changeAcknowledged()
	{
		
	}
	
}
