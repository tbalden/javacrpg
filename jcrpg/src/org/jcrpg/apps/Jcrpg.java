package org.jcrpg.apps;

import java.util.Random;

import org.jcrpg.space.Area;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.scene.RenderedArea;
import org.jcrpg.threed.scene.RenderedCube;

import com.jme.util.LoggingSystem;

public class Jcrpg {

	/**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
    	
    	Random r = new Random();
    	// generating a demo area
    	Area gameArea = new Area();
    	for (int x=0; x<100; x++)
    	{	
        	for (int z=0; z<100; z++)
        	{
            	for (int y=0; y<1; y++)
            	{
            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(r.nextInt(18)>15?1:0),new Side(r.nextInt(18)>16?5:0),new Side(0),new Side(r.nextInt(18)>15?1:0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
//            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(r.nextInt(18)>10?1:0),new Side(0),new Side(0),new Side(r.nextInt(18)>10?1:0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
//            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(0),new Side(0),new Side(0),new Side(0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
            		gameArea.addCube(x, y, z, c);
            	}
        	}
    	}
    	
    	
    	    	
        LoggingSystem.getLogger().setLevel(java.util.logging.Level.WARNING);
        J3DCore app = new J3DCore();
        app.setArea(gameArea);
        app.setViewPosition(50, 0, 50);
        app.initCore();
   }


}
