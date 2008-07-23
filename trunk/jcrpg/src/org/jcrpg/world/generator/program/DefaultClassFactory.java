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
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.geography.Forest;
import org.jcrpg.world.place.geography.Mountain;
import org.jcrpg.world.place.geography.Plain;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.water.Ocean;
import org.jcrpg.world.place.water.River;

public class DefaultClassFactory extends ClassFactory{

	
	@Override
	public Geography createGeography(Class<? extends Geography> type) throws Exception {
		int wMag = program.params.magnification;
		int wX = program.params.sizeX;
		int wY = program.params.sizeY;
		int wZ = program.params.sizeZ;
		int gMag = program.params.geoNormalSize;
		int gWX = (wX*wMag)/gMag;
		int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;
		World world = program.generator.world;
		Geography r = null;
		if (type == Plain.class)
		{
			Plain p = new Plain("BIGPLAIN",world,null,world.getSeaLevel(1),gMag, gWX, gWY, gWZ, 0, world.getSeaLevel(gMag)-1, 0, false);
			r = p;
		} else
		if (type == Forest.class)
		{
			Forest p = new Forest("BIGFOREST",world,null,world.getSeaLevel(1),gMag, gWX, gWY, gWZ, 0, world.getSeaLevel(gMag)-1, 0, false);
			r = p;
		} else
		if (type == Cave.class)
		{
			Cave p = new Cave("BIGCAVE",world,null,world.getSeaLevel(1)+1,world.getSeaLevel(1)+3,gMag, gWX, 2, gWZ, 0, world.getSeaLevel(gMag), 0, 30,Cave.LIMIT_WEST|Cave.LIMIT_SOUTH|Cave.LIMIT_NORTH|Cave.LIMIT_EAST,2,false);
			r = p;
		} else
		if (type == Mountain.class)
		{
			Mountain p = new Mountain("MOUNTAINS",world,null,world.getSeaLevel(1),world.getSeaLevel(1)+5*(int)(Math.sqrt(gMag)*program.params.heightRatio)/10 ,gMag, gWX, gWY, gWZ, 0, world.getSeaLevel(gMag)-1, 0, false);
			r = p;
		}
			
		return r;
	}

	@Override
	public Water createWater(Class<? extends Water> type) throws Exception {
		int wMag = program.params.magnification;
		int wX = program.params.sizeX;
		int wY = program.params.sizeY;
		int wZ = program.params.sizeZ;
		int gMag = program.params.geoNormalSize;
		int gWX = (wX*wMag)/gMag;
		int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;
		World world = program.generator.world;
		
		Water r = null;
		if (type == River.class)
		{
			River ri = new River("RIVERS",world,null,world.getSeaLevel(1), gMag, gWX, gWY, gWZ, 0, world.getSeaLevel(gMag)-1, 0, 1,1,0.2f,4, false);
			r = ri;
		} else
		if (type == Ocean.class)
		{
			Ocean l = new Ocean("OCEANS", program.generator.world, null, program.generator.world.getSeaLevel(1),wMag,wX,wY,wZ,0,0,0,1,program.params.landMass,program.params.landDensity);
			r = l;
		}
		return r;
	}

}
