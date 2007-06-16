package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Place;


public class Cube extends ChangingImpl {


	public static final int BELOW_LEVEL = -1, MID_LEVEL = 0, ABOVE_LEVEL = 1;
	public static final int DEFAULT_LEVEL = MID_LEVEL;

	public Side[] n, e, s, w, top, bottom;
	
	public long lastChangeTimeStamp = System.currentTimeMillis();
	
	
	
	/**
	 * Height in level for graphical/combat purpose etc.
	 */
	public int relativeHeight = DEFAULT_LEVEL;
	
	public Side[][] sides = {n,e,s,w,top,bottom};
	
	public int x,y,z;
	
	public Place parent;

	public Cube(Place parent, int relativeHeight, Side[] sides, int x, int y, int z) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		for (int i=0; i<sides.length; i++)
		{
			this.sides[i] = new Side[] {sides[i]};
		}
	}
	
	public Cube(Place parent, int relativeHeight, Side[][] sides, int x, int y, int z) {
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		for (int i=0; i<sides.length; i++)
		{
			this.sides[i] = sides[i];
		}
	}
	
	public Side[] getSide(int sideId)
	{
		return sides[sideId];
	}
	
	public boolean hasSideOfType(int sideId, String sideType)
	{
		if (sides[sideId]!=null)
		for (int i=0; i<sides[sideId].length; i++)
		{
			if (sides[sideId][i].type==sideType)
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString()
	{
		String r = "";
		for (int s=0; s<6; s++)
		for (int i=0; i<(sides[s]==null?0:sides[s].length); i++)
		{
			r+=sides[s][i]==null?"-":sides[s][i].type;
		}
		return "Cube: "+ x+" "+y+" "+z+" "+r;
	}
	
	public Cube getNeighbour(int direction)
	{
		Object[] o = J3DCore.directionAnglesAndTranslations.get(new Integer(direction));
		int[] f = (int[])o[1];
		Cube n = parent.getCube(x+f[0], y+f[1], z+f[2]);
		if (n==null) System.out.println(this+" : "+parent.id+" "+direction+" NEIGHBOUR = "+n);
		return n;
	}
	
	public Cube copy(Place newParent)
	{
		return new Cube(newParent,relativeHeight,sides,x,y,z);
	}
	
}
