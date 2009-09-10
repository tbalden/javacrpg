/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.util.saveload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jcrpg.game.CharacterCreationRules;
import org.jcrpg.game.GameLogic;
import org.jcrpg.game.GameStateContainer;
import org.jcrpg.game.scenario.Scenario;
import org.jcrpg.game.scenario.ScenarioLoader.ScenarioDescription;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.window.BusyPaneWindow;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EcologyGenerator;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.Party;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.generator.WorldGenerator;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.generator.WorldParamsConfigLoader;
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
	public static final String charsDir = "./chars";
	
	public static void newGame(J3DCore core, Collection<PersistentMemberInstance> partyMembers, CharacterCreationRules cCR, ScenarioDescription desc) 
	{
		
		try {
			core.busyPane.setToType(BusyPaneWindow.LOADING,"Creating World...");
			core.busyPane.show();
			core.updateDisplay(null);
			
			core.gameLost = false;	
			
			GameStateContainer gameState = new GameStateContainer();
			HashUtil.WORLD_RANDOM_SEED = desc.seed;			
			gameState.setCharCreationRules(cCR);
		
			
			Engine engine = new Engine();
			Time wmt = new Time();
			wmt.setHour(13);
			engine.setWorldMeanTime(wmt);
			engine.setNumberOfTurn(0);
			Thread t = new Thread(engine);
			t.start();
			core.engineThread = t;
			
			gameState.setScenarioDesc(desc);
			gameState.onLoad();
			Scenario s = gameState.scenario;

			//WorldParams params = new WorldParams(40,50,2,50,"Ocean", 10,80,1,climates,climateSizeMuls,geos,geoLikenessValues,additionalGeos,additionalGeoLikenessValues,40);
			WorldParams params = (new WorldParamsConfigLoader()).getWorldParams(s.worldParams);
			WorldGenerator gen = new WorldGenerator();
			World world = gen.generateWorld(new DefaultGenProgram(new DefaultClassFactory(),gen,params));
			world.engine = engine;
	
			WorldOrbiterHandler woh = new WorldOrbiterHandler();
			woh.addOrbiter("sun", new SimpleSun("SUN"));
			woh.addOrbiter("moon", new SimpleMoon("moon"));
	
			world.setOrbiterHandler(woh);
	
			EcologyGenerator eGen = new EcologyGenerator(new FileInputStream(s.ecology));
			Ecology ecology = eGen.generateEcology(world);
			
			world.economyContainer.roadNetwork.updateRoads();
			
			int xDiff = -24;
			int yDiff = 7;
			int zDiff = -60;
			if (true==false)
			{
				xDiff = -15;
				yDiff = 0;
				zDiff = 66;
			}
			//if (true==false)
			{
				 xDiff = -35;
				 yDiff = 0;
				 zDiff = -600;//-3;
				 zDiff = -3;//-3;
			}
			int wX = world.realSizeX/2+xDiff;
			int wY = world.getSeaLevel(1)+yDiff;
			int wZ = world.realSizeZ/2+zDiff;

			//wX = world.realSizeX-1;///2+xDiff;
			//wY = world.getSeaLevel(1)+4;//yDiff;
			//wZ = world.realSizeZ-1;///2+zDiff;
			
			PartyInstance party = new PartyInstance(new Party(),world,ecology,Ecology.getNextEntityId(), "Player",0, wX, wY, wZ);
			for (PersistentMemberInstance m:partyMembers)
			{
				m.roamingBoundary = new DistanceBasedBoundary(world,wX,wY,wZ,m.description.getRoamingSize());
				/*
				
				// this is done in EntityMemberInstance constructor currently when PartySetup creates it. commented out.
				 
				for (Class<?extends Obj> o:core.gameState.charCreationRules.profInstances.get(m.description.professions.get(0)).generationNewInstanceObjects)
				{
					m.inventory.add(new ObjInstance(ObjList.getInstance(o)));
					if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ADDING ITEM : "+o);
				}*/
				m.memberState.maximizeAtStart();
				party.addPartyMember(m);
			}
			party.recalcBoundarySizes();
			ecology.addEntity(party);
			party.setPosition(new int[]{wX,wY,wZ});
			//gameState.viewDirection = 2;
			
			// setting up UI elements...
			core.behaviorWindow.party = party;
			core.behaviorWindow.updateToParty();
			
			GameLogic logic = new GameLogic(core,engine,world,ecology,party);
			gameState.setPlayer(party, logic);
			gameState.setWorld(world);
			gameState.setEcology(ecology);
			gameState.setEngine(engine);
			gameState.setViewPosition(wX,wY,wZ);
			gameState.setOrigoRenderPosition(wX,wY,wZ);
			gameState.resetRelativePosition();
			gameState.resetGeneral();
			core.setGameState(gameState);
			if (core.coreFullyInitialized)
			{
				core.sEngine.reinit();
				core.eEngine.reinit();
			}
			core.uiBase.hud.mainBox.addEntry("Press RIGHT button on mouse to toggle between mouselook and cursor.");

			
		} catch (Exception ex)
		{
			
			core.busyPane.hide();
			core.updateDisplay(null);
			
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void saveGame(J3DCore core, String slotName)
	{
		if (core.encounterMode) {
			core.uiBase.hud.mainBox.addEntry("Cannot save while encounter.");
			return;
		}
		if (core.gameLost)
		{
			core.uiBase.hud.mainBox.addEntry("Cannot save lost game.");
			return;			
		}
		try {
			core.busyPane.setToType(BusyPaneWindow.LOADING,"Saving...");
			core.busyPane.show();
			core.updateDisplay(null);
			
			Date d = new Date();
			String dT = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS").format(d);
			String slot = saveDir+"/"+core.gameState.gameId+"_"+dT+"/";
			File f = new File(slot);
			f.mkdirs();
			
			File desc = new File(slot+"desc.txt");
			FileWriter fw = new FileWriter(desc);
			String dT2 = new SimpleDateFormat("yyyy.MM.dd HH:mm").format(d)+ " Seed:"+core.gameState.scenarioDesc.seed;
			fw.write((slotName!=null && slotName.length()>15?slotName.substring(0,15):slotName)+"\n("+dT2+")");
			fw.close();
			
			File saveGame = new File(slot+"savegame.zip");
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveGame));
			zipOutputStream.putNextEntry(new ZipEntry("gamestate.xml"));
			long time = System.currentTimeMillis();
			core.gameState.getGameStateXml(zipOutputStream);
			System.out.println("oooooooooooooooooooooooooooo SAVE TIME = "+(System.currentTimeMillis()-time));
			zipOutputStream.close();
			
			core.busyPane.hide();
			core.updateDisplay(null);
			core.getDisplay().getRenderer().takeScreenShot( slot+"screen" );
			
			core.uiBase.hud.mainBox.addEntry("Game saved.");
			
		} catch (Exception ex)
		{
			core.busyPane.hide();
			core.updateDisplay(null);
			ex.printStackTrace();
		}
	}
	
	public static void loadGame(J3DCore core, File saveGame)
	{
		try {
			
			core.busyPane.setToType(BusyPaneWindow.LOADING,"Loading...");
			core.busyPane.show();
			core.updateDisplay(null);
			
			if (core.engineThread!=null)
			{
				core.engineThread.interrupt();
			}
			//ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveGame));
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(saveGame));
			zipInputStream.getNextEntry();
			Reader reader = new InputStreamReader(zipInputStream);
			long time = System.currentTimeMillis();
			GameStateContainer gameState = GameStateContainer.createGameStateFromXml(reader);
			HashUtil.WORLD_RANDOM_SEED = gameState.scenarioDesc.seed;	
			System.out.println("[][][][][][] LOAD TIME = "+(System.currentTimeMillis()-time));
			gameState.world.onLoad();
			gameState.ecology.onLoad();
			core.setGameState(gameState);
			zipInputStream.close();
			gameState.engine.setPause(true);
			Thread t = new Thread(gameState.engine);
			t.start();
			core.engineThread = t;
			core.gameState.gameLogic.core = core;
			if (core.sEngine!=null)
				core.sEngine.reinit();
			if (core.eEngine!=null)
				core.eEngine.reinit();
			// ui elements update to loaded state
			core.behaviorWindow.party = gameState.player;
			core.behaviorWindow.updateToParty();
			gameState.resetGeneral();
			gameState.onLoad();
			
			core.uiBase.hud.mainBox.addEntry("Game loaded. Scen.: "+gameState.scenarioDesc.name+" "+gameState.scenarioDesc.version);
			core.uiBase.hud.mainBox.addEntry("Press RIGHT button on mouse to toggle between mouselook and cursor.");
			core.gameLost = false;
		} catch (Exception ex)
		{
			core.busyPane.hide();
			core.updateDisplay(null);
			
			ex.printStackTrace();
		}
	}
	
	public static void saveCharacter(MemberPerson person)
	{
		Date d = new Date();
		String dT = new SimpleDateFormat("yyyyddMM-HH.mm.ss.SSS").format(d);
		String slot = charsDir+"/"+dT+"_"+person.foreName+"/";
		File f = new File(slot);
		f.mkdirs();
		File saveGame = new File(slot+"character.zip");
		try {
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveGame));
			zipOutputStream.putNextEntry(new ZipEntry("character.xml"));
			person.getXml(zipOutputStream);
			zipOutputStream.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public static MemberPerson loadCharacter(File fileName)
	{
		try {
			//ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(saveGame));
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileName));
			zipInputStream.getNextEntry();
			Reader reader = new InputStreamReader(zipInputStream);
			return MemberPerson.createFromXml(reader);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
}
