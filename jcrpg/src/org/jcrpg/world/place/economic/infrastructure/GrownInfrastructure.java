package org.jcrpg.world.place.economic.infrastructure;

import java.util.ArrayList;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;

public class GrownInfrastructure extends AbstractInfrastructure {

	public GrownInfrastructure(Population population) {
		super(population);
		buildProgram();
	}

	@Override
	public ArrayList<InfrastructureElementParameters> buildProgramLevel(
			int sizeLevel,
			ArrayList<InfrastructureElementParameters> fixProperties,
			ArrayList<Class<? extends Residence>> residenceTypes,
			ArrayList<Class<? extends EconomicGround>> groundTypes) {

		lastStreetBlockX = maxBlocksOneDim/2; // center the street pointer
		lastStreetBlockZ = maxBlocksOneDim/2; // center the street pointer
		blockUsed = 0;

		ArrayList<InfrastructureElementParameters> list = new ArrayList<InfrastructureElementParameters>();
		
		long seed = population.soilGeo.numericId+population.blockStartX+population.blockStartZ;
		boolean[] occupiedBlocks = new boolean[maxBlocks];
		blockUsed = 0;
		
		for (int j=0; j<(sizeLevel/maxInhabitantPerBlock)+1; j++)
		{
			if (blockUsed>maxBlocks) continue;
			int delta = 0;
			
			boolean found = false;
			while (true){
				int r = HashUtil.mixPercentage((int)seed, j+(delta++), j+1, j+2);
				int [] coords = getRandomCoordinates(r, lastStreetBlockX, lastStreetBlockZ);
				if (isOccupiedBlock(occupiedBlocks, coords[0], coords[1]))
				{
					if (delta>10)
					{
						break;
					}
					continue;
				}
				lastStreetBlockX = coords[0];
				lastStreetBlockZ = coords[1];
				found = true;
				break;
			}
			if (!found)
			{
				delta = 0;
				while (true)
				{
					lastStreetBlockX = (lastStreetBlockX+1)%maxBlocksOneDim;
					lastStreetBlockZ = (lastStreetBlockZ+1)%maxBlocksOneDim;
					if (!isOccupiedBlock(occupiedBlocks, lastStreetBlockX,lastStreetBlockZ))
					{
						break;
					}
					delta++;
					if (delta>10) break;
				}
			}
			if (delta<11) {
				InfrastructureElementParameters p = new InfrastructureElementParameters();
				p.relOrigoX = lastStreetBlockX*BUILDING_BLOCK_SIZE;
				p.relOrigoY = 0;
				p.relOrigoZ = lastStreetBlockZ*BUILDING_BLOCK_SIZE;
				p.sizeX = BUILDING_BLOCK_SIZE;
				p.sizeY = (blockUsed%5)/2+1;
				p.sizeZ = BUILDING_BLOCK_SIZE;
				if (blockUsed%2==0) {
					p.type = groundTypes.get(0);
				} else
				{
					p.type = residenceTypes.get(0);
				}
				list.add(p);
				setOccupiedBlocks(occupiedBlocks,p);
				blockUsed++;
			} else {
				break;
			}
		}
		return list;
	}
	
	public int[] getRandomCoordinates(int r, int x, int z)
	{
		while (true) {
			if (r<33)
			{
				x++;
			} else
			if (r<66)
			{
				z++;
			} else
			if (r<99)
			{
				x--;
			} else
			{
				z--;
			}
			
			/*if (x<0)
			{
				x+=2;
				if (x>maxBlocksOneDim)
				{
					
				}
			}*/
			break;
		}
		
		return new int[]{Math.abs(x)%maxBlocksOneDim,Math.abs(z)%maxBlocksOneDim};
	}
	
	public int lastStreetBlockX = -1;
	public int lastStreetBlockZ = -1;
	
	public int blockUsed = 0;

	@Override
	public void prepareBuildProgram(
			ArrayList<InfrastructureElementParameters> fixProperties,
			ArrayList<Class<? extends Residence>> residenceTypes,
			ArrayList<Class<? extends EconomicGround>> groundTypes) {
		
		
	}


}
