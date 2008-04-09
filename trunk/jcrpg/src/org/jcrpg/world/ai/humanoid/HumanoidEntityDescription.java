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

import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;

public class HumanoidEntityDescription extends AnimalEntityDescription {

	public ArrayList<EconomyTemplate> economyTemplates = new ArrayList<EconomyTemplate>();
	
	
	@Override
	public void setupNewInstance(EntityInstance instance, World world, Ecology ecology)
	{
		instance.homeBoundary = new DistanceBasedBoundary(world, instance.domainBoundary.posX,instance.domainBoundary.posY,instance.domainBoundary.posZ, instance.numberOfMembers);
		ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(instance.domainBoundary.posX, instance.domainBoundary.posZ);
		if (surfaces.size()>0)
		{
			int Y = surfaces.get(0)[0].surfaceY;
			try {
				House h = new House("house"+instance.id+"_"+instance.domainBoundary.posX+"_"+Y+"_"+instance.domainBoundary.posZ,
						surfaces.get(0)[0].self,world,world.treeLocator,10,3,10,instance.domainBoundary.posX,surfaces.get(0)[0].self.worldGroundLevel,instance.domainBoundary.posZ,0,instance.homeBoundary);
				world.addEconomy(h);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
	}
	
}
