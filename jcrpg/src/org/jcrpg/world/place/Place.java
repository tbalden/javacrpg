/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world.place;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.MusicDescription;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.time.Time;

public abstract class Place {
	
	public boolean placeNeedsToBeEnteredForEncounter = false;

	public AudioDescription audioDescriptor = new AudioDescription();
	
	
	public int origoX, origoY, origoZ, sizeX, sizeY, sizeZ, magnification;
	public int groundLevel = 0;
	public static String generatePositionCacheKey(int worldX, int worldY, int worldZ, int lossFactor)
	{
		long k = (((long)(worldX/lossFactor))<< 32) + ((worldY/lossFactor) << 16) + ((worldZ/lossFactor));
		
		return ""+k;
	}

	public String[] generatePositionCacheKeys(int lossFactor)
	{
		int realXOrigo = ((origoX)*magnification)/(lossFactor);
		int realYOrigo = ((origoY)*magnification)/(lossFactor);
		int realZOrigo = ((origoZ)*magnification)/(lossFactor);
		int realXEnd = ((origoX+sizeX)*magnification)/(lossFactor);
		int realYEnd = ((origoY+sizeY)*magnification)/(lossFactor);
		int realZEnd = ((origoZ+sizeZ)*magnification)/(lossFactor);
		ArrayList<String> keys = new ArrayList<String>();
		for (int x= realXOrigo; x<realXEnd; x++)
		{
			for (int y= realYOrigo; y<realYEnd; y++)
			{
				for (int z= realZOrigo; z<realZEnd; z++)
				{
					int k = ((x)<< 16) + ((y) << 8) + ((z));
					//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("ADDING "+id+" "+k);
					keys.add(""+k);
				}				
			}
		}
		return (String[])keys.toArray(new String[0]);
	}

	/**
	 * Totally empty Cube sides.
	 */
	protected static Side[][] EMPTY = new Side[][] { {new Side()}, {new Side()}, {new Side()},{new Side()},{new Side()},{new Side()} };
	/**
	 * Use it for overwriter cubes to clear one side surely.
	 */
	protected static Side[] EMPTY_SIDE = new Side[] { new Side() };

	protected Boundaries boundaries;
	
	
	
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
	
	/**
	 * Unique id.
	 */
	public String id;
	/**
	 * unique calculated numeric id.
	 */
	public long numericId;
	
	public PlaceLocator loc;
	
	public Place parent;
	
	public Place(String id, Place parent, PlaceLocator loc)
	{
		this.id = id;
		if (id!=null) {
			for (int i=0; i<id.length(); i++)
			{
				numericId=(numericId<<8)+id.charAt(i);
			}
			numericId = numericId<<8;
		}
		this.parent = parent;
		this.loc = loc;
	}
	
	/**
	 * The base for getting the contents of a place cube. Generally it should be going down hierarchically
	 * into subplaces calling subplaces getCube(...) and putting together it with their requirements for the cube.
	 * World.getCube(...) is called in 3d engine and other parts of the jcrpg framework, so it should be rather fast!
	 * World will go down into its subelements and when it receives subelements' cube it will be return to the caller.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public Cube getCube(long key, int worldX, int worldY, int worldZ, boolean farView)
	{
		return null;
	}
	
	

	/**
	 * Contains any type of place of unique Cube description, the key is valid only for a fixed Cube structure, so the key must
	 * be calculated based on the fix algorithms that build up the unique Cube structure. E.g. a house is a 1-1 cube structure (place),
	 * which is generated by a fix algorithm that can be parametered example: this.getClass().getName()+" "+sizeX+" "+sizeY+" "+sizeZ;
	 * - if only size matters this key should suffice! If origo coordinates too, they should be in the key too.
	 */
	protected static HashMap<String,HashMap<String, Cube>> parameteredPlaces = new HashMap<String, HashMap<String,Cube>>();

	/**
	 * The key for a unique Cube structure in the parametedPlaces cache. Must be globaly unique not just for the place's class type!!
	 * Example : this.getClass().getName()+" "+sizeX+" "+sizeY+" "+sizeZ;
	 * - if only size matters this key should suffice! If origo coordinates too, they should be in the key too, etc etc.
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
	
	/**
	 * Stores a area hashmap for a unique place parametered key.
	 */
	public void storeParameteredArea()
	{
		String param = getParameteredKey();
		if (param == null) return;
		parameteredPlaces.put(param, hmArea);
	}
	

