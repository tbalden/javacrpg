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

package org.jcrpg.world.place;

import org.jcrpg.space.Cube;
import org.jcrpg.world.ai.flora.FloraCube;
import org.jcrpg.world.ai.flora.FloraDescription;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.time.Time;

public class Geography extends Place {

	public Geography(String id, Place parent,PlaceLocator loc) {
		super(id,parent, loc);
	}

	@Override
	public boolean generateModel() {
		return false;
	}

	@Override
	public boolean loadModelFromFile() {
		return false;
	}

	
	public Cube getFloraCube(int worldX, int worldY, int worldZ, CubeClimateConditions conditions, Time time, boolean onSteep)
	{
		World w = (World)getRoot();
		Cube floraCube = null;
		FloraCube fC = w.getFloraContainer().getFlora(worldX,worldY,worldZ,this.getClass(), conditions, time, onSteep);
		for (FloraDescription fd : fC.descriptions)
		{
			if (floraCube==null) {
				floraCube = fd.instanciateCube(w, worldX,worldY,worldZ);				
			}
			else {
				floraCube = new Cube(floraCube,fd.cubicForm,worldX,worldY,worldZ,SurfaceHeightAndType.NOT_STEEP);
			}
		}
		return floraCube;
	}

	
	@Override
	public Cube getCube(int worldX, int worldY, int worldZ) {
		return super.getCube(worldX, worldY, worldZ);
	}
	
}
