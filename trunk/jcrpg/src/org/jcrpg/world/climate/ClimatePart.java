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

import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.time.Time;

public class ClimatePart {

	public Boundaries boundaries;
	public ClimatePart parent;

	public String id;

	public ClimatePart(String id, ClimatePart parent)
	{
		this.id = id;
		this.parent = parent;
		
	}
	
	public CubeClimateConditions getCubeClimate(Time time, int worldX, int worldY, int worldZ)
	{
		return null;
	}

	public Boundaries getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(Boundaries boundaries) {
		this.boundaries = boundaries;
	}

}
