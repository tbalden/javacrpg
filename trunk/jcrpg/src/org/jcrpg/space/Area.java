package org.jcrpg.space;

import java.util.HashMap;


public class Area {

	
	HashMap hmArea = null;
	
	public Area()
	{
		hmArea = new HashMap();
	}
	
	public Cube getCube(int x,int y, int z)
	{
		return (Cube)hmArea.get(getKeyString(x, y, z));
	}
	
	public void addCube(int x, int y, int z, Cube c)
	{
		hmArea.put(getKeyString(x, y, z), c);
	}
	
	public static String getKeyString(int x, int y, int z)
	{
		return ""+x+" "+y+" "+z;
	}
	
}
