/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.threed.scene;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.World;

public class RenderedArea {
	
	public HashMap<Long, RenderedCube> worldCubeCache = new HashMap<Long, RenderedCube>(); 
	public HashMap<Long, RenderedCube> worldCubeCacheNext = new HashMap<Long, RenderedCube>(); 

	public HashMap<Long, RenderedCube> worldCubeCache_FARVIEW = new HashMap<Long, RenderedCube>(); 
	public HashMap<Long, RenderedCube> worldCubeCacheNext_FARVIEW = new HashMap<Long, RenderedCube>();
	

	public RenderedCube[][] getRenderedSpace(World world, int x, int y, int z, int direction, boolean farViewEnabled)
	{
		int distance = farViewEnabled?J3DCore.RENDER_DISTANCE_FARVIEW:J3DCore.RENDER_DISTANCE;
		
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
		

		worldCubeCacheNext = new HashMap<Long, RenderedCube>();
		worldCubeCacheNext_FARVIEW = new HashMap<Long, RenderedCube>();
		ArrayList<RenderedCube> elements = new ArrayList<RenderedCube>();
		ArrayList<RenderedCube> elements_FARVIEW = new ArrayList<RenderedCube>();
		boolean calcNormalView = false;
		
		for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
		{
			if (Math.abs(x1)<=J3DCore.RENDER_DISTANCE)
			{
				calcNormalView = true;
			}
			for (int y1=-1*Math.min(distance,20); y1<=1*Math.min(distance,20); y1++)
			{
				if (calcNormalView && !(Math.abs(y1)<=J3DCore.RENDER_DISTANCE))
				{
					calcNormalView = false;
				}
				long key = 0; boolean getKey = true;
				for (int z1=Math.round(zMinusMult*distance); z1<=zPlusMult*distance; z1++)
				{
					int worldX = x+x1;
					int worldY = y+y1;
					int worldZ = z-z1;
					if (worldZ<0) getKey = true; // key recalc needed
					worldX = world.shrinkToWorld(worldX);
					worldZ = world.shrinkToWorld(worldZ);

					if (getKey)
					{
						key = Boundaries.getKey(worldX,worldY,worldZ);
						getKey = false;
					} else
					{
						// no key calc needed, decrease it...
						key--;
					}
					
					if (!farViewEnabled || calcNormalView && Math.abs(z1)<J3DCore.RENDER_DISTANCE)
					{
						RenderedCube c = null;
						if (!worldCubeCache.containsKey(key))
						{
							Cube cube = world.getCube(world.engine.getWorldMeanTime(),key, worldX, worldY, worldZ, false);
							if (cube!=null)
							{
								c = new RenderedCube(cube,x1,y1,z1);
							}
							if (c!=null) 
							{	// only gather newly rendered cubes
								elements.add(c);
							}
						} else
						{
							c = worldCubeCache.remove(key);
						}
						worldCubeCacheNext.put(key, c);
					}

					if (farViewEnabled)
					{
						if (worldX%J3DCore.FARVIEW_GAP==0 && worldZ%J3DCore.FARVIEW_GAP==0 && worldY%J3DCore.FARVIEW_GAP==0)
						{
							// render this one for farview
							RenderedCube c = null;
							if (!worldCubeCache_FARVIEW.containsKey(key))
							{
								Cube cube = world.getCube(world.engine.getWorldMeanTime(),key, worldX, worldY, worldZ, true);
								if (cube!=null)
								{
									c = new RenderedCube(cube,x1,y1,z1);
									c.farview = true;
								}
								if (c!=null) 
								{	// only gather newly rendered cubes
									elements_FARVIEW.add(c);
								}
							} else
							{
								c = worldCubeCache_FARVIEW.remove(key);
							}
							worldCubeCacheNext_FARVIEW.put(key, c);
						}
						
					}
					
				}
			}
		}

		/*if (farViewEnabled) 
		{
			distance = J3DCore.RENDER_DISTANCE_FARVIEW;
			
			for (int z1=Math.round(zMinusMult*distance); z1<=zPlusMult*distance; z1++)
			{
				for (int y1=-1*Math.min(distance,20); y1<=1*Math.min(distance,20); y1++)
				{
					for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
					{
						int worldX = x+x1;
						int worldY = y+y1;
						int worldZ = z-z1;
						
						
						
					}
				}
			}
		}*/
		
		RenderedCube[] removable =  worldCubeCache.values().toArray(new RenderedCube[0]);
		worldCubeCache.clear();
		worldCubeCache = worldCubeCacheNext;
		
		// farview part
		RenderedCube[] removable_FARVIEW =  worldCubeCache_FARVIEW.values().toArray(new RenderedCube[0]);
		worldCubeCache_FARVIEW.clear();
		worldCubeCache_FARVIEW = worldCubeCacheNext_FARVIEW;
		
		return new RenderedCube[][]{(RenderedCube[])elements.toArray(new RenderedCube[0]),removable, (RenderedCube[])elements_FARVIEW.toArray(new RenderedCube[0]),removable_FARVIEW};
		
	}
	
	public RenderedCube getCubeAtPosition(World world, int worldX, int worldY, int worldZ)
	{
		worldX = world.shrinkToWorld(worldX);
		worldZ = world.shrinkToWorld(worldZ);
		long key = Boundaries.getKey(worldX, worldY, worldZ);
		return worldCubeCache.get(key);
	}
	
	
}
