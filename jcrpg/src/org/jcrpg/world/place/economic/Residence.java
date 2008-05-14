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

package org.jcrpg.world.place.economic;

import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public abstract class Residence extends Economic {

	public Residence(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner)  throws Exception {
		super(id,soilGeo,parent, loc, homeBoundaries, owner);
		this.origoX = origoX;this.origoY = origoY;this.origoZ = origoZ;
		this.sizeX = sizeX;this.sizeY = sizeY;this.sizeZ = sizeZ;
		this.groundLevel = groundLevel;
		boundaries = BoundaryUtils.createCubicBoundaries(1, sizeX, sizeY, sizeZ, origoX, origoY, origoZ);
		boundaries.boundaryPlace = this;
	}
	
	public Residence()
	{
		super(null,null,null,null,null,null);
	}
	
	public abstract Residence getInstance(String id, Geography soilGeo, Place parent, PlaceLocator loc, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ, int groundLevel, DistanceBasedBoundary homeBoundaries, EntityInstance owner);
	
	

}
