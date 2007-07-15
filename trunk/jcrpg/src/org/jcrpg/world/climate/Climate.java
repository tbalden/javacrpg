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

import java.util.HashMap;

import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.World;
import org.jcrpg.world.time.Time;

public class Climate extends ClimatePart {

	public World world;
	public Boundaries boundaries;
	
	public HashMap<String, ClimateBelt> belts;
	public HashMap<String, ClimateLevel> levels;
	
	public Climate(String id, World w)
	{
		super(id,null);
		boundaries = w.getBoundaries();
		belts = new HashMap<String, ClimateBelt>();
		levels = new HashMap<String, ClimateLevel>();
	}

	@Override
	public CubeClimateConditions getCubeClimate(Time time, int worldX, int worldY, int worldZ) {
		for (ClimateBelt belt : belts.values()) {
			if (belt.boundaries.isInside(worldX, worldY, worldZ))
			{
				CubeClimateConditions c = belt.getCubeClimate(time, worldX, worldY, worldZ);
				c.setBelt(belt);
				c.setLevel(new ClimateLevel("1",this,0,0));
			
				return c;
			}
			
		}
		CubeClimateConditions c = new CubeClimateConditions();
		c.setSeason(new Season());
		c.setDayTime(new Season().getDayTime(time));
		c.setBelt(new ClimateBelt("nil",this));
		c.setLevel(new ClimateLevel("nil",this,0,100));
		return c;
	}
	
	

}
