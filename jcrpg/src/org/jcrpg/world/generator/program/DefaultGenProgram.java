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

package org.jcrpg.world.generator.program;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.generator.ClassFactory;
import org.jcrpg.world.generator.GenProgram;
import org.jcrpg.world.generator.GeneratedPartRuleSet;
import org.jcrpg.world.generator.WorldGenerator;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.generator.program.algorithm.GenAlgoAdd;
import org.jcrpg.world.generator.program.algorithm.GenAlgoBase;
import org.jcrpg.world.generator.program.algorithm.GenAlgoFlow;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;

/**
 * Default Generation Program implementation with Climate/Foundation/Base/Additional geography handling.
 * @author illes
 */
public class DefaultGenProgram extends GenProgram {
	

	public DefaultGenProgram(ClassFactory factory, WorldGenerator generator, WorldParams params) {
		super(factory, generator, params);
		genAlgorithms.put(GenAlgoAdd.GEN_TYPE_NAME, GenAlgoAdd.class);
		genAlgorithms.put(GenAlgoFlow.GEN_TYPE_NAME, GenAlgoFlow.class);
	}

	@Override
	public void runProgram(World world) throws Exception {
		int wMag = params.magnification;
		int wX = params.sizeX;
		int wY = params.sizeY;
		int wZ = params.sizeZ;
		
		// FLORA
		world.setFloraContainer(new BaseFloraContainer());
		
		// ORBITERS
		WorldOrbiterHandler woh = new WorldOrbiterHandler();
		woh.addOrbiter("sun", new SimpleSun("SUN"));
		woh.addOrbiter("moon", new SimpleMoon("moon"));
		world.setOrbiterHandler(woh);		
		
		// CLIMATE
		// TODO -> do not duplicate climate belts! shrinkToWorld must be reconsidered!! turn direction when crossing the world limit
		Climate climate = new Climate("climate",world);
		world.setClimate(climate);
		
		int climateSizeDivider = 0;
		for (int i=0; i<params.climateSizeMuls.length; i++)
		{
			climateSizeDivider+=params.climateSizeMuls[i];
		}
		int climateSize = wZ/(climateSizeDivider*2);
		
		
		System.out.println("WZ = "+wZ+" CLIZ = "+climateSize*(climateSizeDivider*2));
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
				String className = generator.climateBeltMap.get(climateName);
				Class<?> c = Class.forName(className);
				Constructor<?> constructor = (Constructor<?>)c.getConstructors()[0];
				ClimateBelt belt = (ClimateBelt)constructor.newInstance(climateName+j+" "+i,climate);
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
		//int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;
		
		Geography foundation = instantiateGeography(params.foundationGeo, world);
		world.addGeography(foundation);
		System.out.println("--- "+gWX+" - "+gWZ+ " = "+gWX*gWZ);
		
		// collection normal geos
		TreeMap<String , Geography> mainGeos = new TreeMap<String, Geography>();
		for (int i=0; i<params.geos.length; i++)
		{
			String geo = params.geos[i];
			Geography g = instantiateGeography(geo, world);
			if (!GeneratedPartRuleSet.GEN_TYPE_RANDOM.equals(g.ruleSet.getGeneratorType())) continue;
			mainGeos.put(g.ruleSet.geoTypeName, g);
			world.addGeography(g);
		}
		
		// collecting additional geos
		TreeMap<String , Geography> additionalGeos = new TreeMap<String, Geography>();
		for (int i=0; i<params.additionalGeos.length; i++)
		{
			String geo = params.additionalGeos[i];
			Geography g = instantiateGeography(geo, world);
			if (GeneratedPartRuleSet.GEN_TYPE_RANDOM.equals(g.ruleSet.getGeneratorType())) continue;
			additionalGeos.put(g.ruleSet.geoTypeName, g);
			world.addGeography(g);
		}
		
		// normal "random" generation
		HashMap<String, Integer> geoToLikeness = new HashMap<String, Integer>(); 
		for (int x=0; x<gWX; x++)
		{
			for (int z=0; z<gWZ;z++)
			{
				boolean basePresent =  foundation.isAlgorithmicallyInside(x*gMag, foundation.worldGroundLevel, z*gMag);
				int sumOfLikenesses = 0;
				geoToLikeness.clear();
				for (Geography g:mainGeos.values())
				{
					// check if base geography is present and the give geography can coexist with it
					if (!g.ruleSet.presentWhereBaseExists() && basePresent)
					{
						// ..it cannot
						continue;
					}
					int likeness = g.ruleSet.likenessToNeighbor(getNeighbors(mainGeos.values(), x, world.getSeaLevel(gMag), z));
					sumOfLikenesses+=likeness;
					geoToLikeness.put(g.ruleSet.geoTypeName, likeness);
				}
				// roll out the random value...
				int randomValue = (HashUtil.mixPer1000(x, 0, z)*sumOfLikenesses)/1000;
				int limit = 0;
				// check through the geographies, which was randomly sorted out...
				for (Geography g:mainGeos.values())
				{
					if (!g.ruleSet.presentWhereBaseExists() && basePresent)
					{
						continue;
					}
					limit+=geoToLikeness.get(g.ruleSet.geoTypeName);
					if (limit>randomValue)
					{
						// here we go, found the geography sorted out...
						//System.out.println(randomValue+"<"+limit+" "+g.ruleSet.geoTypeName);
						g.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
						g.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag)-1, z);
						break;
					}
				}
						
			}
		}
		
		
		// Running programs for additional geos
		for (Geography geo: additionalGeos.values())
		{
			System.out.println("ADDITIONAL GEO:"+geo.ruleSet.geoTypeName+" "+geo.ruleSet.genType);
			// get algorithm type...
			Class<? extends GenAlgoBase> algo = genAlgorithms.get(geo.getRuleSet().getGeneratorType());
			if (algo==null) continue;
			// get constructor of the algo class
			Constructor<? extends GenAlgoBase> cons = (Constructor<? extends GenAlgoBase>)algo.getConstructor(World.class, Object[].class, Geography.class);
			// create instance and run...
			cons.newInstance(world,geo.ruleSet.genParams,geo).runGeneration(this);
		}

	}

	// get neighboring geography types.
	public String[] getNeighbors(Collection<Geography> worldSizedGeos, int x, int y, int z)
	{
		int xMinus = x-1;
		int zMinus = y-1;
		int xPlus = x+1;
		int zPlus = y+1;
		xMinus = generator.world.shrinkToWorld(xMinus);
		zMinus = generator.world.shrinkToWorld(zMinus);
		xPlus = generator.world.shrinkToWorld(xPlus);
		zPlus = generator.world.shrinkToWorld(zPlus);		
		
		HashSet<String> found = new HashSet<String>();
		for (Geography geo:worldSizedGeos)
		{
			if (geo.getBoundaries().isInside(xPlus, y, z))
			{
				found.add(geo.ruleSet.geoTypeName);
			}
			if (geo.getBoundaries().isInside(xPlus, y, zPlus))
			{
				found.add(geo.ruleSet.geoTypeName);
			}
			if (geo.getBoundaries().isInside(xPlus, y, zMinus))
			{
				found.add(geo.ruleSet.geoTypeName);
			}
			if (geo.getBoundaries().isInside(xMinus, y, zMinus))
			{
				found.add(geo.ruleSet.geoTypeName);
			}
		}
		return found.toArray(new String[0]);
	}
	
	/**
	 * create an instance of the given geoclass.
	 * @param geoClass
	 * @param world
	 * @return
	 * @throws Exception
	 */
	public Geography instantiateGeography(String geoClass, World world) throws Exception
	{
		Geography r = null;
		if (geoClass.equals("Ocean"))
		{
			Ocean l = (Ocean)factory.createWater(Ocean.class);
			r = l;
		}
		if (geoClass.equals("Plain"))
		{
			r = factory.createGeography(Plain.class);
		}
		if (geoClass.equals("Forest"))
		{
			r = factory.createGeography(Forest.class);
		}
		if (geoClass.equals("Cave"))
		{
			r = factory.createGeography(Cave.class);
		}
		if (geoClass.equals("Mountain"))
		{
			r = factory.createGeography(MountainNew.class);
		}
		if (geoClass.equals("River"))
		{
			r = factory.createWater(River.class);
		}
		return r;
	}

}
