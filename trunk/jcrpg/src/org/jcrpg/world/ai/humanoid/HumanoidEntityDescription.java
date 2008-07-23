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

package org.jcrpg.world.ai.humanoid;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.Population;

public class HumanoidEntityDescription extends AnimalEntityDescription {

	
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
				Jcrpg.LOGGER.finer("g: "+g);
				Jcrpg.LOGGER.finer("XY "+coords[0]+coords[1]);
				ArrayList<Class<? extends Population>> list = economyTemplate.populationTypes.get(g.getClass());
				if (list!=null && list.size()>0) {
					Jcrpg.LOGGER.finer("G: THIS");
					// check if this is an occupied population zone.
					Population pO = world.economyContainer.isOccupied(g, coords[0], coords[1]);
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
						// okay, here we can settle the population for this group...
						instance.homeBoundary = new DistanceBasedBoundary(world, coords[0],g.worldGroundLevel,coords[1], instance.numberOfMembers);
						Class<? extends Population> p = list.get(0);
						Population pI = ((Population)EconomyTemplate.economicBase.get(p)).getInstance("population"+instance.id,g,world,null, instance,coords[2],coords[3],coords[0],coords[1]);
						pI.update();
						world.economyContainer.addPopulation(pI);
						namePopulation(pI);
						world.economyContainer.checkDistrictToTownIntegration(pI);
						instance.homeEconomy = pI; // setting the population as home for the instance, it is not a homeless anymore :)
						// TODO enteredPopulation shouldn't be set here
						instance.fragments.fragments.get(0).populatePopulation(pI);
						break;
					}
				}
			}
			
		}
	}
	
}
