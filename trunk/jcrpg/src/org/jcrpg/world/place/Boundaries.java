package org.jcrpg.world.place;

import java.util.HashMap;

/**
 * Cube Coordinate based area bounding
 * @author pali
 *
 */
public class Boundaries {


	/**
	 * One coordinate of this boundary is MAGNIFICATION times bigger than a normal Cube 
	 */
	public int magnification;
	public HashMap<String,String> area;
	
	public Boundaries(int magnification)
	{
		this.magnification = magnification;
		area = new HashMap<String, String>();
	}
	
	
	public void addCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		String key = getKey(x, y, z);
		area.put(key,key);
	}
	public void removeCube(int magnification, int x, int y, int z) throws Exception
	{
		if (magnification!=this.magnification) throw new Exception("Wrong magnification");
		area.remove(getKey(x, y, z));
	}
	
	public void mergeBoundaries(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		area.putAll(area2.getArea());
	}
	
	public void subtractBoundaries(Boundaries area2) throws Exception
	{
		if (area2.magnification!=this.magnification) throw new Exception("Wrong magnification");
		for (String element : area2.getArea().values()) {
			area.remove(element);
		};
	}
	
	public boolean isInside(int absouluteX, int absoluteY, int absoluteZ)
	{
		boolean ret = area.get(getKey(absouluteX/magnification, absoluteY/magnification, absoluteZ/magnification))!=null;
		if (ret && magnification==1) System.out.println(" -- "+absouluteX+" "+absoluteZ+ " MAG: "+magnification+ " == "+ret);
		return ret;
	}
	
	public HashMap<String,String> getArea()
	{
		return area;
	}
	
	public String getKey(int x,int y,int z){
		return x+" "+y+" "+z;
	}
	
}
