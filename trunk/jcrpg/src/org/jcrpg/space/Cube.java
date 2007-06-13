package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Place;


public class Cube extends ChangingImpl {


	public static final int BELOW_LEVEL = -1, MID_LEVEL = 0, ABOVE_LEVEL = 1;
	public static final int DEFAULT_LEVEL = MID_LEVEL;

	public Side n, e, s, w, top, bottom;
	
	public long lastChangeTimeStamp = System.currentTimeMillis();
	
	
	
	/**
	 * Height in level for graphical/combat purpose etc.
	 */
	public int relativeHeight = DEFAULT_LEVEL;
	
	public Side[] sides = {n,e,s,w,top,bottom};
	
	public int x,y,z;
	
	public Place parent;
	
	public Cube(Place parent, int relativeHeight, Side[] sides, int x, int y, int z) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		for (int i=0; i<sides.length; i++)
		{
			this.sides[i] = sides[i];
		}
	}
	
	public Side getSide(int sideId)
	{
		return sides[sideId];
	}
	
	public String toString()
	{
		return "Cube: "+ x+" "+y+" "+z;
	}
	
	public Cube getNeighbour(int direction)
	{
		Object[] o = J3DCore.directionAnglesAndTranslations.get(new Integer(direction));
		int[] f = (int[])o[1];
		
		return parent.getCube(x+f[0], y+f[1], z+f[2]);
	}
	
}
