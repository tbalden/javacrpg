/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.threed.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.World;

public class RenderedArea {
	
	public HashMap<String, RenderedCube> worldCubeCache = new HashMap<String, RenderedCube>(); 
	public HashMap<String, RenderedCube> worldCubeCacheNext = new HashMap<String, RenderedCube>(); 
	
	public RenderedCube[][] getRenderedSpace(World world, int x, int y, int z, int direction)
	{
		int distance = J3DCore.RENDER_DISTANCE;
		
		float xPlusMult = 1;
		float xMinusMult = -1;
		float zPlusMult = 1;
		float zMinusMult = -1;
		float viewPercent = 1f;//0.3f;
		
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
		worldCubeCacheNext = new HashMap<String, RenderedCube>();
		ArrayList<RenderedCube> elements = new ArrayList<RenderedCube>();
		for (int z1=Math.round(zMinusMult*distance); z1<=zPlusMult*distance; z1++)
		{
			for (int y1=-1*distance; y1<=1*distance; y1++)
			{
				for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
				{
					int s = ((x+x1)<< 16) + ((y+y1) << 8) + (z-z1);
					String key = ""+s;
					//String key =  (x+x1)+" "+(y+y1)+" "+(z-z1);
					RenderedCube c = worldCubeCache.remove(key);
					if (c==null) {
						Cube cube = world.getCube(world.engine.getWorldMeanTime(),x+x1, y+y1, z-z1);
						if (cube!=null)
						{
							c = new RenderedCube(cube,x1,y1,z1);
						}
					} else
					{
						//System.out.println("IN RCUBECACHE");
					}
					worldCubeCacheNext.put(key, c);
					if (c!=null) 
					{	
						elements.add(c);
					}
				}
			}
		}
		
		/*for (String c:worldCubeCacheNext.keySet())
		{
			worldCubeCache.remove(c);
		}*/
		RenderedCube[] removable =  worldCubeCache.values().toArray(new RenderedCube[0]);
		worldCubeCache.clear();
		worldCubeCache = worldCubeCacheNext;
		
		return new RenderedCube[][]{(RenderedCube[])elements.toArray(new RenderedCube[0]),removable};
		
	}
	
	
}
