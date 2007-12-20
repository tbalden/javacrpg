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
import org.jcrpg.world.WorldGenerator;
import org.jcrpg.world.ai.flora.impl.BaseFloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.generator.program.DefaultGenProgram;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
import org.jcrpg.world.place.water.Lake;
import org.jcrpg.world.place.water.River;
import org.jcrpg.world.time.Time;


public class Jcrpg {

	/**
     * @param args
     */
    public static void main(String[] args) {
    	//System.setProperty("java.util.logging.config.file", "./lib/logging.properties");
    	java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
    	try {
    		start();
    	} catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
   }

    public static void start() throws Exception {
		Engine e = new Engine();
		Time wmt = new Time();
		wmt.setHour(17);
		e.setWorldMeanTime(wmt);
		Thread t = new Thread(e);
		t.start();
		J3DCore app = new J3DCore();
		
		
		String[] climates = new String[] {"Arctic","Continental","Desert","Tropical"};
		int[] climateSizeMuls = new int[] {1,4,2,2};
		String[] geos = new String[] {"Plain","Forest","MountainNew"};
		int[] geoLikenessValues = new int[] {4,4,2};
		String[] additionalGeos = new String[] {"River","Cave"};
		int[] additionalGeoLikenessValues = new int[] {4,4,2};
		
		World w2 = new WorldGenerator().generateWorld(new WorldParams(40,10,2,10,"Ocean", 10,80,1,climates,climateSizeMuls,geos,geoLikenessValues,additionalGeos,additionalGeoLikenessValues,40), new DefaultGenProgram());
		w2.engine = e;

		WorldOrbiterHandler woh = new WorldOrbiterHandler();
		woh.addOrbiter("sun", new SimpleSun("SUN"));
		woh.addOrbiter("moon", new SimpleMoon("moon"));

		w2.setOrbiterHandler(woh);

		/**/app.setWorld(w2);
		app.setEngine(e);
		app.setViewPosition(w2.realSizeX/2, w2.getSeaLevel(1)+1, w2.realSizeZ/2);
		app.initCore();/**/

		// ----
		// old code
		
		World w = new World("world", null,100,2,2,2);
		w.engine = e;
		
		w.setFloraContainer(new BaseFloraContainer());
		
		w.setOrbiterHandler(woh);
		//if (true == true) return;
		
		Climate climate = new Climate("climate",w);
		w.setClimate(climate);
		
		Continental continental = new Continental("cont1",climate);
		continental.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 20, 20, 0, 0, 0));
		climate.belts.put(continental.id, continental);
		Tropical tropical = new Tropical("trop1",climate);
		tropical.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 20, 20, 2, 0, 0));
		climate.belts.put(tropical.id, tropical);
		Desert desert = new Desert("desert1",climate);
		desert.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 20, 20, 4, 0, 0));
		climate.belts.put(desert.id, desert);
		Arctic arctic = new Arctic("arctic1",climate);
		arctic.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 20, 20, 6, 0, 0));
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

		Plain p = new Plain("p1",w,null,w.getSeaLevel(1),10, 1, 2, 1, 0, w.getSeaLevel(10)-1, 0, true);
		System.out.println("SEALEV PLAIN:"+w.getSeaLevel(10));
		w.geographies.put(p.id, p);

		Cave cave = new Cave("cave",w,null,w.getSeaLevel(1),w.getSeaLevel(1)+2,2,20,1,20,5,w.getSeaLevel(2),5,30,Cave.LIMIT_WEST|Cave.LIMIT_SOUTH,2,true);
		w.geographies.put(cave.id, cave);
		
		
		Mountain m = new Mountain("m1",w,null,w.getSeaLevel(1),20,10,5,2,5,1,w.getSeaLevel(10)-1,1,true);
		w.geographies.put(m.id, m);
		
		River r = new River("r1",w,null,w.getSeaLevel(1),10,4,10,4,1,w.getSeaLevel(10)-5,0,2,1,0.2f,12,true);
		w.waters.put(r.id, r);
		r.noWaterInTheBed = false;
		
		Lake l = new Lake("l1",w,null,w.getSeaLevel(1),1,8,6,8,3,w.getSeaLevel(1)-3,2,1,25);
		l.noWaterInTheBed = false;
		w.waters.put(l.id, l);

		p = new Plain("21",w,null,w.getSeaLevel(1),10, 1, 2, 1, 0, w.getSeaLevel(10)-1, 1, true);
		w.geographies.put(p.id, p);
		p = new Plain("22",w,null,w.getSeaLevel(1),10, 1, 2, 1, 0, w.getSeaLevel(10)-1, 2, true);
		w.geographies.put(p.id, p);
		
		p = new Plain("24",w,null,w.getSeaLevel(1),10, 4, 2, 1, 4, w.getSeaLevel(10)-1, 0, true);
		w.geographies.put(p.id, p);

		Forest f = new Forest("4",w,null,w.getSeaLevel(1),10, 2, 2, 1, 1, w.getSeaLevel(10)-1, 0,true);
		w.geographies.put(f.id, f);
		/*p = new Plain("4",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 2, 2, 1, 1, w.getSeaLevel(10)-1, 0));
		w.geographies.put(p.id, p);*/
		
		p = new Plain("5",w,null,w.getSeaLevel(1),10, 1, 2, 1, 3, w.getSeaLevel(10)-1, 0, true);
		w.geographies.put(p.id, p);
		
		w.worldMap = w2.worldMap; // dirty hack here for the release only
		/** /app.setWorld(w);
		app.setEngine(e);
		app.setViewPosition(0, w.getSeaLevel(1), 0);
		app.initCore();/**/
		
	}
}
