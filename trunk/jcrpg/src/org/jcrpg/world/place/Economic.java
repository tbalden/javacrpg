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

import java.util.ArrayList;

import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;

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
		return owner.numberOfMembers;
	}
	
	public DistanceBasedBoundary getOwnerHomeBoundary()
	{
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

	public PersistentMemberInstance getOwnerMember() {
		return ownerMember;
	}

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
	
	

}
