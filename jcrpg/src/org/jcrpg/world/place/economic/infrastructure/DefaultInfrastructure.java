package org.jcrpg.world.place.economic.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;

public class DefaultInfrastructure extends AbstractInfrastructure {

	/**
	 * Minimum size mapped to the next array of infrastructure elements.
	 */
	public transient HashMap<Integer, ArrayList<InfrastructureElementParameters>> sizeDrivenBuildProgram = new HashMap<Integer,ArrayList<InfrastructureElementParameters>>();
	
	public transient boolean[] occupiedBlocks;
	
	public transient int lastUpdatedInhabitantNumber = -1;
	
	public DefaultInfrastructure(Population population) {
		super(population);
		buildProgram();
	}
	
	public int[] fullShuffledBlocks;
	
	public void buildProgram()
	{
		fullShuffledBlocks = shuffleRange(population.blockStartX+population.blockStartZ+population.soilGeo.numericId, maxBlocks);
		
		System.out.println(population.soilGeo);
		ArrayList<Class<?extends Residence>> residenceTypes = population.owner.description.economyTemplate.residenceTypes.get(population.soilGeo.getClass());
		System.out.println("PROGRAM res: "+residenceTypes);
		ArrayList<Class<?extends EconomicGround>> groundTypes = population.owner.description.economyTemplate.groundTypes.get(population.soilGeo.getClass());
		System.out.println("PROGRAM ground: "+groundTypes);
		// base parts
		
		
		// collecting fix properties of fix NPCs of entityInstance
		ArrayList<InfrastructureElementParameters> fixProperties = new ArrayList<InfrastructureElementParameters>();
		for (EntityMemberInstance i:population.owner.fixMembers.values())
		{
			for (Class<?extends Economic> ecoClass:i.ownedInfrastructures)
			{
				InfrastructureElementParameters prop = new InfrastructureElementParameters();
				prop.type = ecoClass;
				prop.owner = i;
				fixProperties.add(prop);
			}
		}
		
		InfrastructureElementParameters mainStreet = new InfrastructureElementParameters();
		mainStreet.relOrigoX = 0;
		mainStreet.relOrigoY = 0;
		mainStreet.relOrigoZ = BUILDING_BLOCK_SIZE*(maxBlocksOneDim/2);
		mainStreet.sizeX = maxSize;
		mainStreet.sizeY = 1;
		mainStreet.sizeZ = BUILDING_BLOCK_SIZE;
		mainStreet.type = groundTypes.get(0);
		
		
		
		for (int i=0; i<maxInhabitantPerBlock*maxBlocks; i+=INHABITANTS_PER_UPDATE)
		{
			ArrayList<InfrastructureElementParameters> list = new ArrayList<InfrastructureElementParameters>();
			list.add(mainStreet);
			//list.add(baseResidence);
			occupiedBlocks = new boolean[maxBlocks];
			setOccupiedBlocks(mainStreet);
			int shuffleCount = 0;
			for (int j=0; j<(i/maxInhabitantPerBlock)+1; j++)
			{
				int blockCount = -1;
				int oldShuffleCount = shuffleCount;
				while (true) {
					blockCount = fullShuffledBlocks[shuffleCount];
					shuffleCount = (shuffleCount+1)%fullShuffledBlocks.length;
					if (shuffleCount == oldShuffleCount) {
						blockCount=-1;
						System.out.println("!!! NO BLOCK FOUND FOR RESIDENCE...");
						break; // arrived to the beginning counter, no room found
					}
					if (isOccupiedBlock(blockCount)) continue;
					break;
				}
				if (blockCount!=-1) {
					int[] coord = toBlockCoordinate(blockCount);
					InfrastructureElementParameters baseResidence = new InfrastructureElementParameters();
					baseResidence.relOrigoX = BUILDING_BLOCK_SIZE*(coord[0]);
					baseResidence.relOrigoY = 0;
					baseResidence.relOrigoZ = BUILDING_BLOCK_SIZE*(coord[1]);
					baseResidence.sizeX = BUILDING_BLOCK_SIZE;
					baseResidence.sizeY = 1;
					baseResidence.sizeZ = BUILDING_BLOCK_SIZE;
					baseResidence.type = residenceTypes.get(0);
					list.add(baseResidence);
					setOccupiedBlocks(baseResidence);
					
				} else
				{
					System.out.println("NO BLOCK...");
					break; // no free space found...
				}
				
			}
			//System.out.println("adding program part to "+i);
			sizeDrivenBuildProgram.put(i, list);		
		}
		
	}
	
