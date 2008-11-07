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
package org.jcrpg.world.place.economic.checker;

import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.InfrastructureBlockChecker;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.geography.sub.Cave;

public class CaveChecker implements InfrastructureBlockChecker {

	public boolean[] getAvailableBlocks(AbstractInfrastructure infrastructure) {
		Population population = infrastructure.population;
		boolean[] unavailableBlocks = new boolean[infrastructure.maxBlocks];
		int maxBlocksOneDim = infrastructure.maxBlocksOneDim;
		int BUILDING_BLOCK_SIZE = infrastructure.BUILDING_BLOCK_SIZE;
		
		// checking for waters...
		for (int x=0; x<maxBlocksOneDim; x++)
		{
			for (int z=0; z<maxBlocksOneDim; z++)
			{
				for (Geography w:((World)population.soilGeo.getRoot()).geographies.values())
				{
					if (w instanceof Cave && w.getBoundaries().isInside(population.blockStartX+x*BUILDING_BLOCK_SIZE, w.worldGroundLevel, population.blockStartZ+z*BUILDING_BLOCK_SIZE))
					{
						//System.out.println("CHECKING CAVE");
						for (int xx = 0; xx<BUILDING_BLOCK_SIZE; xx++)
						{
							for (int zz=0; zz<BUILDING_BLOCK_SIZE; zz++)
							{
								SurfaceHeightAndType sht = infrastructure.population.soilGeo.getPointSurfaceData(population.blockStartX+x*BUILDING_BLOCK_SIZE + xx, population.blockStartZ+z*BUILDING_BLOCK_SIZE + zz, null, false)[0];
								//System.out.println("--- "+sht.surfaceY+" <= "+(w.worldGroundLevel+w.sizeY));
								if (sht.surfaceY<=w.worldGroundLevel+w.sizeY)
								{
									//System.out.println("## CAVE FOUND");
									unavailableBlocks[x+z*maxBlocksOneDim] = true;
								}
							}
						}
					}
				}
			}
		}
		return unavailableBlocks;
	}
	

}
