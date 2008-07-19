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

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;

/**
 * Helps locating things in the world in a way similar to octree implementations, dividing space into parts recoursively.
 * @author illes
 *
 */
public class TreeLocator extends PlaceLocator {
	
	public static final int MAX_DEPTH = 5;
	
	public static int DEEPEST_LEVEL_GRANULATION_X = -1;
	public static int DEEPEST_LEVEL_GRANULATION_Y = -1;
	public static int DEEPEST_LEVEL_GRANULATION_Z = -1;
	
	public static final int SEGMENTS_PER_LEVEL_PER_COORDINATE = 2;
	
	public int depth = -1;
	
	/**
	 * Helper class to specify the sub-locator's area.
	 * @author illes
	 *
	 */
	public class Range
	{
		public int xMin, xMax, yMin, yMax, zMin, zMax;
		public Range(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax)
		{
			this.xMax = xMax;
			this.xMin = xMin;
			this.yMax = yMax;
			this.yMin = yMin;
			this.zMax = zMax;
			this.zMin = zMin;
		}
		public boolean isInside(int worldX, int worldY, int worldZ)
		{
			if (worldX>=xMin && worldX<=xMax && worldY>=yMin && worldY<=yMax && worldZ>=zMin && worldZ<=zMax)
				return true;
			return false;
		}
	}
	
	Range range = null;
	
	public ArrayList<TreeLocator> locators = new ArrayList<TreeLocator>();
	public ArrayList<Object> content = null;

	/**
	 * Used internally for sublocators.
	 * @param range
	 * @param depth
	 */
	private TreeLocator(Range range, int depth)
	{
		this.range = range;
		this.depth = depth;
	}
	/**
	 * Highest level TreeLocator constructor.
	 * @param world
	 */
	public TreeLocator(World world)
	{
		this.depth = 0;
		int x= world.realSizeX;
		int y= world.realSizeY;
		int z= world.realSizeZ;
		this.range = new Range(0,x,0,y,0,z);
		DEEPEST_LEVEL_GRANULATION_X = x;
		DEEPEST_LEVEL_GRANULATION_Z = z;
		DEEPEST_LEVEL_GRANULATION_Y = y;
		for (int i=0; i<MAX_DEPTH; i++)
		{
			DEEPEST_LEVEL_GRANULATION_X /= 2;
			DEEPEST_LEVEL_GRANULATION_Z /= 2;
		}
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("### DEEPEST LEVEL X = "+DEEPEST_LEVEL_GRANULATION_X);
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("### DEEPEST LEVEL Z = "+DEEPEST_LEVEL_GRANULATION_Z);
		
	}
	
	/**
	 * Removing an element from the TreeLocator hierarchy
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param o
	 */
	public void removeElement(int worldX, int worldY, int worldZ, Object o)
	{
		if (this.depth==MAX_DEPTH)
		{
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RETURNING CONTENT "+content.size()+" - "+((Economic)content.get(0)).id);
			if (content!=null) content.remove(o);
		} else
		{
			for (TreeLocator loc:locators)
			{
				if (loc.range.isInside(worldX, worldY, worldZ))
				{
					loc.removeElement(worldX, worldY, worldZ,o);
				}
			}
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RETURNING CONTENT NULL");
			return;
		}
	}
	