	public int[] shuffleRange(long seed, int maximum)
	{
		int[] ret = new int[maximum];
		for (int i=0; i<maximum; i++)
		{
			ret[i] = -1;
		}
		for (int i=0; i<maximum; i++)
		{
			int r = HashUtil.mixPer1000((int)seed, i, i+1, i+2);
			r = r%maximum;
			while (ret[r]!=-1)
			{
				r = (r+1)%maximum;
			}
			ret[r] = i;
		}
		return ret;
	}
	
	public void setOccupiedBlocks(InfrastructureElementParameters mainStreet)
	{
		for (int x=mainStreet.relOrigoX; x<mainStreet.sizeX; x++)
		{
			for (int z=mainStreet.relOrigoZ; z<mainStreet.sizeZ; z++)
			{
				occupiedBlocks[x+z*maxBlocksOneDim] = true;
			}
		}
	}
	
	public int[] toBlockCoordinate(int count)
	{
		int z = count/maxBlocksOneDim;
		int x = count%maxBlocksOneDim;
		return new int[] {x,z};
	}
	public boolean isOccupiedBlock(int count)
	{
		return occupiedBlocks[count];
	}
	public boolean isOccupiedBlock(int x, int z)
	{
		return occupiedBlocks[x+z*maxBlocksOneDim];
	}
	
	public void onLoad()
	{
		buildProgram();
	}
	

	public void build(InfrastructureElementParameters p)
	{
		World world = (World)population.getRoot();
		Economic e = EconomyTemplate.economicBase.get(p.type);
		if (e instanceof EconomicGround)
		{
			EconomicGround ground = ((EconomicGround)e);
			
			int oX = 
					population.blockStartX+p.relOrigoX;
			int oZ = population.blockStartZ+p.relOrigoZ;
			
			int[] minMaxHeight = getMinMaxHeight(population.soilGeo, oX, oZ, p.sizeX, p.sizeZ);
			int minimumHeight = minMaxHeight[0];
			int maximumHeight = minMaxHeight[1];

			ground = ground.getInstance(population+"_"+p, population.soilGeo, population, world.economyContainer.treeLocator, 
					p.sizeX, maximumHeight-minimumHeight+2, p.sizeZ, 
					population.blockStartX+p.relOrigoX, minimumHeight, population.blockStartZ+p.relOrigoZ, 
					0, population.owner.homeBoundary, population.owner);
			population.addEcoGround(ground);
			System.out.println("ADDING ecoground "+(population.blockStartX+p.relOrigoX)+" "+(population.blockStartZ+p.relOrigoZ)+ " "+ground.sizeY+" "+ground.sizeX+"/"+ground.sizeZ);
		} else
		if (e instanceof Residence)
		{
			Residence ground = ((Residence)e);
			
			int oX = 
					population.blockStartX+p.relOrigoX;
			int oZ = population.blockStartZ+p.relOrigoZ;
			
			int[] minMaxHeight = getMinMaxHeight(population.soilGeo, oX, oZ, p.sizeX, p.sizeZ);
			int minimumHeight = minMaxHeight[0];
			int maximumHeight = minMaxHeight[1];

			ground = ground.getInstance(population+"_"+p, population.soilGeo, population, world.economyContainer.treeLocator, 
					p.sizeX, maximumHeight-minimumHeight+2, p.sizeZ, 
					population.blockStartX+p.relOrigoX, minimumHeight, population.blockStartZ+p.relOrigoZ, 
					0, population.owner.homeBoundary, population.owner);
			population.addResidence(ground);
			System.out.println("ADDING residence "+(population.blockStartX+p.relOrigoX)+" "+(population.blockStartZ+p.relOrigoZ)+ " "+ground.sizeY+" "+ground.sizeX+"/"+ground.sizeZ);
			
		}
	}
	
	public int getNearestSizeProgramCount(int size)
	{
		return (size/(INHABITANTS_PER_UPDATE))*INHABITANTS_PER_UPDATE;
	}
	
	@Override
	public void update() {
		
		int newNumber = getNearestSizeProgramCount(population.getNumberOfInhabitants());
		if (newNumber!=lastUpdatedInhabitantNumber) {
			lastUpdatedInhabitantNumber = newNumber;
			ArrayList<InfrastructureElementParameters> toBuild = sizeDrivenBuildProgram.get(newNumber);
			if (toBuild!=null) {
				System.out.println("TO BUILD SIZE = "+toBuild.size()+" "+toBuild);

				for (InfrastructureElementParameters p:toBuild)
				{
					build(p);
				}
			}
		}
				
	}

}
