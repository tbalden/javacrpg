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

package org.jcrpg.world.climate.impl.tropical;

import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Season;
import org.jcrpg.world.climate.conditions.Rain;
import org.jcrpg.world.climate.conditions.Warm;
import org.jcrpg.world.climate.impl.continental.Summer;
import org.jcrpg.world.time.Time;

public class Tropical extends ClimateBelt {

	public static String TROPICAL_ID = Tropical.class.getCanonicalName();
	
	static Summer summer = new Summer();

	public Tropical(String id, Climate parent) throws Exception {
		super(id, parent);
		STATIC_ID = TROPICAL_ID;
		beltConditionsExternal.add(new Warm(50));
		beltConditionsExternal.add(new Rain(50));
		colorBytes = new byte[] { (byte)40,(byte)140,(byte)40 };
		audioDescriptor.ENVIRONMENTAL = new String[]{"tropical_1","tropical_2"};
	}

	
	@Override
	public Season getSeason(Time time) {
		
		return summer;
		
	}
	
}
