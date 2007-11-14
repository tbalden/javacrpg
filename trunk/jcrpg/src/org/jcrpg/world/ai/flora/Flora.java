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

/**
 * Base class for plants, can define multiple states -> flora description in statesToFloraDescription,
 * so cubic form can change based on Season/Daytime etc.
 * @author pali
 *
 */
public class Flora {

	/**
	 * Maps flora descriptions to Season and DayTime type.
	 */
	public HashMap<String, FloraDescription> statesToFloraDescription = new HashMap<String, FloraDescription>();
	
	public static final String POSITION_GROUND = "__GROUND";
	public static final String POSITION_MIDDLE = "__MIDDLE";
	public static final String POSITION_TOP = "__TOP";
	
	/**
	 * Determines if a plant can grow on steep.
	 */
	public boolean growsOnSteep = false; 
	
	public String floraPosition = POSITION_TOP;
	
	/**
	 * Fallback description if not flora description found for a state.
	 */
	public FloraDescription defaultDescription;
	
	public FloraDescription getFloraDescription(String key)
	{
		FloraDescription d = statesToFloraDescription.get(key);
		if (d==null) d = defaultDescription;
		return d;
	}
	
}
