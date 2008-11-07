/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.world.place.economic.ground;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.economic.EconomicGround;

public class PavedStorageAreaGround extends EconomicGround{
	
	public PavedStorageAreaGround() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PavedStorageAreaGround(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX,
			int origoY, int origoZ, int groundLevel,
			DistanceBasedBoundary homeBoundaries, EntityInstance owner)
			throws Exception {
		super(id, soilGeo, parent, loc, sizeX, sizeY, sizeZ, origoX, origoY, origoZ,
				groundLevel, homeBoundaries, owner);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public boolean overrideGeoHeight() {
		// TODO Auto-generated method stub
		//
		return true;
	}

	@Override
	public EconomicGround getInstance(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)
	{
		try {
			return new PavedStorageAreaGround(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	public static HashMap<Integer, Cube> hmKindCubeOverride = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCubeOverride_FARVIEW = new HashMap<Integer, Cube>();
	
	public static final String TYPE_HOUSE = "WOODEN_HOUSE";
	public static final SideSubType SUBTYPE_EXTERNAL_GROUND = new GroundSubType(TYPE_HOUSE+"_EXTERNAL_GROUND",true);
	public static final SideSubType SUBTYPE_CRATE = new SideSubType(TYPE_HOUSE+"_CRATE");
	public static final SideSubType SUBTYPE_BARREL = new SideSubType(TYPE_HOUSE+"_BARREL");
	public static final SideSubType SUBTYPE_BASKET = new SideSubType(TYPE_HOUSE+"_BASKET");
	public static final SideSubType SUBTYPE_PAVILION = new SideSubType(TYPE_HOUSE+"_PAVILION");
	static Side[][] GROUND = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] GROUND_CRATE = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND),new Side(TYPE_HOUSE,SUBTYPE_PAVILION),new Side(TYPE_HOUSE,SUBTYPE_CRATE)} };
	static Side[][] GROUND_BARREL = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND),new Side(TYPE_HOUSE,SUBTYPE_PAVILION),new Side(TYPE_HOUSE,SUBTYPE_BARREL)} };
	static Side[][] GROUND_BASKET = new Side[][] { null, null, null,null,null,{new Side(TYPE_HOUSE,SUBTYPE_EXTERNAL_GROUND),new Side(TYPE_HOUSE,SUBTYPE_PAVILION),new Side(TYPE_HOUSE,SUBTYPE_BASKET)} };
	
	Cube crate = new Cube(null,GROUND_CRATE,0,0,0,false,true);
	Cube barrel = new Cube(null,GROUND_BARREL,0,0,0,false,true);
	Cube basket = new Cube(null,GROUND_BASKET,0,0,0,false,true);

	static 
	{
		Cube ground = new Cube(null,GROUND,0,0,0,true,true);
		hmKindCubeOverride.put(K_NORMAL_GROUND, ground);
		Cube waterGround = new Cube(null,EXTERNAL_WATER_WOODEN_GROUND,0,0,0,true,false);
		hmKindCubeOverride.put(K_WATER_GROUND, waterGround );		
		/*Cube stepsEast = new Cube(null,STEPS_EAST,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_EAST, stepsEast);
		Cube stepWest = new Cube(null,STEPS_WEST,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_WEST, stepWest);
		Cube stepNorth = new Cube(null,STEPS_NORTH,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_NORTH, stepNorth);
		Cube stepSouth = new Cube(null,STEPS_SOUTH,0,0,0,true,true);
		hmKindCubeOverride.put(K_STEEP_SOUTH, stepSouth);*/

		//hmKindCubeOverride_FARVIEW.put(K_NORMAL_GROUND, new Cube(null,House.EXTERNAL,0,0,0));
	}
	
	public HashMap<Integer, Cube> getOverrideMap()
	{
		return hmKindCubeOverride;
	}
	
	@Override
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		Cube c = farView?hmKindCubeOverride_FARVIEW.get(kind):getOverrideMap().get(kind);
		if (c!=null && kind==K_NORMAL_GROUND)
		{
			int perc = HashUtil.mixPercentage(worldX, worldZ, worldY);
			if (perc%3==0)
			{
				if ((perc+worldY)%8<3)
				{
					return barrel;
				} else
				if ((perc+worldY)%8<5)
				{
					return crate;
				} else
				{
					return basket;
				}
			}
		}
		return c;
	}

	
}