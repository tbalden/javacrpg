package org.jcrpg.threed.scene;

import java.util.ArrayList;
import java.util.HashSet;

import org.jcrpg.space.Area;
import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;

public class RenderedArea {
	
	
	
	public static RenderedCube[] getRenderedSpace(Area space, int x, int y, int z)
	{
		int distance = J3DCore.RENDER_DISTANCE;
						
		ArrayList elements = new ArrayList();
		for (int z1=-1*distance; z1<=1*distance; z1++)
		{
			for (int y1=-1*distance; y1<=1*distance; y1++)
			{
				for (int x1=-1*distance; x1<=1*distance; x1++)
				{
					
					Cube c = space.getCube(x+x1, y+y1, z+z1);
					if (c!=null)System.out.println("CUBE Coords: "+ (c.x)+" "+c.y);
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
