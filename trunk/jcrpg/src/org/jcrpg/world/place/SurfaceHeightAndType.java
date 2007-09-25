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

public class SurfaceHeightAndType {

	
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
	public SurfaceHeightAndType(int surfaceY, boolean canContain,int steepDirection) {
		super();
		this.surfaceY = surfaceY;
		this.canContain = canContain;
		this.steepDirection = steepDirection;
	}
	
}
