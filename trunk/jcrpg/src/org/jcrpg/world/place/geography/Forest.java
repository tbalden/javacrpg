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

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.geography.forest.Bushes;
import org.jcrpg.world.place.geography.forest.Clearing;

public class Forest extends Geography {

	public static final String TYPE_FOREST = "FOREST";
	public static final SideSubType SUBTYPE_TREE = new SideSubType(TYPE_FOREST+"_TREE");
	public static final SideSubType SUBTYPE_TREE_2 = new SideSubType(TYPE_FOREST+"_TREE_2");
	public static final SideSubType SUBTYPE_GRASS = new SideSubType(TYPE_FOREST+"_GRASS");

	public HashMap<String, Clearing>clearings;
	public HashMap<String, Bushes>bushes;
	
	public int groundLevel;
	public int magnification;
	private int worldGroundLevel;
	
	public Forest(String id, Place parent, PlaceLocator loc, int groundLevel, int magnification) {
		super(id, parent, loc);
		clearings = new HashMap<String, Clearing>();
		bushes = new HashMap<String, Bushes>();
		this.groundLevel = groundLevel;
		this.magnification = magnification;
		worldGroundLevel=groundLevel*magnification;
	}

	static Side[][] TREE = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side(TYPE_FOREST,SUBTYPE_GRASS),new Side(TYPE_FOREST,SUBTYPE_TREE)} };
	static Side[][] TREE_2 = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side(TYPE_FOREST,SUBTYPE_GRASS),new Side(TYPE_FOREST,SUBTYPE_TREE_2)} };
	static Side[][] GRASS = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side(TYPE_FOREST,SUBTYPE_GRASS)} };

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		Place[] places = getDirectSubPlacesForCoordinates(worldX, worldY, worldZ, new HashMap[]{clearings,bushes});
		for (Place place : places) {
			return place.getCube(worldX, worldY, worldZ);
		}
		return new Cube(this,worldY==worldGroundLevel?((worldX+worldZ)%3==0?GRASS:((worldX+worldZ)%2==0?TREE:TREE_2)):EMPTY,worldX,worldY,worldZ);
	}
	
	

}
