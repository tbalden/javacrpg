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

package org.jcrpg.world.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.ai.fauna.mammals.fox.FoxFamily;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;
import org.jcrpg.world.ai.fauna.mammals.warthog.Warthogs;
import org.jcrpg.world.ai.fauna.mammals.wolf.WolfPack;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.time.Time;

public class EcologyGenerator {
	public Ecology generateEcology(World world) throws Exception
	{
		Ecology ecology = new Ecology();
		int nX = 10;
		int nY = 10;
		GorillaHorde gorillaDesc = new GorillaHorde();
		WolfPack wolfDesc = new WolfPack();
		Warthogs wartDesc = new Warthogs();
		FoxFamily foxDesc = new FoxFamily();
		ArrayList<EntityDescription> descs = new ArrayList<EntityDescription>();
		descs.add(gorillaDesc);
		descs.add(wolfDesc);
		descs.add(wartDesc);
		descs.add(foxDesc);
		HashMap<Class<? extends Geography>, HashSet<EntityDescription>> hmGeography = new HashMap<Class<? extends Geography>, HashSet<EntityDescription>>();
		HashMap<Class<? extends ClimateBelt>, HashSet<EntityDescription>> hmClimate = new HashMap<Class<? extends ClimateBelt>, HashSet<EntityDescription>>();
		for (EntityDescription desc:descs)
		{
			if (desc instanceof AnimalEntityDescription)
			{
				AnimalEntityDescription aDesc = (AnimalEntityDescription)desc;
				for (Class<? extends Geography> g: aDesc.getGeographies())
				{
					HashSet<EntityDescription> hsD = hmGeography.get(g);
					if (hsD==null)
					{
						hsD = new HashSet<EntityDescription>();
						hmGeography.put(g, hsD);
					}
					hsD.add(aDesc);
				}
				for (Class<? extends ClimateBelt> g: aDesc.getClimates())
				{
					HashSet<EntityDescription> hsD = hmClimate.get(g);
					if (hsD==null)
					{
						hsD = new HashSet<EntityDescription>();
						hmClimate.put(g, hsD);
					}
					hsD.add(aDesc);
				}
			}
		}
		for (int i=0; i<nX; i++) {
			for (int j=0; j<nY; j++)
			{
				int wX = 1+(int)((world.realSizeX*1f/nX)*i);
				int wY = world.getSeaLevel(1);
				int wZ = (int)((world.realSizeZ*1f/nY)*j);
				CubeClimateConditions ccc = world.getCubeClimateConditions(new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();
				HashSet<EntityDescription> beltDescs = hmClimate.get(beltClass);
				if (beltDescs!=null)
				for (EntityDescription desc: beltDescs) {
					ecology.addEntity(new EntityInstance(desc,world,ecology,desc.getClass().getSimpleName()+i+" "+j,50,wX,wY,wZ));	
				}
				
				//ecology.addEntity(new EntityInstance(wolfDesc,world,ecology,"WO-"+i+" "+j,20,wX,wY,wZ));
				//ecology.addEntity(new EntityInstance(wartDesc,world,ecology,"WA-"+i+" "+j,50,wX,wY,wZ));
			}
		}
		return ecology;
	}

}
