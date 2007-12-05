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
import java.util.Iterator;

import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Lake;
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
		
		Climate climate = new Climate("climate",w);
		w.setClimate(climate);
		
		int climateSize = wZ/(params.climates.length*4);
		for (int i=0; i<params.climates.length; i++)
		{
			String climateName = params.climates[i];
			System.out.println("CLIMATE: "+climateName);
			String className = climateBeltMap.get(climateName);
			Class c = Class.forName(className);
			Constructor<ClimateBelt> constructor = c.getConstructors()[0];
			ClimateBelt belt1 = constructor.newInstance(climateName+"1",climate);
			belt1.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, climateSize, 0, 0, climateSize*i));
			ClimateBelt belt2 = constructor.newInstance(climateName+"2",climate);
			belt2.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, climateSize, 0, 0, (wZ/2)-climateSize*i));
			ClimateBelt belt3 = constructor.newInstance(climateName+"3",climate);
			belt3.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, climateSize, 0, 0, (wZ/2)+climateSize*i));
			ClimateBelt belt4 = constructor.newInstance(climateName+"4",climate);
			belt4.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, climateSize, 0, 0, (wZ)-climateSize*i));
			climate.belts.put(belt1.id, belt1);
			climate.belts.put(belt2.id, belt2);
			climate.belts.put(belt3.id, belt3);
			climate.belts.put(belt4.id, belt4);
		}
		
		//--------
		//|XXXXXX|
		//|YYYYYY|
		//|ZZZZZZ|
		//|XXXXXX|
		//|ZZZZZZ|
		//|YYYYYY|
		// XXXXXX_
		
		// TODO size based!
		Continental continental = new Continental("cont1",climate);
		continental.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, wZ, 0, 0, 0));
		//climate.belts.put(continental.id, continental);
		/*Tropical tropical = new Tropical("trop1",climate);
		tropical.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 2, 0, 0));
		climate.belts.put(tropical.id, tropical);
		Desert desert = new Desert("desert1",climate);
		desert.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 4, 0, 0));
		climate.belts.put(desert.id, desert);
		Arctic arctic = new Arctic("arctic1",climate);
		arctic.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 6, 0, 0));
		climate.belts.put(arctic.id, arctic);*/

		Plain p = new Plain("BIGPLAIN",w,null,w.getSeaLevel(wMag),wMag);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, wZ, 0, w.getSeaLevel(wMag)-1, 0));
		w.geographies.put(p.id, p);

		Ocean l = new Ocean("OCEANS", w, null, w.getSeaLevel(wMag),wMag,wX,wY,wZ,0,0,0,1,params.landMass,params.landDensity);
		w.waters.put(l.id, l);

		//int i =0;
		House h = null; 
		long time = System.currentTimeMillis(); 
		h = new House("house",w,null,4,1,4,0,w.getSeaLevel(1),5);		
		w.economics.put(h.id, h);
		
		w.worldMap = new WorldMap(w);

		return w;
	}
	
	
}
