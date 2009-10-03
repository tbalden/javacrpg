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

package org.jcrpg.world.ai.humanoid;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.population.DungeonDistrict;

public class HumanoidEntityDescription extends AnimalEntityDescription {

	
	public static HashMap<Long, Population> bugger = new HashMap<Long, Population>();
	
	@Override
	public void setupNewInstance(EntityInstance instance, World world, Ecology ecology)
	{
		
		int[] coords = world.economyContainer.getPopulationCoordinatesInZone(instance.domainBoundary.posX, instance.domainBoundary.posZ, world.magnification);
		ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(coords[0],coords[1]);
		if (surfaces.size()>0)
		{
			int count = 0;
			while (surfaces.size()>count) {
				Geography g = surfaces.get(count++)[0].self;
				//int[] coords = world.economyContainer.getPopulationCoordinatesInZone(instance.domainBoundary.posX, instance.domainBoundary.posZ, g.blockSize);
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("g: "+g);
				//System.out.println()
				if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("XY "+coords[0]+coords[1]);
				ArrayList<Class<? extends Population>> list = economyTemplate.populationTypes.get(g.getClass());
				if (list!=null && list.size()>0) {
					if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("G: THIS");
					// check if this is an occupied population zone.
					Population pO = world.economyContainer.isOccupied(g, coords[0], coords[1]);
					
					// TODO fix isOccupied, and remove this temporary solution!!!
					long coor = (coords[0]<<16)+ coords[1];
					if (bugger.containsKey(coor)) continue;
					
					if (pO!=null) {
						if (pO.owner.wouldMergeWithOther(instance))
						{
							// the instance is given the possibility to join...
							// TODO decision if instance wants or not..
							pO.owner.merge(instance);
							// TODO enteredPopulation shouldn't be set here
							instance.fragments.fragments.get(0).populatePopulation(pO);
							break;
						}
					} else {
						// okay, here we can try to settle the population for this group...
						Population pI = null;
						for (Class<? extends Population> p:list)
						{
							//if (p==DungeonDistrict.class) System.out.println("INSPECTING p" +p);
							instance.homeBoundary = new DistanceBasedBoundary(world, coords[0],g.worldGroundLevel,coords[1], instance.numberOfMembers);
							pI = ((Population)EconomyTemplate.getBase(p)).getInstance("population"+instance.id,g,world,null, instance,coords[2],coords[3],coords[0],coords[1]);
							if (!pI.isGeographyAreaUsable()) 
							{
								//if (p==DungeonDistrict.class) System.out.println("NOT USABLE");
								instance.homeBoundary = null;
								pI = null;
								continue;
							}
							break;
						}
						if (pI==null) continue;
						//System.out.println("CREATED pI" +pI);
						pI.update();
						world.economyContainer.addPopulation(pI);
						namePopulation(pI);
						world.economyContainer.checkDistrictToTownIntegration(pI);
						instance.homeEconomy = pI; // setting the population as home for the instance, it is not a homeless anymore :)
						// TODO enteredPopulation shouldn't be set here
						instance.fragments.fragments.get(0).populatePopulation(pI);
						if (bugger.containsKey(coor))
						{
							System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
							System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
							System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
							System.out.println(" "+coords[0]+" "+coords[1]);
							System.out.println(""+bugger.get(coor).getClass()+ " "+pI.getClass());
							System.out.println(""+bugger.get(coor).owner.homeBoundary.posX+" / "+bugger.get(coor).owner.homeBoundary.posZ);
							System.out.println(""+pI.owner.homeBoundary.posX+" / "+pI.owner.homeBoundary.posZ);
							System.out.println("" + bugger.get(coor).soilGeo.getClass()+" "+ pI.soilGeo.getClass());
						}
						bugger.put(coor, pI);
						break;
					}
				}
			}
			
		}
	}
	
}
