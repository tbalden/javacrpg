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

import java.util.ArrayList;
import java.util.logging.Level;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.WorldGenerator;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EcologyGenerator;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.Party;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.generator.program.DefaultClassFactory;
import org.jcrpg.world.generator.program.DefaultGenProgram;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.orbiter.moon.SimpleMoon;
import org.jcrpg.world.place.orbiter.sun.SimpleSun;
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
		Engine e = new Engine();
		Time wmt = new Time();
		wmt.setHour(17);
		e.setWorldMeanTime(wmt);
		Thread t = new Thread(e);
		t.start();
		J3DCore app = new J3DCore();
		
		
		String[] climates = new String[] {"Arctic","Continental","Desert","Tropical"};
		int[] climateSizeMuls = new int[] {1,4,2,2};
		String[] geos = new String[] {"Plain","Forest","Mountain"};
		int[] geoLikenessValues = new int[] {4,4,2};
		String[] additionalGeos = new String[] {"River","Cave"};
		int[] additionalGeoLikenessValues = new int[] {4,4,2};
		WorldParams params = new WorldParams(40,40,2,40,"Ocean", 10,80,1,climates,climateSizeMuls,geos,geoLikenessValues,additionalGeos,additionalGeoLikenessValues,40);
		WorldGenerator gen = new WorldGenerator();
		World w2 = gen.generateWorld(new DefaultGenProgram(new DefaultClassFactory(),gen,params));
		w2.engine = e;

		WorldOrbiterHandler woh = new WorldOrbiterHandler();
		woh.addOrbiter("sun", new SimpleSun("SUN"));
		woh.addOrbiter("moon", new SimpleMoon("moon"));

		w2.setOrbiterHandler(woh);

		EcologyGenerator eGen = new EcologyGenerator();
		Ecology ecology = eGen.generateEcology(w2);
		
		ArrayList<MemberPerson> partyMembers = new ArrayList<MemberPerson>();
		Party p = new Party(w2,"Player",partyMembers,w2.realSizeX/2, w2.getSeaLevel(1)+1, w2.realSizeZ/2);
		ecology.addEntity(p);
		
		app.setWorld(w2);
		app.setEcology(ecology);
		app.setEngine(e);
		app.setViewPosition(w2.realSizeX/2, w2.getSeaLevel(1)+1, w2.realSizeZ/2);
		app.setOrigoRenderPosition(w2.realSizeX/2, w2.getSeaLevel(1)+1, w2.realSizeZ/2);
		//app.setViewPosition(w2.realSizeX-3, w2.getSeaLevel(1)+1, w2.realSizeZ);
		//app.setOrigoRenderPosition(w2.realSizeX-3, w2.getSeaLevel(1)+1, w2.realSizeZ);
		app.initCore();

		
	}
}
