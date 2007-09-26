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


package org.jcrpg.world.climate.conditions.light;

import org.jcrpg.world.climate.Condition;

public class Dusk extends Condition {
	public static String DUSK_ID = Dusk.class.getCanonicalName();
	public Dusk(int weightPercentage) throws Exception {
		super(weightPercentage);
		ID = DUSK_ID;
	}


}
