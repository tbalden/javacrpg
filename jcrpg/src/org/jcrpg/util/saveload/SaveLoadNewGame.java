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

package org.jcrpg.util.saveload;

import org.jcrpg.game.PlayerTurnLogic;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EcologyGenerator;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.player.Party;
import org.jcrpg.world.generator.WorldGenerator;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.generator.program.DefaultClassFactory;
import org.jcrpg.world.generator.program.DefaultGenProgram;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.time.Time;

/**
 * Object for creating new / saving / loading game state.
 * @author pali
 *
 */
public class SaveLoadNewGame {

	
	public static void newGame(J3DCore core) 
	{
		try {
		
			Engine engine = new Engine();
			Time wmt = new Time();
			wmt.setHour(17);
			engine.setWorldMeanTime(wmt);
			engine.setNumberOfTurn(0);
			Thread t = new Thread(engine);
			t.start();
			
			
			String[] climates = new String[] {"Arctic","Continental","Desert","Tropical"};
			int[] climateSizeMuls = new int[] {1,4,2,2};
			String[] geos = new String[] {"Plain","Forest","Mountain"};
			int[] geoLikenessValues = new int[] {4,8,2};
			String[] additionalGeos = new String[] {"River","Cave"};
			int[] additionalGeoLikenessValues = new int[] {4,4,2};
			WorldParams params = new WorldParams(40,10,2,10,"Ocean", 10,80,1,climates,climateSizeMuls,geos,geoLikenessValues,additionalGeos,additionalGeoLikenessValues,40);
			WorldGenerator gen = new WorldGenerator();
			World world = gen.generateWorld(new DefaultGenProgram(new DefaultClassFactory(),gen,params));
			world.engine = engine;
	
			WorldOrbiterHandler woh = new WorldOrbiterHandler();
			woh.addOrbiter("sun", new SimpleSun("SUN"));
			woh.addOrbiter("moon", new SimpleMoon("moon"));
	
			world.setOrbiterHandler(woh);
	
			EcologyGenerator eGen = new EcologyGenerator();
			Ecology ecology = eGen.generateEcology(world);
			
			int xDiff = +10;
			int yDiff = 0;
			int zDiff = -77;
			int wX = world.realSizeX/2+xDiff;
			int wY = world.getSeaLevel(1)+yDiff;
			int wZ = world.realSizeZ/2+zDiff;
			//ArrayList<MemberPerson> partyMembers = new ArrayList<MemberPerson>();
			EntityInstance party = new EntityInstance(new Party(),world,ecology,"Player",6, wX, wY, wZ);
			ecology.addEntity(party);
			
			PlayerTurnLogic logic = new PlayerTurnLogic(core,engine,world,ecology,party);
			core.setPlayer(party,logic);
			core.setWorld(world);
			core.setEcology(ecology);
			core.setEngine(engine);
			core.setViewPosition(wX,wY,wZ);
			core.setOrigoRenderPosition(wX,wY,wZ);
			core.resetRelativePosition();
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void saveGame(J3DCore core)
	{
		
	}
	
	public void loadGame(J3DCore core)
	{
		
	}
	
}
