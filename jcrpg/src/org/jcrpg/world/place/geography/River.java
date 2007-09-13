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
import org.jcrpg.space.sidetype.Swimming;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public class River extends Place{

	public static final String TYPE_RIVER = "RIVER";
	public static final Swimming SUBTYPE_WATER = new Swimming(TYPE_RIVER+"_WATER");
	
	public int curvedness = 1;
	public int width = 2;
	public int depth = 2;
	public int numberOfBranches = 1;
	//public int[]  
	
	public River(String id, Place parent, PlaceLocator loc) {
		super(id, parent, loc);
	}
	
	public Cube getRiverCube(int x,int y,int z)
	{
		if (true)
			return null;
		
		return null;
	}

}
