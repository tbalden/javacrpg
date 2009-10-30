/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.ai;

import org.jcrpg.threed.jme.VectorPool;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.World;

import com.jme.math.Vector3f;

/**
 * Distance/position based boundary - tells if a position is inside its radius. Used with moving units e.g.
 * @author pali
 *
 */
public class DistanceBasedBoundary extends Boundaries {

	int gWX, gWY, gWZ;
	
	public int radiusInRealCubes, posX, posY, posZ;
	public int previousTurnRadiusInRealCubes, previousTurnPosX, previousTurnPosY, previousTurnPosZ;
	public Vector3f pv;
	
	public DistanceBasedBoundary(DistanceBasedBoundary b)
	{
		super(1);
		this.radiusInRealCubes = b.radiusInRealCubes;
		this.gWX = b.gWX;
		this.gWY = b.gWY;
		this.gWZ = b.gWZ;
		this.posX = b.posX;
		this.posY = b.posY;
		this.posZ = b.posZ;
	}
	public DistanceBasedBoundary(World w, int positionX, int positionY, int positionZ, int radiusInRealCubes) {
		super(1);
		if (w!=null) 
		{
			int wX = w.sizeX;
			int wY = w.sizeY;
			int wZ = w.sizeZ;
			int wMag = w.magnification;
			int gMag = magnification;
			gWX = (wX*wMag)/gMag;
			gWY = (wY*wMag)/gMag;
			gWZ = (wZ*wMag)/gMag;
		}
		
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
		previousTurnPosX = posX;
		previousTurnPosY = posY;
		previousTurnPosZ = posZ;
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
		Vector3f v = VectorPool.getVector3f(x, 0, z);
		float dist = v.distance(pv);
		VectorPool.releaseVector3f(v);
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
		this.previousTurnRadiusInRealCubes = radiusInRealCubes;
		this.radiusInRealCubes = radiusInRealCubes;
	}
	
	public static int[][] zero = new int[][]{{0,0},{0,0,0}};
	
	/**
	 * Returns two percent values for each boundary representing how big portion of their radius is inside the other. 
	 * @param one
	 * @param two
	 * @return two int[] arrays: first is the common area ratio for [one in two] and [two in one]; second is the [x,y,z] coordinates of the middle.
	 */
	public static int[][] getCommonRadiusRatiosAndMiddlePoint(DistanceBasedBoundary one, DistanceBasedBoundary two)
	{
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("o: "+one.getRadiusInRealCubes()+" "+two.getRadiusInRealCubes());
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
		
		if (bigger.getRadiusInRealCubes()==0 || smaller.getRadiusInRealCubes()==0) return zero;
		if (dist> bigger.getRadiusInRealCubes()+smaller.getRadiusInRealCubes()) return zero;
		if ( dist + smaller.getRadiusInRealCubes()<=bigger.getRadiusInRealCubes())
		{
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("smaller is fully inside");
			// smaller is fully inside
			common_distance = (smaller.getRadiusInRealCubes()*2);
			
		} else
		if (dist<=bigger.getRadiusInRealCubes() && dist+smaller.getRadiusInRealCubes()>bigger.getRadiusInRealCubes())
		{
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("smaller is more than half inside");
			// smaller is more than half inside
			common_distance = smaller.getRadiusInRealCubes() + (bigger.getRadiusInRealCubes()  - dist);
		} else
		if (dist-smaller.getRadiusInRealCubes()<=bigger.getRadiusInRealCubes())
		{
			//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("smaller is half or less inside");
			// smaller is half or less inside
			common_distance = smaller.getRadiusInRealCubes() - (dist - bigger.getRadiusInRealCubes());
		} else
		{
			// no intersection
			return zero;
		}
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("CD: "+common_distance);
		//  |        .    |    .  |  |
		if (true) {
			Vector3f normalizedDistDirVect = bigger.pv.subtract(smaller.pv).normalize();
			float f = (common_distance * 1f ) / 2f;
			Vector3f middle = smaller.pv.add(normalizedDistDirVect.mult(smaller.getRadiusInRealCubes())).subtract( normalizedDistDirVect.mult(f) );
			return new int[][]{ {common_distance*50 /  one.getRadiusInRealCubes(), common_distance*50 /  two.getRadiusInRealCubes()}, {(int)middle.x, two.posY+((one.posY-two.posY)/2), (int)middle.z}};
		}
		return new int[][]{ {common_distance*50 /  one.getRadiusInRealCubes(), common_distance*50 /  two.getRadiusInRealCubes()}, {0, 0, 0}};
		
	}
	
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println((int)0.86f+" "+(int)0.99f);
		if (true) return;
		DistanceBasedBoundary d1 = new DistanceBasedBoundary(new World("id",null,100,100,100,100),120,0,150,50);
		DistanceBasedBoundary d2 = new DistanceBasedBoundary(new World("id",null,100,100,100,100),163,0,120,6);
		int[][] ratios = getCommonRadiusRatiosAndMiddlePoint(d1, d2);
		System.out.println("RATIOS: "+ratios[0][0]+" "+ratios[0][1] + " "+ratios[1][0]+" "+ratios[1][1]+" "+ratios[1][2]);
	}
	
	public boolean changedInTurn()
	{
		if (previousTurnPosX!=posX||previousTurnPosY!=posY||previousTurnPosZ!=posZ||previousTurnRadiusInRealCubes!=radiusInRealCubes)
			return true;
		return false;
	}

}
