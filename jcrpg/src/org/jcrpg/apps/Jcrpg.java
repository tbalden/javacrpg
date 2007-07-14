/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.apps;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.flora.FloraContainer;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Mountain2;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.time.Time;

import com.jme.util.LoggingSystem;

public class Jcrpg {

	/**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
    	    	try {
    	    		start();
    	    	} catch (Exception ex)
    	    	{
    	    		ex.printStackTrace();
    	    	}
   }

    public static void start() throws Exception {
		LoggingSystem.getLogger().setLevel(java.util.logging.Level.WARNING);
		Engine e = new Engine();
		Time wmt = new Time();
		wmt.setHour(5);
		e.setWorldMeanTime(wmt);
		Thread t = new Thread(e);
		t.start();
		J3DCore app = new J3DCore();
		
		World w = new World("world", null,100,2,1,2);
		w.engine = e;
		w.setFloraContainer(new BaseFloraContainer());
		
		WorldOrbiterHandler woh = new WorldOrbiterHandler();
		woh.addOrbiter("sun", new SimpleSun("SUN"));
		woh.addOrbiter("moon", new SimpleMoon("moon"));
		w.setOrbiterHandler(woh);
		
		Climate climate = new Climate("climate",w);
		w.setClimate(climate);
		
		Continental continental = new Continental("cont1",climate);
		continental.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 0, 0, 0));
		climate.belts.put(continental.id, continental);
		Tropical tropical = new Tropical("trop1",climate);
		tropical.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 2, 0, 0));
		climate.belts.put(tropical.id, tropical);
		Desert desert = new Desert("desert1",climate);
		desert.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 4, 0, 0));
		climate.belts.put(desert.id, desert);
		Arctic arctic = new Arctic("arctic1",climate);
		arctic.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 10, 20, 6, 0, 0));
		climate.belts.put(arctic.id, arctic);
		
		//int i =0;
		House h = null; 
		long time = System.currentTimeMillis(); 
		h = new House("house",w,null,4,1,4,0,w.getSeaLevel(1),5);		
		w.economics.put(h.id, h);
		System.out.println(System.currentTimeMillis()-time+" ms");

		h = new House("house1",w,null,7,2,7,0,w.getSeaLevel(1),15);		
		w.economics.put(h.id, h);
		System.out.println(System.currentTimeMillis()-time+" ms");

		Plain p = new Plain("p1",w,null,w.getSeaLevel(10),10);
		System.out.println("SEALEV PLAIN:"+w.getSeaLevel(10));
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, w.getSeaLevel(10), 0));
		w.geographies.put(p.id, p);
		Mountain m = new Mountain("m1",w,null,10,10,1,10,1,w.getSeaLevel(10),1);
		w.geographies.put(m.id, m);

		p = new Plain("21",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, w.getSeaLevel(10), 1));
		w.geographies.put(p.id, p);
		p = new Plain("22",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, w.getSeaLevel(10), 2));
		w.geographies.put(p.id, p);
		
		p = new Plain("24",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 4, 1, 1, 4, w.getSeaLevel(10), 0));
		w.geographies.put(p.id, p);

		Forest f = new Forest("4",w,null,w.getSeaLevel(10),10);
		f.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 1, 1, 1, w.getSeaLevel(10), 0));
		w.geographies.put(f.id, f);
		
		p = new Plain("5",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 3, w.getSeaLevel(10), 0));
		w.geographies.put(p.id, p);
		
		
		app.setWorld(w);
		app.setEngine(e);
		app.setViewPosition(2, w.getSeaLevel(1), 2);
		app.initCore();
	}
}
