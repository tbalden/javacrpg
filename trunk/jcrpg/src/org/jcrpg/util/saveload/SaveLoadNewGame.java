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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jcrpg.game.GameStateContainer;
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

	public static final String saveDir = "./save";
	
	public static void newGame(J3DCore core) 
	{
		try {
			
			GameStateContainer gameState = new GameStateContainer();
			
		
			Engine engine = new Engine();
			Time wmt = new Time();
			wmt.setHour(17);
			engine.setWorldMeanTime(wmt);
			engine.setNumberOfTurn(0);
			Thread t = new Thread(engine);
			t.start();
			core.engineThread = t;
			
			
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
			gameState.setPlayer(party, logic);
			gameState.setWorld(world);
			gameState.setEcology(ecology);
			gameState.setEngine(engine);
			gameState.setViewPosition(wX,wY,wZ);
			gameState.setOrigoRenderPosition(wX,wY,wZ);
			gameState.resetRelativePosition();
			core.setGameState(gameState);
			if (core.coreFullyInitialized)
				core.sEngine.reinit();
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void saveGame(J3DCore core)
	{
		if (core.encounterMode) {
			core.uiBase.hud.mainBox.addEntry("Cannot save while encounter.");
			return;
		}
		try {
			Date d = new Date();
			String dT = new SimpleDateFormat("yyyyddMM-HH.mm.ss.SSS").format(d);
			String slot = saveDir+"/"+core.gameState.gameId+"_"+dT+"/";
			File f = new File(slot);
			f.mkdirs();
			
			File saveGame = new File(slot+"savegame.zip");
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveGame));
			zipOutputStream.putNextEntry(new ZipEntry("gamestate.xml"));
			core.gameState.getGameStateXml(zipOutputStream);
			core.getDisplay().getRenderer().takeScreenShot( slot+"screen" );
			zipOutputStream.close();
			
			core.uiBase.hud.mainBox.addEntry("Game saved.");
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void loadGame(J3DCore core, File saveGame)
	{
		try {
			if (core.engineThread!=null)
			{
				core.engineThread.interrupt();
			}
			//ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveGame));
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(saveGame));
			zipInputStream.getNextEntry();
			Reader reader = new InputStreamReader(zipInputStream);
			GameStateContainer gameState = GameStateContainer.createGameStateFromXml(reader);
			core.setGameState(gameState);
			zipInputStream.close();
			gameState.engine.setPause(true);
			Thread t = new Thread(gameState.engine);
			t.start();
			core.engineThread = t;
			core.gameState.playerTurnLogic.core = core;
			if (core.sEngine!=null)
				core.sEngine.reinit();
			core.uiBase.hud.mainBox.addEntry("Game loaded.");
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
}
