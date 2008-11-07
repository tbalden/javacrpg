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


package org.jcrpg.world.climate;

import java.util.ArrayList;

import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.time.Time;

/**
 * Vertical split of the Climate
 * @author pali
 *
 */
public class ClimateBelt  extends ClimatePart {

	public String STATIC_ID = ClimateBelt.class.getCanonicalName();
	
	public byte[] colorBytes = new byte[] {0,0,(byte)150}; 
	
	public static Season genericSeason = new Season();
	
	public AudioDescription audioDescriptor = new AudioDescription();;

	/**
	 * External area base conditions
	 */
	public ArrayList<Condition> beltConditionsExternal = new ArrayList<Condition>();
	
	/**
	 * Internal area base conditions.
	 */
	public ArrayList<Condition> beltConditionsInternal = new ArrayList<Condition>();
	
	public ClimateBelt(String id, Climate parent) {
		super(id,parent);
	}
	
	public Season getSeason(Time time)
	{
		return genericSeason;
	}

	@Override
	public CubeClimateConditions getCubeClimate(Time time, int worldX, int worldY, int worldZ, boolean internal) {

		Season s = getSeason(time);
		CubeClimateConditions c = new CubeClimateConditions();
		s.getConditions(c, time, worldX, worldY, worldZ);
		// based upon internal flag merge with appropriate base conditions
		c.mergeConditions(!internal?beltConditionsExternal:beltConditionsInternal);
		
		return c;
	}
	
}
