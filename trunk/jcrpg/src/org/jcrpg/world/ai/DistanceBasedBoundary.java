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

package org.jcrpg.world.ai;

import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.World;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;

/**
 * Distance/position based boundary - tells if a position is inside its radius. Used with moving units e.g.
 * @author pali
 *
 */
public class DistanceBasedBoundary extends Boundaries {

	int gWX, gWY, gWZ;
	
	public int radiusInRealCubes, posX, posY, posZ;
	public Vector3f pv;
	
	public DistanceBasedBoundary(World w, int positionX, int positionY, int positionZ, int radiusInRealCubes) {
		super(1);
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
		posZ = positionZ;
		pv = new Vector3f(posX,0,posZ);
	}

	@Override
	public void addCube(int magnification, int x, int y, int z) throws Exception {
		setPosition(magnification, x, y, z);
	}
	
	/**
	 * Sets the center of the circle of boundary.
	 * @param magnification
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(int magnification, int x, int y, int z) 
	{
		posX = x*magnification;
		posY = y*magnification;
		posZ = z*magnification;
		pv.set(posX,0,posZ);
	}

	@Override
	public boolean isInside(int absoluteX, int absoluteY, int absoluteZ) {
		int x = absoluteX;
		//int y = absoluteY;
		int z = absoluteZ;
		float dist = new Vector3f(x,0,z).distance(pv);
		if (dist<=radiusInRealCubes)
		{
			return true;
		}
		return false;
	}

	public int getRadiusInRealCubes() {
		return radiusInRealCubes;
	}

	public void setRadiusInRealCubes(int radiusInRealCubes) {
		this.radiusInRealCubes = radiusInRealCubes;
	}
	
	public static int getCommonDistance(DistanceBasedBoundary one, DistanceBasedBoundary two)
	{
		
		Vector3f dir = one.pv.subtract(two.pv).normalize();
		
		Vector3f rad1 = dir.mult(one.getRadiusInRealCubes());
		Vector3f point1_1 = one.pv.subtract(rad1);
		Vector3f point1_2 = one.pv.add(rad1);
		
		Vector3f rad2 = dir.mult(one.getRadiusInRealCubes());
		Vector3f point2_1 = two.pv.subtract(rad2);
		Vector3f point2_2 = two.pv.add(rad2);
		
		//Sphere s = null;
		//s.get
		
		
		
		// TODO do this for calculating encounter likeness in Ecology
		int dist = (int)one.pv.distance(two.pv);
		int d1 = dist - one.getRadiusInRealCubes();
		int d2 = dist - two.getRadiusInRealCubes();
		if (d1<two.getRadiusInRealCubes())
		{
			return Math.abs(dist - (d1+d2));
		}
		return 0;
	}

}
