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

package org.jcrpg.world.ai.flora.impl;

import org.jcrpg.world.ai.flora.FloraContainer;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.ground.EncounterGround;
import org.jcrpg.world.place.economic.ground.PavedStorageAreaGround;
import org.jcrpg.world.place.economic.population.DungeonDistrict;
import org.jcrpg.world.place.economic.residence.House;
import org.jcrpg.world.place.economic.residence.WoodenHouse;
import org.jcrpg.world.place.economic.residence.dungeon.SimpleDungeonPart;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;

public class BaseFloraContainer extends FloraContainer{


	public BaseFloraContainer() {
		super();
		defaultGenerator = new BaseFloraGenerator();
		hmPlaceToGenerator.put(Forest.class,new ForestFloraGenerator());
		hmPlaceToGenerator.put(Plain.class,new PlainFloraGenerator());
		hmPlaceToGenerator.put(Mountain.class,new MountainFloraGenerator());
		hmPlaceToGenerator.put(EconomicGround.class,new EconomicFloraGenerator());
		hmPlaceToGenerator.put(PavedStorageAreaGround.class,new EconomicFloraGenerator());
		hmPlaceToGenerator.put(SimpleDungeonPart.class,new EconomicFloraGenerator());
		hmPlaceToGenerator.put(House.class,new EconomicFloraGenerator());
		hmPlaceToGenerator.put(WoodenHouse.class,new EconomicFloraGenerator());
		hmPlaceToGenerator.put(EncounterGround.class,new EconomicFloraGenerator());
	}
	
}
