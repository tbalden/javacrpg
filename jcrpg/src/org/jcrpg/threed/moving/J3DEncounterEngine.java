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
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Plain;
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
		optimizeAngle = false;
		
	}
	transient HashMap<String, World> encounterGroundWorlds = new HashMap<String, World>();
	
	public World getEncounterGroundWorld(Cube c, CubeClimateConditions ccc, String specialType) throws Exception
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
		
		Class beltClass = ccc.getBelt().getClass();
		Constructor<?> constructor = (Constructor<?>)beltClass.getConstructors()[0];
		ClimateBelt belt = (ClimateBelt)constructor.newInstance("encWorld "+beltClass,climate);
		belt.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, 1, 0, 0, 0));
		climate.belts.put(belt.id, belt);
		
		Plain plain = new Plain("plain1",baseWorld,null,0,100,1,1,1,0,0,0,true);
		baseWorld.addGeography(plain);
		encounterGroundWorlds.put("BASE", baseWorld);
		fallbackWorld = baseWorld;
		return baseWorld;
	}
	
	public String lastType = "________";
	public void renderToEncounterWorld(int worldX, int worldY, int worldZ, World realWorld, String specialType)
	{
		if (encounterGroundWorlds==null) encounterGroundWorlds = new HashMap<String, World>();
		Cube c = realWorld.getCube(-1, worldX, worldY, worldZ, false);
		CubeClimateConditions ccc = realWorld.getCubeClimateConditions(world.engine.getWorldMeanTime(), worldX, worldY, worldZ, c.internalCube);
		
		String type = ccc.getPartialBeltLevelKey()+"__"+specialType; 
		
		if (type == null && lastType == null || type.equals(lastType)) return;
		lastType = type;
		
		World w = encounterGroundWorlds.get(type);
		if (w==null)
		{ 
			try {
				w = getEncounterGroundWorld(c, ccc, specialType);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				w = fallbackWorld;
			}
			encounterGroundWorlds.put(type, w);
		}
		
		world = w;
		rerender = true;
		renderToViewPort();
		rerender = false;
		//re
	}

	@Override
	public void reinit() {
		super.reinit();
		extRootNode = core.encounterExtRootNode;
		intRootNode = core.encounterExtRootNode;
		renderedArea = core.renderedEncounterArea;
		lastType = "_______";
	}
	
	

}
