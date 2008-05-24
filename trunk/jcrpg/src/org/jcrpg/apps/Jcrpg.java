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

import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jcrpg.game.GameStateContainer;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.time.Time;


public class Jcrpg extends Formatter implements Filter  {

	Date dateF = new Date(); 
	@Override
	public String format(LogRecord record) {
		dateF.setTime(record.getMillis());
		StringBuffer b = new StringBuffer();
		b.append(dateF.toString()+" "+record.getSourceClassName()+" - "+record.getMessage()+'\n');
		if (record.getThrown()!=null)
		{
			b.append(record.getThrown()+"\n");
			StackTraceElement[] elements = record.getThrown().getStackTrace();
			for (StackTraceElement e:elements)
			{
				b.append(e.toString()+"\n");
			}
		}
		return b.toString();
	}
	public boolean isLoggable(LogRecord record) {
		if (record.getSourceClassName().equals("com.jme.scene.Node"))
			return false;
		if (record.getSourceClassName().equals("com.jme.renderer.lwjgl.LWJGLRenderer"))
			return false;
		if (record.getSourceClassName().startsWith("sun.awt"))
			return false;
		return true;
	}

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
			h.setFormatter(new Jcrpg());
			h.setFilter(new Jcrpg());
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
