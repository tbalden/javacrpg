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

import java.util.logging.Level;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.time.Time;


public class Jcrpg {

	/**
     * @param args
     */
    public static void main(String[] args) {
    	//System.setProperty("java.util.logging.config.file", "./lib/logging.properties");
    	java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
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
		core.setEngine(engine);
		core.initCore();
		
	}
}
