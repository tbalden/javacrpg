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

package org.jcrpg.world.place.geography;

import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;

public class Plain extends Geography {

	public static final String TYPE_PLAIN = "PLAIN";
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_PLAIN+"_GROUND");

	public Plain(String id, Place parent,PlaceLocator loc, int worldGroundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc,worldGroundLevel,worldGroundLevel+2,magnification,sizeX,sizeY,sizeZ,origoX,origoY,origoZ,fillBoundaries);
	}


	static Side[][] GRASS = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND)} };
	
	SurfaceHeightAndType[] cachedType = null;
	
	@Override
	public int getPointHeight(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ)
	{
		if (overrideHeightForRiver(worldX, 0, worldZ)) return 0;
		if (!boundaries.isInside(worldX, worldGroundLevel, worldZ)) 
		{
			return super.getPointHeight(x, z, sizeX, sizeZ, worldX, worldZ);
		}
		//if (x<0 || z<0 || x>=sizeX || z>=sizeZ) return 0;
		int Y = 0;
		Y+=(((((HashUtil.mixPercentage(worldX/((HashUtil.mixPercentage(worldX/5, worldZ/5, 0)+20)/20), worldZ/((HashUtil.mixPercentage(worldZ/5, worldX/5, 0)+20)/20), 0)))+30)%100)/50);
		//int ret = Math.min(0,-Y/30); // valley
		int ret = Math.max(0,Y); // mountain
		//System.out.println("PLAIN H: "+ret);
		return ret;

	}

	
}