	/**
	 * Utility function to get all subplaces that contain the coordinate in a list of places in HashMaps.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @param placeHashmaps
	 * @return Places containing the coordinates.
	 */
	public static Place[] getDirectSubPlacesForCoordinates(int worldX, int worldY, int worldZ, HashMap<String,Place>[] placeHashmaps)
	{
		ArrayList<Place> r = new ArrayList<Place>();
		for (HashMap<String,Place> map : placeHashmaps) {
			for (Place place: map.values()) {
				if (((Place)place).boundaries.isInside(worldX, worldY, worldZ))
				{
					r.add((Place)place);
				}
			}			
		}
		return r.toArray(new Place[0]);		
	}

	
	/**
	 * Exactly designed places' cache, should be filled in the place's constructor.
	 */
	protected HashMap<String,Cube> hmArea = null;
	
	/**
	 * Get a stored a cube for 1-1 exactly designed Places like House (constructor of place should create the hmArea cube cache!)
	 * @param x
	 * @param y
	 * @param z
	 * @return Cube.
	 */
	public Cube getStoredCube(int x,int y, int z)
	{
		if (hmArea==null) hmArea = new HashMap<String, Cube>();
		return (Cube)hmArea.get(getStoredCubeKeyString(x, y, z));
	}
	
	/**
	 * Add a stored a cube for 1-1 exactly designed Places like House (constructor of place should call this when creating the hmArea!)
	 * @param x
	 * @param y
	 * @param z
	 * @return Cube.
	 */
	public void addStoredCube(int x, int y, int z, Cube c)
	{
		if (hmArea==null) hmArea = new HashMap<String, Cube>();
		hmArea.put(getStoredCubeKeyString(x, y, z), c);
	}
	
	/**
	 * Stored cubes cache key getter.
	 * @param x
	 * @param y
	 * @param z
	 * @return String that represents the key for the coordinates.
	 */
	public static String getStoredCubeKeyString(int x, int y, int z)
	{
		return ""+x+" "+y+" "+z;
	}
	
	/**
	 * @return the Root place of all (recoursive call!).
	 */
	public Place getRoot()
	{
		return parent.getRoot();
	}
	

	public World thisWorld = null;
	
	public void clear()
	{
		thisWorld = null;
	}
	
	/**
	 * Return the climatic conditions of a given cube at coordinates.
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return conditions.
	 */
	public CubeClimateConditions getCubeClimateConditions(Time time, int worldX, int worldY, int worldZ, boolean internal)
	{
		if (thisWorld==null)
		{
			thisWorld = ((World)getRoot());
		}
		return thisWorld.getClimate().getCubeClimate(time, worldX, worldY, worldZ, internal);
	}
	
	
	public int getGeographyHashPercentage(int x, int y, int z)
	{
		if (thisWorld==null)
		{
			thisWorld = ((World)getRoot());
		}
		return thisWorld.getGeographyHashPercentage(x, y, z);
	}

	public int shrinkToWorld(int x)
	{
		if (thisWorld==null)
		{
			thisWorld = ((World)getRoot());
		}
		return thisWorld.shrinkToWorld(x);
	}

	/**
	 * If this geo is not covering the coordinate, this should look up other geo that covers it and return the height.
	 * @param worldX
	 * @param worldZ
	 * @return the relative height.
	 */
	public float getPointHeightOutside(int worldX, int worldZ, boolean farView)
	{	
		Geography nonReturnerFallback = null;
		for (Geography geo:((World)getRoot()).geographies.values())
		{
			if (this!=geo && geo.returnsGeoOutsideHeight)
			{
				if (geo.boundaries.isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					int[] values = geo.calculateTransformedCoordinates(worldX, geo.worldGroundLevel, worldZ);
					return geo.getPointHeight(values[3], values[5], values[0], values[2],worldX,worldZ, farView);
				}
			} else
			{
				if (this!=geo && !geo.returnsGeoOutsideHeight && geo.boundaries.isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					nonReturnerFallback = geo;
				}
			}
		}
		if (nonReturnerFallback!=null)
		{
			int[] values = nonReturnerFallback.calculateTransformedCoordinates(worldX, nonReturnerFallback.worldGroundLevel, worldZ);
			return nonReturnerFallback.getPointHeight(values[3], values[5], values[0], values[2],worldX,worldZ, farView);
		}
		//System.out.println("NO POINT GEO OUTSIDE");
		return 0;
	}
	
	/**
	 * Load related things called upon loading the game, like filling up transient (non-save data) with generation.
	 */
	public void onLoad()
	{
		
	}
	
	/**
	 * This determines if Geography's own height is overwritten by this Place's height. Used especially with
	 * economic things like house,ecoGround.
	 * @return
	 */
	public boolean overrideGeoHeight()
	{
		return false;
	}
	
	public boolean denyOtherEnvironmentSounds()
	{
		return false;
	}
	
	public MusicDescription getMusicDescription()
	{
		return null;
	}


	/**
	 * Returns worldY intervals that are not empty in a given X/Z column of the place.
	 * @param worldX
	 * @param worldZ
	 * @return
	 */
	public int[][] getFilledZonesOfY(int worldX, int worldZ, int minY, int maxY)
	{
		return null;
	}

}
