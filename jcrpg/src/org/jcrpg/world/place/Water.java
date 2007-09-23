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

	public Water(String id, Place parent, PlaceLocator loc) {
		super(id, parent, loc);
	}

	public abstract boolean isWaterPoint(int x,int y, int z);
	public abstract int getDepth(int x,int y, int z);
	public abstract Cube getWaterCube(int x, int y, int z, Cube geoCube);

}
