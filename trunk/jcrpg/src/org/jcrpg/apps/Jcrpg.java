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
		
		int i =0;
		House h = null; 
		long time = System.currentTimeMillis(); 
		h = new House("house",null,4,1,4,0,0,5);		
		w.economics.put(h.id, h);
		System.out.println(System.currentTimeMillis()-time+" ms");

		h = new House("house1",null,7,2,7,0,0,15);		
		w.economics.put(h.id, h);
		System.out.println(System.currentTimeMillis()-time+" ms");

		Plain p = new Plain("p1",null);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, 0, 0));
		//w.geographies.put(p.id, p);
		Mountain m = new Mountain("m1",null,10,1,1,1,0,0,0);
		//p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, 0, 0));
		w.geographies.put(m.id, m);

		p = new Plain("21",null);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, 0, 1));
		w.geographies.put(p.id, p);
		p = new Plain("22",null);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 0, 0, 2));
		w.geographies.put(p.id, p);

		p = new Plain("3",null);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 2, 0, 0));
		w.geographies.put(p.id, p);
		
		p = new Plain("5",null);
		p.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 3, 0, 0));
		w.geographies.put(p.id, p);
		
		Forest f = new Forest("4",null);
		f.setBoundaries(BoundaryUtils.createCubicBoundaries(10, 1, 1, 1, 1, 0, 0));
		w.geographies.put(f.id, f);
		
		app.setWorld(w);
		app.setEngine(e);
		app.setViewPosition(2, 0, 2);
		app.initCore();
	}
}
