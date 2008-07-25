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

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.World;


public abstract class AbstractInfrastructure {

	/**
	 * Minimum size mapped to the next array of infrastructure elements.
	 */
	public transient HashMap<Integer, ArrayList<InfrastructureElementParameters>> sizeDrivenBuildProgram = new HashMap<Integer,ArrayList<InfrastructureElementParameters>>();

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
	
	/**
	 * loader.
	 */
	public void onLoad()
	{
		sizeDrivenBuildProgram = new HashMap<Integer,ArrayList<InfrastructureElementParameters>>();
		lastUpdatedInhabitantNumber=-1;
		buildProgram();
	}

	
	public transient int lastUpdatedInhabitantNumber = -1;
	
	/**
	 * save game stores this number, so that on load the groups that joined in between eco update turns won't be counted in.
	 */
	public int savedInhabitantNumber = -1;

	
	/**
	 * Updates a population if size changed that much, running the buildprogram for the given population
	 * size.
	 */
	public void update() {
		
		// if loading, used the saved number instead of the actual number (which will be only used when a real new economy turn will come)
		int newNumber = loading?savedInhabitantNumber:getNearestSizeProgramCount(population.getNumberOfInhabitants());
		if (newNumber!=lastUpdatedInhabitantNumber) {
			population.clear();
			lastUpdatedInhabitantNumber = newNumber;
			savedInhabitantNumber = newNumber;
			ArrayList<InfrastructureElementParameters> toBuild = sizeDrivenBuildProgram.get(newNumber);
			if (toBuild!=null) {
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("AbstractInfrastructure.update: TO BUILD SIZE = "+toBuild.size()+" "+toBuild);

				for (InfrastructureElementParameters p:toBuild)
				{
					build(p);
				}
			}
		}
	}
	
	/**
	 * Returns the int value for which update should look up the list of infrastructures in the sizeDrivenBuildProgram hashmap.
	 * @param size
	 * @return the key.
	 */
	public int getNearestSizeProgramCount(int size)
	{
		return (size/(INHABITANTS_PER_UPDATE))*INHABITANTS_PER_UPDATE;
	}
	
	/**
	 * the program generation.
	 */
	public void buildProgram()
	{
		
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("AbstractInfrastructure.buildProgram: soilGeo="+population.soilGeo);
		ArrayList<Class<?extends Residence>> residenceTypes = population.owner.description.economyTemplate.residenceTypes.get(population.soilGeo.getClass());
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("AbstractInfrastructure.buildProgram: res: "+residenceTypes);
		ArrayList<Class<?extends EconomicGround>> groundTypes = population.owner.description.economyTemplate.groundTypes.get(population.soilGeo.getClass());
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("AbstractInfrastructure.buildProgram: ground: "+groundTypes);
		// base parts
		
		
		// collecting fix properties of fix NPCs of entityInstance
		ArrayList<InfrastructureElementParameters> fixProperties = new ArrayList<InfrastructureElementParameters>();
		for (PersistentMemberInstance i:population.owner.fixMembers.values())
		{
			if (i.getOwnedInfrastructures()!=null)
			for (InfrastructureElementParameters prop:i.getOwnedInfrastructures())
			{
				prop.owner = i;
				fixProperties.add(prop);
			}
		}
		
		prepareBuildProgram(fixProperties, residenceTypes, groundTypes);
		
		for (int i=0; i<maxInhabitantPerBlock*maxBlocks; i+=INHABITANTS_PER_UPDATE)
		{
			ArrayList<InfrastructureElementParameters> list = buildProgramLevel(i, fixProperties, residenceTypes, groundTypes);
			
			//Jcrpg.LOGGER.finer.println("adding program part to "+i+" -> "+sizeDrivenBuildProgram+" "+list);
			sizeDrivenBuildProgram.put(i, list);		
		}
		
	}

	/**
	 * Build the program for a given population size. The main things to code in an infrastructure extension. 
	 * @param sizeLevel
	 * @param fixProperties the fixed properties of NPC in this population.
	 * @param residenceTypes The usable residence types.
	 * @param groundTypes The usable ecoGround types
	 * @return the generated arraylist of the infrastructure elements for the given pop. size.
	 */
	public abstract ArrayList<InfrastructureElementParameters> buildProgramLevel(int sizeLevel, ArrayList<InfrastructureElementParameters> fixProperties, ArrayList<Class<?extends Residence>> residenceTypes, ArrayList<Class<?extends EconomicGround>> groundTypes);
	