	/**
	 * Add a coordinate of an object to the elements.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param o
	 */
	public void addElement(int worldX, int worldY, int worldZ, Object o)
	{
		if (this.depth==MAX_DEPTH)
		{
			if (content == null)
			{
				content = new ArrayList<Object>();
			}
			/*if (o instanceof Boundaries && ((Boundaries)o).boundaryPlace!=null)
			{
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("ADDING "+worldX+"/"+worldY+"/"+worldZ+" "+((Boundaries)o).boundaryPlace);
			}*/
			if (!content.contains(o)) 
			{
				//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("ADDING CONTENT: "+o+" - "+worldX+" / "+worldY+" / "+worldZ);
				content.add(o);
			}
		} else 
		{
			for (TreeLocator loc:locators)
			{
				if (loc.range.isInside(worldX, worldY, worldZ))
				{
					loc.addElement(worldX, worldY, worldZ, o);
					return;
				}
			}
			int newMinX = range.xMin;
			int newMinY = range.yMin;
			int newMinZ = range.zMin;
			
			int newMaxX = range.xMax;
			int newMaxY = range.yMax;
			int newMaxZ = range.zMax;
			
			int middleX = range.xMin+((range.xMax-range.xMin)/SEGMENTS_PER_LEVEL_PER_COORDINATE);
			
			if (worldX<=middleX)
			{
				newMaxX = middleX;
			} else
			{
				newMinX = middleX+1;
			}
			/*
			// commented out to speed up things, good because Y range of world is small no need to split it up.
			int middleY = range.yMin+((range.yMax-range.yMin)/SEGMENTS_PER_LEVEL_PER_COORDINATE);
			if (worldY<=middleY)
			{
				newMaxY = middleY;
			} else
			{
				newMinY = middleY+1;
			}*/
			
			int middleZ = range.zMin+((range.zMax-range.zMin)/SEGMENTS_PER_LEVEL_PER_COORDINATE);
			
			if (worldZ<=middleZ)
			{
				newMaxZ = middleZ;
			} else
			{
				newMinZ = middleZ+1;
			}
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("NEW RANGE: "+newMinX+"/"+newMaxX+" "+newMinY+"/"+newMaxY+" "+newMinZ+"/"+newMaxZ);
			Range r = new Range(newMinX,newMaxX,newMinY,newMaxY,newMinZ,newMaxZ);
			TreeLocator newLocator = new TreeLocator(r,depth+1);
			newLocator.addElement(worldX, worldY, worldZ, o);
			locators.add(newLocator);
		}
	}
	
	/**
	 * Returns the elements located near the coordinate on the lowest level (recursively).
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public ArrayList<Object> getElements(int worldX, int worldY, int worldZ)
	{
		if (this.depth==MAX_DEPTH)
		{
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RETURNING CONTENT "+content.size()+" - "+((Economic)content.get(0)).id);
			return content;
		} else
		{
			for (TreeLocator loc:locators)
			{
				if (loc.range.isInside(worldX, worldY, worldZ))
				{
					return loc.getElements(worldX, worldY, worldZ);
				}
			}
			//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("RETURNING CONTENT NULL");
			return null;
		}
	}
	
	// -------------- jcrpg specific things...
	
	/**
	 * Updates an economic place with new data, removing old coordinates defined by the int params.
	 */
	public void updateEconomic(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, Economic e)
	{
		removeElement(xMin, yMin, zMin, e);
		removeElement(xMax, yMin, zMin, e);
		removeElement(xMin, yMax, zMin, e);
		removeElement(xMax, yMax, zMin, e);
		removeElement(xMin, yMin, zMax, e);
		removeElement(xMax, yMin, zMax, e);
		removeElement(xMin, yMax, zMax, e);
		removeElement(xMax, yMax, zMax, e);
		xMin = e.origoX;
		xMax = xMin+e.sizeX;
		yMin = e.origoY;
		yMax = yMin+e.sizeY;
		zMin = e.origoZ;
		zMax = zMin+e.sizeZ;
		addElement(xMin, yMin, zMin, e);
		addElement(xMax, yMin, zMin, e);
		addElement(xMin, yMax, zMin, e);
		addElement(xMax, yMax, zMin, e);
		addElement(xMin, yMin, zMax, e);
		addElement(xMax, yMin, zMax, e);
		addElement(xMin, yMax, zMax, e);
		addElement(xMax, yMax, zMax, e);
	}
	/**
	 * Adding a new economic to the locator hierarchy.
	 * @param e
	 */
	public void addEconomic(Economic e)
	{
		if (e.origoX==-1) return;
		int xMin = e.origoX;
		int xMax = xMin+e.sizeX-1;
		int yMin = e.origoY;
		int yMax = yMin+e.sizeY-1;
		int zMin = e.origoZ;
		int zMax = zMin+e.sizeZ-1;
		while (xMin<=xMax)
		{
			zMin = e.origoZ;
			while (zMin<=zMax)
			{
				addElement(xMin, yMin, zMin, e);
				addElement(xMin, yMax, zMin, e);
				zMin+=1;//DEEPEST_LEVEL_GRANULATION_Z/2; //TODO this is not working correctly for a few cases, why?replaced with +1 for now
			}
			addElement(xMin, yMin, zMax, e);
			addElement(xMin, yMax, zMax, e);
			xMin+=1;//DEEPEST_LEVEL_GRANULATION_X/2;
		}
		addElement(xMax, yMin, zMin, e);
		addElement(xMax, yMax, zMin, e);
		addElement(xMax, yMin, zMax, e);
		addElement(xMax, yMax, zMax, e);
	}

	
	public void addBoundary(Boundaries e)
	{
		int xMin = e.limitXMin;
		int xMax = e.limitXMax;
		int yMin = e.limitYMin;
		int yMax = e.limitYMax;
		int zMin = e.limitZMin;
		int zMax = e.limitZMax;
		for (int x=xMin; x<=xMax;)
		{
			for (int z=zMin; z<=zMax; )
			{
				addElement(x, yMin, z, e);
				addElement(x, yMax, z, e);
				z+=1;//DEEPEST_LEVEL_GRANULATION_Z/2; //TODO this is not working correctly for a few cases, why?replaced with +1 for now
			}			
			addElement(x, yMin, zMax, e);
			addElement(x, yMax, zMax, e);
			x+=1;//DEEPEST_LEVEL_GRANULATION_X/2;
		}
		addElement(xMax, yMin, zMin, e);
		addElement(xMax, yMax, zMin, e);
		addElement(xMax, yMin, zMax, e);
		addElement(xMax, yMax, zMax, e);
	}
	public void removeBoundary(Boundaries e)
	{
		int xMin = e.limitXMin;
		int xMax = e.limitXMax;
		int yMin = e.limitYMin;
		int yMax = e.limitYMax;
		int zMin = e.limitZMin;
		int zMax = e.limitZMax;
		while (xMin<=xMax)
		{
			zMin = e.limitZMin;
			while (zMin<=zMax)
			{
				removeElement(xMin, yMin, zMin, e);
				removeElement(xMin, yMax, zMin, e);
				zMin+=DEEPEST_LEVEL_GRANULATION_Z/2;
			}
			removeElement(xMin, yMin, zMax, e);
			removeElement(xMin, yMax, zMax, e);
			xMin+=DEEPEST_LEVEL_GRANULATION_X/2;
		}
		removeElement(xMax, yMin, zMax, e);
		removeElement(xMax, yMax, zMax, e);
	}
	
