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

package org.jcrpg.world.climate;

import java.util.HashMap;

import org.jcrpg.util.HashUtil;
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
	public CubeClimateConditions getCubeClimate(Time time, int worldX, int worldY, int worldZ, boolean internal) {
		for (ClimateBelt belt : belts.values()) {
			// smoothing together climates with quasi random coordinates:
			int perVariation = HashUtil.mixPercentage(worldX, worldY, worldZ)/50-2; // +/- 1 cube
			if (belt.boundaries.isInside(worldX + perVariation, worldY, worldZ + perVariation))
			{
				CubeClimateConditions c = belt.getCubeClimate(time, worldX, worldY, worldZ, internal);
				c.setBelt(belt);
				c.setLevel(new ClimateLevel("1",this,0,0));
				c.setInternal(internal);
			
				return c;
			}
			
		}
		CubeClimateConditions c = new CubeClimateConditions();
		c.setSeason(new Season());
		c.setDayTime(new Season().getDayTime(time));
		c.setBelt(new ClimateBelt("nil",this));
		c.setLevel(new ClimateLevel("nil",this,0,100));
		c.setInternal(internal);
		return c;
	}
	
	

}
