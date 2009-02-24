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

package org.jcrpg.world.place.geography;

import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class Forest extends Geography {

	public static final String TYPE_FOREST = "FOREST";
	public static final SideSubType SUBTYPE_FOREST = new SideSubType(TYPE_FOREST+"_FOREST");

	
	public Forest(String id, Place parent, PlaceLocator loc, int worldGroundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc,worldGroundLevel,worldGroundLevel,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,fillBoundaries);
		colorBytes = new byte[] {(byte)80,(byte)145,(byte)80};
	}

	static Side[][] FOREST = new Side[][] { null, null, null,null,null,{new Side(TYPE_FOREST,SUBTYPE_FOREST)} };

	@Override
	protected float getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView)
	{
		Float overrideHeight = overrideHeightForException(worldX, 0, worldZ, farView);
		if (overrideHeight!=null) return overrideHeight;
		//if (x<0 || z<0 || x>=sizeX || z>=sizeZ) return 0;
		float Y = 0;
		Y+=(((((HashUtil.mixPercentage(worldX/5, worldZ/5, 0)))-30)%100)/50f);
		//int ret = Math.min(0,-Y/30); // valley
		// adding some hill thing...
		int x1 = sizeX / 8;
		int z1 = sizeZ / 8;
		
		int x2 = x%(blockSize/4)-(sizeX/8)*3;
		int z2 = z%(blockSize/4)-(sizeZ/8)*3;
		
		int r = sizeX / 8;
		
		float sY = Math.max(0, r*r - ( (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1) ))*5;
		sY/=40f;
		Y+=sY;
		
		//Y+=(((((HashUtil.mixPercentage(worldX/((HashUtil.mixPercentage(worldX/8, worldZ/8, 0)+20)/20), worldZ/((HashUtil.mixPercentage(worldZ/8, worldX/8, 0)+20)/20), 0)))+30)%100)/50);
		//int ret = Math.min(0,-Y/30); // valley
		float ret = Math.max(0,Y); // mountain
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("PLAIN H: "+ret);
		return ret;

	}
	

}
