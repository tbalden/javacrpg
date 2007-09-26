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

package org.jcrpg.world.climate.impl.continental;

import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Season;
import org.jcrpg.world.time.Time;

public class Continental extends ClimateBelt {

	public static String CONTINENTAL_ID = Continental.class.getCanonicalName();
	
	public Continental(String id, Climate parent) {
		super(id, parent);
		STATIC_ID = CONTINENTAL_ID;
	}
	
	static Spring spring = new Spring();
	static Summer summer = new Summer();
	static Autumn autumn = new Autumn();
	static Winter winter = new Winter();

	@Override
	public Season getSeason(Time time) {
		
		int p = time.getCurrentYearPercent();
		
		// if on southern hemisphere, percent reversed
		if (time.inverseSeasons) p = 100-p;
		
		if (p<25) return spring;
		if (p>25 && p<50) return spring;
		if (p>50 && p<75) return autumn;
		if (p>75) return winter;
		
		return super.getSeason(time);
	}

	
	
}