	public void addEntityInstance(EntityInstance i)
	{
		for (EntityFragment f: i.fragments.fragments) {
			int r = f.roamingBoundary.radiusInRealCubes;
			int xMin = f.roamingBoundary.posX-r;
			int xMax = f.roamingBoundary.posX+r;
			int yMin = f.roamingBoundary.posY;
			int zMin = f.roamingBoundary.posZ-r;
			int zMax = f.roamingBoundary.posZ+r;
			// TODO circular addition with steps...	instead of box
			while (xMin<=xMax)
			{
				zMin = f.roamingBoundary.posZ-r;
				while (zMin<=zMax)
				{
					addElement(xMin, yMin, zMin, f);
					zMin+=DEEPEST_LEVEL_GRANULATION_Z/2;
				}
				addElement(xMin, yMin, zMax, i);
				xMin+=DEEPEST_LEVEL_GRANULATION_X/2;
			}
			addElement(xMax, yMin, zMax, f);
		}
	}
	public void removeEntityInstance(EntityInstance i)
	{
		for (EntityFragment f: i.fragments.fragments) {
			removeAllOfAnObject(f);
		}
	}

	
	/**
	 * Adding an entity fragment's roaming boundary sized.
	 * @param f
	 */
	public void addEntityFragment(EntityFragment f)
	{
		int r = f.roamingBoundary.radiusInRealCubes;
		int xMin = f.roamingBoundary.posX-r;
		int xMax = f.roamingBoundary.posX+r;
		int yMin = f.roamingBoundary.posY;
		int zMin = f.roamingBoundary.posZ-r;
		int zMax = f.roamingBoundary.posZ+r;
		// TODO circular addition with steps...	instead of box
		while (xMin<=xMax)
		{
			zMin = f.roamingBoundary.posZ-r;
			while (zMin<=zMax)
			{
				addElement(xMin, yMin, zMin, f);
				zMin+=DEEPEST_LEVEL_GRANULATION_Z/2;
			}
			addElement(xMin, yMin, zMax, f);
			xMin+=DEEPEST_LEVEL_GRANULATION_X/2;
		}
		addElement(xMax, yMin, zMax, f);
	}
	public void removeEntityFragment(EntityFragment f)
	{
		removeAllOfAnObject(f);
	}
	
	/**
	 * removing all instances of an object in the hieararchy content.
	 * @param o
	 */
	public void removeAllOfAnObject(Object o)
	{
		for (TreeLocator l:locators)
		{
			l.removeAllOfAnObject(o);
		}
		if (content!=null)
			content.remove(o);
		
	}
	/**
	 * clearing the full locator hierarchy.
	 */
	public void clear()
	{
		for (TreeLocator l:locators)
		{
			l.clear();
		}
		if (content!=null)
			content.clear();
	}
	
}
