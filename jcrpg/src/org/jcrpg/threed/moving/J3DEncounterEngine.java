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
package org.jcrpg.threed.moving;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.World.WorldTypeDesc;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.Residence;
import org.jcrpg.world.place.economic.ground.EncounterGround;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;

public class J3DEncounterEngine extends J3DStandingEngine {
	
	transient World fallbackWorld = null;
	public J3DEncounterEngine(J3DCore core) {
		super(core);
		extRootNode = core.encounterExtRootNode;
		intRootNode = core.encounterIntRootNode;
		renderedArea = core.renderedEncounterArea;
		rerenderWithRemove = true;
		optimizeAngle = false;
		fragmentedViewDivider = 2;
		
	}
	transient HashMap<String, World> encounterGroundWorlds = new HashMap<String, World>();
	
	public World getEncounterGroundWorld(Cube c, CubeClimateConditions ccc, Geography geo, EconomicGround ground, Residence house, String specialType) throws Exception
	{
		World baseWorld = new World("encounterGround",null,100,1,1,1);
		baseWorld.engine = core.gameState.engine;
		int wMag = 100;
		int wX = 1;
		int wY = 1;
		//int wZ = 1;
		
		// FLORA
		baseWorld.setFloraContainer(new BaseFloraContainer());
		
		// ORBITERS
		WorldOrbiterHandler woh = new WorldOrbiterHandler();
		woh.addOrbiter("sun", new SimpleSun("SUN"));
		woh.addOrbiter("moon", new SimpleMoon("moon"));
		baseWorld.setOrbiterHandler(woh);
		
		Climate climate = new Climate("climate",baseWorld);
		baseWorld.setClimate(climate);
		
		try {
			Class beltClass = ccc.getBelt().getClass();
			Constructor<?> constructor = (Constructor<?>)beltClass.getConstructors()[0];
			ClimateBelt belt = (ClimateBelt)constructor.newInstance("encWorld "+beltClass,climate);
			belt.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, 1, 0, 0, 0));
			climate.belts.put(belt.id, belt);
		} catch (Exception ex)
		{
			
		}
		if (geo!=null && geo.getClass() == Cave.class)
		{
			Cave cave = new Cave("cave1",baseWorld,null,0,2,100,1,1,1,0,0,0,0,0,2,true);
			cave.alwaysInsideCubesForEncounterGround = true;
			baseWorld.addGeography(cave);
		} else
		{
			Plain plain = new Plain("plain1",baseWorld,null,0,100,1,1,1,0,0,0,true);
			baseWorld.addGeography(plain);
		}
		if (ground!=null)
		{
			EconomicGround g = ground.getInstance("enc", geo, baseWorld, null, 6, 10, 8, 44, 0, 38, 0, null, null);
			baseWorld.economyContainer.addGround(g);
		} else
		{
			EconomicGround g = new EncounterGround("enc", geo, baseWorld, null, 6, 10, 8, 44, 0, 38, 0, null, null);
			baseWorld.economyContainer.addGround(g);
		}
		if (house!=null)
		{
			Residence r = house.getInstance("enc", geo, baseWorld, null, 4, house.getMinimumHeight()+1, 4, 40, 0, 45, 0, null, null);
			Residence r2 = house.getInstance("enc", geo, baseWorld, null, 4, house.getMinimumHeight(), 5, 50, 0, 44, 0, null, null);
			Residence r3 = house.getInstance("enc", geo, baseWorld, null, 4, house.getMinimumHeight(), 4, 45, 0, 46, 0, null, null);
			baseWorld.economyContainer.addPopulation(r);
			baseWorld.economyContainer.addPopulation(r2);
			baseWorld.economyContainer.addPopulation(r3);
		}
		encounterGroundWorlds.put("BASE", baseWorld);
		fallbackWorld = baseWorld;
		return baseWorld;
	}
	
	public String lastType = "________";
	public boolean renderToEncounterWorld(int worldX, int worldY, int worldZ, World realWorld, String specialType)
	{
		if (encounterGroundWorlds==null) encounterGroundWorlds = new HashMap<String, World>();
		
		WorldTypeDesc desc = realWorld.getWorldDescAtPosition(worldX,worldY,worldZ,false);
		Geography geo = desc.g;
		
		
		Cube c = realWorld.getCube(-1, worldX, worldY, worldZ, false);
		CubeClimateConditions ccc = null;
		ccc = realWorld.getCubeClimateConditions(world.engine.getWorldMeanTime(), worldX, worldY, worldZ, c!=null?c.internalCube:false);
		
		String type = ccc.getPartialBeltLevelKey()+"__"+specialType;
		if (geo!=null)
		{
			type+=geo.getClass().getSimpleName();
		}
		EconomicGround ground = null; // TODO sophisticate this with population type specified ground rather then used
		Residence residence = null;
		if (desc.population!=null)
		{
			
			try{
				ground = desc.population.groundList.get(0);
				type+=desc.population.groundList.get(0).getClass();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			try{
				residence = desc.population.residenceList.get(0);
				type+=desc.population.residenceList.get(0).getClass();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		if (type.equals(lastType)) return true; // no need for rerender
		lastType = type;
		
		World w = encounterGroundWorlds.get(type);
		if (w==null)
		{ 
			try {
				w = getEncounterGroundWorld(c, ccc, geo, ground, residence, specialType);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				w = fallbackWorld;
			}
			encounterGroundWorlds.put(type, w);
		}
		
		world = w;
		return false;
		//re
	}

	@Override
	public void reinit() {
		super.reinit();
		extRootNode = core.encounterExtRootNode;
		intRootNode = core.encounterExtRootNode;
		renderedArea = core.renderedEncounterArea;
		rerenderWithRemove = true;
		lastType = "_______";
	}
	
	

}
