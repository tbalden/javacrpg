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

import java.util.HashMap;

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
	public HashMap<String,int[]> area;

	/**
	 * The limiting coordinates
	 */
	public HashMap<String,int[]> limits;
	
	public Boundaries(int magnification)
	{
		this.magnification = magnification;
		area = new HashMap<String, int[]>();
		limits = new HashMap<String, int[]>();
	}
	

	public void addLimiterCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		String key = getKey(x*magnification, y*magnification, z*magnification);
		limits.put(key,new int[]{x,y,z});
	}
	public void removeLimiterCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		limits.remove(getKey(x*magnification, y*magnification, z*magnification));
	}
	
	
	public void addCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		String key = getKey(x*magnification, y*magnification, z*magnification);
		area.put(key,new int[]{x,y,z});
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
		for (String element : area2.getArea().keySet()) {
			if (limits.containsKey(element))
			{
				limits.remove(element);
			} else
			{
				// if not an internal element already, it is a new limiter
				if (!area.containsKey(element)) 
					limits.put(element, area2.getArea().get(element));
			}
		};
		area.putAll(area2.getArea());
	}
	
	public void subtractBoundaries(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		for (String element : area2.getArea().keySet()) {
			area.remove(element);
		};
		/*
		 * The common limits can be removed, they are overlapping,
		 * not common limits are new limits.
		 */
		for (String element : area2.getArea().keySet()) {
			if (limits.containsKey(element))
			{
				limits.remove(element);
			} else
			{
				limits.put(element, area2.getArea().get(element));
			}
		};
	}

	public boolean isOverlapping(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		for (String element : area2.getArea().keySet()) {
			return area.containsKey(element);
		}
		return false;
	}

	public boolean isInside(int absouluteX, int absoluteY, int absoluteZ)
	{
		//if (absoluteY<0 && magnification>1) {
			//absoluteY=absoluteY-1*magnification;
		//}
		boolean ret = area.containsKey(getKey(absouluteX-absouluteX%magnification, absoluteY-absoluteY%magnification, absoluteZ-absoluteZ%magnification));
		//if (ret && magnification==1) System.out.println(" -- "+absouluteX+" "+absoluteZ+ " MAG: "+magnification+ " == "+ret);
		return ret;
	}
	
	public HashMap<String,int[]> getArea()
	{
		return area;
	}
	public HashMap<String,int[]> getLimits()
	{
		return limits;
	}
	
	public String getKey(int x,int y,int z){
		//System.out.println("GETKEY "+ x +" "+ y +" "+  z);
		int s = ((x) << 16) + ((y) << 8) + (z);
		return ""+s;
	}
	
}
