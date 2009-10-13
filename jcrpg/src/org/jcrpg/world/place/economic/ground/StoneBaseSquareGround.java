/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.world.place.economic.ground;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.economic.EconomicGround;

public class StoneBaseSquareGround extends EconomicGround{
	
	public StoneBaseSquareGround() {
		super();
		// TODO Auto-generated constructor stub
	}

	public StoneBaseSquareGround(String id, Geography soilGeo, Place parent,
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
			return new StoneBaseSquareGround(id,soilGeo,parent,loc,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,groundLevel, homeBoundaries, owner);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	public static HashMap<Integer, Cube> hmKindCubeOverride = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCubeOverride_FARVIEW = new HashMap<Integer, Cube>();
	
	public static final String TYPE_STONEBASE = "STONEBASE";
	public static final SideSubType SUBTYPE_EXTERNAL_GROUND = new GroundSubType(TYPE_STONEBASE+"_STONEBASE",true);
	public static final SideSubType SUBTYPE_EXTERNAL_GROUND_EMPTY = new GroundSubType(TYPE_STONEBASE+"_EMPTY",true);
	static Side[][] GROUND = new Side[][] { null, null, null,null,null,{new Side(TYPE_STONEBASE,SUBTYPE_EXTERNAL_GROUND)} };
	static Side[][] GROUND_NO_MODEL = new Side[][] { null, null, null,null,null,{new Side(TYPE_STONEBASE,SUBTYPE_EXTERNAL_GROUND_EMPTY)} };
	
	static Cube stoneBase = new Cube(null,GROUND,0,0,0,true,true);

	static 
	{
		stoneBase.walkHeight = 0.3f;;
		Cube groundNoModel = new Cube(null,GROUND_NO_MODEL,0,0,0,true,true);
		groundNoModel.walkHeight = 0.3f;
		hmKindCubeOverride.put(K_NORMAL_GROUND, groundNoModel);
		
	}
	
	public HashMap<Integer, Cube> getOverrideMap()
	{
		return hmKindCubeOverride;
	}
	
	@Override
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		Cube c = farView?hmKindCubeOverride_FARVIEW.get(kind):getOverrideMap().get(kind);
		if (kind!=K_EMPTY)
		{
			boolean stonePlace = (origoX-worldX)%4==0 && (origoZ-worldZ)%4==0;
			if (stonePlace) 
			{
				return stoneBase;
			}
		}
		return c;
	}

	
}
