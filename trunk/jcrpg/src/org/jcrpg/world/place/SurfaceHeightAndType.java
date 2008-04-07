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

/**
 * Surface information data.
 * @author pali
 *
 */
public class SurfaceHeightAndType {

	public Geography self;
	public int surfaceY;
	public int steepDirection = NOT_STEEP;
	public boolean canContain;
	
	public static final int NOT_STEEP = -1;
	
	/**
	 * Constructor
	 * @param surfaceY On which worldY is the surface.
	 * @param canContain Can it contain things on it.
	 * @param steepDirection Is it a steep surface, and which direction.
	 */
	public SurfaceHeightAndType(Geography self, int surfaceY, boolean canContain,int steepDirection) {
		super();
		this.self = self;
		this.surfaceY = surfaceY;
		this.canContain = canContain;
		this.steepDirection = steepDirection;
	}
	
}
