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
	
	public WeakHashMap<String, RenderedCube> worldCubeCache = new WeakHashMap<String, RenderedCube>(); 
	public WeakHashMap<String, RenderedCube> worldCubeCacheNext = new WeakHashMap<String, RenderedCube>(); 
	
	public RenderedCube[] getRenderedSpace(World world, int x, int y, int z, int direction)
	{
		int distance = J3DCore.RENDER_DISTANCE;
		
		float xPlusMult = 1;
		float xMinusMult = -1;
		float zPlusMult = 1;
		float zMinusMult = -1;
		float viewPercent = 0.3f;
		
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
		worldCubeCacheNext.clear();
		worldCubeCacheNext = new WeakHashMap<String, RenderedCube>();			
		ArrayList<RenderedCube> elements = new ArrayList<RenderedCube>();
		for (int z1=Math.round(zMinusMult*Math.min(distance,8)); z1<=zPlusMult*Math.min(distance,8); z1++)
		{
			for (int y1=-1*(distance); y1<=1*(distance); y1++)
			{
				for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
				{
					String key =  (x+x1)+" "+(y+y1)+" "+(z-z1);
					RenderedCube c = worldCubeCache.get(key);
					if (c==null) {
						Cube cube = world.getCube(x+x1, y+y1, z-z1);
						if (cube!=null)
						{
							c = new RenderedCube(cube,x1,y1,z1);
						}
					} else
					{
						//System.out.println("IN RCUBECACHE");
					}
					worldCubeCacheNext.put(key, c);
					//if (c!=null)System.out.println("CUBE Coords: "+ (c.x)+" "+c.y);
					//System.out.println("Coordinates: "+(x+x1)+ "-"+ (y+y1)+"-"+(z+z1) );
					if (c!=null) 
					{	
						//System.out.println(c.toString());
						elements.add(c);
					}
				}
			}
		}
		worldCubeCache.clear();
		worldCubeCache = worldCubeCacheNext;
		
		return (RenderedCube[])elements.toArray(new RenderedCube[0]);
		
	}
	
	
}
