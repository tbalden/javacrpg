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
	
	
	int renderDistance, renderDistanceFarview;
	
	public RenderedArea()
	{
		
	}
	public RenderedArea(int renderDistance, int renderDistanceFarview)
	{
		this.renderDistance = renderDistance;
		this.renderDistanceFarview = renderDistanceFarview;
	}
	
	public World lastRenderedWorld = null;
	public int lastX = -99999999;
	public int lastY = -99999999;
	public int lastZ = -99999999;
	
	public static int[][][] ALL_COORDINATES = new int[0][][];
	
	public class Period
	{
		public int min, max;
		
		public Period(int min, int max)
		{
			this.min = min; this.max = max;
		}
		
		public boolean isInside(int value)
		{
			if (min<=value && max>=value) return true;
			return false;
		}
		
		public boolean overlaps(Period p)
		{
			return isInside(p.min)||isInside(p.max);
		}
		public boolean isParameterGreater(Period p)
		{
			if (p.min>min)
				return true;
			return false;
		}
	}
	
	public int[][][] getNeededCoordinateIntervals(World world, int x, int y, int z, boolean old)
	{
		if ( ( lastX==-99999999 ) && old)
		{
			// no render yet (or not in the new world), no removal of previously rendered old needed
			return null;
		}
		
		int newXMin = x-renderDistance;
		int newXMax = x+renderDistance;
		Period pNewX = new Period(newXMin,newXMax);
		int newYMin = y-Math.min(renderDistance,15);
		int newYMax = y+Math.min(renderDistance,15);
		Period pNewY = new Period(newYMin,newYMax);
		int newZMin = z-renderDistance;
		int newZMax = z+renderDistance;
		Period pNewZ = new Period(newZMin,newZMax);
		
		if (( lastX==-99999999 || world!=lastRenderedWorld || lastRenderedWorld==null) && !old)
		{
			 // new render is needed for full area (no render yet or not in the new world)
			return new int[][][] {{{newXMin,newXMax},{newYMin,newYMax},{newZMin,newZMax}}};
		}
		
		int oldXMin = lastX-renderDistance;
		int oldXMax = lastX+renderDistance;
		Period pOldX = new Period(oldXMin,oldXMax);
		int oldYMin = lastY-Math.min(renderDistance,15);
		int oldYMax = lastY+Math.min(renderDistance,15);
		Period pOldY = new Period(oldYMin,oldYMax);
		int oldZMin = lastZ-renderDistance;
		int oldZMax = lastZ+renderDistance;
		Period pOldZ = new Period(oldZMin,oldZMax);
		
		if ((lastRenderedWorld==null || world!=lastRenderedWorld) && old)
		{
			// old world needs to be cleared out...
			return new int[][][] {{{oldXMin,oldXMax},{oldYMin,oldYMax},{oldZMin,oldZMax}}};
		}
		
		// middle coordinate inside the area which cuts the area box into 8 parts
		int midX, midY, midZ;
		// tells if the new X/Y/Z is a greater coordinate than the middle (so it determines
		// the direction of the not included area)
		boolean XGreater, YGreater, ZGreater;
		if (old)
		{
			// checking if related to old - is the new area with greater coordinates X/Y/Z
			// and finding middle 'edge of new area' coordinates in the old area 
			XGreater =pOldX.isParameterGreater(pNewX);
			if (XGreater) midX = pNewX.min; else midX = pNewX.max;
			YGreater =pOldY.isParameterGreater(pNewY);
			if (YGreater) midY = pNewY.min; else midY = pNewY.max;
			ZGreater =pOldZ.isParameterGreater(pNewZ);
			if (ZGreater) midZ = pNewZ.min; else midZ = pNewZ.max;
		} else
		{
			// checking if related to new - is the old area with greater coordinates X/Y/Z
			// and finding middle 'edge of old area' coordinates in the new area 
			XGreater =pNewX.isParameterGreater(pOldX);
			if (XGreater) midX = pOldX.min; else midX = pOldX.max;
			YGreater =pNewY.isParameterGreater(pOldY);
			if (YGreater) midY = pOldY.min; else midY = pOldY.max;
			ZGreater =pNewZ.isParameterGreater(pOldZ);
			if (ZGreater) midZ = pOldZ.min; else midZ = pOldZ.max;
		}
		
		if (!pOldX.overlaps(pNewX) ||
				!pOldY.overlaps(pNewY) ||
				!pOldZ.overlaps(pNewZ) )
		{
			// old/new area not overlapping at all at least in one or more direction
			if (old) // removal of all old
				return new int[][][] {{{oldXMin,oldXMax},{oldYMin,oldYMax},{oldZMin,oldZMax}}};
			// addition of all new
			return new int[][][] {{{newXMin,newXMax},{newYMin,newYMax},{newZMin,newZMax}}};
		}
		
		// overlapping detected, creating the 7 needed box limiter int array with the start/end coordinates
		if (old)
		{
			int[][][] returnArray = new int[7][][];
			int counter = 0;
			if (XGreater||YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ oldXMin, midX },
						{ oldYMin, midY }, 
						{ oldZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, oldXMax },
						{ oldYMin, midY }, 
						{ oldZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (XGreater||!YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ oldXMin, midX },
						{ midY+1, oldYMax }, 
						{ oldZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (XGreater||YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ oldXMin, midX },
						{ oldYMin, midY }, 
						{ midZ+1, oldZMax } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||!YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, oldXMax },
						{ midY+1, oldYMax }, 
						{ oldZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, oldXMax },
						{ oldYMin, midY }, 
						{ midZ+1, oldZMax } };
				returnArray[counter++] = limiters;
			}
			if (XGreater||!YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ oldXMin, midX },
						{ midY+1, oldYMax }, 
						{ midZ+1, oldZMax } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||!YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, oldXMax },
						{ midY+1, oldYMax }, 
						{ midZ+1, oldZMax } };
				returnArray[counter++] = limiters;
			}
			
			return returnArray;
			
		} else
		{
			int[][][] returnArray = new int[7][][];
			int counter = 0;
			if (XGreater||YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ newXMin, midX },
						{ newYMin, midY }, 
						{ newZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, newXMax },
						{ newYMin, midY }, 
						{ newZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (XGreater||!YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ newXMin, midX },
						{ midY+1, newYMax }, 
						{ newZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (XGreater||YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ newXMin, midX },
						{ newYMin, midY }, 
						{ midZ+1, newZMax } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||!YGreater||ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, newXMax },
						{ midY+1, newYMax }, 
						{ newZMin, midZ } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, newXMax },
						{ newYMin, midY }, 
						{ midZ+1, newZMax } };
				returnArray[counter++] = limiters;
			}
			if (XGreater||!YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ newXMin, midX },
						{ midY+1, newYMax }, 
						{ midZ+1, newZMax } };
				returnArray[counter++] = limiters;
			}
			if (!XGreater||!YGreater||!ZGreater)
			{
				int[][] limiters = new int[][] { 
						{ midX+1, newXMax },
						{ midY+1, newYMax }, 
						{ midZ+1, newZMax } };
				returnArray[counter++] = limiters;
			}
			
			return returnArray;
		}
		
	}
	

	public RenderedCube[][] getRenderedSpace(World world, int x, int y, int z, int direction, boolean farViewEnabled, boolean rerender)
	{
		/*try
		{
			throw new Exception();
		}catch (Exception ex)
		{
			ex.printStackTrace();
		}*/
		Time wtime = world.engine.getWorldMeanTime();		
		world.perf_eco_t0 = 0;
		world.perf_climate_t0 = 0;
		world.perf_flora_t0 = 0;
		world.perf_geo_t0 = 0;
		world.perf_surface_t0 = 0;
		world.perf_water_t0 = 0;
		
		long time1 = System.currentTimeMillis();
		System.out.println("WCC START : "+worldCubeCache.size());
	
		int[][][] toRemoveCoordinates = getNeededCoordinateIntervals(world, x, y, z, true);
		int[][][] toAddCoordinates = getNeededCoordinateIntervals(world, x, y, z, false);
		
		RenderedCube[] toAdd = new RenderedCube[0];
		RenderedCube[] toRemove = new RenderedCube[0];
		
		if (rerender) lastRenderedWorld = null; 
		
		if (toRemoveCoordinates!=null)
		{
			if (toRemoveCoordinates==ALL_COORDINATES)
			{
				System.out.println("REMOVING ALL COORDINATES");
				toRemove = worldCubeCache.values().toArray(new RenderedCube[0]);
				worldCubeCache.clear();
			} else
			{
				// removal cycle
				ArrayList<RenderedCube> removed = new ArrayList<RenderedCube>();
				
				for (int count = 0; count<toRemoveCoordinates.length; count++)
				{
					int[][] limiters = toRemoveCoordinates[count];
					
					for (int worldX=limiters[0][0]; worldX<=limiters[0][1]; worldX++)
					{
						for (int worldZ=limiters[2][0]; worldZ<=limiters[2][1]; worldZ++)
						{
							long key = 0; boolean getKey = true;
							for (int worldY=limiters[1][0]; worldY<=limiters[1][1]; worldY++)
							{

								worldX = world.shrinkToWorld(worldX);
								worldZ = world.shrinkToWorld(worldZ);
								if (worldY<0) getKey = true; // key recalc needed
								if (getKey)
								{
									key = Boundaries.getKey(worldX,worldY,worldZ);
									getKey = false;
								} else
								{
									// no key calc needed, increase it...
									key++;
								}
								//if (worldCubeCache.containsKey(key))
								{
									removed.add(worldCubeCache.remove(key));								
								}
							}
						}
					}
				}
				toRemove = removed.toArray(new RenderedCube[0]);
			}
		}

		if (toAddCoordinates!=null)
		{
			{
				// addition cycle
				ArrayList<RenderedCube> added = new ArrayList<RenderedCube>();
				RenderedCube c = null;
				
				for (int count = 0; count<toAddCoordinates.length; count++)
				{
					int[][] limiters = toAddCoordinates[count];
					
					for (int worldX=limiters[0][0]; worldX<=limiters[0][1]; worldX++)
					{
						for (int worldZ=limiters[2][0]; worldZ<=limiters[2][1]; worldZ++)
						{
							long key = 0; boolean getKey = true;
							for (int worldY=limiters[1][0]; worldY<=limiters[1][1]; worldY++)
							{
								int wX = worldX;
								int wZ = worldZ;
								worldX = world.shrinkToWorld(worldX);
								worldZ = world.shrinkToWorld(worldZ);
								if (worldY<0) getKey = true; // key recalc needed
								if (getKey)
								{
									key = Boundaries.getKey(worldX,worldY,worldZ);
									getKey = false;
								} else
								{
									// no key calc needed, increase it...
									key++;
								}
								c = null;
								Cube cube = world.getCube(wtime,key, worldX, worldY, worldZ, false);
								//if (cube!=null) System.out.println(cube);
								if (cube!=null)
								{
									//c = new RenderedCube(cube,wX-x,worldY-y,z-wZ);
									c = new RenderedCube(cube,wX,worldY,wZ);
									//System.out.println(c.renderedX+" "+c.renderedY+" "+c.renderedZ);
									c.world = world;
									// gather newly rendered cubes
									added.add(c);
								}
								worldCubeCache.put(key, c);
								
							}
						}
					}
				}
				toAdd = added.toArray(new RenderedCube[0]);
			}
		}
		
		lastRenderedWorld = world;
		lastX = x;
		lastY = y;
		lastZ = z;
		
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- eco = "+world.perf_eco_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- geo = "+world.perf_geo_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- flo = "+world.perf_flora_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- cli = "+world.perf_climate_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- wat = "+world.perf_water_t0);
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("-- sur = "+world.perf_surface_t0);

		System.out.println("WORLDCUBECACHE = "+worldCubeCache.size()+ " ADD: "+toAdd.length+" REM: "+toRemove.length);
		System.out.println("-- eco = "+world.perf_eco_t0);
		System.out.println("-- geo = "+world.perf_geo_t0);
		System.out.println("-- flo = "+world.perf_flora_t0);
		System.out.println("-- cli = "+world.perf_climate_t0);
		System.out.println("-- wat = "+world.perf_water_t0);
		System.out.println("-- sur = "+world.perf_surface_t0);
		System.out.println("FULL TIME = "+(System.currentTimeMillis()-time1));
		return new RenderedCube[][]{toAdd,toRemove, new RenderedCube[0],new RenderedCube[0]};
		
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
	public void setRenderDistance(int renderDistance) {
		this.renderDistance = renderDistance;
	}
	public void setRenderDistanceFarview(int renderDistanceFarview) {
		this.renderDistanceFarview = renderDistanceFarview;
	}
	
}
