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

package org.jcrpg.world.place;

import org.jcrpg.space.Cube;


/**
 * Base for waters.
 * @author pali
 *
 */
public abstract class Water extends Geography {

	/**
	 * If noWater is true, the bed of the water should be empty of water.
	 */
	public boolean noWaterInTheBed = false;
	
	public Water(String id, Place parent, PlaceLocator loc) {
		super(id, parent, loc);
	}

	/**
	 * Tells if one cube coordinate is a waterpoint.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public abstract boolean isWaterPoint(int x,int y, int z);
	/**
	 * Returns the depth of the water at a given point.
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public abstract int getDepth(int x,int y, int z);
	/**
	 * Returns the water cube e.g. water surface, water block and the bottom/bed based on coordinates a cube and surface info.
	 * @param x
	 * @param y
	 * @param z
	 * @param geoCube the cube which may be overwritten.
	 * @param surface the surface information at the give x/z point (do depth and such calculation based on this).
	 * @return
	 */
	public abstract Cube getWaterCube(int x, int y, int z, Cube geoCube, SurfaceHeightAndType surface);

}
