/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.ai.fauna;

import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.World;

/**
 * Distance/position based boundary - tells if a position is inside its radius. Used with moving units e.g.
 * @author pali
 *
 */
public class DistanceBasedBoundary extends Boundaries {

	int gWX, gWY, gWZ;
	
	public int radiusInRealCubes, posX, posY;
	
	public DistanceBasedBoundary(int magnification, World w, int positionX, int positionY, int radiusInRealCubes) {
		super(magnification);
		int wX = w.sizeX;
		int wY = w.sizeY;
		int wZ = w.sizeZ;
		int wMag = w.magnification;
		int gMag = magnification;
		gWX = (wX*wMag)/gMag;
		gWY = (wY*wMag)/gMag;
		gWZ = (wZ*wMag)/gMag;
		
		this.radiusInRealCubes = radiusInRealCubes;
		posX = positionX;
		posY = positionY;
	}

	
	@Override
	public void addCube(int magnification, int x, int y, int z) throws Exception {
		return;
	}

	@Override
	public boolean isInside(int absoluteX, int absoluteY, int absoluteZ) {
		int x = absoluteX / magnification;
		int y = absoluteY / magnification;
		int z = absoluteZ / magnification;
		return false; // TODO 
	}

}
