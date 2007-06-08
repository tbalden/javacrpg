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
    	
    	
    	int[][][][] cubes = new int[][][][]
    	 
{
				{
					{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },   
       			},
				{
       				{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {1,5},{1,1},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{1,1},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{1,1},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{1,1},{1,1},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },   
       			},
				{
       				{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {1,5},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{1,1},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },   
       			},
				{
       				{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {1,5},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{1,1},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },   
       			},
				{
       				{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {1,5},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{1,1},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },   
       			},
				{
       				{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },{ {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },  { {0,0},{0,0},{0,0},{0,0},{1,2},{0,0} },   
       			}, // TODO rossz iranyba hajlik a fal!
}
 ;
    	
    	Random r = new Random();
    	// generating a demo area
    	Area gameArea = new Area();
    	for (int x=0; x<100; x++)
    	{	
        	for (int z=0; z<100; z++)
        	{
            	for (int y=0; y<1; y++)
            	{
            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(z%5==0&&x%2!=0?1:0,z%5==0&&x%2!=0?0:0),new Side(0,x%10==0?0:0),new Side(z%10==5&&x%6!=0?1:0,z%10==5&&x%6!=0?(x%4==0?5:1):0),new Side(0,x%10==5?0:0),new Side(0,0)/*new Side(r.nextInt(18)>10?4:0)*/,new Side(0,r.nextInt(18)>10?2:3)},x,y,z);
//            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(r.nextInt(18)>10?1:0),new Side(0),new Side(0),new Side(r.nextInt(18)>10?1:0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
//            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(0),new Side(0),new Side(0),new Side(0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
            		gameArea.addCube(x, y, z, c);
            	}
        	}
    	}
    	
    	gameArea = new Area();

    	for (int x=0; x<cubes.length; x++)
    	{	
        	for (int z=0; z<cubes[x].length; z++)
        	{
            	for (int y=0; y<1; y++)
            	{
            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{
            				new Side(cubes[x][z][0][0],cubes[x][z][0][1]),
            				new Side(cubes[x][z][1][0],cubes[x][z][1][1]),
            				new Side(cubes[x][z][2][0],cubes[x][z][2][1]),
            				new Side(cubes[x][z][3][0],cubes[x][z][3][1]),
            				new Side(cubes[x][z][5][0],cubes[x][z][5][1]),
            				new Side(cubes[x][z][4][0],cubes[x][z][4][1])
            				},
            				x,y,z);
//            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(r.nextInt(18)>10?1:0),new Side(0),new Side(0),new Side(r.nextInt(18)>10?1:0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
//            		Cube c = new Cube(gameArea,Cube.DEFAULT_LEVEL,new Side[]{new Side(0),new Side(0),new Side(0),new Side(0),new Side(r.nextInt(18)>10?4:0),new Side(r.nextInt(18)>10?2:3)},x,y,z);
            		gameArea.addCube(x, y, z, c);
            	}
        	}
    	}
    	
    	    	
        LoggingSystem.getLogger().setLevel(java.util.logging.Level.WARNING);
        J3DCore app = new J3DCore();
        app.setArea(gameArea);
        app.setViewPosition(2, 0, 2);
        app.initCore();
   }


}
