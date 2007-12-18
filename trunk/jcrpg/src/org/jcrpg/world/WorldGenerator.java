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

import java.util.HashMap;

import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.generator.GenProgram;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;

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
	
	public World generateWorld(WorldParams params, GenProgram program) throws Exception
	{
		World w = new World("world", null,params.magnification,params.sizeX,params.sizeY,params.sizeZ);
		
		w.lossFactor = params.geoNormalSize;
		
		w.GEOGRAPHY_RANDOM_SEED = params.randomSeed;
		
		program.runProgram(this, w, params);
		
		w.worldMap = new WorldMap(w);

		return w;
	}
	
	
}
