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
import java.util.HashMap;

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
	public WorldMap worldMap;
	
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
	
	


	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		int _worldX = worldX;
		int _worldY = worldY;
		int _worldZ = worldZ;
		if (WORLD_IS_GLOBE) {
			_worldX = shrinkToWorld(_worldX);
			_worldZ = shrinkToWorld(_worldZ);
		}
		Time localTime = engine.getWorldMeanTime().getLocalTime(this, _worldX, _worldY, _worldZ);
		return getCube(localTime,worldX,worldY,worldZ);
	}
	
	HashMap<Geography,SurfaceHeightAndType> tempGeosForSurface = new HashMap<Geography,SurfaceHeightAndType>();
	
	public Cube getCube(Time localTime, int worldX, int worldY, int worldZ) {

		if (WORLD_IS_GLOBE) {
			worldX = shrinkToWorld(worldX);
			worldZ = shrinkToWorld(worldZ);
		}
		
		if (boundaries.isInside(worldX, worldY, worldZ))
		{
			for (Economic eco : economics.values()) {
				if (eco.getBoundaries().isInside(worldX, worldY, worldZ))
					return eco.getCube(worldX, worldY, worldZ);
			}
			Cube retCube = null;
			currentMerged = null;
			overLappers.clear();
			boolean insideGeography = false;
			tempGeosForSurface.clear();
			//System.out.println(geographies.values().size());
			ArrayList<Geography> cachedOnes = geographyCache.get(generatePositionCacheKey(worldX, worldY, worldZ, lossFactor));
			//if (cachedOnes!=null) System.out.println(cachedOnes.size());
			//if (cachedOnes!=null)
			//for (Geography geo : cachedOnes) {
			for (Geography geo : geographies.values()) {
				//System.out.print("-!");
				if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
				{
					/*try {
						throw new Exception("--");
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}*/
					insideGeography = true;
					Cube geoCube = geo.getCube(worldX, worldY, worldZ);
					collectCubes(geoCube);
					if (geoCube!=null && geo instanceof Surface)
					{
						SurfaceHeightAndType[] surf = ((Surface)geo).getPointSurfaceData(worldX, worldZ);
						for (int surfCount = 0; surfCount<surf.length; surfCount++) {
							if (surf!=null && surf[surfCount].canContain) {
								// collecting surfaces that can contain, e.g. waters
								tempGeosForSurface.put(geo,surf[surfCount]);
							}
							if (worldY==surf[surfCount].surfaceY && surf[surfCount].canContain)
							{
								// this can cotain things upon it, do the clima and flora... 
								CubeClimateConditions conditions = getCubeClimateConditions(localTime,worldX, worldY, worldZ, geoCube.internalCube);
								Cube floraCube = null;
								floraCube = geo.getFloraCube(worldX, worldY, worldZ, conditions, localTime, geoCube.steepDirection!=SurfaceHeightAndType.NOT_STEEP);
								if (floraCube!=null)
								{
									Cube newCube = new Cube(geoCube,floraCube,worldX,worldY,worldZ,geoCube.steepDirection);
									collectCubes(newCube);
									//retCube = appendCube(retCube, newCube, worldX, worldY, worldZ);									
									/*try {
										retCube.internalCube = geoCube.internalCube;
									} catch (Exception ex)
									{
										System.out.println("!! --"+geo.id);
									}*/
								} 
								else 
								{
									if (geoCube.internalCube) {
										//collectCubes(geoCube);
										//retCube = appendCube(retCube, geoCube, worldX, worldY, worldZ);
										/*try {
											retCube.internalCube = geoCube.internalCube;
										} catch (Exception ex)
										{
											System.out.println("!! --"+geo.id);
											//ex.printStackTrace();
										}*/
									} else 
									{
										// outside green ground appended
										Cube newCube = new Cube(geoCube,new Cube(this,GROUND,worldX,worldY,worldZ),worldX,worldY,worldZ,geoCube.steepDirection);
										newCube.internalCube = geoCube.internalCube;
										retCube = appendCube(retCube, newCube, worldX, worldY, worldZ);
										collectCubes(newCube);
									}
									
								}
							} else 
							{
								//collectCubes(ge)
								//retCube = appendCube(retCube, geoCube, worldX, worldY, worldZ);
							}
						}
					} else 
					{
						//retCube = appendCube(retCube, geoCube, worldX, worldY, worldZ);
					}
				}
			}
			//if (insideGeography) 
			{
				// waters
				
				for (Water w : waters.values()) {
					if (w.boundaries.isInside(worldX, worldY, worldZ)) 
					{
						//System.out.println("WATER INSIDE: "+w.id);
						if (w.isWaterPoint(worldX, worldY, worldZ))
						{
							for (SurfaceHeightAndType s:tempGeosForSurface.values())
							{
								int y = s.surfaceY;
								int depth = w.getDepth(worldX, worldY, worldZ);
								int bottom = y - depth;
								if (worldY>=bottom&&worldY<=y)
								{
									Cube c = w.getWaterCube(worldX, worldY, worldZ, retCube, s);
									if (currentMerged.overwrite) {
										collectCubes(c);
										//c = appendCube(retCube, c, worldX, worldY, worldZ);
										c = mergeCubes();
									}
									
									return c;
								}
							}
						} else
						{
							continue; // yes, we must look for the next water if there's one!
						}
					}
				}
			}
			if (insideGeography) return mergeCubes();

			// not in geography, return ocean
			return worldY==worldGroundLevel?new Cube(this,OCEAN,worldX,worldY,worldZ):null;
		}
		else return null;
	}

	//public ArrayList<Cube> normal = new ArrayList<Cube>();
	public ArrayList<Cube> overLappers = new ArrayList<Cube>();
	
	public Cube currentMerged;
	
	public void collectCubes(Cube cube)
	{
		if (cube!=null && !cube.onlyIfOverlaps && currentMerged==null) {
			currentMerged=cube;
		} else
		if (cube!=null && !cube.onlyIfOverlaps)
		{
			if (cube!=null)
				currentMerged = appendCube(currentMerged, cube, cube.x, cube.y, cube.z);
		}
		if (cube!=null && cube.onlyIfOverlaps) overLappers.add(cube);
	}
	public Cube mergeCubes()
	{
		if (currentMerged==null) return currentMerged;
		for (Cube cube:overLappers)
		{
			System.out.println(currentMerged.toString());
			System.out.println(cube.toString());
			currentMerged = appendCube(currentMerged, cube, cube.x, cube.y, cube.z);
		}
		return currentMerged;
	}
	
	public Cube appendCube(Cube orig, Cube newCube, int worldX, int worldY, int worldZ)
	{
		if (orig!=null && newCube!=null) {
			return new Cube(orig,newCube,worldX,worldY,worldZ,orig.steepDirection);
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
	
	public void addGeography(Geography g)
	{
		geographies.put(g.id, g);
		/*String[] keys = g.generatePositionCacheKeys(lossFactor);
		for (String key : keys)
		{
			ArrayList<Geography> geos = geographyCache.get(key);
			if (geos==null)
			{
				geos = new ArrayList<Geography>();
				geographyCache.put(key, geos);
			}
			geos.add(g);
		}*/
	}

}
