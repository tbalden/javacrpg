/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.world.generator.program;

import org.jcrpg.world.generator.ClassFactory;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.water.Ocean;

public class DefaultClassFactory extends ClassFactory{

	
	@Override
	public Geography createGeography(Class<? extends Geography> type) {
		return null;
	}

	@Override
	public Water createWater(Class<? extends Water> type) throws Exception {
		int wMag = program.params.magnification;
		int wX = program.params.sizeX;
		int wY = program.params.sizeY;
		int wZ = program.params.sizeZ;
		Water r = null;
		if (type == Ocean.class)
		{
			Ocean l = new Ocean("OCEANS", program.generator.world, null, program.generator.world.getSeaLevel(1),wMag,wX,wY,wZ,0,0,0,1,program.params.landMass,program.params.landDensity);
			r = l;
		}
		return r;
	}

}
