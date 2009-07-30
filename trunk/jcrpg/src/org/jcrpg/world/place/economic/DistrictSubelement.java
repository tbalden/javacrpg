/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.world.place.economic;

import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;

public abstract class DistrictSubelement extends Economic {

	public DistrictSubelement(String id, Geography soilGeo, Place parent,
			PlaceLocator loc, DistanceBasedBoundary boundaries,
			EntityInstance owner) {
		super(id, soilGeo, parent, loc, boundaries, owner);
	}
	
	/**
	 * Tells if this district element is a type which fills out a full block (a maze district),
	 * or smaller element (like houses).
	 * @return
	 */
	public boolean isFullBlockSized()
	{
		return false;
	}

	/**
	 * Tells if the district element is usable for the given population.
	 * @param populationType
	 * @return
	 */
	public boolean isValidForDistrict(Class<? extends Population> populationType)
	{
		if (this.isFullBlockSized() == ((Population)EconomyTemplate.economicBase.get(populationType)).needsFullBlockSizedSubelement())
		{
			return true;
		}
		return false;
	}
	
}
