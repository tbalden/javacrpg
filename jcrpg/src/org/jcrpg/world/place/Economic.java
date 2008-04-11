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

import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;

/**
 * Intelligently created like cities, outposts, agriculture
 * @author pali
 *
 */
public class Economic extends Place{

	public Geography soilGeo = null;
	public EntityInstance owner = null;
	
	public Economic(String id, Geography soilGeo, Place parent, PlaceLocator loc, DistanceBasedBoundary boundaries, EntityInstance owner) {
		super(id, parent, loc);
		this.soilGeo = soilGeo;
		this.owner = owner;
		// TODO Auto-generated constructor stub
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
		((World)parent.getRoot()).updateEconomy(origoX, origoX+sizeX, origoY, origoY+sizeY, origoZ, origoZ+sizeZ, this);
	}
	
	/**
	 * This should update the things of an economic. Should be called if the owner entity implies a change
	 * for it, etc.
	 */
	public void update()
	{
		
	}

}
