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

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.WorldGenerator;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
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

	@Override
	public void runProgram(WorldGenerator gen, World w, WorldParams params) throws Exception {
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
				String className = gen.climateBeltMap.get(climateName);
				Class<?> c = Class.forName(className);
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
		
		Geography l = instantiateGeography(params.foundationGeo, w, params);
		if (l instanceof Water) {
			w.waters.put(l.id, (Water)l);
		} else
		{
			w.geographies.put(l.id, l);
		}
		System.out.println("--- "+gWX+" - "+gWZ+ " = "+gWX*gWZ);
		
		Plain p = new Plain("BIGPLAIN",w,null,w.getSeaLevel(1),gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, false);
		w.addGeography(p);
		Forest f = new Forest("BIGFOREST",w,null,w.getSeaLevel(1),gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, false);
		w.addGeography(f);
		Cave c = new Cave("BIGCAVE",w,null,w.getSeaLevel(1)+1,w.getSeaLevel(1)+3,gMag, gWX, 2, gWZ, 0, w.getSeaLevel(gMag), 0, 30,Cave.LIMIT_WEST|Cave.LIMIT_SOUTH|Cave.LIMIT_NORTH|Cave.LIMIT_EAST,2,false);
		w.addGeography(c);
		MountainNew m = new MountainNew("MOUNTAINS",w,null,w.getSeaLevel(1),w.getSeaLevel(1)+5*(int)(Math.sqrt(gMag)*params.heightRatio)/10 ,gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, false);
		w.addGeography(m);
		River r = new River("RIVERS",w,null,w.getSeaLevel(1), gMag, gWX, gWY, gWZ, 0, w.getSeaLevel(gMag)-1, 0, 1,1,0.2f,4, false);
		w.waters.put(r.id, r); //r.noWaterInTheBed = true;
		
		for (int x=0; x<gWX; x++)
		{
			for (int z=0; z<gWZ;z++)
			{
				if (x%2==0 && !l.isAlgrithmicallyInside(x*gMag, l.worldGroundLevel, z*gMag)) 
				{
					r.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag), z);
					r.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag)-1, z);
					r.flowDirections.setCubeFlowDirection(x, w.getSeaLevel(gMag), z, J3DCore.NORTH, true);
					r.flowDirections.setCubeFlowDirection(x, w.getSeaLevel(gMag), z, J3DCore.WEST, true);
					r.flowDirections.setCubeFlowDirection(x, w.getSeaLevel(gMag)-1, z, J3DCore.NORTH, true);
					r.flowDirections.setCubeFlowDirection(x, w.getSeaLevel(gMag)-1, z, J3DCore.WEST, true);
				}
				
				if ((x+z)%2==0)
				{
					p.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag), z);
					p.getBoundaries().addCube(gMag, x, w.getSeaLevel(gMag)-1, z);
				} else
				{
					if (!l.isAlgrithmicallyInside(x*gMag, l.worldGroundLevel, z*gMag)) 
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
	}
	
	public Geography instantiateGeography(String geoClass, World w, WorldParams params) throws Exception
	{
		int wMag = params.magnification;
		int wX = params.sizeX;
		int wY = params.sizeY;
		int wZ = params.sizeZ;
		if (geoClass.equals("Ocean"))
		{
			Ocean l = new Ocean("OCEANS", w, null, w.getSeaLevel(1),wMag,wX,wY,wZ,0,0,0,1,params.landMass,params.landDensity);
			return l;
		}
		return null;
	}

}
