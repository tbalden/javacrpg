/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.place.geography;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;

import com.jme.math.FastMath;

public class River extends Geography{

	public static final String TYPE_RIVER = "RIVER";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_RIVER+"_WATER");

	static Side[] WATER = {new Side(TYPE_RIVER,SUBTYPE_WATER)};
	static Side[][] RIVER_WATER = new Side[][] { null, null, null,null,null,WATER };
	//
	public int curvedness = 1;
	//
	public int width = 2;
	//
	public int depth = 2;
	// where the river begins
	public int startSide = 0;
	public int endSide = 2;

	int magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ;
	int realMiddleX, realMiddleZ;
	private int worldGroundLevel;
	
	public River(String id, Place parent, PlaceLocator loc, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception {
		super(id, parent, loc);
		this.magnification = magnification;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		realMiddleX = sizeX*magnification/2;
		realMiddleZ = sizeZ*magnification/2;
		setBoundaries(BoundaryUtils.createCubicBoundaries(magnification, sizeX, sizeY, sizeZ, origoX, origoY, origoZ));
	}
	
	@Override
	public Cube getCube(int x,int y,int z)
	{
		// replace coordinates based on startSide
		// TODO
		if ( FastMath.abs(startSide-endSide) == 2 ) {
			
			int checkX = realMiddleX + origoX*magnification;
			if (x>=checkX-width && x<=checkX+width)
			{
				//System.out.println(" RIVER !!!!!!!!!!! ");
				Cube c = new Cube (this,RIVER_WATER,x,y,z,SurfaceHeightAndType.NOT_STEEP);
				c.onlyIfOverlaps = true;
				c.overwrite = true;
				c.overwritePower = 1; // TODO the cave is not good with this!!
				return c;
			}
		}
		
		if (true)
			return null;//new Cube();
		
		return null;
	}

}
