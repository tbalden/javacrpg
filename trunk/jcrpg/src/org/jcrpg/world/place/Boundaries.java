package org.jcrpg.world.place;

import java.util.HashMap;

/**
 * Cube Coordinate based area bounding
 * @author pali
 *
 */
public class Boundaries {


	public Boundaries()
	{
		area = new HashMap<String, String>();
	}
	
	HashMap<String,String> area;
	
	public void addCube(int x, int y, int z)
	{
		String key = getKey(x, y, z);
		area.put(key,key);
	}
	public void removeCube(int x, int y, int z)
	{
		area.remove(getKey(x, y, z));
	}
	
	public void mergeBoundaries(Boundaries area2)
	{
		area.putAll(area2.getArea());
	}
	
	public void subtractBoundaries(Boundaries area2)
	{
		for (String element : area2.getArea().values()) {
			area.remove(element);
		};
	}
	
	public boolean isInside(int x, int y, int z)
	{
		return area.get(getKey(x, y, z))!=null;
	}
	
	public HashMap<String,String> getArea()
	{
		return area;
	}
	
	public String getKey(int x,int y,int z){
		return x+" "+y+" "+z;
	}
	
}