	/**
	 * It's called in buildProgram, before starting the actual buildProgram process.
	 * @param fixProperties
	 * @param residenceTypes
	 * @param groundTypes
	 */
	public abstract void prepareBuildProgram(ArrayList<InfrastructureElementParameters> fixProperties, ArrayList<Class<?extends Residence>> residenceTypes, ArrayList<Class<?extends EconomicGround>> groundTypes);
	
	
	/**
	 * Return the surface height min and max in an area.
	 * @param g the Geography.
	 * @param oX
	 * @param oZ
	 * @param sizeX
	 * @param sizeZ
	 * @return [min, max]
	 */
	public int[] getMinMaxHeight(Geography g, int oX, int oZ, int sizeX, int sizeZ)
	{
		int minimumHeight = -1;
		int maximumHeight = -1;
		for (int x = oX; x<=oX+sizeX; x++)
		{
			for (int z = oZ; z<=oZ+sizeZ; z++)
			{
				int[] values = g.calculateTransformedCoordinates(x, g.worldGroundLevel, z);
				int height = g.getPointHeight(values[3], values[5], values[0], values[2],x,z, false)[0] + g.worldGroundLevel;
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
		return new int[]{minimumHeight+(maximumHeight-minimumHeight)/2,maximumHeight};
	}
	
	/**
	 * Add an instantiated economic to the parent population based on the parameters.    
	 * @param param
	 */
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
			// setting the owner member from the parameter
			if (param.owner!=null) {
				ground.setOwnerMember(param.owner);
				param.owner.addGeneratedOwnInfrastructure(ground);
			}
			population.addEcoGround(ground);
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("AbstractInfrastructure.build : ADDING ecoground "+(population.blockStartX+param.relOrigoX)+" "+(population.blockStartZ+param.relOrigoZ)+ " "+ground.sizeY+" "+ground.sizeX+"/"+ground.sizeZ);
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
			// setting the owner member from the parameter
			if (param.owner!=null) {
				res.setOwnerMember(param.owner);
				param.owner.addGeneratedOwnInfrastructure(res);
			}
			population.addResidence(res);
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("AbstractInfrastructure.build : ADDING residence "+(population.blockStartX+param.relOrigoX)+" "+(population.blockStartZ+param.relOrigoZ)+ " "+res.sizeY+" "+res.sizeX+"/"+res.sizeZ);
			
		}
	}
	
	protected boolean loading = false;
	public void setLoadingState(boolean state)
	{
		loading = state;
	}

	// block occupation methods

	/**
	 * Sets occupied building blocks specified by the area of the infrastructure element parameter. 
	 * @param param The parameter infrastructure.
	 */
	public void setOccupiedBlocks(boolean[] occupiedBlocks, InfrastructureElementParameters param)
	{
		for (int x=param.relOrigoX; x<param.relOrigoX+param.sizeX; x++)
		{
			for (int z=param.relOrigoZ; z<param.relOrigoZ+param.sizeZ; z++)
			{
				occupiedBlocks[x/BUILDING_BLOCK_SIZE+(z/BUILDING_BLOCK_SIZE)*maxBlocksOneDim] = true;
			}
		}
	}
	
	/**
	 * Maps a block counter (of the boolean array) to x,z coordinates. 
	 * @param count
	 * @return
	 */
	public int[] toBlockCoordinate(int count)
	{
		int z = count/maxBlocksOneDim;
		int x = count%maxBlocksOneDim;
		return new int[] {x,z};
	}
	/**
	 * Tells if a block at counter is occupied
	 * @param occupiedBlocks
	 * @param count
	 * @return
	 */
	public boolean isOccupiedBlock(boolean[] occupiedBlocks,int count)
	{
		return occupiedBlocks[count];
	}
	/**
	 * Tells if a block is occupied at x,z.
	 * @param occupiedBlocks
	 * @param x
	 * @param z
	 * @return
	 */
	public boolean isOccupiedBlock(boolean[] occupiedBlocks,int x, int z)
	{
		return isOccupiedBlock(occupiedBlocks,x+z*maxBlocksOneDim);
	}
	
}
