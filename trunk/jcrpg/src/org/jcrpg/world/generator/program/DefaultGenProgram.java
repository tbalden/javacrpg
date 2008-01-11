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
import org.jcrpg.world.WorldGenerator;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.generator.ClassFactory;
import org.jcrpg.world.generator.GenProgram;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.MountainNew;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;

public class DefaultGenProgram extends GenProgram {
	

	public DefaultGenProgram(ClassFactory factory, WorldGenerator generator, WorldParams params) {
		super(factory, generator, params);
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
				Constructor<ClimateBelt> constructor = (Constructor<ClimateBelt>)c.getConstructors()[0];
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
		
		Geography l = instantiateGeography(params.foundationGeo, world);
		if (l instanceof Water) {
			world.waters.put(l.id, (Water)l);
		} else
		{
			world.geographies.put(l.id, l);
		}
		System.out.println("--- "+gWX+" - "+gWZ+ " = "+gWX*gWZ);
		
		TreeMap<String , Geography> mainGeos = new TreeMap<String, Geography>();
		for (int i=0; i<params.geos.length; i++)
		{
			String geo = params.geos[i];
			Geography g = instantiateGeography(geo, world);
			mainGeos.put(g.ruleSet.geoTypeName, g);
			world.addGeography(g);
		}
		
		River r = new River("RIVERS",world,null,world.getSeaLevel(1), gMag, gWX, gWY, gWZ, 0, world.getSeaLevel(gMag)-1, 0, 1,1,0.2f,4, false);
		world.waters.put(r.id, r); //r.noWaterInTheBed = true;
		
		HashMap<String, Integer> geoToLikeness = new HashMap<String, Integer>(); 
		for (int x=0; x<gWX; x++)
		{
			for (int z=0; z<gWZ;z++)
			{
				boolean basePresent =  l.isAlgorithmicallyInside(x*gMag, l.worldGroundLevel, z*gMag);
				int sumOfLikenesses = 0;
				geoToLikeness.clear();
				for (Geography g:mainGeos.values())
				{
					if (!g.ruleSet.presentWhereBaseExists() && basePresent)
					{
						continue;
					}
					int likeness = g.ruleSet.likenessToNeighbor(getNeighbors(mainGeos.values(), x, world.getSeaLevel(gMag), z));
					sumOfLikenesses+=likeness;
					geoToLikeness.put(g.ruleSet.geoTypeName, likeness);
				}
				int randomValue = (HashUtil.mixPer1000(x, 0, z)*sumOfLikenesses)/1000;
				int limit = 0;
				for (Geography g:mainGeos.values())
				{
					if (!g.ruleSet.presentWhereBaseExists() && basePresent)
					{
						continue;
					}
					limit+=geoToLikeness.get(g.ruleSet.geoTypeName);
					if (limit>randomValue)
					{
						//System.out.println(randomValue+"<"+limit+" "+g.ruleSet.geoTypeName);
						g.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
						g.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag)-1, z);
						break;
					}
				}
				/*
				if (x%2==0 && !l.isAlgorithmicallyInside(x*gMag, l.worldGroundLevel, z*gMag)) 
				{
					r.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
					r.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag)-1, z);
					r.flowDirections.setCubeFlowDirection(x, world.getSeaLevel(gMag), z, J3DCore.NORTH, true);
					r.flowDirections.setCubeFlowDirection(x, world.getSeaLevel(gMag), z, J3DCore.WEST, true);
					r.flowDirections.setCubeFlowDirection(x, world.getSeaLevel(gMag)-1, z, J3DCore.NORTH, true);
					r.flowDirections.setCubeFlowDirection(x, world.getSeaLevel(gMag)-1, z, J3DCore.WEST, true);
				}
				
				if ((x+z)%2==0)
				{
					p.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
					p.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag)-1, z);
				} else
				{
					if (!l.isAlgorithmicallyInside(x*gMag, l.worldGroundLevel, z*gMag)) 
					{
						m.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
						m.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag)-1, z);
						c.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
						
					} else
					{
						//if (!m.getBoundaries().isInside(x*gMag, m.worldGroundLevel, z*gMag))
						{
							f.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag), z);
							f.getBoundaries().addCube(gMag, x, world.getSeaLevel(gMag)-1, z);
						}
					}
				}				*/	
			}
		}

		//int i =0;
		House h = null; 
		long time = System.currentTimeMillis(); 
		h = new House("house",world,null,4,1,4,0,world.getSeaLevel(1),5);		
		world.economics.put(h.id, h);
	}
	
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
		return r;
	}

}
