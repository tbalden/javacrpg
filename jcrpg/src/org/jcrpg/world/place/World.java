/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.time.Time;

public class World extends Place {

	public Engine engine;
	public transient WorldMap worldMap;
	
	public TreeLocator treeLocator = null;
	
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
	
	public HashMap<String, Geography> geographies;
	public HashMap<String, ArrayList<Geography>> geographyCache = new HashMap<String, ArrayList<Geography>>();
	public HashMap<String, Water> waters;
	public HashMap<String, Political> politicals;
	public HashMap<String, Economic> economics;

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
		treeLocator = new TreeLocator(this);
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
		economics = new HashMap<String, Economic>();
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
	

	public ArrayList<SurfaceHeightAndType[]> getSurfaceData(int worldX, int worldZ)
	{
		ArrayList<SurfaceHeightAndType[]> list = new ArrayList<SurfaceHeightAndType[]>();
		int worldY = getSeaLevel(1);
		for (Geography geo : geographies.values()) {
			if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
			{
				long t0 = 0;
				t0 = System.currentTimeMillis();
				Cube geoCube = geo.getCube(-1, worldX, worldY, worldZ, false);
				perf_geo_t0+=System.currentTimeMillis()-t0;
				collectCubes(geoCube,false);
				if (geo instanceof Surface)
				{
					t0 = System.currentTimeMillis();
					SurfaceHeightAndType[] surf = ((Surface)geo).getPointSurfaceData(worldX, worldZ, false);
					if (surf!=null && surf.length>0)
						list.add(surf);
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
	
	HashMap<Geography,SurfaceHeightAndType> tempGeosForSurface = new HashMap<Geography,SurfaceHeightAndType>();
	
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
	
	public Cube getCube(Time localTime, long key, int worldX, int worldY, int worldZ, boolean farView) {

		if (WORLD_IS_GLOBE) {
			worldX = shrinkToWorld(worldX);
			worldZ = shrinkToWorld(worldZ);
		}
		int CONST_FARVIEW = farView?J3DCore.FARVIEW_GAP:1;
		
		if (boundaries.isInside(worldX, worldY, worldZ))
		{
			long t0 = System.currentTimeMillis();
			//System.out.println("ECONOMICS SIZE: "+economics.size());
			/*int worldXProbe = worldX/PROBE_DISTANCE;
			int worldYProbe = worldY/PROBE_DISTANCE;
			int worldZProbe = worldZ/PROBE_DISTANCE;
			boolean newProbe = false;
			if (worldXProbe!=lastXProbe || worldYProbe!=lastYProbe || worldZProbe!=lastZProbe )
			{
				lastXProbe = worldXProbe;
				lastYProbe = worldYProbe;
				lastZProbe = worldZProbe;
				lastKey = Boundaries.getKey(lastXProbe, lastYProbe, lastZProbe);
				hsProvedToBeNear = provedToBeNear.get(lastKey);
				hsProvedToBeAway = provedToBeAway.get(lastKey);
				if (hsProvedToBeNear == null)
				{
					hsProvedToBeNear = new HashSet<Object>();
					provedToBeNear.put(lastKey, hsProvedToBeNear);
					hsProvedToBeAway = new HashSet<Object>();
					provedToBeAway.put(lastKey, hsProvedToBeAway);
					newProbe = true;
				}
			} 
			
			if (!newProbe)
			{
				for (Object o:hsProvedToBeNear)
				{
					Economic eco = (Economic)o;
					if (eco.getBoundaries().isInside(worldX, worldY, worldZ)) {
						perf_eco_t0+=System.currentTimeMillis()-t0;
						return eco.getCube(key, worldX, worldY, worldZ, farView);
					}
				}
				for (Object o:hsProvedToBeAway)
				{
					Economic eco = (Economic)o;
					if (eco.getBoundaries().changed())
					{
						eco.getBoundaries().changeAcknowledged();
						if (eco.getBoundaries().isNear(lastXProbe, lastYProbe, lastZProbe))
						{
							provedToBeAway.remove(eco);
							if (eco.getBoundaries().isInside(worldX, worldY, worldZ)) {
								perf_eco_t0+=System.currentTimeMillis()-t0;
								return eco.getCube(key, worldX, worldY, worldZ, farView);
							}
						}
						
					}
					
				}
			} else
			{
				// cleaning cache's beginning in removal list, 
				// refreshing newly probed to the end of the list removalList...
				if (probeCacheRemovalList.contains(lastKey))
				{
					probeCacheRemovalList.remove(probeCacheRemovalList.indexOf(lastKey));
				}
				probeCacheRemovalList.add(lastKey);
				while (probeCacheRemovalList.size()>5) {
					Long removed = probeCacheRemovalList.remove(0);
					provedToBeAway.remove(removed);
					provedToBeNear.remove(removed);
				}
				
				for (Economic eco : economics.values()) {
					if (eco.getBoundaries().isNear(lastXProbe, lastYProbe, lastZProbe))
					{
						System.out.println("### IS NEAR ! "+eco.id+" "+" "+worldX+" - "+worldY+" - "+worldZ+" | "+eco.origoX+" "+eco.origoZ+ " -- "+ eco.boundaries.limitXMin+ " "+eco.boundaries.limitXMax+" / "+eco.boundaries.limitYMin+" "+eco.boundaries.limitYMax+" / "+eco.boundaries.limitZMin+" "+eco.boundaries.limitZMax);
						hsProvedToBeNear.add(eco);
					} else
					{
						//System.out.println("### IS NOT NEAR ! "+eco.id+" "+" "+worldX+" - "+worldY+" - "+worldZ+" | "+eco.origoX+" "+eco.origoZ+ " -- "+ eco.boundaries.limitXMin+ " "+eco.boundaries.limitXMax+" / "+eco.boundaries.limitYMin+" "+eco.boundaries.limitYMax+" / "+eco.boundaries.limitZMin+" "+eco.boundaries.limitZMax);
						//System.out.println("## PROVED TO BE AWAY: "+eco);
						hsProvedToBeAway.add(eco);
					}
				}
				for (Object o:hsProvedToBeNear)
				{
					Economic eco = (Economic)o;
					if (eco.getBoundaries().isInside(worldX, worldY, worldZ)) {
						perf_eco_t0+=System.currentTimeMillis()-t0;
						return eco.getCube(key, worldX, worldY, worldZ, farView);
					}
				}
			}*/
			
			ArrayList<Object> economicsList = treeLocator.getElements(worldX, worldY, worldZ);
			if (economicsList!=null)
			for (Object o:economicsList)
			{
				Economic eco = (Economic)o;
				if (eco.getBoundaries().isInside(worldX, worldY, worldZ)) {
					perf_eco_t0+=System.currentTimeMillis()-t0;
					return eco.getCube(key, worldX, worldY, worldZ, farView);
				}
			}
			
			perf_eco_t0+=System.currentTimeMillis()-t0;
			//Cube retCube = null;
			currentMerged = null;
			overLappers.clear();
			finalRounders.clear();
			boolean insideGeography = false;
			tempGeosForSurface.clear();

			for (Geography geo : geographies.values()) {
				//System.out.print("-!");
				if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
				{
					insideGeography = true;
					
					t0 = System.currentTimeMillis();
					Cube geoCube = geo.getCube(key, worldX, worldY, worldZ, farView);
					perf_geo_t0+=System.currentTimeMillis()-t0;
					collectCubes(geoCube,false);
					if (geo instanceof Surface)
					{
						t0 = System.currentTimeMillis();
						SurfaceHeightAndType[] surf = ((Surface)geo).getPointSurfaceData(worldX, worldZ, farView);
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
									geoCube.canContain = true;
									Cube floraCube = null;
									t0 = System.currentTimeMillis();
									floraCube = geo.getFloraCube(worldX, worldY, worldZ, conditions, localTime, geoCube.steepDirection!=SurfaceHeightAndType.NOT_STEEP);
									perf_flora_t0+=System.currentTimeMillis()-t0;
									if (floraCube!=null)
									{
										floraCube.internalCube = geoCube.internalCube;
										collectCubes(floraCube,true);
									} 
									else 
									{
										if (geoCube.internalCube) 
										{
										} else 
										{
											// outside green ground appended
											collectCubes(new Cube(this,GROUND,worldX,worldY,worldZ),false);
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
			mergeCubes();
			
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
							if (worldY>=bottom&&worldY<=y)
							{
								Cube c = w.getWaterCube(worldX, worldY, worldZ, currentMerged, s, farView);
								if (currentMerged!=null && currentMerged.overwrite) {
									collectCubes(c,false);
									c = mergeCubes();
								}
								if (c!=null)
									return c;
							}
						}
					} else
					{
						continue; // yes, we must look for the next water if there's one!
					}
				}
			}
			perf_water_t0+=System.currentTimeMillis()-t0;
			if (insideGeography) return currentMerged;

			// not in geography, return null
			return null;
			//return worldY==worldGroundLevel?new Cube(this,OCEAN,worldX,worldY,worldZ):null; -- world generation made this deprecated
		}
		else return null;
	}

	public ArrayList<Cube> overLappers = new ArrayList<Cube>();
	public ArrayList<Cube> finalRounders = new ArrayList<Cube>();
	
	public Cube currentMerged;
	
	public void collectCubes(Cube cube,boolean finalRound)
	{
		if (cube!=null && cube.onlyIfOverlaps) {
			overLappers.add(cube);
			return;
		}
		if (cube!=null && finalRound) {
			finalRounders.add(cube);
			return;
		}
		if (cube!=null && !cube.onlyIfOverlaps && currentMerged==null) {
			currentMerged=cube;
		} else
		if (cube!=null && !cube.onlyIfOverlaps && currentMerged!=null)
		{
			currentMerged = appendCube(currentMerged, cube, cube.x, cube.y, cube.z);
		}
		
	}
	public Cube mergeCubes()
	{
		if (currentMerged==null) return currentMerged;
		for (Cube cube:overLappers)
		{
			currentMerged = appendCube(cube,currentMerged, cube.x, cube.y, cube.z);
		}
		for (Cube cube:finalRounders)
		{
			currentMerged = appendCube(currentMerged, cube, cube.x, cube.y, cube.z);
		}
		overLappers.clear();
		finalRounders.clear();
		return currentMerged;
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
	
	/**
	 * If size or location changes this must be called to update treelocator.
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param zMin
	 * @param zMax
	 * @param e
	 */
	public void updateEconomy(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax, Economic e)
	{
		treeLocator.updateEconomic(xMin, xMax, yMin, yMax, zMin, zMax, e);
	}
	
	public void addEconomy(Economic e)
	{
		treeLocator.addEconomic(e);
		economics.put(e.id, e);
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
		economics.clear();
		geographyCache.clear();
		waters.clear();
		overLappers.clear();
		politicals.clear();
	}
}
