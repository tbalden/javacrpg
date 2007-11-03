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

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.flora.FloraContainer;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.orbiter.WorldOrbiterHandler;
import org.jcrpg.world.time.Time;

public class World extends Place {

	public Engine engine;
	
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
	
	public int realSizeX, sizeYMulMag, realSizeZ;

	public World(String id, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ) throws Exception {
		super(id, null, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		realSizeX = sizeX*magnification;
		sizeYMulMag = sizeY*magnification;
		realSizeZ = sizeZ*magnification;
		worldGroundLevel = (sizeY*magnification/2);
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, 0, 0, 0));
		geographies = new HashMap<String, Geography>();
		waters = new HashMap<String, Water>();
		politicals = new HashMap<String, Political>();
		economics = new HashMap<String, Economic>();
	}
	
	
	public int getSeaLevel(int magnification)
	{
		return ((sizeY*this.magnification)/2)/magnification;
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
		Time localTime = engine.getWorldMeanTime().getLocalTime(this, worldX, worldY, worldZ);
		return getCube(localTime,worldX,worldY,worldZ);
	}
	
	HashMap<Geography,SurfaceHeightAndType> tempGeosForSurface = new HashMap<Geography,SurfaceHeightAndType>();
	
	public Cube getCube(Time localTime, int worldX, int worldY, int worldZ) {
		if (WORLD_IS_GLOBE) {
			
			if (worldX<0)
			{
				//System.out.println("WORLDX - "+worldX);
				worldX = realSizeX+worldX;
			}
			if (worldZ<0)
			{
				worldZ = realSizeZ+worldZ;
			}
			worldX = worldX%(realSizeX);
			worldZ = worldZ%(realSizeZ); // TODO Houses dont display going round the globe?? Static fields in the way or what?
		}
		
		if (boundaries.isInside(worldX, worldY, worldZ))
		{
			for (Economic eco : economics.values()) {
				if (eco.getBoundaries().isInside(worldX, worldY, worldZ))
					return eco.getCube(worldX, worldY, worldZ);
			}
			Cube retCube = null;
			boolean insideGeography = false;
			tempGeosForSurface.clear();
			for (Geography geo : geographies.values()) {	
				if (geo.getBoundaries().isInside(worldX, worldY, worldZ))
				{
					insideGeography = true;
					Cube geoCube = geo.getCube(worldX, worldY, worldZ);
					if (geoCube!=null && geo instanceof Surface)
					{
						SurfaceHeightAndType[] surf = ((Surface)geo).getPointSurfaceData(worldX, worldZ);
						if (surf!=null && surf[0].canContain) { // TODO multilevel surface data processing!!
							// collecting surfaces that can contain, e.g. waters
							tempGeosForSurface.put(geo,surf[0]);
						}
						//if (geo instanceof Mountain)
							//System.out.println("CUBE = "+r+" SURF = "+surf.surfaceY);
						if (worldY==surf[0].surfaceY && surf[0].canContain)
						{
							// this can cotain things upon it, do the clima and flora... 
							CubeClimateConditions conditions = getCubeClimateConditions(localTime,worldX, worldY, worldZ);
							Cube floraCube = null;
							floraCube = geo.getFloraCube(worldX, worldY, worldZ, conditions, localTime, geoCube.steepDirection!=SurfaceHeightAndType.NOT_STEEP);
							if (floraCube!=null)
							{
								Cube newCube = new Cube(geoCube,floraCube,worldX,worldY,worldZ,geoCube.steepDirection);
								retCube = appendCube(retCube, newCube, worldX, worldY, worldZ);
							} 
							else 
							{
								Cube newCube = new Cube(geoCube,new Cube(this,GROUND,worldX,worldY,worldZ),worldX,worldY,worldZ,geoCube.steepDirection);
								retCube = appendCube(retCube, newCube, worldX, worldY, worldZ);
							}
						} else 
						{
							retCube = appendCube(retCube, geoCube, worldX, worldY, worldZ);
						}
					} else 
					{
						retCube = appendCube(retCube, geoCube, worldX, worldY, worldZ);
					}
				}
			}
			//if (insideGeography) 
			{
				// waters
				
				for (Water w : waters.values()) {
					if (w.boundaries.isInside(worldX, worldY, worldZ)) 
					{
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
									return c;
								}
							}
						}
					}
				}
			}
			if (insideGeography) return retCube;

			// not in geography, return ocean
			return worldY==worldGroundLevel?new Cube(this,OCEAN,worldX,worldY,worldZ):null;
		}
		else return null;
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
	
	

}
