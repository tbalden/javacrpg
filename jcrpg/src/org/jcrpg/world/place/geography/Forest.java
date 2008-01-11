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

package org.jcrpg.world.place.geography;

import java.util.HashMap;

import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.geography.forest.Bushes;
import org.jcrpg.world.place.geography.forest.Clearing;

public class Forest extends Geography {

	public static final String TYPE_FOREST = "FOREST";
	public static final SideSubType SUBTYPE_FOREST = new SideSubType(TYPE_FOREST+"_FOREST");

	public HashMap<String, Clearing>clearings;
	public HashMap<String, Bushes>bushes;
	
	
	public Forest(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc,worldGroundLevel,worldGroundLevel,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,fillBoundaries);
		colorBytes = new byte[] {(byte)70,(byte)115,(byte)70};
		clearings = new HashMap<String, Clearing>();
		bushes = new HashMap<String, Bushes>();
	}

	static Side[][] FOREST = new Side[][] { null, null, null,null,null,{new Side(TYPE_FOREST,SUBTYPE_FOREST)} };

	@Override
	protected int getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView)
	{
		if (overrideHeightForRiver(worldX, 0, worldZ, farView)) return 0;
		//if (x<0 || z<0 || x>=sizeX || z>=sizeZ) return 0;
		int Y = 0;
		Y+=(((((HashUtil.mixPercentage(worldX/5, worldZ/5, 0)))-30)%100)/50);
		//int ret = Math.min(0,-Y/30); // valley
		int ret = Math.max(0,Y); // mountain
		//System.out.println("PLAIN H: "+ret);
		return ret;

	}
	

}
