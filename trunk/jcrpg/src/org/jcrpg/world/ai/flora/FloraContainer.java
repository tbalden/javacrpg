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

package org.jcrpg.world.ai.flora;

import java.util.HashMap;
import java.util.Map;

import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.time.Time;

/**
 * Contains the base for FloraContaining encapsulating map of FloraGenerator to Place types.
 * @author pali
 */
public class FloraContainer {

	public Map<Class<? extends Geography>, FloraGenerator> hmPlaceToGenerator = new HashMap<Class<? extends Geography>, FloraGenerator>();
	public FloraGenerator defaultGenerator; 
	
	public FloraCube getFlora(int worldX, int worldY, int worldZ, Class<? extends Geography> place, CubeClimateConditions conditions, Time time, boolean onSteep)
	{
		if (hmPlaceToGenerator.get(place)!=null) return hmPlaceToGenerator.get(place).generate(worldX, worldY, worldZ, conditions,time, onSteep);
		if (defaultGenerator!=null) return defaultGenerator.generate(worldX, worldY, worldZ, conditions,time, onSteep);
		return new FloraCube();
	}
	
}
