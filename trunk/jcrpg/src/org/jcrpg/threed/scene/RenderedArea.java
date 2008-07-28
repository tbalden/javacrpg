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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.space.Cube;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.World;
import org.jcrpg.world.time.Time;

public class RenderedArea {
	
	public HashMap<Long, RenderedCube> worldCubeCache = new HashMap<Long, RenderedCube>(); 
	public HashMap<Long, RenderedCube> worldCubeCacheNext = new HashMap<Long, RenderedCube>(); 

	public HashMap<Long, RenderedCube> worldCubeCache_FARVIEW = new HashMap<Long, RenderedCube>(); 
	public HashMap<Long, RenderedCube> worldCubeCacheNext_FARVIEW = new HashMap<Long, RenderedCube>();
	

	public RenderedCube[][] getRenderedSpace(World world, int x, int y, int z, int direction, boolean farViewEnabled, boolean rerender)
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
		
		Time wtime = world.engine.getWorldMeanTime();
		
		worldCubeCacheNext = new HashMap<Long, RenderedCube>();
		worldCubeCacheNext_FARVIEW = new HashMap<Long, RenderedCube>();
		ArrayList<RenderedCube> elements = new ArrayList<RenderedCube>();
		ArrayList<RenderedCube> elements_FARVIEW = new ArrayList<RenderedCube>();
		long sumTime = 0, sumTime_1 = 0, sumTime_2 = 0;
		world.perf_eco_t0 = 0;
		world.perf_climate_t0 = 0;
		world.perf_flora_t0 = 0;
		world.perf_geo_t0 = 0;
		world.perf_surface_t0 = 0;
		world.perf_water_t0 = 0;
		//HashSet<Long> keysToRemove = new HashSet<Long>();
		for (int x1=Math.round(xMinusMult*distance); x1<=xPlusMult*distance; x1++)
		{
			for (int z1=Math.round(zMinusMult*distance); z1<=zPlusMult*distance; z1++)
			{
				long key = 0; boolean getKey = true;
				for (int y1=-1*Math.min(distance,15); y1<=1*Math.min(distance,15); y1++)
				{
					long t0_1 = System.currentTimeMillis();
					int worldX = x+x1;
					int worldY = y+y1;
					int worldZ = z-z1;
					if (worldY<0) getKey = true; // key recalc needed
					worldX = world.shrinkToWorld(worldX);
					worldZ = world.shrinkToWorld(worldZ);
					
					boolean normalView = !farViewEnabled;
					if (!normalView)
					{
						if (Math.abs(z1)<=J3DCore.RENDER_DISTANCE && Math.abs(y1)<=J3DCore.RENDER_DISTANCE && Math.abs(x1)<=J3DCore.RENDER_DISTANCE)
						{
							normalView = true;
						}
					}

					if (normalView && getKey)
					{
						key = Boundaries.getKey(worldX,worldY,worldZ);
						getKey = false;
					} else
					{
						// no key calc needed, increase it...
						key++;
					}
					sumTime_1+=System.currentTimeMillis()-t0_1;
					if (normalView)
					{
						long t0 = System.currentTimeMillis();
						RenderedCube c = null;
						if (rerender || !worldCubeCache.containsKey(key))
						{
							//t0 = System.currentTimeMillis();
							Cube cube = world.getCube(wtime,key, worldX, worldY, worldZ, false);
							sumTime+=System.currentTimeMillis()-t0;
							
							if (cube!=null)
							{
								c = new RenderedCube(cube,x1,y1,z1);
								c.world = world;
								// gather newly rendered cubes
								elements.add(c);
							}
							//worldCubeCache.put(key, c);
						} else
						{
							//t0 = System.currentTimeMillis();
							c = worldCubeCache.remove(key);
							
						}
						worldCubeCacheNext.put(key, c);
						sumTime_2+=System.currentTimeMillis()-t0;
					}

					if (farViewEnabled)
					{
						if (worldX%J3DCore.FARVIEW_GAP==0 && worldZ%J3DCore.FARVIEW_GAP==0 && worldY%J3DCore.FARVIEW_GAP==0)
						{
							if (!normalView)
							{
								key = Boundaries.getKey(worldX,worldY,worldZ);
								getKey = false;
							}
							
							// render this one for farview
							RenderedCube c = null;
							if (rerender || !worldCubeCache_FARVIEW.containsKey(key))
							{
								Cube cube = world.getCube(world.engine.getWorldMeanTime(),key, worldX, worldY, worldZ, true);
								if (cube!=null)
								{
									c = new RenderedCube(cube,x1,y1,z1);
									c.world = world;
									c.farview = true;
									// gather newly rendered cubes
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
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("Key calculation sumTime = "+sumTime_1);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("World.getCube sumTime = "+sumTime);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("World.getCube + cache sumTime = "+sumTime_2);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- eco = "+world.perf_eco_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- geo = "+world.perf_geo_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- flo = "+world.perf_flora_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- cli = "+world.perf_climate_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- wat = "+world.perf_water_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- sur = "+world.perf_surface_t0);
		
		long t0 = System.currentTimeMillis();
		
		//RenderedCube[] removable =  new RenderedCube[0];//worldCubeCache.values().toArray(new RenderedCube[0]);
		RenderedCube[] removable = worldCubeCache.values().toArray(new RenderedCube[0]);
		worldCubeCache.clear();
		worldCubeCache = worldCubeCacheNext;
		
		// farview part
		RenderedCube[] removable_FARVIEW =  worldCubeCache_FARVIEW.values().toArray(new RenderedCube[0]);
		worldCubeCache_FARVIEW.clear();
		worldCubeCache_FARVIEW = worldCubeCacheNext_FARVIEW;
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("TO ARRAY TIME: "+(t0-System.currentTimeMillis()));
		return new RenderedCube[][]{(RenderedCube[])elements.toArray(new RenderedCube[0]),removable, (RenderedCube[])elements_FARVIEW.toArray(new RenderedCube[0]),removable_FARVIEW};
		
	}
	
	public RenderedCube getCubeAtPosition(World world, int worldX, int worldY, int worldZ)
	{
		worldX = world.shrinkToWorld(worldX);
		worldZ = world.shrinkToWorld(worldZ);
		long key = Boundaries.getKey(worldX, worldY, worldZ);
		return worldCubeCache.get(key);
	}
	public RenderedCube getCubeAtPosition(World world, int worldX, int worldY, int worldZ,boolean farview)
	{
		worldX = world.shrinkToWorld(worldX);
		worldZ = world.shrinkToWorld(worldZ);
		long key = Boundaries.getKey(worldX, worldY, worldZ);
		if (farview)
			return worldCubeCache_FARVIEW.get(key);
		return worldCubeCache.get(key);
		
	}
	
	/**
	 * big update is being done, clear out all cached cubes.
	 */
	public void fullUpdateClear()
	{
		worldCubeCache.clear();
		worldCubeCache_FARVIEW.clear();
		worldCubeCacheNext.clear();
		worldCubeCacheNext_FARVIEW.clear();
	}
	
}
