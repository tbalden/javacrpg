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

import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;


public abstract class AbstractInfrastructure {
	
	public int currentSize = 0;
	public int maxSize = -1;
	public int centerX = -1;
	public int centerZ = -1;
	public int maxBlocks = 0;
	public int maxBlocksOneDim = 0;
		
	public int maxInhabitantPerBlock = 4;
	public int maxLevelsOfBuildings = 3;
	
	public int BUILDING_BLOCK_SIZE = 4;
	
	public int INHABITANTS_PER_UPDATE = -1;
	
	public class InfrastructureElementParameters
	{
		public int relOrigoX,relOrigoY,relOrigoZ;
		public int sizeX, sizeY, sizeZ;
		public Class<? extends Economic> type;
		public EntityMemberInstance owner = null;
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
		INHABITANTS_PER_UPDATE=maxInhabitantPerBlock;
	}
	
	public abstract void onLoad();
	
	public abstract void update();
	
	public abstract void buildProgram();
	
	public int[] getMinMaxHeight(Geography g, int oX, int oZ, int sizeX, int sizeZ)
	{
		int minimumHeight = -1;
		int maximumHeight = -1;
		for (int x = oX; x<=oX+sizeX; x++)
		{
			for (int z = oZ; z<=oZ+sizeZ; z++)
			{
				int[] values = g.calculateTransformedCoordinates(x, g.worldGroundLevel, z);
				int height = g.getPointHeight(values[3], values[5], values[0], values[2],x,z, false) + g.worldGroundLevel;
				if (height>maximumHeight)
				{
					maximumHeight = height;
				}
				if (height<minimumHeight||minimumHeight==-1)
				{
					minimumHeight = height;
				}
			}
		}
		return new int[]{minimumHeight,maximumHeight};
	}
	
	public void build(InfrastructureElementParameters param)
	{
		World world = (World)population.getRoot();
		Economic e = EconomyTemplate.economicBase.get(param.type);
		if (e instanceof EconomicGround)
		{
			EconomicGround ground = ((EconomicGround)e);
			
			int oX = 
					population.blockStartX+param.relOrigoX;
			int oZ = population.blockStartZ+param.relOrigoZ;
			
			int[] minMaxHeight = getMinMaxHeight(population.soilGeo, oX, oZ, param.sizeX, param.sizeZ);
			int minimumHeight = minMaxHeight[0];
			int maximumHeight = minMaxHeight[1];

			ground = ground.getInstance(population+"_"+param, population.soilGeo, population, world.economyContainer.treeLocator, 
					param.sizeX, maximumHeight-minimumHeight+2, param.sizeZ, 
					population.blockStartX+param.relOrigoX, minimumHeight, population.blockStartZ+param.relOrigoZ, 
					0, population.owner.homeBoundary, population.owner);
			population.addEcoGround(ground);
			System.out.println("ADDING ecoground "+(population.blockStartX+param.relOrigoX)+" "+(population.blockStartZ+param.relOrigoZ)+ " "+ground.sizeY+" "+ground.sizeX+"/"+ground.sizeZ);
		} else
		if (e instanceof Residence)
		{
			Residence res = ((Residence)e);
			
			int oX = 
					population.blockStartX+param.relOrigoX;
			int oZ = population.blockStartZ+param.relOrigoZ;
			
			int[] minMaxHeight = getMinMaxHeight(population.soilGeo, oX, oZ, param.sizeX, param.sizeZ);
			int minimumHeight = minMaxHeight[0];
			//int maximumHeight = minMaxHeight[1];

			res = res.getInstance(population+"_"+param, population.soilGeo, population, world.economyContainer.treeLocator, 
					param.sizeX, param.sizeY, param.sizeZ, 
					population.blockStartX+param.relOrigoX, minimumHeight, population.blockStartZ+param.relOrigoZ, 
					0, population.owner.homeBoundary, population.owner);
			population.addResidence(res);
			System.out.println("ADDING residence "+(population.blockStartX+param.relOrigoX)+" "+(population.blockStartZ+param.relOrigoZ)+ " "+res.sizeY+" "+res.sizeX+"/"+res.sizeZ);
			
		}
	}
	
	protected boolean loading = false;
	public void setLoadingState(boolean state)
	{
		loading = state;
	}
	
}
