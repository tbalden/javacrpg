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

package org.jcrpg.world.place.water;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;

public class Lake extends Water {

	public static final String TYPE_LAKE = "LAKE";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_LAKE+"_WATER");

	static Side[] WATER = {new Side(TYPE_LAKE,SUBTYPE_WATER)};

	static Side[][] LAKE_WATER = new Side[][] { null, null, null,null,null,WATER };

	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;

	public int depth = 1;
	int noWaterPercentage = 0;
	private int worldGroundLevel;
	int groundLevel;
	
	int centerX, centerZ, realSizeX, realSizeZ;

	public Lake(String id, Place parent, PlaceLocator loc, int groundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int depth, int noWaterPercentage) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		this.depth = depth;
		centerX = sizeX*magnification/2;
		centerZ = sizeZ*magnification/2;
		realSizeX = sizeX*magnification;
		realSizeZ = sizeZ*magnification;
		
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
		this.groundLevel = groundLevel;
		worldGroundLevel=groundLevel*magnification;
	}

	@Override
	public int getDepth(int x, int y, int z) {
		return depth;
	}

	@Override
	public Cube getWaterCube(int x, int y, int z, Cube geoCube,
			SurfaceHeightAndType surface) {
		if (y==worldGroundLevel) 
		{
			return new Cube (this,LAKE_WATER,x,y,z,SurfaceHeightAndType.NOT_STEEP);
		}
		return new Cube (this,EMPTY,x,y,z,SurfaceHeightAndType.NOT_STEEP);
	}

	@Override
	public boolean isWaterPoint(int x, int y, int z) {
		int localX = x-realSizeX;
		int localY = y-worldGroundLevel;
		int localZ = z-realSizeZ;
		if (y==worldGroundLevel) 
		{
			return true;
		}
		return false;
	}

}
