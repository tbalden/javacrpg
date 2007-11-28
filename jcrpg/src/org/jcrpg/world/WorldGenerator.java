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

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Lake;
import org.jcrpg.world.place.water.Ocean;

public class WorldGenerator {

	
	
	//public static final int 
	
	
	public World generateWorld(WorldParams params) throws Exception
	{
		World w = new World("world", null,params.magnification,params.sizeX,params.sizeY,params.sizeZ);
		
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
		
		// TODO size based!
		Continental continental = new Continental("cont1",climate);
		continental.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, wZ, 0, 0, 0));
		climate.belts.put(continental.id, continental);
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

		Ocean l = new Ocean("OCEANS", w, null, w.getSeaLevel(wMag),wMag,wX,wY,wZ,0,0,0,1,30);
		w.waters.put(l.id, l);

		//int i =0;
		House h = null; 
		long time = System.currentTimeMillis(); 
		h = new House("house",w,null,4,1,4,0,w.getSeaLevel(1),5);		
		w.economics.put(h.id, h);

		return w;
	}
	
}
