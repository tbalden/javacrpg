package org.jcrpg.space;


public class Cube {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, TOP = 4, BOTTOM = 5;

	public static final int BELOW_LEVEL = -1, MID_LEVEL = 0, ABOVE_LEVEL = 1;
	public static final int DEFAULT_LEVEL = MID_LEVEL;

	public Side n, e, s, w, top, bottom;
	
	
	
	/**
	 * Height in level for graphical/combat purpose etc.
	 */
	public int relativeHeight = DEFAULT_LEVEL;
	
	public Side[] sides = {n,e,s,w,top,bottom};
	
	public int x,y,z;
	
	public Area parent;
	
	public Cube(Area parent, int relativeHeight, Side[] sides, int x, int y, int z) {
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
	
}
