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

package org.jcrpg.world.climate.impl.desert;

import org.jcrpg.world.ai.MusicDescription;
import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Season;
import org.jcrpg.world.climate.conditions.Warm;
import org.jcrpg.world.time.Time;

public class Desert extends ClimateBelt {


	public static String DESERT_ID = Desert.class.getCanonicalName();


	static Spring spring;
	static Summer summer = new Summer();
	static {
		try {
			spring = new Spring();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Desert(String id, Climate parent) throws Exception {
		super(id, parent);
		STATIC_ID = DESERT_ID;
		
		beltConditionsExternal.add(new Warm(40));
		
		colorBytes = new byte[] { (byte)240,(byte)240,(byte)100 };
		audioDescriptor.ENVIRONMENTAL = new String[]{"eagle_1"};
	}

	@Override
	public Season getSeason(Time time) {
		
		int p = time.getCurrentYearPercent();
		
		// if on southern hemisphere, percent reversed
		if (time.inverseSeasons) p = 100-p;
		
		if (p<5 && p>95) return spring;
		return summer;
		
	}
	
	public MusicDescription musicDesc = new MusicDescription("desert");
	@Override
	public MusicDescription getMusicDescription() {
		return musicDesc;
	}
	
}
