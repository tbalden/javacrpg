package org.jcrpg.threed.scene;

import java.util.HashSet;

import org.jcrpg.space.Area;
import org.jcrpg.space.Cube;

public class RenderedArea {
	
	
	static int distance = 3;
	
	public static RenderedCube[] getRenderedSpace(Area space, int x, int y, int z, int direction)
	{
						
		HashSet elements = new HashSet();
		for (int x1=-1*distance; x1<=1*distance; x1++)
		{
			for (int y1=-1*distance; y1<=1*distance; y1++)
			{
				for (int z1=-1*distance; z1<=1*distance; z1++)
				{
					
					Cube c = space.getCube(x+x1, y+y1, z+z1);
					System.out.println("Coordinates: "+(x+x1)+ "-"+ (y+y1)+"-"+(z+z1) );
					if (c!=null) 
					{	
						System.out.println(c.toString());
						elements.add(new RenderedCube(c,x1,z1,y1));
					}
				}
			}
		}
		
		return (RenderedCube[])elements.toArray(new RenderedCube[0]);
		
	}
	
	
}
