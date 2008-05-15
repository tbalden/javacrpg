package org.jcrpg.world.place.economic.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;

/**
 * Default infrastructure builder, on a completely shuffled basis, with a main street in middle.
 * @author illes
 *
 */
public class DefaultInfrastructure extends AbstractInfrastructure {

	/**
	 * Minimum size mapped to the next array of infrastructure elements.
	 */
	public transient HashMap<Integer, ArrayList<InfrastructureElementParameters>> sizeDrivenBuildProgram = new HashMap<Integer,ArrayList<InfrastructureElementParameters>>();
	
	//public transient boolean[] occupiedBlocks;
	
	public transient int lastUpdatedInhabitantNumber = -1;
	
	/**
	 * save game stores this number, so that on load the groups that joined in between eco update turns won't be counted in.
	 */
	public int savedInhabitantNumber = -1;
	
	public DefaultInfrastructure(Population population) {
		super(population);
		buildProgram();
	}
	
	/**
	 * the maxBlock numbers shuffled in a pseudo random order (the order is always the same at the given
	 * geo block coordinates).
	 */
	public transient int[] fullShuffledBlocks;
	
	/**
	 * the pseudo-random program generation.
	 */
	public void buildProgram()
	{
		//System.out.println("-- NUM ID = "+(population.blockStartX+population.blockStartZ+population.soilGeo.numericId));
		fullShuffledBlocks = shuffleRange(population.blockStartX+population.blockStartZ+population.soilGeo.numericId, maxBlocks,6,2);
		/*StringBuffer b = new StringBuffer();
		for (int i=0; i<fullShuffledBlocks.length; i++)
		{
			b.append(fullShuffledBlocks[i]).append(", ");
		}
		System.out.println("SHUFFLED = "+b);*/
		
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
			boolean[] occupiedBlocks = new boolean[maxBlocks];

			ArrayList<InfrastructureElementParameters> list = new ArrayList<InfrastructureElementParameters>();
			list.add(mainStreet);

			setOccupiedBlocks(occupiedBlocks,mainStreet);
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
						//System.out.println("!!! NO BLOCK FOUND FOR RESIDENCE...");
						break; // arrived to the beginning counter, no room found
					}
					if (isOccupiedBlock(occupiedBlocks,blockCount)) {
						//System.out.println("###!!! OCCUPIED BLOCK FOUND FOR RESIDENCE..."+shuffleCount+" "+blockCount);
						blockCount = -1;
						continue;
					}
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
					setOccupiedBlocks(occupiedBlocks,baseResidence);
					
				} else
				{
					System.out.println("NO BLOCK...");
					break; // no free space found...
				}
				
			}
			//System.out.println("adding program part to "+i+" -> "+sizeDrivenBuildProgram+" "+list);
			sizeDrivenBuildProgram.put(i, list);		
		}
		
	}
	
	/**
	 * shuffle algorithm, returns a 'maximum' sized array of ints, with 0-maximum integers in it shuffled. 
	 * @param seed The rendom seed for the hash util.
	 * @param maximum The maximum of the range. (the minimum is 0.)
	 * @return the shuffled range.
	 */
	public int[] shuffleRange(long seed, int maximum, int stickyRange, int leapStick)
	{
		int[] ret = new int[maximum];
		for (int i=0; i<maximum; i++)
		{
			ret[i] = -1;
		}
		int toGo = maximum-1;
		while (toGo>=0)
		{
			int r = HashUtil.mixPer1000((int)seed, toGo, toGo+1, toGo+2);
			r = r%maximum;
			int sticking = stickyRange;
			while (sticking-->0)
			{
				boolean leapDone = false;
				while (ret[r]!=-1)
				{
					if (!leapDone && sticking%leapStick==0) {
						r = (r+maxBlocksOneDim-leapStick)%maximum;
						leapDone = true;
					} else
					{
						r = (r+1)%maximum;
					}
				}
				ret[r] = toGo;
				toGo--;
				if (toGo<0) break; 
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param param
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
	
	public int[] toBlockCoordinate(int count)
	{
		int z = count/maxBlocksOneDim;
		int x = count%maxBlocksOneDim;
		return new int[] {x,z};
	}
	public boolean isOccupiedBlock(boolean[] occupiedBlocks,int count)
	{
		return occupiedBlocks[count];
	}
	public boolean isOccupiedBlock(boolean[] occupiedBlocks,int x, int z)
	{
		return occupiedBlocks[x+z*maxBlocksOneDim];
	}
	
	public void onLoad()
	{
		sizeDrivenBuildProgram = new HashMap<Integer,ArrayList<InfrastructureElementParameters>>();
		lastUpdatedInhabitantNumber=-1;
		buildProgram();
	}
	
	public int getNearestSizeProgramCount(int size)
	{
		return (size/(INHABITANTS_PER_UPDATE))*INHABITANTS_PER_UPDATE;
	}
	
	@Override
	public void update() {
		
		// if loading, used the saved number instead of the actual number (which will be only used when a real new economy turn will come)
		int newNumber = loading?savedInhabitantNumber:getNearestSizeProgramCount(population.getNumberOfInhabitants());
		if (newNumber!=lastUpdatedInhabitantNumber) {
			population.clear();
			lastUpdatedInhabitantNumber = newNumber;
			savedInhabitantNumber = newNumber;
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
