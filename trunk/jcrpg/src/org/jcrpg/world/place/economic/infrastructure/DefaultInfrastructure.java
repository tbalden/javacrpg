package org.jcrpg.world.place.economic.infrastructure;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.InfrastructureElementParameters;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;

/**
 * Default infrastructure builder, on a completely shuffled basis, with a main street in middle.
 * @author illes
 *
 */
public class DefaultInfrastructure extends AbstractInfrastructure {

	
	//public transient boolean[] occupiedBlocks;
	
	
	public DefaultInfrastructure(Population population) {
		super(population);
		buildProgram();
	}
	
	/**
	 * the maxBlock numbers shuffled in a pseudo random order (the order is always the same at the given
	 * geo block coordinates).
	 */
	public transient int[] fullShuffledBlocks;
	

	public ArrayList<InfrastructureElementParameters> buildProgramLevel(int sizeLevel, ArrayList<InfrastructureElementParameters> fixProperties, ArrayList<Class<?extends Residence>> residenceTypes, ArrayList<Class<?extends EconomicGround>> groundTypes)
	{
		boolean[] occupiedBlocks = new boolean[maxBlocks];
		ArrayList<InfrastructureElementParameters> list = new ArrayList<InfrastructureElementParameters>();
		list.add(mainStreet);
		setOccupiedBlocks(occupiedBlocks,mainStreet);
		
		int shuffleCount = 0;
		for (int j=0; j<(sizeLevel/maxInhabitantPerBlock)+1; j++)
		{
			int blockCount = -1;
			int oldShuffleCount = shuffleCount;
			while (true) {
				blockCount = fullShuffledBlocks[shuffleCount];
				shuffleCount = (shuffleCount+1)%fullShuffledBlocks.length;
				if (shuffleCount == oldShuffleCount) {
					blockCount=-1;
					//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("!!! NO BLOCK FOUND FOR RESIDENCE...");
					break; // arrived to the beginning counter, no room found
				}
				if (isOccupiedBlock(occupiedBlocks,blockCount)) {
					//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("###!!! OCCUPIED BLOCK FOUND FOR RESIDENCE..."+shuffleCount+" "+blockCount);
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
				if (baseResidence.sizeY<((Residence)EconomyTemplate.economicBase.get(baseResidence.type)).getMinimumHeight())
				{
					baseResidence.sizeY = ((Residence)EconomyTemplate.economicBase.get(baseResidence.type)).getMinimumHeight();
				}
				list.add(baseResidence);
				setOccupiedBlocks(occupiedBlocks,baseResidence);
				
			} else
			{
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("DefaultInfrastructure NO MORE BLOCK...");
				break; // no free space found...
			}
			
		}
		return list;
	}
	
	InfrastructureElementParameters mainStreet = null;
	public void prepareBuildProgram(ArrayList<InfrastructureElementParameters> fixProperties, ArrayList<Class<?extends Residence>> residenceTypes, ArrayList<Class<?extends EconomicGround>> groundTypes)
	{
		//if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("-- NUM ID = "+(population.blockStartX+population.blockStartZ+population.soilGeo.numericId));
		fullShuffledBlocks = shuffleRange(population.blockStartX+population.blockStartZ+population.soilGeo.numericId, maxBlocks,6,2);
		/*StringBuffer b = new StringBuffer();
		for (int i=0; i<fullShuffledBlocks.length; i++)
		{
			b.append(fullShuffledBlocks[i]).append(", ");
		}
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("SHUFFLED = "+b);*/
		mainStreet = new InfrastructureElementParameters();
		mainStreet.relOrigoX = 0;
		mainStreet.relOrigoY = 0;
		mainStreet.relOrigoZ = BUILDING_BLOCK_SIZE*(maxBlocksOneDim/2);
		mainStreet.sizeX = maxSize;
		mainStreet.sizeY = 1;
		mainStreet.sizeZ = BUILDING_BLOCK_SIZE;
		mainStreet.type = groundTypes.get(0);	
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
	
	

}
