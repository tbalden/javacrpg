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
package org.jcrpg.world.place.economic;

import java.util.ArrayList;

import org.jcrpg.space.Cube;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.GroupedBoundaries;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;

public class Population extends Economic{
	
	public ArrayList<Economic> residenceList = new ArrayList<Economic>(); 

	public Population(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner) {
		super(id, soilGeo, parent, loc,null,owner);
		boundaries = new GroupedBoundaries(parent);
		update();
	}
	
	/**
	 * Adds a residential building to the list and to the boundaries.
	 * @param residence
	 */
	public void addResidence(Economic residence)
	{
		residenceList.add(residence);
		((GroupedBoundaries)boundaries).addBoundary(residence.getBoundaries());
	}
	
	/**
	 * recalculating things based on instance's groupedboundaries,
	 * also updateLocationAndSize later. TODO this.
	 */
	public void recalculate()
	{
		((GroupedBoundaries)boundaries).recalculateLimits();
		// updating limits
		origoX = boundaries.limitXMin;
		sizeX = boundaries.limitXMax - boundaries.limitXMin;
		origoY = boundaries.limitYMin;
		sizeY = boundaries.limitYMax - boundaries.limitYMin;
		origoZ = boundaries.limitZMin;
		sizeZ = boundaries.limitZMax - boundaries.limitZMin;
		//updateLocationAndSize();
	}

	@Override
	public void update() {
		int hsizeX =5 , hsizeY = 2, hsizeZ = 5;
		int streetSize = 0;
		for (int i=0; i<owner.groupSizes.length; i++)
		{
			if (residenceList.size()<=i)
			{
				System.out.println("ADDING HOUSE!"+i);
				World world = (World)getRoot();
				ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(owner.domainBoundary.posX, owner.domainBoundary.posZ+i*12);
				if (surfaces.size()>0)
				{
					int Y = surfaces.get(0)[0].surfaceY;
					try {
						House h = new House("house"+owner.id+"_"+owner.domainBoundary.posX+"_"+Y+"_"+owner.domainBoundary.posZ,
								surfaces.get(0)[0].self,world,world.treeLocator,hsizeX,hsizeY,hsizeZ,
								owner.domainBoundary.posX,surfaces.get(0)[0].self.worldGroundLevel,owner.domainBoundary.posZ+(hsizeZ+streetSize)*i,0,
								owner.homeBoundary, owner);
						addResidence(h);
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
					
				}
				
			}
		}
		super.update();
		recalculate();
	}

	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		// going through the possible residences...
		for (Economic e:residenceList)
		{
			// if inside..
			if (e.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				// return cube.
				return e.getCube(key, worldX, worldY, worldZ, farView);
			}
		}
		// no cube here...
		return null;
	}
	

}
