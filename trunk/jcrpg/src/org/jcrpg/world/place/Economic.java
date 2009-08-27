/*
 *  This file is part of JavaCRPG.
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
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.space.sidetype.trigger.StorageObjectSideSubType;
import org.jcrpg.ui.map.BlockPattern;
import org.jcrpg.ui.map.WorldMap;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.place.economic.InfrastructureBlockChecker;
import org.jcrpg.world.place.economic.checker.WaterChecker;

/**
 * Intelligently created like cities, outposts, agriculture
 * @author pali
 *
 */
public class Economic extends Geography {

	@Override
	public boolean overrideGeoHeight() {
		return true;
	}
	
	/**
	 * Tells if the given economic needs flora cube addition when generating cubes in EconomyContainer.
	 */
	public boolean needsFlora = true;

	public Geography soilGeo = null;
	public EntityInstance owner = null;
	/**
	 * if it is owned by a persistent member, this should be set to it.
	 */
	public PersistentMemberInstance ownerMember = null;
	
	public Economic(String id, Geography soilGeo, Place parent, PlaceLocator loc, DistanceBasedBoundary boundaries, EntityInstance owner) {
		super(id, parent, loc);
		this.soilGeo = soilGeo;
		this.owner = owner;
	}
	
	public int getNumberOfInhabitants()
	{
		if (owner==null) return 0;
		return owner.numberOfMembers;
	}
	
	public DistanceBasedBoundary getOwnerHomeBoundary()
	{
		if (owner==null) return null;
		return owner.homeBoundary;
	}

	@Override
	public boolean generateModel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loadModelFromFile() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void updateLocationAndSize()
	{
		// TODO
		((World)parent.getRoot()).economyContainer.updateEconomy(origoX, origoX+sizeX, origoY, origoY+sizeY, origoZ, origoZ+sizeZ, this);
	}

	/**
	 * 
	 * @return The owner (NPC or PC) of infrastructure.
	 */
	public PersistentMemberInstance getOwnerMember() {
		return ownerMember;
	}

	/**
	 * Sets the person (NPC or PC) who owns this economic.
	 * @param ownerMember
	 */
	public void setOwnerMember(PersistentMemberInstance ownerMember) {
		this.ownerMember = ownerMember;
	}
	
	/**
	 * Returns coordinates for places where a unit can settle in a given economy.
	 * @return
	 */
	public ArrayList<int[]> getPossibleSettlePlaces()
	{
		return null;
	}
	
	
	
	public static final ArrayList<Class <? extends InfrastructureBlockChecker>> STANDARD_WATER_CHECKER_LIST = new ArrayList<Class<? extends InfrastructureBlockChecker>>();
	static 
	{
		STANDARD_WATER_CHECKER_LIST.add(WaterChecker.class);
	}
	
	/**
	 * Gets a list of block availability checkers for infrastructure generation. This should return
	 * the filtering checkers that filter out such population blocks that would be unsuitable for
	 * this economic class.
	 * @return The list.
	 */
	public ArrayList<Class <? extends InfrastructureBlockChecker>> getGenerationBlockAvailabilityCheckers()
	{
		return STANDARD_WATER_CHECKER_LIST;
	}
	
	/**
	 * 
	 * @return The likeness that this economic unit is farthest away from center. Between 1-0 (1 farthest, 0 closest to center).
	 */
	public float getLikelyDistanceRatioFromCenter()
	{
		return 0f;
	}
	
	/**
	 * Returns the list of coordinates where storage objects can be placed.
	 * @return coordinates.
	 */
	public HashMap<Long,int[]> getStorageObjectPlaces()
	{
		return null;
	}
	
	
	public static final String TYPE_STORAGE = "STORAGE";
	public static final SideSubType SUBTYPE_CHEST = new StorageObjectSideSubType(TYPE_STORAGE+"_CHEST");
	static Side[] CHEST = new Side[]{new Side(TYPE_STORAGE,SUBTYPE_CHEST)};
	static Side[][] CHEST_SIDES = new Side[][] { null, null, null,null,null,CHEST };
	Cube storage_chest = new Cube(this,CHEST_SIDES,0,0,0,false,false);

	/**
	 * Returns the Cube representation of the storage object depending on the parameters.
	 * @param param
	 * @return
	 */
	public Cube getStorageObjectCube(int param)
	{
		Cube r = storage_chest.copy(this);
		r.containingInternalEconomicUnit = this;
		return r;
	}
	
	@Override
	public byte[] getMapColor()
	{
		return new byte[]{(byte)255,(byte)255,(byte)255};
	}
	
	static BlockPattern pattern = new BlockPattern();
	static 
	{
		pattern.PATTERN = WorldMap.CITY;
	}
	
	public BlockPattern getMapPattern()
	{
		return pattern;
	}

}
