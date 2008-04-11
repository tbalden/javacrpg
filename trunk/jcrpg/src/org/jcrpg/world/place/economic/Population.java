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
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.GroupedBoundaries;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.sub.Cave;

public class Population extends Economic{
	
	public ArrayList<Economic> residenceList = new ArrayList<Economic>(); 

	public Population()
	{
		super(null, null, null, null,null,null);
	}
	
	public Population(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner) {
		super(id, soilGeo, parent, loc,null,owner);
		boundaries = new GroupedBoundaries(parent,this);
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
		int zOffset = 0;
		int streetSize = 0;
		for (int i=0; i<owner.groupSizes.length; i++)
		{
			int rand = HashUtil.mixPercentage(owner.numericId, i, i);
			if (rand>66)
			{
				hsizeY = 1;
				hsizeZ = Math.max(4,3+owner.groupSizes[i]/2);
				hsizeX = 4;
			}
			else if (rand>33)
			{
				hsizeY = 1;
				hsizeX = Math.max(4,3+owner.groupSizes[i]/2);
				hsizeZ = 4;
			} else
			{
				hsizeY = Math.max(1,1+owner.groupSizes[i]/4);
				hsizeX = 4;
				hsizeZ = 4;
			}
			if (residenceList.size()<=i)
			{
				System.out.println("ADDING HOUSE!"+i);
				World world = (World)getRoot();
				ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(owner.domainBoundary.posX, owner.domainBoundary.posZ+i*12);
				if (surfaces.size()>0)
				{
					int Y = surfaces.get(0)[0].surfaceY;
					//Geography g = 
					try {
						ArrayList<Class<?extends Residence>> list = ((HumanoidEntityDescription)(owner.description)).economyTemplate.residenceTypes.get(soilGeo.getClass());
						if (list!=null && list.size()>0)
						{
							Class<? extends Residence> r = list.get(0);
							Residence rI = ((Residence)EconomyTemplate.economicBase.get(r)).getInstance(
									"house"+owner.id+"_"+owner.domainBoundary.posX+"_"+Y+"_"+owner.domainBoundary.posZ,
									surfaces.get(0)[0].self,world,world.treeLocator,hsizeX,hsizeY,hsizeZ,
									owner.domainBoundary.posX,surfaces.get(0)[0].self.worldGroundLevel,owner.domainBoundary.posZ+zOffset,0,
									owner.homeBoundary, owner);
							if (soilGeo instanceof Cave)
							{
								System.out.println("### CAVE HOUSE = "+rI.id);
							}
							addResidence(rI);
						}
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
					
				}
				
			}
			zOffset+=(hsizeZ+streetSize);
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
	
	public Population getInstance(String id,Geography soilGeo, World parent, PlaceLocator loc, EntityInstance owner)
	{
		return new Population(id,soilGeo,parent,loc,owner);
	}
}
