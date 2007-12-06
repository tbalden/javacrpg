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

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Surface;
import org.jcrpg.world.place.SurfaceHeightAndType;

public class Plain extends Geography implements Surface {

	public static final String TYPE_PLAIN = "PLAIN";
	public static final SideSubType SUBTYPE_GROUND = new GroundSubType(TYPE_PLAIN+"_GROUND");
	
	public int groundLevel;
	private int worldGroundLevel;
	

	public Plain(String id, Place parent,PlaceLocator loc, int groundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, boolean fillBoundaries) throws Exception {
		super(id, parent, loc);
		this.groundLevel = groundLevel;
		this.magnification = magnification;
		worldGroundLevel=groundLevel*magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		if (fillBoundaries)
			setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		else
			setBoundaries(new Boundaries(magnification));
	}


	static Side[][] GRASS = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GROUND)} };
	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		return new Cube(this, worldY==worldGroundLevel?GRASS:EMPTY,worldX,worldY,worldZ);
	}

	SurfaceHeightAndType[] cachedType = null;
	
	public SurfaceHeightAndType[] getPointSurfaceData(int worldX, int worldZ) {
		if (cachedType==null) cachedType = new SurfaceHeightAndType[]{new SurfaceHeightAndType(worldGroundLevel,true,SurfaceHeightAndType.NOT_STEEP)}; 
		return cachedType; 
	}
	
}
