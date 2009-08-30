/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world.place;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.flora.FloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;
import org.jcrpg.world.time.Time;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

public class World extends Place implements TileBasedMap {

	public Engine engine;
	public transient WorldMap worldMap;
	
	
	public int GEOGRAPHY_RANDOM_SEED = 0;
	
	/**
	 * Tells which direction sun goes around - E-W (true) or W-E (false)
	 */
	public boolean sunLikeOnEarth = true;
	/**
	 * Tells if in half Z year percentage should return reversed for Seasions. Earthlike equator - set true! 
	 */
	public boolean timeSwitchOnEquator = true;
	
	public WorldOrbiterHandler orbiterHandler;
	
	public Climate climate;
	public FloraContainer floraContainer;
	public EconomyContainer economyContainer;
	
	public HashMap<String, Geography> geographies;
	public HashMap<String, ArrayList<Geography>> geographyCache = new HashMap<String, ArrayList<Geography>>();
	public HashMap<String, Water> waters;
	public HashMap<String, Political> politicals;

	public static final String TYPE_WORLD = "WORLD";
	public static final Swimming SUBTYPE_OCEAN = new Swimming(TYPE_WORLD+"_OCEAN");
	public static final GroundSubType SUBTYPE_GROUND = new GroundSubType(TYPE_WORLD+"_GROUND");
	
	public boolean WORLD_IS_GLOBE = true;

	static Side[][] OCEAN = new Side[][] { null, null, null,null,null,{new Side(TYPE_WORLD,SUBTYPE_OCEAN)} };
	static Side[][] GROUND = new Side[][] { null, null, null,null,null,{new Side(TYPE_WORLD,SUBTYPE_GROUND)} };
	
	
	public int sizeX, sizeY, sizeZ, magnification, worldGroundLevel;
	
	public int realSizeX, realSizeY, realSizeZ;

