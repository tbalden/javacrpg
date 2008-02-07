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

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.World;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GameStateContainer {
	
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
	public EntityInstance player = null;
	public PlayerTurnLogic playerTurnLogic = null;

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
		System.out.println(" NEW VIEW POSITION = "+coords[0]+" - "+coords[1]+" - "+coords[2]);
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
	
	public void setPlayer(EntityInstance player, PlayerTurnLogic playerTurnLogic)
	{
		this.player = player;
		this.playerTurnLogic = playerTurnLogic;
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
	
	
}
