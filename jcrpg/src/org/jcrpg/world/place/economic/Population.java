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
	
	public transient ArrayList<Residence> residenceList = new ArrayList<Residence>(); 

	public Population()
	{
		super(null, null, null, null,null,null);
	}
	
	@Override
	public void onLoad()
	{
		residenceList = new ArrayList<Residence>();
		boundaries = new GroupedBoundaries((World)parent,this);
		update();
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
	public void addResidence(Residence residence)
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
		sizeX++;
		sizeZ++;
		//updateLocationAndSize();
	}
	
	// TODO write a quick fitter function to build up a population structure quickl.

	@Override
	public void update() {
		int hsizeX =5 , hsizeY = 2, hsizeZ = 5;
		int xOffset = 0;
		int zOffset = 0;
		int streetSize = 0;
		int sizeX = (int)Math.sqrt(owner.getGroupSizes().length)+1;
		int sizeY = sizeX;
		System.out.println("SIZEX/Y"+sizeX+" "+sizeY);
		for (int x1=0; x1<sizeX; x1++)
		{
			hsizeX = 4;
			zOffset = 0;
			for (int z1=0; z1<sizeY; z1++)
			{
				int i = x1*sizeX+z1;
				//System.out.println("Addig "+i);
				if (i>=owner.getGroupSizes().length) continue;
				
				{
					hsizeY = Math.max(1,1+owner.getGroupSizes()[i]/4);
					hsizeZ = 4;
				}
				
				if (residenceList.size()<=i)
				{
					
					World world = (World)getRoot();
					ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(owner.homeBoundary.posX, owner.homeBoundary.posZ+zOffset);
					if (surfaces.size()>0)
					{
						
						for (SurfaceHeightAndType[] s:surfaces)
						{
							int Y = s[0].surfaceY;
							Geography g = s[0].self;
							ArrayList<Class<?extends Residence>> list = ((HumanoidEntityDescription)(owner.description)).economyTemplate.residenceTypes.get(soilGeo.getClass());
							if (list!=null && list.size()>0)
							{
								Class<? extends Residence> r = list.get(0);
								int maximumHeight = -1;
								for (int x = owner.homeBoundary.posX; x<=owner.homeBoundary.posX+hsizeX; x++)
								{
									for (int z = owner.homeBoundary.posZ+zOffset; z<=owner.homeBoundary.posZ+zOffset+hsizeZ; z++)
									{
										int[] values = g.calculateTransformedCoordinates(x, g.worldGroundLevel, z);
										int height = g.getPointHeight(values[3], values[5], values[0], values[2],x,z, false) + g.worldGroundLevel;
										if (height>maximumHeight)
										{
											maximumHeight = height;
										}
									}
								}
								
								if (world.getEconomicCube(-1, owner.homeBoundary.posX, maximumHeight, owner.homeBoundary.posZ+zOffset, false)!=null)
								{
									continue;
								}
								
								
								if (world.getEconomicCube(-1, owner.homeBoundary.posX, maximumHeight, owner.homeBoundary.posZ, false)!=null)
								{
									continue;
								}
								
								Residence rI = ((Residence)EconomyTemplate.economicBase.get(r)).getInstance(
										"house"+owner.id+"_"+owner.homeBoundary.posX+"_"+maximumHeight+"_"+(owner.homeBoundary.posZ+zOffset),
										g,world,world.treeLocator,hsizeX,hsizeY,hsizeZ,
										owner.homeBoundary.posX+xOffset,maximumHeight,owner.homeBoundary.posZ+zOffset,0,
										owner.homeBoundary, owner);
								System.out.println("ADDING HOUSE!"+x1+":"+z1+" __ "+Y+ " "+rI.id);
								if (soilGeo instanceof Cave)
								{
									System.out.println("### CAVE HOUSE = "+rI.id);
								}
								addResidence(rI);
								break;
							}
							
						}
						
						
					}
					
				}
				zOffset+=(hsizeZ+streetSize);
			}
			xOffset+=(hsizeX+streetSize);
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
