/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.world.generator.program.algorithm;

import java.util.ArrayList;
import java.util.Collection;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.generator.GenProgram;
import org.jcrpg.world.generator.WorldParams;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;

/**
 * Algorithm to generate additional things to certain places.
 * @author pali
 *
 */
public class GenAlgoAdd extends GenAlgoBase {

	public static String GEN_TYPE_NAME = "Add"; 

	/**
	 * The additional geography to add into.
	 */
	public Geography added;
	/**
	 * Geographies where to add.
	 */
	public Collection<Geography> baseGeos;
	
	/**
	 * the likeness that a geo is added
	 */
	public int likeness = 50;
	
	public int[] worldHeightsToAddTo = new int[] {};
	
	public GenAlgoAdd(World w, Object[] paramsArray, Geography geo)
	{
		GenAlgoAddParams params = (GenAlgoAddParams) paramsArray[0];
		this.added = geo;
		baseGeos = new ArrayList<Geography>();
		
		Collection<Geography> pool = w.getAllGeographies();

		for (Geography g:pool)
		{
			String geoTypeName = g.ruleSet.geoTypeName;
			for (String s:params.baseGeos)
			{
				if (s.equals(geoTypeName))
				{
					baseGeos.add(g);
				}
			}
		}
		likeness = params.likeness;
		worldHeightsToAddTo = params.worldHeightsToAddTo;
	}
	
	public GenAlgoAdd(Geography added, Collection<Geography> baseGeos, int likeness, int[] worldHeightsToAddTo)
	{
		this.added = added;
		this.baseGeos = baseGeos;
		this.likeness = likeness;
		this.worldHeightsToAddTo = worldHeightsToAddTo;
	}

	@Override
	public void runGeneration(GenProgram program) {
		WorldParams params = program.params;
		int wMag = params.magnification;
		int wX = params.sizeX;
		//int wY = params.sizeY;
		int wZ = params.sizeZ;
		int gMag = params.geoNormalSize;
		int gWX = (wX*wMag)/gMag;
		//int gWY = (wY*wMag)/gMag;
		int gWZ = (wZ*wMag)/gMag;
		for (int x=0; x<gWX; x++)
		{
			for (int z=0; z<gWZ;z++)
			{
				if (HashUtil.mixPercentage(x, 1, z)>likeness)
				{
					continue;
				}
				for (Geography g:baseGeos)
				{
					if (g.getBoundaries().isInside(x*gMag, g.worldGroundLevel, z*gMag))
					{
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("AlgoAdd: ADDING GEO..."+x+" "+z);
						for (int i=0; i<worldHeightsToAddTo.length; i++) 
						{
							try {
								added.getBoundaries().addCube(gMag, x, program.generator.world.getSeaLevel(gMag)+worldHeightsToAddTo[i], z);
							} catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
						break;
					}
				}
			}
		}
	}
	
	

}