	public World(String id, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ) throws Exception {
		super(id, null, loc);
		if (magnification%10!=0) throw new Exception("Magnification should be divisible by 10");
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		realSizeX = sizeX*magnification;
		realSizeY = sizeY*magnification;
		realSizeZ = sizeZ*magnification;
		if (realSizeX<J3DCore.MINIMUM_WORLD_REALSIZE)
		{
			throw new Exception("World X size is smaller than J3DCore.MINIMUM_WORLD_REALSIZE ("+J3DCore.MINIMUM_WORLD_REALSIZE+")");
		}
		if (realSizeZ<J3DCore.MINIMUM_WORLD_REALSIZE)
		{
			throw new Exception("World Z size is smaller than J3DCore.MINIMUM_WORLD_REALSIZE ("+J3DCore.MINIMUM_WORLD_REALSIZE+")");
		}
		worldGroundLevel = (sizeY*magnification/2);
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, 0, 0, 0));
		geographies = new HashMap<String, Geography>();
		waters = new HashMap<String, Water>();
		politicals = new HashMap<String, Political>();
		economyContainer = new EconomyContainer(this);
	}
	
	
	public int getSeaLevel(int magnification)
	{
		return (int)(((sizeY*this.magnification)/2)/magnification);
	}


	@Override
	public boolean generateModel() {
		
		return true;
	}


	@Override
	public boolean loadModelFromFile() {
		// TODO Auto-generated method stub
		return false;
	}
	

	/**
	 * returns on a per geography base a list of surface type and height array belonging to the give geographies at a given coordinates.
	 * @param worldX
	 * @param worldZ
	 * @return
	 */
	public ArrayList<SurfaceHeightAndType[]> getSurfaceData(int worldX, int worldZ)
	{
		ArrayList<SurfaceHeightAndType[]> list = new ArrayList<SurfaceHeightAndType[]>();
		int worldY = getSeaLevel(1);
		for (Geography geo : geographies.values()) {
			if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				long t0 = 0;
				//t0 = System.currentTimeMillis();
				//Cube geoCube = geo.getCube(-1, worldX, worldY, worldZ, false);
				//perf_geo_t0+=System.currentTimeMillis()-t0;
				//collectCubes(geoCube,false);
				if (geo instanceof Surface)
				{
					t0 = System.currentTimeMillis();
					SurfaceHeightAndType[] surf = ((Surface)geo).getPointSurfaceData(worldX, worldZ, null, false);
					if (surf!=null && surf.length>0)
						list.add(surf);
				}
			}
		}
		return list;
	}
	
	/**
	 * returns the list of geographies that are Surface at a given X/Z coordinates of the world. Used with RoadNetwork for example to get possible geographies
	 * where to place road upon.
	 * @param worldX
	 * @param worldZ
	 * @return
	 */
	public ArrayList<Geography> getSurfaceGeographies(int worldX, int worldZ)
	{
		ArrayList<Geography> list = new ArrayList<Geography	>();
		int worldY = getSeaLevel(1);
		for (Geography geo : geographies.values()) {
			if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				if (geo instanceof Surface)
				{
					list.add(geo);
				}
			}
		}
		return list;
	}
	
	@Override
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView) {
		int _worldX = worldX;
		int _worldY = worldY;
		int _worldZ = worldZ;
		if (WORLD_IS_GLOBE) {
			_worldX = shrinkToWorld(_worldX);
			_worldZ = shrinkToWorld(_worldZ);
		}
		Time localTime = engine.getWorldMeanTime().getLocalTime(this, _worldX, _worldY, _worldZ);
		return getCube(localTime,key, worldX,worldY,worldZ, farView);
	}
	
	
	public long perf_eco_t0 = System.currentTimeMillis();
	public long perf_flora_t0 = System.currentTimeMillis();
	public long perf_geo_t0 = perf_flora_t0;
	public long perf_climate_t0 = perf_flora_t0;
	public long perf_water_t0 = perf_flora_t0;
	public long perf_surface_t0 = perf_flora_t0;
	
	public int lastXProbe = -1, lastYProbe = -1, lastZProbe = -1;
	public Long lastKey;
	public static int PROBE_DISTANCE = 100;
	public static HashMap<Long,HashSet<Object>> provedToBeAway = new HashMap<Long,HashSet<Object>>();
	public static HashMap<Long,HashSet<Object>> provedToBeNear = new HashMap<Long,HashSet<Object>>();
	public static ArrayList<Long> probeCacheRemovalList = new ArrayList<Long>();

	public static HashSet<Object> hsProvedToBeNear = null;
	public static HashSet<Object> hsProvedToBeAway = null;
	
	/**
	 * For parallel use of world in more than 1 thread, we need separate merger objects...
	 */
	public static ArrayList<CubeMergerInfo> reusedMergerInfos = new ArrayList<CubeMergerInfo>();
	static
	{
		for (int i=0; i<8; i++)
		{
			reusedMergerInfos.add(new CubeMergerInfo());
		}
	}
	
	public static ArrayList<HashMap<Geography,SurfaceHeightAndType>> reusedGeosForSurface = new ArrayList<HashMap<Geography, SurfaceHeightAndType>>();
	static
	{
		for (int i=0; i<8; i++)
		{
			reusedGeosForSurface.add(new HashMap<Geography,SurfaceHeightAndType>());
		}
	}
	
	public Cube getCube(Time localTime, long key, int worldX, int worldY, int worldZ, boolean farView) {

		if (WORLD_IS_GLOBE) {
			worldX = shrinkToWorld(worldX);
			worldZ = shrinkToWorld(worldZ);
		}
		int CONST_FARVIEW = farView?J3DCore.FARVIEW_GAP:1;
		
		
		if (boundaries.isInside(worldX, worldY, worldZ))
		{
			long t0 = System.currentTimeMillis();
				
			Cube ecoCube = economyContainer.getEconomicCube(key, worldX, worldY, worldZ, farView, localTime);
			perf_eco_t0+=System.currentTimeMillis()-t0;
			if (ecoCube!=null) 
			{
				
				return ecoCube;
			}
			CubeMergerInfo info = null;
			synchronized (reusedMergerInfos)
			{
				info = reusedMergerInfos.remove(0);
				info.currentMerged = null;
				info.finalRounders.clear();
				info.overLappers.clear();
			}
			
			perf_eco_t0+=System.currentTimeMillis()-t0;
			//Cube retCube = null;
			info.currentMerged = null;
			info.overLappers.clear();
			info.finalRounders.clear();
			boolean insideGeography = false;
			HashMap<Geography,SurfaceHeightAndType> tempGeosForSurface = null;
			synchronized (reusedGeosForSurface)
			{
				tempGeosForSurface = reusedGeosForSurface.remove(0);
				tempGeosForSurface.clear();
			}

			for (Geography geo : geographies.values()) {
				//System.out.print("-!");
				if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
				{
					insideGeography = true;
					
					t0 = System.currentTimeMillis();
					Cube geoCube = geo.getCube(key, worldX, worldY, worldZ, farView);
					perf_geo_t0+=System.currentTimeMillis()-t0;
					collectCubes(info,geoCube,false);
					if (geo instanceof Surface)
					{
						t0 = System.currentTimeMillis();
						SurfaceHeightAndType[] surf = ((Surface)geo).getPointSurfaceData(worldX, worldZ, geoCube, farView);
						perf_surface_t0+=System.currentTimeMillis()-t0;
						for (int surfCount = 0; surfCount<surf.length; surfCount++) {
							if (surf!=null && surf[surfCount].canContain) {
								// collecting surfaces that can contain, e.g. waters
								tempGeosForSurface.put(geo,surf[surfCount]);
							}
							if (geoCube!=null) { 
								if (worldY/CONST_FARVIEW==surf[surfCount].surfaceY/CONST_FARVIEW && surf[surfCount].canContain)
								{
									// this can contain things upon it, do the climate and flora... 
									t0 = System.currentTimeMillis();
									CubeClimateConditions conditions = getCubeClimateConditions(localTime,worldX, worldY, worldZ, geoCube.internalCube);
									perf_climate_t0+=System.currentTimeMillis()-t0;
									geoCube.climateId = conditions.belt.STATIC_ID;
									// setting canContain for the geoCube, this is a surface cube.
									geoCube.canContainFlora = true;
									geoCube.canHoldUnit= true;
									Cube floraCube = null;
									t0 = System.currentTimeMillis();
									floraCube = geo.getFloraCube(worldX, worldY, worldZ, conditions, localTime, geoCube.steepDirection!=SurfaceHeightAndType.NOT_STEEP);
									perf_flora_t0+=System.currentTimeMillis()-t0;
									if (floraCube!=null)
									{
										floraCube.internalCube = geoCube.internalCube;
										collectCubes(info,floraCube,true);
									} 
									else 
									{
										if (geoCube.internalCube) 
										{
										} else 
										{
											// outside green ground appended
											collectCubes(info,new Cube(this,GROUND,worldX,worldY,worldZ),false);
										}
										
									}
								} else 
								if (farView)
								{
									// for far view set climate always even if its not surface
									CubeClimateConditions conditions = getCubeClimateConditions(localTime,worldX, worldY, worldZ, geoCube.internalCube);
									geoCube.climateId = conditions.belt.STATIC_ID;
								}
							}
						}
					} else 
					{
					}
				}
			}
			mergeCubes(info);
			
			// waters
			t0 = System.currentTimeMillis();
			for (Water w : waters.values()) {
				if (w.boundaries.isInside(worldX, worldY, worldZ)) 
				{
					//boolean thePoint = false;
					if (w.isWaterPoint(worldX, worldY, worldZ, farView))
					{
						for (SurfaceHeightAndType s:tempGeosForSurface.values())
						{
							int y = s.surfaceY;
							int depth = w.getDepth(worldX, worldY, worldZ);
							int bottom = y - depth;
							if (farView) y = y - y%J3DCore.FARVIEW_GAP;
							if (farView) bottom = bottom - bottom%J3DCore.FARVIEW_GAP;
							//if (farView) System.out.println("Y = "+y+" = "+s.surfaceY+" BOTTOM = "+ bottom + " ## y <= "+worldY +" >= bottom");
							if (worldY>=bottom&&worldY<=y)
							{
								Cube c = w.getWaterCube(worldX, worldY, worldZ, info.currentMerged, s, farView);
								if (info.currentMerged!=null && info.currentMerged.overwrite) {
									collectCubes(info,c,false);
									c = mergeCubes(info);
								}
								if (c!=null)
								{
									reusedMergerInfos.add(info);
									reusedGeosForSurface.add(tempGeosForSurface);
									return c;
								}
							}
						}
					} else
					{
						continue; // yes, we must look for the next water if there's one!
					}
				}
			}
			perf_water_t0+=System.currentTimeMillis()-t0;
			Cube c = info.currentMerged;
			reusedMergerInfos.add(info);
			reusedGeosForSurface.add(tempGeosForSurface);
			if (insideGeography) return c;

			// not in geography, return null
			return null;
			//return worldY==worldGroundLevel?new Cube(this,OCEAN,worldX,worldY,worldZ):null; -- world generation made this deprecated
		}
		else return null;
	}

	public static class CubeMergerInfo
	{
		public ArrayList<Cube> overLappers = new ArrayList<Cube>();
		public ArrayList<Cube> finalRounders = new ArrayList<Cube>();
		Cube currentMerged = null;
	}
	
	
	//public Cube currentMerged;
	
	public void collectCubes(CubeMergerInfo info, Cube cube,boolean finalRound)
	{
		if (cube!=null && cube.onlyIfOverlaps) {
			info.overLappers.add(cube);
			return;
		}
		if (cube!=null && finalRound) {
			info.finalRounders.add(cube);
			return;
		}
		if (cube!=null && !cube.onlyIfOverlaps && info.currentMerged==null) {
			info.currentMerged=cube;
		} else
		if (cube!=null && !cube.onlyIfOverlaps && info.currentMerged!=null)
		{
			info.currentMerged = appendCube(info.currentMerged, cube, cube.x, cube.y, cube.z);
		}
		
	}
	public Cube mergeCubes(CubeMergerInfo info)
	{
		if (info.currentMerged==null) return info.currentMerged;
		for (Cube cube:info.overLappers)
		{
			info.currentMerged = appendCube(cube,info.currentMerged, cube.x, cube.y, cube.z);
		}
		for (Cube cube:info.finalRounders)
		{
			info.currentMerged = appendCube(info.currentMerged, cube, cube.x, cube.y, cube.z);
		}
		info.overLappers.clear();
		info.finalRounders.clear();
		return info.currentMerged;
	}
	
	public Cube appendCube(Cube orig, Cube newCube, int worldX, int worldY, int worldZ)
	{
		if (orig!=null && newCube!=null) {
			orig.merge(newCube,worldX,worldY,worldZ,orig.steepDirection);
			//orig = new Cube(orig,newCube,worldX,worldY,worldZ,orig.steepDirection);
			return orig;
					//
		}
		if (orig!=null && newCube==null)
		{
			if (orig.onlyIfOverlaps) {
				return null;
			}
		}
		if (orig==null && newCube!=null)
		{
			if (newCube.onlyIfOverlaps) {
				return null;
			}
		}
		return newCube==null?orig:newCube;
	}
	
	
	public static ArrayList<ArrayList<int[]>> reusedZoneArrays = new ArrayList<ArrayList<int[]>>();
	static
	{
		for (int i=0; i<8; i++)
		{
			reusedZoneArrays.add(new ArrayList<int[]>());
		}
	}
	
	@Override
	public int[][] getFilledZonesOfY(int worldX, int worldZ, int minY, int maxY)
	{
		if (WORLD_IS_GLOBE) {
			worldX = shrinkToWorld(worldX);
			worldZ = shrinkToWorld(worldZ);
		}
		
		ArrayList<int[]> list = reusedZoneArrays.remove(0);
		list.clear();
		if (boundaries.isInside(worldX, worldGroundLevel, worldZ))
		{
			for (Water w : waters.values()) {
				if (w.boundaries.isInside(worldX, w.worldGroundLevel, worldZ)) 
				{
					int[][] v = w.getFilledZonesOfY(worldX, worldZ, minY, maxY);
					if (v!=null)
					{
						boolean add = true;
						for (int[] vi:v){
							if (vi[0]<minY && vi[1]<minY) continue;
							if (vi[0]>maxY && vi[1]>maxY) continue;
							add = true;
							for (int[] m:list)
							{
								if (vi[0]>=m[0] && vi[0]<=m[1])
								{
									add = false;
									if (vi[1]>m[1])
									{
										m[1] = vi[1];
									}
									break;
								} else
								if (vi[1]>=m[0] && vi[1]<=m[1])
								{
									add = false;
									if (vi[0]<m[0])
									{
										m[0] = vi[0];
									}
									break;
								}
							}
							if (add)
							{
								list.add(vi);
							}
						}
					}
				}
			}
			for (Geography geo : geographies.values()) {
				//System.out.print("-!");
				if (geo.getBoundaries().isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					int[][] v = geo.getFilledZonesOfY(worldX,worldZ,minY,maxY);
					if (v!=null)
					{
						boolean add = true;
						for (int[] vi:v){
							if (vi[0]<minY && vi[1]<minY) continue;
							if (vi[0]>maxY && vi[1]>maxY) continue;
							add = true;
							for (int[] m:list)
							{
								if (vi[0]>=m[0] && vi[0]<=m[1])
								{
									add = false;
									if (vi[1]>m[1])
									{
										m[1] = vi[1];
									}
									break;
								} else
								if (vi[1]>=m[0] && vi[1]<=m[1])
								{
									add = false;
									if (vi[0]<m[0])
									{
										m[0] = vi[0];
									}
									break;
								}
							}
							if (add)
							{
								list.add(vi);
							}
						}
					}
				}
			}
			ArrayList<Economic> ecos = economyContainer.getEconomicsInColumn(worldX, worldZ);
			if (ecos!=null)
			{
				for (Economic e:ecos)
				{
					int[][] v = e.getFilledZonesOfY(worldX, worldZ,minY,maxY);
					if (v!=null)
					{
						boolean add = true;
						for (int[] vi:v){
							if (vi[0]<minY && vi[1]<minY) continue;
							if (vi[0]>maxY && vi[1]>maxY) continue;
							add = true;
							for (int[] m:list)
							{
								if (vi[0]>=m[0] && vi[0]<=m[1])
								{
									// new interval's minimum inside an old interval, no addition needed
									add = false;
									if (vi[1]>m[1])
									{
										// maximum is greater, we should set the higher maximum
										m[1] = vi[1];
									}
									break;
								} else
								if (vi[1]>=m[0] && vi[1]<=m[1])
								{
									// new interval's maximum inside an old interval, no addition needed
									add = false;
									if (vi[0]<m[0])
									{
										// minimum is lesser, we should set the lower minimum
										m[0] = vi[0];
									}
									break;
								}
							}
							if (add)
							{
								// no intersection, we should add the new
								list.add(vi);
							}
						}
					}
				}
			}
			reusedZoneArrays.add(list);
			return (int[][])list.toArray(new int[0][]);
		}		
		reusedZoneArrays.add(list);
		return null;
		
	}
	

	/**
	 * 
	 * @return world climate.
	 */
	public Climate getClimate() {
		return climate;
	}


	public void setClimate(Climate climate) {
		this.climate = climate;
	}


	@Override
	public Place getRoot() {
		return this;
	}


	/**
	 * 
	 * @return The flora's foundation.
	 */
	public FloraContainer getFloraContainer() {
		return floraContainer;
	}


	public void setFloraContainer(FloraContainer floraContainer) {
		this.floraContainer = floraContainer;
	}


	public WorldOrbiterHandler getOrbiterHandler() {
		return orbiterHandler;
	}


	public void setOrbiterHandler(WorldOrbiterHandler orbiterHandler) {
		this.orbiterHandler = orbiterHandler;
	}
	
	public int getGeographyHashPercentage(int x,int y, int z)
	{
		return HashUtil.mixPercentage(GEOGRAPHY_RANDOM_SEED,x,y,z);
	}

	public int shrinkToWorld(int x)
	{
		if (x<0) x=realSizeX+x;
		if (x>=realSizeX) x = x%realSizeX;
		return x;
	}
	
	public int lossFactor = 1000;
	
	
	public class WorldTypeDesc
	{
		public Geography g;
		public int surfaceY;
		public Population population;
		public Economic detailedEconomic;
		public Water w;
	}
	public WorldTypeDesc getWorldDescAtPosition(int worldX, int worldY, int worldZ, boolean scanAroundForPopulation)
	{
		return getWorldDescAtPosition(worldX, worldY, worldZ, scanAroundForPopulation, null);
	}
	/**
	 * Return a description of the point, filtering for geographies if its list is not null.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param scanAroundForPopulation
	 * @param filter
	 * @return
	 */
	public WorldTypeDesc getWorldDescAtPosition(int worldX, int worldY, int worldZ, boolean scanAroundForPopulation, ArrayList<Class<?extends Geography>> filter)
	{
		Geography geo = null;
		Geography backupGeo = null;
		int closestDiffToY = 999999;
		int backupClosestDiffToY = 999999;
		int surfaceY = 9999999;
		int backupSurfaceY = 9999999;
		ArrayList<SurfaceHeightAndType[]> list = getSurfaceData(worldX, worldZ);
		for (SurfaceHeightAndType[] subList : list)
		{
			if (subList.length>0)
			{
				for (SurfaceHeightAndType surface:subList)
				{
					if (filter!=null && !filter.contains(surface.self.getClass())) continue;
					if (worldY-surface.surfaceY>=0 && worldY-surface.surfaceY<closestDiffToY)
					{
						if (surface.self.getCube(-1, worldX, surface.surfaceY, worldZ, false)!=null)
						{
							closestDiffToY = worldY-surface.surfaceY;
							surfaceY = surface.surfaceY;
							geo = surface.self;
						}
						
					} else
					{
						if (Math.abs(worldY-surface.surfaceY)<backupClosestDiffToY)
						{
							if (surface.self.getCube(-1, worldX, surface.surfaceY, worldZ, false)!=null)
							{
								backupClosestDiffToY = Math.abs(worldY-surface.surfaceY);
								backupSurfaceY = surface.surfaceY;
								backupGeo = surface.self;
							}
						}
					}
				}
			}
		}
		WorldTypeDesc desc = new WorldTypeDesc();
		if (geo!=null)
		{
			desc.g = geo;
			desc.surfaceY = surfaceY;
			
		} else
		{
			if (backupGeo==null) return desc; // no geo found here, return
			
			desc.g = backupGeo;
			desc.surfaceY = backupSurfaceY;
		}
		
		Population population = economyContainer.getPopulationAt(worldX, worldY, worldZ);
		if (population==null)
		{
			population = economyContainer.getPopulationAt(worldX, desc.surfaceY, worldZ);
			// if scan around, get some more probes if no population found yet.
			if (scanAroundForPopulation)
			{
				if (population==null)
				{
					population = economyContainer.getPopulationAt(worldX+1, desc.surfaceY, worldZ);
				}
				if (population==null)
				{
					population = economyContainer.getPopulationAt(worldX-1, desc.surfaceY, worldZ);
				}
				if (population==null)
				{
					population = economyContainer.getPopulationAt(worldX, desc.surfaceY, worldZ+1);
				}
				if (population==null)
				{
					population = economyContainer.getPopulationAt(worldX, desc.surfaceY, worldZ-1);
				}
			}
		}
		desc.population = population;
		if (population!=null)
			desc.detailedEconomic = population.getSubEconomicAtPosition(-1, worldX, worldY, worldZ, false);
		
		for (Water w:waters.values())
		{
			try {
				if (w.getBoundaries().isInside(worldX, worldY, worldZ) && w.isAlgorithmicallyInside(worldX, worldY, worldZ) && w.isWaterPoint(worldX, worldY, worldZ, false))
				{
					desc.w = w;
					break;
				}
			} catch (Exception ex)
			{
				
			}
		}
		
		return desc;
		
	}
	
	
	public void addGeography(Geography g)
	{
		if (g instanceof Water)
		{
			waters.put(g.id, (Water)g);
		} else
			geographies.put(g.id, g);
	}
	
	public Collection<Geography> getAllGeographies()
	{
		ArrayList<Geography> g = new ArrayList<Geography>();
		g.addAll(geographies.values());
		g.addAll(waters.values());
		return g;
	}

	public void clearAll()
	{
		geographies.clear();
		geographyCache.clear();
		waters.clear();
		//overLappers.clear();
		politicals.clear();
		economyContainer.clearAll();
	}
	
	public void clearCaches()
	{
		for (Geography g:geographies.values())
		{
			g.clearCaches();
		}
	}
	
	@Override
	public void onLoad()
	{
		economyContainer.onLoad(this);
	}


	// aStar methods

	public boolean blocked(Mover mover, int x, int y) {
		for (Water w : waters.values()) {
			
			if (w instanceof Ocean)
			{
				Ocean water = (Ocean)w;
				boolean oceanWater = ((Ocean)water).isWaterPointSpecial(x*((Ocean)water).magnification, ((Ocean)water).worldGroundLevel, y*((Ocean)water).magnification, false, false);
				//System.out.println(w.magnification+" ? "+magnification+ "--- "+x+" "+z+" "+!oceanWater);
				//if (!oceanWater) System.out.println("TRUE");
				return oceanWater;
			}
			
		}
		//System.out.println("--- "+x+" "+z+" "+true);
		return false;
	}


	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		if (economyContainer.roadNetwork.getBoundaries().isInside(tx*magnification, getSeaLevel(1), ty*magnification))
		{
			return 5;
		}
		for (Water water :waters.values())
		{
			if (water instanceof River) // TODO river into a new image for not overwriting geo things!!
			{
				
				boolean riverWater = ((River)water).isWaterBlock(tx*magnification, worldGroundLevel, ty*magnification);
				if (riverWater) {
					//if (water.getRoadBuildingPrice()>1) System.out.println("PRICE W: "+water.getRoadBuildingPrice());
					return water.getRoadBuildingPrice();
				}
			}
		}
		for (Geography g:geographies.values())
		{
			if (g.getClass()==Cave.class) continue;
			if (g.getBoundaries().isInside(tx*magnification, g.worldGroundLevel, ty*magnification))
			{
				//if (g.getRoadBuildingPrice()>1) System.out.println("PRICE"+g.getClass()+" "+g.getRoadBuildingPrice());
				return g.getRoadBuildingPrice();
			}
		}
		return 0;
	}


	public int getHeightInTiles() {
		return sizeZ;
	}


	public int getWidthInTiles() {
		return sizeX;
	}


	public void pathFinderVisited(int x, int y) {
	}
	
}
