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

import com.jme.bounding.BoundingSphere;
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
	
	public static int[] zero = new int[]{0,0};
	
	/**
	 * Returns two percent values for each boundary representing how big portion of their radius is inside the other. 
	 * @param one
	 * @param two
	 * @return
	 */
	public static int[] getCommonRadiusRatios(DistanceBasedBoundary one, DistanceBasedBoundary two)
	{
		DistanceBasedBoundary bigger, smaller;
		if (one.getRadiusInRealCubes()>two.getRadiusInRealCubes())
		{
			bigger = one; smaller = two;
		} else
		{
			bigger = two; smaller = one; 
		}
		
		int common_distance = 0;
		
		int dist = (int)one.pv.distance(two.pv);
		
		if ( dist + smaller.getRadiusInRealCubes()<=bigger.getRadiusInRealCubes())
		{
			//System.out.println("smaller is fully inside");
			// smaller is fully inside
			common_distance = (smaller.getRadiusInRealCubes()*2);
			
		} else
		if (dist<=bigger.getRadiusInRealCubes() && dist+smaller.getRadiusInRealCubes()>bigger.getRadiusInRealCubes())
		{
			//System.out.println("smaller is more than half inside");
			// smaller is more than half inside
			common_distance = smaller.getRadiusInRealCubes() + (bigger.getRadiusInRealCubes()  - dist);
		} else
		if (dist-smaller.getRadiusInRealCubes()<=bigger.getRadiusInRealCubes())
		{
			//System.out.println("smaller is half or less inside");
			// smaller is half or less inside
			common_distance = smaller.getRadiusInRealCubes() - (dist - bigger.getRadiusInRealCubes());
		} else
		{
			// no intersection
			return zero;
		}
		//System.out.println("CD: "+common_distance);
		//  |        .    |    .  |  | 		
		return new int[]{ common_distance*50 /  one.getRadiusInRealCubes(), common_distance*50 /  two.getRadiusInRealCubes()};
		
	}
	
	public static Vector3f intersects(DistanceBasedBoundary[] boundaries)
	{
		Vector3f sum = new Vector3f(0,0,0);
		for (DistanceBasedBoundary b:boundaries)
		{
			sum.addLocal(b.pv);
		}
		sum.divideLocal(boundaries.length);
		
		for (DistanceBasedBoundary b:boundaries)
		{
			if (!b.isInside((int)sum.x, (int)sum.y, (int)sum.z)) {
				return null;
			}
		}
		
		return sum;
	}
	
	public static void main(String[] args) throws Exception 
	{
		DistanceBasedBoundary d1 = new DistanceBasedBoundary(new World("id",null,100,100,100,100),10,0,10,10);
		DistanceBasedBoundary d2 = new DistanceBasedBoundary(new World("id",null,100,100,100,100),20,0,10,2);
		int[] ratios = getCommonRadiusRatios(d1, d2);
		System.out.println("RATIOS: "+ratios[0]+" "+ratios[1]);
	}

}
