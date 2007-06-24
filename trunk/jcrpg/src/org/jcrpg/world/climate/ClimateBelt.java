/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.climate;

import org.jcrpg.world.time.Time;

/**
 * Vertical split of the Climate
 * @author pali
 *
 */
public class ClimateBelt  extends ClimatePart {

	public String STATIC_ID = ClimateBelt.class.getCanonicalName();
	
	public static Season genericSeason = new Season(); 
	
	public ClimateBelt(String id, Climate parent) {
		super(id,parent);
	}
	
	public Season getSeason(Time time)
	{
		return genericSeason;
	}

	@Override
	public CubeClimateConditions getCubeClimate(Time time, int worldX, int worldY, int worldZ) {

		Season s = getSeason(time);
		CubeClimateConditions c = new CubeClimateConditions();
		s.getConditions(c, time, worldX, worldY, worldZ);
		
		return c;
	}
	
}
