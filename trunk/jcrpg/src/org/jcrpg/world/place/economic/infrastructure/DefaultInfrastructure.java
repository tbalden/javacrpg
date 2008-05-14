package org.jcrpg.world.place.economic.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.AbstractInfrastructure;
import org.jcrpg.world.place.economic.EconomicGround;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.Residence;
import org.jcrpg.world.place.geography.sub.Cave;

public class DefaultInfrastructure extends AbstractInfrastructure {

	/**
	 * Minimum size mapped to the next array of infrastructure elements.
	 */
	public transient HashMap<Integer, ArrayList<InfrastructureElementParameters>> sizeDrivenBuildProgram = new HashMap<Integer,ArrayList<InfrastructureElementParameters>>();
	
	public transient boolean[] occupiedBlocks;
	
	public transient int lastUpdatedInhabitantNumber = 0;
	
	public DefaultInfrastructure(Population population) {
		super(population);
		buildProgram();
	}
	
	public void buildProgram()
	{
		occupiedBlocks = new boolean[maxBlocks];
		
		System.out.println(population.soilGeo);
		ArrayList<Class<?extends Residence>> residenceTypes = population.owner.description.economyTemplate.residenceTypes.get(population.soilGeo.getClass());
		System.out.println("PROGRAM res: "+residenceTypes);
		ArrayList<Class<?extends EconomicGround>> groundTypes = population.owner.description.economyTemplate.groundTypes.get(population.soilGeo.getClass());
		System.out.println("PROGRAM ground: "+groundTypes);
		// base parts
		ArrayList<InfrastructureElementParameters> baseList = new ArrayList<InfrastructureElementParameters>();
		sizeDrivenBuildProgram.put(0, baseList);
		
		InfrastructureElementParameters mainStreet = new InfrastructureElementParameters();
		mainStreet.relOrigoX = 0;
		mainStreet.relOrigoY = 0;
		mainStreet.relOrigoZ = population.blockSize/2;
		mainStreet.sizeX = maxSize;
		mainStreet.sizeY = 1;
		mainStreet.sizeZ = BUILDING_BLOCK_SIZE;
		mainStreet.type = groundTypes.get(0);
		baseList.add(mainStreet);
		
		// growing parts		
		//for (int i=0; i<max; i++)
		//{
			
		//}
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

			ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(oX, oZ);
			int maximumHeight = -1;
			int minimumHeight = -1;
			if (surfaces.size()>0)
			{
				
				for (SurfaceHeightAndType[] s:surfaces)
				{
					//int Y = s[0].surfaceY;
					Geography g = s[0].self;
					System.out.println("G: "+g);
					// TODO geography preference in economyTemplate order -> not going through surfaces up here, but go through economyTemplate's geographies
					// and see if surface is available -> ordering problems solution
					if (population.soilGeo.getClass().equals(g.getClass()))
					{
						
						for (int x = oX; x<=oX+p.sizeX; x++)
						{
							for (int z = oZ; z<=oZ+p.sizeZ; z++)
							{
								int[] values = g.calculateTransformedCoordinates(x, g.worldGroundLevel, z);
								int height = g.getPointHeight(values[3], values[5], values[0], values[2],x,z, false) + g.worldGroundLevel;
								//System.out.println("HEIGHT = "+height);
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
					}
				}
			}
			ground = ground.getInstance(population+"_"+p, population.soilGeo, population, world.economyContainer.treeLocator, 
					p.sizeX, maximumHeight-minimumHeight+2, p.sizeZ, 
					population.blockStartX+p.relOrigoX, minimumHeight, population.blockStartZ+p.relOrigoZ, 
					0, population.owner.homeBoundary, population.owner);
			population.addEcoGround(ground);
			System.out.println("ADDING ecoground "+(population.blockStartX+p.relOrigoX)+" "+(population.blockStartZ+p.relOrigoZ)+ " "+ground.sizeY+" "+ground.sizeX+"/"+ground.sizeZ);
		}
	}
	
	@Override
	public void update() {
		for (int i=lastUpdatedInhabitantNumber; i<population.getNumberOfInhabitants(); i++)
		{
			ArrayList<InfrastructureElementParameters> toBuild = sizeDrivenBuildProgram.get(i);
			if (toBuild!=null) {
				System.out.println("TO BUILD SIZE = "+toBuild.size()+" "+toBuild);

				for (InfrastructureElementParameters p:toBuild)
				{
					build(p);
				}
			}
		}
		lastUpdatedInhabitantNumber = population.getNumberOfInhabitants();
		
		if (true) return;
		
		int hsizeX =5 , hsizeY = 2, hsizeZ = 5;
		int xOffset = 0;
		int zOffset = 0;
		int streetSize = 0;
		int sizeX = (int)Math.sqrt(population.getNumberOfInhabitants())+1;
		int sizeY = sizeX;
		System.out.println("SIZEX/Y"+sizeX+" "+sizeY);
		EntityInstance owner = population.owner; // TODO create districts for different owners?
		for (int x1=0; x1<sizeX; x1++)
		{
			hsizeX = 4;
			zOffset = 0;
			for (int z1=0; z1<sizeY; z1++)
			{
				int i = x1*sizeX+z1;
				//System.out.println("Addig "+i);
				if (i>=owner.getGroupSizes().length) continue;
				
				{
					hsizeY = Math.max(1,1+owner.getGroupSizes()[i]/4);
					hsizeZ = 4;
				}
				
				if (population.residenceList.size()<=i)
				{
					
					World world = (World)population.getRoot();
					ArrayList<SurfaceHeightAndType[]> surfaces = world.getSurfaceData(owner.homeBoundary.posX, owner.homeBoundary.posZ+zOffset);
					if (surfaces.size()>0)
					{
						
						for (SurfaceHeightAndType[] s:surfaces)
						{
							//int Y = s[0].surfaceY;
							Geography g = s[0].self;
							System.out.println("G: "+g);
							// TODO geography preference in economyTemplate order -> not going through surfaces up here, but go through economyTemplate's geographies
							// and see if surface is available -> ordering problems solution
							ArrayList<Class<?extends Residence>> list = ((HumanoidEntityDescription)(owner.description)).economyTemplate.residenceTypes.get(g.getClass());
							if (list!=null && list.size()>0)
							{
								Class<? extends Residence> r = list.get(0);
								int maximumHeight = -1;
								int minimumHeight = -1;
								for (int x = owner.homeBoundary.posX; x<=owner.homeBoundary.posX+hsizeX; x++)
								{
									for (int z = owner.homeBoundary.posZ+zOffset; z<=owner.homeBoundary.posZ+zOffset+hsizeZ; z++)
									{
										int[] values = g.calculateTransformedCoordinates(x, g.worldGroundLevel, z);
										int height = g.getPointHeight(values[3], values[5], values[0], values[2],x,z, false) + g.worldGroundLevel;
										//System.out.println("HEIGHT = "+height);
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
								
								if (world.economyContainer.getEconomicCube(-1, owner.homeBoundary.posX, maximumHeight, owner.homeBoundary.posZ+zOffset, false)!=null)
								{
									continue;
								}
								
								
								if (world.economyContainer.getEconomicCube(-1, owner.homeBoundary.posX, maximumHeight, owner.homeBoundary.posZ, false)!=null)
								{
									continue;
								}
								EconomicGround eg = null;
								Residence rI = null;
								if (i%2 == 0)
								{
									rI = ((Residence)EconomyTemplate.economicBase.get(r)).getInstance(
										"house"+owner.id+"_"+owner.homeBoundary.posX+"_"+maximumHeight+"_"+(owner.homeBoundary.posZ+zOffset),
										g,world,world.economyContainer.treeLocator,hsizeX,hsizeY,hsizeZ,
										owner.homeBoundary.posX+xOffset,maximumHeight,owner.homeBoundary.posZ+zOffset,0,
										owner.homeBoundary, owner);
									
									population.addResidence(rI);
								} else {
									
									try {
										eg = new EconomicGround("house"+owner.id+"_"+owner.homeBoundary.posX+"_"+maximumHeight+"_"+(owner.homeBoundary.posZ+zOffset),
											g,world,world.economyContainer.treeLocator,hsizeX,maximumHeight-minimumHeight+2,hsizeZ,
											owner.homeBoundary.posX+xOffset,minimumHeight,owner.homeBoundary.posZ+zOffset,0,
											owner.homeBoundary, owner);
										population.addEcoGround(eg);
									} catch (Exception ex)
									{
										ex.printStackTrace();
									}
								}
								
								if (eg!=null) System.out.println("ADDING GROUNDS!"+x1+":"+z1+" __ "+minimumHeight+ " - " + maximumHeight+ " "+eg.id);
								if (rI!=null) System.out.println("ADDING HOUSE!"+x1+":"+z1+" __ "+minimumHeight+ " - " + maximumHeight+ " "+rI.id);
								if (population.soilGeo instanceof Cave)
								{
								//	System.out.println("### CAVE HOUSE = "+rI.id);
								}
								//addResidence(rI);
								break;
							}
							
						}
					}
				}
				zOffset+=(hsizeZ+streetSize);
			}
			xOffset+=(hsizeX+streetSize);
		}
				
	}

}
