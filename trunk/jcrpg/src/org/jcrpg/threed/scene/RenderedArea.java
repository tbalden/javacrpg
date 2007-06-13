package org.jcrpg.threed.scene;

import java.util.ArrayList;

import org.jcrpg.space.Area;
import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.World;

public class RenderedArea {
	
	public static RenderedCube[] getRenderedSpace(World space, int x, int y, int z, int direction)
	{
		int distance = J3DCore.RENDER_DISTANCE;
		
		float xPlusMult = 1;
		float xMinusMult = -1;
		float zPlusMult = 1;
		float zMinusMult = -1;
		float viewPercent = 0.1f;
		
		if (J3DCore.OPTIMIZED_RENDERING) {
			if (direction==J3DCore.NORTH)
			{
				zPlusMult = viewPercent;
				
			}
			if (direction==J3DCore.SOUTH)
			{
				zMinusMult = -viewPercent;
				
			}
			if (direction==J3DCore.WEST)
			{
				xPlusMult = viewPercent;
				
			}
			if (direction==J3DCore.EAST)
			{
				xMinusMult = -viewPercent;
				
			}
		}
						
		ArrayList elements = new ArrayList();
		for (int z1=Math.round(zMinusMult*distance); z1<=zPlusMult*distance; z1++)
		{
			for (int y1=-1*distance; y1<=1*distance; y1++)
			{
				for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
				{
					
					Cube c = space.getCube(x+x1, y+y1, z-z1);
					//if (c!=null)System.out.println("CUBE Coords: "+ (c.x)+" "+c.y);
					//System.out.println("Coordinates: "+(x+x1)+ "-"+ (y+y1)+"-"+(z+z1) );
					if (c!=null) 
					{	
						//System.out.println(c.toString());
						elements.add(new RenderedCube(c,x1,y1,z1));
					}
				}
			}
		}
		
		return (RenderedCube[])elements.toArray(new RenderedCube[0]);
		
	}
	
	
}
