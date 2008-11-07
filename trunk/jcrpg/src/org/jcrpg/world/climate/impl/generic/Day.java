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

package org.jcrpg.world.climate.impl.generic;

import org.jcrpg.world.climate.DayTime;
import org.jcrpg.world.climate.conditions.Warm;
import org.jcrpg.world.climate.conditions.light.Light;


public class Day extends DayTime{

	
	public Day() throws Exception
	{
		conditions.add(new Light(50));
		conditions.add(new Warm(10));
	}
	
}
