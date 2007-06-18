package org.jcrpg.apps;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.Engine;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.House;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;

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
		Thread t = new Thread(e);
		t.start();
		J3DCore app = new J3DCore();
		
		World w = new World("world", null,100,1,1,1);
		
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

		p = new Plain("3",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 2, w.getSeaLevel(10), 0));
		w.geographies.put(p.id, p);
		
		p = new Plain("5",w,null,w.getSeaLevel(10),10);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 3, w.getSeaLevel(10), 0));
		w.geographies.put(p.id, p);
		
		Forest f = new Forest("4",w,null,w.getSeaLevel(10),10);
		f.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 1, w.getSeaLevel(10), 0));
		w.geographies.put(f.id, f);
		
		app.setWorld(w);
		app.setEngine(e);
		app.setViewPosition(2, w.getSeaLevel(1), 2);
		app.initCore();
	}
}
