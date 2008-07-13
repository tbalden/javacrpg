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

import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.standing.J3DStandingEngine;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;

public class J3DEncounterEngine extends J3DStandingEngine {
	
	HashMap<String, World> encounterGroundWorlds = new HashMap<String, World>();
	World debugWorld = null;
	public J3DEncounterEngine(J3DCore core) {
		super(core);
		extRootNode = core.encounterRootNode;
		intRootNode = core.encounterRootNode;
		renderedArea = core.renderedEncounterArea;
		optimizeAngle = false;
		
		try 
		{
			World baseWorld = new World("encounterGround",null,100,1,1,1);
			baseWorld.engine = core.gameState.engine;
			int wMag = 100;
			int wX = 1;
			int wY = 1;
			int wZ = 1;
			
			// FLORA
			baseWorld.setFloraContainer(new BaseFloraContainer());
			
			// ORBITERS
			WorldOrbiterHandler woh = new WorldOrbiterHandler();
			woh.addOrbiter("sun", new SimpleSun("SUN"));
			woh.addOrbiter("moon", new SimpleMoon("moon"));
			baseWorld.setOrbiterHandler(woh);
			
			// CLIMATE
			// TODO -> do not duplicate climate belts! shrinkToWorld must be reconsidered!! turn direction when crossing the world limit
			Climate climate = new Climate("climate",baseWorld);
			baseWorld.setClimate(climate);
			
			ClimateBelt belt = new Tropical("tropical",climate);
			belt.setBoundaries(BoundaryUtils.createCubicBoundaries(wMag, wX, wY, 1, 0, 0, 0));
			climate.belts.put(belt.id, belt);
			
			Plain plain = new Plain("plain1",baseWorld,null,0,100,1,1,1,0,0,0,true);
			baseWorld.addGeography(plain);
			encounterGroundWorlds.put("BASE", baseWorld);
			debugWorld = baseWorld;
		} catch (Exception ex)
		{	
			ex.printStackTrace();
		}
	}
	
	public String lastType = "________";
	public void renderToEncounterWorld(String type)
	{
		if (type == null && lastType == null || type.equals(lastType)) return;
		lastType = type;
		world = debugWorld;
		rerender = true;
		renderToViewPort();
		rerender = false;
		//re
	}

	@Override
	public void reinit() {
		super.reinit();
		extRootNode = core.encounterRootNode;
		intRootNode = core.encounterRootNode;
		renderedArea = core.renderedEncounterArea;
	}
	
	

}
