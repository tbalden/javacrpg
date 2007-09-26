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

package org.jcrpg.world.place.geography;

import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.Surface;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.forest.Bushes;
import org.jcrpg.world.place.geography.forest.Clearing;
import org.jcrpg.world.time.Time;

public class Forest extends Geography implements Surface {

	public static final String TYPE_FOREST = "FOREST";
	public static final SideSubType SUBTYPE_FOREST = new SideSubType(TYPE_FOREST+"_FOREST");

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

	static Side[][] FOREST = new Side[][] { null, null, null,null,null,{new Side(TYPE_FOREST,SUBTYPE_FOREST)} };

	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		Place[] places = getDirectSubPlacesForCoordinates(worldX, worldY, worldZ, new HashMap[]{clearings,bushes});
		for (Place place : places) {
			return place.getCube(worldX, worldY, worldZ);
		}
		
		if (worldY!=worldGroundLevel) return new Cube(this,EMPTY,worldX,worldY,worldZ);
		Cube base = new Cube(this,FOREST,worldX,worldY,worldZ);
		return base;
	}

	SurfaceHeightAndType cachedType = null;

	public SurfaceHeightAndType getPointSurfaceData(int worldX, int worldZ) {
		if (cachedType==null) cachedType = new SurfaceHeightAndType(worldGroundLevel,true,SurfaceHeightAndType.NOT_STEEP); 
		return cachedType; 
	}
	
	

}
