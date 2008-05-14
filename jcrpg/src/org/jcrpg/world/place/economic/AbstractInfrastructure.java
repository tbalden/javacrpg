/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.world.place.economic;

import org.jcrpg.world.place.Economic;


public abstract class AbstractInfrastructure {
	
	public int currentSize = 0;
	public int maxSize = -1;
	public int centerX = -1;
	public int centerZ = -1;
	public int maxBlocks = 0;
	public int maxBlocksOneDim = 0;
		
	public int maxInhabitantPerBlock = 20;
	public int maxLevelsOfBuildings = 5;
	
	public int BUILDING_BLOCK_SIZE = 4;
	
	public class InfrastructureElementParameters
	{
		public int relOrigoX,relOrigoY,relOrigoZ;
		public int sizeX, sizeY, sizeZ;
		public Class<? extends Economic> type;
	}
	
	public Population population;
	
	public AbstractInfrastructure(Population population)
	{
		this.population = population;
		maxSize = population.soilGeo.blockSize;
		centerX = population.owner.homeBoundary.posX;
		centerZ = population.owner.homeBoundary.posZ;
		maxBlocks = (maxSize/BUILDING_BLOCK_SIZE)*(maxSize/BUILDING_BLOCK_SIZE);
		maxBlocksOneDim = maxSize/BUILDING_BLOCK_SIZE;
	}
	
	public abstract void update(); 
}
