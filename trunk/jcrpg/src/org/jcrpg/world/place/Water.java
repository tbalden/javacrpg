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
	 * Tells if the given area is within a block with crossing river. 
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return True if river passes.
	 */
	public abstract boolean isWaterBlock(int worldX, int worldY,int worldZ);

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
