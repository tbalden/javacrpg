/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.apps;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jcrpg.game.GameStateContainer;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.time.Time;


public class Jcrpg {

	public static Logger LOGGER = null;
	/**
     * @param args
     */
    public static void main(String[] args) {
    	//System.setProperty("java.util.logging.config.file", "./lib/logging.properties");
    	LOGGER = java.util.logging.Logger.getLogger("");
    	for (Handler handler:LOGGER.getHandlers())
    	{
    		LOGGER.removeHandler(handler);
    		System.out.println("H: "+handler);
    	}
		try {
			FileHandler h = new FileHandler("./jcrpg-log%u.txt");
			h.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(h);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    	LOGGER.setLevel(Level.ALL); //LOGGER.setLevel(Level.SEVERE);
    	try {
    		start();
    	} catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
   }

    public static void start() throws Exception {
    	
		Engine engine = new Engine();
		Time wmt = new Time();
		wmt.setHour(17);
		engine.setWorldMeanTime(wmt);
		engine.setNumberOfTurn(0);
		Thread t = new Thread(engine);
		t.start();
		J3DCore core = new J3DCore();
		GameStateContainer gameState = new GameStateContainer();
		core.setGameState(gameState);
		core.gameState.setEngine(engine);
		core.initCore();
		
	}
}
