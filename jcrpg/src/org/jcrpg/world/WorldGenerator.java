/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Ocean;

public class WorldGenerator {

	public HashMap<String, String> climateBeltMap = new HashMap<String, String>();
	public HashMap<String, String> geographyMap = new HashMap<String, String>();

	public WorldGenerator()
	{
		climateBeltMap.put("Continental", Continental.class.getName());
		climateBeltMap.put("Tropical", Tropical.class.getName());
		climateBeltMap.put("Arctic", Arctic.class.getName());
		climateBeltMap.put("Desert", Desert.class.getName());
		
		geographyMap.put("Plain", Plain.class.getName());
		geographyMap.put("Forest", Forest.class.getName());
		geographyMap.put("Mountain", Mountain.class.getName());
		
	}
	
	public World generateWorld(WorldParams params) throws Exception
	{
		World w = new World("world", null,params.magnification,params.sizeX,params.sizeY,params.sizeZ);
		
		w.lossFactor = params.geoNormalSize;
		
		w.GEOGRAPHY_RANDOM_SEED = params.randomSeed;
		
		int wMag = params.magnification;
		int wX = params.sizeX;
		int wY = params.sizeY;
		int wZ = params.sizeZ;
		
		
		
		// FLORA
		w.setFloraContainer(new BaseFloraContainer());
		
		// ORBITERS
		WorldOrbiterHandler woh = new WorldOrbiterHandler();
		woh.addOrbiter("sun", new SimpleSun("SUN"));
		woh.addOrbiter("moon", new SimpleMoon("moon"));
		w.setOrbiterHandler(woh);		
		
		// CLIMATE
		// TODO -> do not duplicate climate belts! shrinkToWorld must be reconsidered!! turn direction when crossing the world limit
		Climate climate = new Climate("climate",w);
		w.setClimate(climate);
		
		int climateSizeDivider = 0;
		for (int i=0; i<params.climateSizeMuls.length; i++)
		{
			climateSizeDivider+=params.climateSizeMuls[i];
		}
		int climateSize = wZ/(climateSizeDivider*2);
		
		
		System.out.println("WZ = "+wZ+" CLIZ = "+climateSize*(climateSizeDivider*2));
		int[] correctedClimateSizes = new int[params.climates.length];
		// calc the remaining size in the world
		int diffAvailable = wZ-climateSize*(climateSizeDivider*2);
		System.out.println("DIFF = "+diffAvailable);
		int mod = climateSizeDivider*2/diffAvailable;
		int currentWorldZ = 0;
		for (int j=0; j<2; j++) {
			boolean orderAsc = j%2==0?true:false;
			for (int i=0; i<params.climates.length; i++)
			{
				int count = orderAsc? i:params.climates.length-i-1;
				String climateName = params.climates[count];
				System.out.println("CLIMATE: "+climateName);
				String className = climateBeltMap.get(climateName);
				Class c = Class.forName(className);
				Constructor<ClimateBelt> constructor = c.getConstructors()[0];
				ClimateBelt belt = constructor.newInstance(climateName+j+" "+i,climate);
				int climateSizeCorrected = climateSize*params.climateSizeMuls[count];
				if (diffAvailable>0 && ((i+j*params.climates.length)%mod == 0))
				{
					climateSizeCorrected+=1;
					diffAvailable-=1;
				}
				if ((j==1 && i==params.climates.length-1))
				{
					while (diffAvailable>0)
					{
						climateSizeCorrected++;
						diffAvailable--;
					}
					
				}
				belt.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, climateSizeCorrected, 0, 0, currentWorldZ));
				currentWorldZ+=climateSizeCorrected;
				climate.belts.put(belt.id, belt);
			}
		}

		
		//--------
		//|XXXXXX|
		//|YYYYYY|
		//|ZZZZZZ|
		//|XXXXXX|
		//|ZZZZZZ|
		//|YYYYYY|
		// XXXXXX_

		// GEOGRAPHIES
		
		int gMag = params.geoNormalSize;
		int gWX = (wX*wMag)/gMag;
		int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;
		
		Ocean l = new Ocean("OCEANS", w, null, w.getSeaLevel(1),wMag,wX,wY,wZ,0,0,0,1,params.landMass,params.landDensity);
		w.waters.put(l.id, l);
		System.out.println("--- "+gWX+" - "+gWZ+ " = "+gWX*gWZ);
		
		Plain p = new Plain("BIGPLAIN",w,null,w.getSeaLevel(1),gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, false);
		w.addGeography(p);
		Forest f = new Forest("BIGFOREST",w,null,w.getSeaLevel(1),gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, false);
		w.addGeography(f);
		Cave c = new Cave("BIGCAVE",w,null,w.getSeaLevel(1),w.getSeaLevel(1)+1,gMag, gWX, 2, gWZ, 0, w.getSeaLevel(gMag), 0, 30,Cave.LIMIT_WEST|Cave.LIMIT_SOUTH|Cave.LIMIT_NORTH|Cave.LIMIT_EAST,0,1,1,false);
		w.addGeography(c);
		Mountain m = new Mountain("MOUNTAIN",w,null,w.getSeaLevel(1),w.getSeaLevel(1)+4*gMag/10 ,gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, false);
		w.addGeography(m);
		
		for (int x=0; x<gWX; x++)
		{
			for (int z=0; z<gWZ;z++)
			{
				if ((x+z)%2==0)
				{
					p.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag), z);
					p.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag)-1, z);
				} else
				{
					if (!l.isWaterPointSpecial(x*gMag, l.worldGroundLevel, z*gMag,false)) 
					{
						m.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag), z);
						m.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag)-1, z);
						c.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag), z);
						
					} else
					{
						//if (!m.getBoundaries().isInside(x*gMag, m.worldGroundLevel, z*gMag))
						{
							f.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag), z);
							f.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag)-1, z);
						}
					}
				}					
			}
		}

		//int i =0;
		House h = null; 
		long time = System.currentTimeMillis(); 
		h = new House("house",w,null,4,1,4,0,w.getSeaLevel(1),5);		
		w.economics.put(h.id, h);
		
		w.worldMap = new WorldMap(w);

		return w;
	}
	
	
}
