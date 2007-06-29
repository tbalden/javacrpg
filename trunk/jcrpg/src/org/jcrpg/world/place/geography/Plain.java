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
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Surface;
import org.jcrpg.world.place.SurfaceHeightAndType;

public class Plain extends Geography implements Surface {

	public static final String TYPE_PLAIN = "PLAIN";
	public static final SideSubType SUBTYPE_GRASS = new GroundSubType(TYPE_PLAIN+"_GRASS");
	public static final SideSubType SUBTYPE_TREE = new SideSubType(TYPE_PLAIN+"_TREE");
	
	public int groundLevel;
	public int magnification;
	private int worldGroundLevel;
	
	public Plain(String id, Place parent,PlaceLocator loc, int groundLevel, int magnification) {
		super(id, parent, loc);
		this.groundLevel = groundLevel;
		this.magnification = magnification;
		worldGroundLevel=groundLevel*magnification;
	}


	static Side[][] GRASS = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GRASS)} };
	static Side[][] TREE = new Side[][] { null, null, null,null,null,{new Side(TYPE_PLAIN,SUBTYPE_GRASS),new Side(TYPE_PLAIN,SUBTYPE_TREE)} };
	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		return new Cube(this, worldY==worldGroundLevel?(worldX%10==0&&worldZ%10==0?TREE:GRASS):EMPTY,worldX,worldY,worldZ);
	}

	public SurfaceHeightAndType getPointSurfaceData(int worldX, int worldZ) {
		return new SurfaceHeightAndType(worldGroundLevel,true);
	}
	
}
