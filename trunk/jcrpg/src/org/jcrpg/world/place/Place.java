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

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.time.Time;

public abstract class Place {

	protected static Side[][] EMPTY = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side()} };

	protected Boundaries boundaries;
	
	Object model;
	

	public Object getModel()
	{
		return model;
	}
	
	
	public Boundaries getBoundaries() {
		return boundaries;
	}


	public void setBoundaries(Boundaries boundaries) {
		this.boundaries = boundaries;
	}


	public boolean loadModelFromFile(){
		return false;
	}
	public boolean generateModel(){
		return false;
	}
	
	public String id;
	
	public PlaceLocator loc;
	
	public Place parent;
	
	public Place(String id, Place parent, PlaceLocator loc)
	{
		this.id = id;
		this.parent = parent;
	}
	
	public Cube getCube(int worldX, int worldY, int worldZ)
	{
		return null;
	}

	/**
	 * Contains any type of place of unique Cube description, the key is valid only for a fixed Cube structure, so the key mus
	 * be calculated based on the fix algorithms that build up the unique Cube structure.
	 */
	protected static HashMap<String,HashMap<String, Cube>> parameteredPlaces = new HashMap<String, HashMap<String,Cube>>();

	/**
	 * The key for a unique Cube structure in the parametedPlaces cache.
	 * @return
	 */
	public String getParameteredKey() {
		return null;
	}
	
	/**
	 * Tries to look up stored Cube structure Area that is valid for the Place's key in the hashmap cache, 
	 * and loads it into the Place object's hmArea. Use it if you are using parametered area type places and overloaded getParameteredKey.
	 * @return
	 */
	public boolean searchLoadParameteredArea()
	{
		String param = getParameteredKey();
		if (param == null) return false;
		HashMap<String, Cube> hm = parameteredPlaces.get(param);
		if (hm!=null)
		{
			hmArea = hm;
			return true;
		}
		return false;
	}
	
	public void storeParameteredArea()
	{
		String param = getParameteredKey();
		if (param == null) return;
		parameteredPlaces.put(param, hmArea);
	}
	

	public final Place[] getDirectSubPlacesForCoordinates(int worldX, int worldY, int worldZ, HashMap[] placeHashmaps)
	{
		ArrayList<Place> r = new ArrayList<Place>();
		for (HashMap map : placeHashmaps) {
			for (Object place: map.values()) {
				if (((Place)place).boundaries.isInside(worldX, worldY, worldZ))
				{
					r.add((Place)place);
				}
			}			
		}
		return r.toArray(new Place[0]);		
	}

	
	protected HashMap<String,Cube> hmArea = null;
	
	public Cube getStoredCube(int x,int y, int z)
	{
		if (hmArea==null) hmArea = new HashMap<String, Cube>();
		return (Cube)hmArea.get(getStoredCubeKeyString(x, y, z));
	}
	
	public void addStoredCube(int x, int y, int z, Cube c)
	{
		if (hmArea==null) hmArea = new HashMap<String, Cube>();
		hmArea.put(getStoredCubeKeyString(x, y, z), c);
	}
	
	public static String getStoredCubeKeyString(int x, int y, int z)
	{
		return ""+x+" "+y+" "+z;
	}
	
	public Place getRoot()
	{
		return parent.getRoot();
	}
	
	public CubeClimateConditions getCubeClimateConditions(int worldX, int worldY, int worldZ)
	{
		return ((World)getRoot()).getClimate().getCubeClimate(new Time(), worldX, worldY, worldZ);
	}
	
}
