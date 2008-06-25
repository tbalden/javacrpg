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

package org.jcrpg.game;

import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.GroupingMemberProps;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.time.Time;

import com.jme.renderer.ColorRGBA;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GameStateContainer {
	
	public String gameId = "game1";
	
	public int viewDirection = J3DCore.NORTH;
	public int viewPositionX = 0;
	public int viewPositionY = 0;
	public int viewPositionZ = 0;
	public int relativeX = 0, relativeY = 0, relativeZ = 0;
	public int origoX = 0;
	public int origoY = 0;
	public int origoZ = 0;
	public boolean onSteep = false;
	public boolean insideArea = false;

	public Engine engine = null;
	public World world = null;

	public Ecology ecology = null;
	public PartyInstance player = null;
	public GameLogic gameLogic = null;
	public CharacterCreationRules charCreationRules = null;
	
	public GameStateContainer()
	{
		charCreationRules = new CharacterCreationRules(null,null);
	}

	/**
	 * If doing an engine-paused encounter mode this is with value true, switch it with core->switchEncounterMode(value) only!
	 */
	public boolean encounterMode = false;

	public void setEngine(Engine engine)
	{
		this.engine = engine;
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}

	public void setEcology(Ecology ecology)
	{
		this.ecology = ecology;
	}

	public void setViewPosition(int[] coords)
	{
		Jcrpg.LOGGER.info(" NEW VIEW POSITION = "+coords[0]+" - "+coords[1]+" - "+coords[2]);
		viewPositionX = coords[0];
		viewPositionY = coords[1];
		viewPositionZ = coords[2];
		player.setPosition(coords);
	}
	public void setRelativePosition(int[] coords)
	{
		relativeX = coords[0];
		relativeY = coords[1];
		relativeZ = coords[2];
	}
	
	public void setViewPosition(int x,int y,int z)	
	{
		System.out.println("!!!!!!!!!! VIEW POS: "+y);
		viewPositionX = x;
		viewPositionY = y;
		viewPositionZ = z;
		player.setPosition(new int[]{x,y,z});
	}
	
	/**
	 * For storing the origo cube coordinate in the world when starting a game session - for rendering use. 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setOrigoRenderPosition(int x,int y,int z)
	{
		origoX = x;
		origoY = y;
		origoZ = z;
	}
	
	public void resetRelativePosition()
	{
		relativeX = 0;
		relativeY = 0;
		relativeZ = 0;
		viewDirection = 0;
	}
	
	public void setPlayer(PartyInstance player, GameLogic playerTurnLogic)
	{
		this.player = player;
		this.gameLogic = playerTurnLogic;
	}
	
	public String getGameStateXml()
	{
		XStream xstream = new XStream(new DomDriver());
		
		String xml = xstream.toXML(this);
		
		return xml;
	}
	public void getGameStateXml(OutputStream output)
	{
		XStream xstream = new XStream(new DomDriver());
		xstream.toXML(this,output);
	}
	
	public static GameStateContainer createGameStateFromXml(String xml)
	{
		XStream xstream = new XStream(new DomDriver());
		GameStateContainer gameState = (GameStateContainer)xstream.fromXML(xml);
		return gameState;
	}
	public static GameStateContainer createGameStateFromXml(Reader xml)
	{
		XStream xstream = new XStream(new DomDriver());
		GameStateContainer gameState = (GameStateContainer)xstream.fromXML(xml);
		return gameState;
	}
	
	public void clearAll()
	{
		world.clearAll();
		ecology.clearAll();
	}

	public CharacterCreationRules getCharCreationRules() {
		return charCreationRules;
	}

	public void setCharCreationRules(CharacterCreationRules charCreationRules) {
		this.charCreationRules = charCreationRules;
	}
	
	/**
	 * Repositions player to the nearest sane surface - on economy update this
	 * is a must.
	 */
	public void repositionPlayerToSurface()
	{
		// looking for surface...
		ArrayList<SurfaceHeightAndType[]> list = world.getSurfaceData(viewPositionX,viewPositionZ);
		int y = viewPositionY;
		int minDiff = 1000;
		if (list!=null)
		for (SurfaceHeightAndType[] st:list)
		{
			for (SurfaceHeightAndType s:st)
			{
				if (Math.abs((s.surfaceY-viewPositionY))<minDiff)
				{
					minDiff = Math.abs(s.surfaceY-viewPositionY);
					y = s.surfaceY;
				}
			}
		}
		relativeY += y-viewPositionY;
		viewPositionY = y;
	}
	
	/**
	 * Big world update that changes the world structure.
	 */
	public void doEconomyUpdate()
	{
		
		J3DCore.getInstance().uiBase.hud.mainBox.addEntry("A new economy turn has come...");
		
		J3DCore.getInstance().uiBase.hud.sr.setVisibility(true, "ECONOMY");

		// big update for economy, re-rendering environment around
		world.economyContainer.doEconomyUpdate();
		//renderedArea.fullUpdateClear();
		
		repositionPlayerToSurface();
		J3DCore.getInstance().setCalculatedCameraLocation();
		
		J3DCore.getInstance().sEngine.rerender = true;
		J3DCore.getInstance().sEngine.renderToViewPort();
		J3DCore.getInstance().sEngine.rerender = false;
		engine.economyUpdateFinished();
		J3DCore.getInstance().uiBase.hud.sr.setVisibility(false, "ECONOMY");
		J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Constructions done.");
	
	}
	
	/**
	 * Updates entityIcons hud part with the nearby entity icons.
	 */
	public void updateEntityIcons()
	{
		TreeMap<String, String> map = new TreeMap<String, String>();
		Collection<Object> list = ecology.getEntities(player.world, player.fragments.fragments.get(0).roamingBoundary.posX, player.theFragment.roamingBoundary.posY, player.theFragment.roamingBoundary.posZ);
		if (list!=null)
		{
			for (Object o:list)
			{
				EntityFragment i = ((EntityFragment)o);
				if (i==player.theFragment) continue;
				//System.out.println("I: "+i.instance.description);
				if (DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(player.theFragment.roamingBoundary,i.roamingBoundary)==null) continue;
				map.put(i.instance.description.iconPic,i.instance.description.iconPic);
			}
		}
		J3DCore.getInstance().uiBase.hud.entityOMeter.update(map.values());
	}
	
	/**
	 * Check if player has entered/left a population, write to log if so.
	 */
	public void checkEnterLeavePopulation()
	{
		Population p = world.economyContainer.getPopulationAt(viewPositionX, viewPositionY, viewPositionZ);
		if (p==null)
		{
			p = world.economyContainer.getPopulationAt(viewPositionX, viewPositionY-1, viewPositionZ);
		}
		if (p!=null)
		{
			String name = p.town.foundationName+" ("+p.foundationName+")";
			if (!lastPopulationName.equals(name)) {
				J3DCore.getInstance().uiBase.hud.mainBox.addEntry(new TextEntry("Entering town "+name, ColorRGBA.red));
				lastPopulationName = name;
			}
		} else
		{	if (lastPopulationName!=null && lastPopulationName.length()>0)
			{
			J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Leaving town "+lastPopulationName);
			}
			lastPopulationName = "";
		}
	}
	/**
	 * Last population entered, left.
	 */
	String lastPopulationName = "";
	
	int environmentAudioCount = 0;
	
	/**
	 * Non-gameplay related environmental happenings (audio etc.).
	 */
	public void doEnvironmental()
	{
		if (environmentAudioCount==0)
		{
			// climate related sounds
			CubeClimateConditions c = player.world.getClimate().getCubeClimate(new Time(), player.theFragment.roamingBoundary.posX, player.theFragment.roamingBoundary.posY, player.theFragment.roamingBoundary.posZ, true);
			if (c.getBelt()!=null && c.getBelt().audioDescriptor!=null && c.getBelt().audioDescriptor.ENVIRONMENTAL!=null)
			{
				for (String audio : c.getBelt().audioDescriptor.ENVIRONMENTAL)
				{
					if (Engine.getTrueRandom().nextInt(10)>3) 
					{
						J3DCore.getInstance().audioServer.playLoading(audio, "climate");
						break;
					}
				}
			}
			environmentAudioCount++;
		}
		else 
		{
			// ecology related sounds			
			Collection<Object> list = ecology.getEntities(player.world, player.theFragment.roamingBoundary.posX, player.theFragment.roamingBoundary.posY, player.theFragment.roamingBoundary.posZ);
			if (list!=null)
			{
				boolean played = false;
				for (Object o:list)
				{
					EntityFragment i = ((EntityFragment)o);
					{
						if (DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(i.roamingBoundary, player.theFragment.roamingBoundary)==null) continue;
						System.out.println("#_# Audio: "+i.instance.id);
						if (i.instance.description.groupingRule.possibleMembers!=null)
						for (GroupingMemberProps p:i.instance.description.groupingRule.possibleMembers)
						{
							if (p.memberType.audioDescription!=null)
							{
								if (p.memberType.audioDescription.ENVIRONMENTAL!=null)
								{
									if (p.memberType.audioDescription.ENVIRONMENTAL.length>0)
									{
										for (String sound:p.memberType.audioDescription.ENVIRONMENTAL) {
											if (Engine.getTrueRandom().nextInt(10)>6)
											{
												J3DCore.getInstance().uiBase.hud.mainBox.addEntry("You here faint sounds around.");
												J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Probably "+p.memberType.visibleTypeId+".");
												J3DCore.getInstance().audioServer.playLoading(sound, "ai");
												played = true; break;
											}
										}
									}
								}
							}
							if (played) break;
						}
						if (played) break;
					}
				}
			}
			environmentAudioCount--;
		}
	}
	
	public boolean levelingInProgress = false;
	public void checkAndDoLeveling()
	{
		if (levelingInProgress) return;
		for (PersistentMemberInstance i:player.orderedParty)
		{
			if (i.memberState.isDead()) continue;
			if (i.memberState.checkLevelingAvailable())
			{
				J3DCore.getInstance().charLevelingWindow.setPageData(player, i);
				J3DCore.getInstance().charLevelingWindow.toggle();
				levelingInProgress = true;
				break;
			}
		}
	}
	
}
