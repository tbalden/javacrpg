package org.jcrpg.world.place.economic.road;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.audio.AudioServer;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.Climbing;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.place.Boundaries;
import org.jcrpg.world.place.BoundaryUtils;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.EconomyContainer;
import org.jcrpg.world.place.FlowGeography;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.Place;
import org.jcrpg.world.place.PlaceLocator;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.Water;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.WorldSizeBitBoundaries;
import org.jcrpg.world.place.WorldSizeFlowDirections;
import org.jcrpg.world.place.economic.Town;
import org.jcrpg.world.place.geography.sub.Cave;
import org.jcrpg.world.place.water.Ocean;

import com.jme.math.Vector2f;

/**
 * Class of all the overland roads - connecting towns.
 * @author pali
 *
 */
public class RoadNetwork extends Economic implements FlowGeography {
	
	// TODO Handle the flow direction  in getCubeObject!
	// Add ocean avoiding road network generation :) 

	
	public EconomyContainer container;
	
	transient Boundaries boundaries;
	
	public RoadNetwork(String id, EconomyContainer container,int worldGroundLevel, int magnification, int sizeX, int sizeY, int sizeZ, int origoX, int origoY, int origoZ) throws Exception
	{
		super(id,null, container.w,null, null,null);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.worldRelHeight = worldHeight - worldGroundLevel;
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("" + this.getClass()+" "+worldGroundLevel+" "+ "SIZE = "+worldRelHeight+ " --- "+worldGroundLevel/magnification+" - "+ origoY);
		this.sizeZ = sizeZ;
		this.origoX = origoX;
		this.origoY = origoY;
		this.origoZ = origoZ;
		//this.groundLevel = groundLevel;
		this.worldGroundLevel=worldGroundLevel;
		this.worldHeight = worldHeight;
		this.blockSize = magnification;
		
		this.container = container;
		this.magnification = blockSize;

		//flowDirections = new WorldSizeFlowDirections(magnification,(World)parent);
		//setBoundaries(new WorldSizeBitBoundaries(magnification,(World)parent));
	}
	
	
	public class Connection 
	{
		public Town a,b;
		public Connection(Town a,Town b)
		{
			this.a = a;
			this.b = b;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o==null) return false;
			if (o instanceof Connection)
			{
				if (
						((Connection) o).a == a && 
						((Connection) o).b == b || 
						((Connection) o).a == b && 
						((Connection) o).b == a 
					)
					return true;
						
			}
			return false;
		}
		@Override
		public String toString()
		{
			return a.foundationName + a.getCenterCoordinates()[0] +"/"+ a.getCenterCoordinates()[1] +" -- "+b.foundationName + b.getCenterCoordinates()[0] +"/"+ b.getCenterCoordinates()[1];
		}
	}
	
	transient ArrayList<Connection> connections = new ArrayList<Connection>();

	public int TOWNSIZE_THRESHOLD = 2;
	
	

	/**
	 * Updates road network, based on size and distance (closest bigger cities are connected). 
	 */
	public void updateRoads()
	{
		connections = new ArrayList<Connection>();
		flowDirections = new WorldSizeFlowDirections(magnification,(World)parent);
		setBoundaries(new WorldSizeBitBoundaries(magnification,(World)parent));
		
		ArrayList<Town> bigTowns = new ArrayList<Town>();
		
		for (Town t:container.towns)
		{
			if (t.getSize()>=TOWNSIZE_THRESHOLD)
			{
				System.out.println("BIG TOWN: "+t + " "+t.getCenterCoordinates());
				bigTowns.add(t);
			}
		}

		HashMap<Town, Town> closest = new HashMap<Town, Town>();
		HashMap<Town, Float> closestDist = new HashMap<Town, Float>();
		HashMap<Town, Town> secondClosest = new HashMap<Town, Town>();

		for (Town bt:bigTowns)
		{
			if (bt.getCenterCoordinates()==null) continue;
			for (Town bt2:bigTowns)
			{
				if (bt2.getCenterCoordinates()==null) continue;
				if (bt!=bt2)
				{
					int cX1 = bt.getCenterCoordinates()[0];
					int cZ1 = bt.getCenterCoordinates()[1];
					int cX2 = bt2.getCenterCoordinates()[0];
					int cZ2 = bt2.getCenterCoordinates()[1];
					
					Vector2f v = new Vector2f(cX1,cZ1);
					Vector2f v2 = new Vector2f(cX2, cZ2);
					Float distNew = v.distance(v2);
					
					
					Float dist = closestDist.get(bt);
					if (dist==null || distNew<dist)
					{
						closestDist.put(bt, distNew);
						if (closest.get(bt)!=null)
						{
							secondClosest.put(bt, closest.get(bt));
						}
						closest.put(bt, bt2);
					}
				}
			}
		}
		
		TreeMap<String, Town> orderedTowns = new TreeMap<String, Town>();
		
		for (Town t:closest.keySet())
		{
			orderedTowns.put(t.foundationName, t);
		}
		
		for (Town t:orderedTowns.values())
		{
			Town t2 = closest.get(t);
			Town t3 = secondClosest.get(t);
			if (t2!=null)
			{
				Connection c = new Connection(t,t2);
				boolean found = false;
				for (Connection lC:connections)
				{
					if (lC.equals(c)) {found = true;break;}
				}
				if (!found) connections.add(c);
			}
			if (t3!=null && t2!=t3)
			{
				Connection c = new Connection(t,t3);
				boolean found = false;
				for (Connection lC:connections)
				{
					if (lC.equals(c)) {found = true;break;}
				}
				if (!found) connections.add(c);
			}
		}
		
		// iterate connections, draw line between them like a river flowing (use flow directions)
		
		// check how to build the graph, and store it efficiently to read up which
		// block coordinates of the world contains a road - in !transient! form (not saved) 
		// + getCube and such...

		System.out.println("TOWN CONNECTIONS CREATED: ");
		for (Connection c:connections)
		{
			System.out.println("---------- "+c.toString());
			
			
			int[] coords1 = c.a.getCenterCoordinates().clone();
			int[] coords2 = c.b.getCenterCoordinates().clone();
			
			coords1[0] = coords1[0]/blockSize;
			coords1[1] = coords1[1]/blockSize;
			coords2[0] = coords2[0]/blockSize;
			coords2[1] = coords2[1]/blockSize;
			
			int diffX = coords1[0] - coords2[0];
			int diffZ = coords1[1] - coords2[1];

			int startX = 0;
			int startZ = 0;
			int endX = 0;
			int endZ = 0;
			if (diffX<0)
			{
				startX = coords1[0];
				endX = coords2[0];
			} else
			{
				startX = coords2[0];
				endX = coords1[0];
			}
			if (diffZ<0)
			{
				startZ = coords1[1];
				endZ = coords2[1];
			} else
			{
				startZ = coords2[1];
				endZ = coords1[1];
			}

			if (Math.abs(diffX)>Math.abs(diffZ))
			{
				float deltaZperXunit = diffZ*1f/diffX;

				// road drawing - geo block by block
				
				float zChange = 0;
				int z = startZ;
				for (int x=startX; x<=endX; x++)
				{

					// draw X+1
					try {
						System.out.println(x+" - "+z);
						getWorldSizeFlowDirections().setCubeFlowDirection(x, container.w.getSeaLevel(blockSize), z, J3DCore.WEST, true); 
						getBoundaries().addCube(magnification, x, container.w.getSeaLevel(blockSize), z);
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
					
					// if needed draw Z+1
					zChange+=deltaZperXunit;
					if (zChange>=1f)
					{
						zChange = zChange-1f;
						z++;
						try {
							System.out.println(x+" | "+z);
							getWorldSizeFlowDirections().setCubeFlowDirection(x, container.w.getSeaLevel(blockSize), z, J3DCore.NORTH, true);
							getWorldSizeFlowDirections().setCubeFlowDirection(x, container.w.getSeaLevel(blockSize), z, J3DCore.WEST, true);
							getBoundaries().addCube(magnification, x, container.w.getSeaLevel(blockSize), z);
						} catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
					
					
				}
				
				
				
			} else
			{
				float deltaXperZunit = diffX*1f/diffZ;
				
				// road drawing - geo block by block
				
				float xChange = 0;
				int x = startX;
				for (int z=startZ; z<=endZ; z++)
				{

					// draw Z+1
					try {
						System.out.println(x+" || "+z);
						getWorldSizeFlowDirections().setCubeFlowDirection(x, container.w.getSeaLevel(blockSize), z, J3DCore.NORTH, true);
						getBoundaries().addCube(magnification, x, container.w.getSeaLevel(blockSize), z);
					} catch (Exception ex)
					{
						ex.printStackTrace();
					}
					
					// if needed draw X+1
					xChange+=deltaXperZunit;
					if (xChange>=1f)
					{
						xChange = xChange-1f;
						x++;
						try {
							System.out.println(x+" -- "+z);
							getWorldSizeFlowDirections().setCubeFlowDirection(x, container.w.getSeaLevel(blockSize), z, J3DCore.NORTH, true);
							getWorldSizeFlowDirections().setCubeFlowDirection(x, container.w.getSeaLevel(blockSize), z, J3DCore.WEST, true);
							getBoundaries().addCube( magnification, x, worldGroundLevel, z);
						} catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
					
					
				}

			}
			
			
		}
		
		
		
	}

	public transient WorldSizeFlowDirections flowDirections;

	public WorldSizeFlowDirections getWorldSizeFlowDirections() {
		return flowDirections;
	}
	
	public static final String TYPE_ECOGROUND = "ECOGROUND";
	public static final String TYPE_ROADNETWORK = "ROADNETWORK";
	
	public static final SideSubType SUBTYPE_STAIRS = new Climbing(TYPE_ROADNETWORK+"_STAIRS",true);
	public static final SideSubType SUBTYPE_STREETGROUND = new GroundSubType(TYPE_ECOGROUND+"_STREETGROUND",false);
	public static final SideSubType SUBTYPE_EXTERNAL_WOODEN_GROUND = new GroundSubType(TYPE_ECOGROUND+"_EXTERNAL_GROUND",true);
	
	static
	{
		SUBTYPE_STREETGROUND.continuousSoundType = "town_street_easy";
		SUBTYPE_STREETGROUND.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_STREETGROUND.colorOverwrite = true;
		SUBTYPE_STREETGROUND.colorBytes = new byte[] {(byte)150,(byte)150,(byte)150};
		SUBTYPE_STAIRS.audioStepType = AudioServer.STEP_STONE;
		SUBTYPE_STAIRS.colorOverwrite = true;
		SUBTYPE_STAIRS.colorBytes = new byte[] {(byte)150,(byte)50,(byte)50};
	}
	public static Side[] STAIRS = new Side[]{new Side(TYPE_ROADNETWORK,SUBTYPE_STAIRS)};
	public static Side[] ECOGROUND = new Side[]{new Side(TYPE_ECOGROUND,SUBTYPE_STREETGROUND)};
	

	static Side[][] STEPS_NORTH = new Side[][] { STAIRS, I_EMPTY, INTERNAL_ROCK_SIDE,I_EMPTY,BLOCK, ECOGROUND};
	static Side[][] STEPS_SOUTH = new Side[][] { INTERNAL_ROCK_SIDE, I_EMPTY, STAIRS,I_EMPTY,BLOCK,ECOGROUND};
	static Side[][] STEPS_WEST = new Side[][] { I_EMPTY, INTERNAL_ROCK_SIDE, I_EMPTY,STAIRS,BLOCK,ECOGROUND};
	static Side[][] STEPS_EAST = new Side[][] { I_EMPTY, STAIRS, I_EMPTY,INTERNAL_ROCK_SIDE,BLOCK,ECOGROUND};

	public static Side[][] EXTERNAL = new Side[][] { null, null, null,null,null,ECOGROUND };
	public static Side[][] EXTERNAL_WATER_WOODEN_GROUND = new Side[][] { null, null, null,null,null,{Ocean.SHALLOW_WATER_SIDE,new Side(TYPE_ECOGROUND,SUBTYPE_EXTERNAL_WOODEN_GROUND)}};

	
	
	public static HashMap<Integer, Cube> hmKindCubeOverride = new HashMap<Integer, Cube>();
	public static HashMap<Integer, Cube> hmKindCubeOverride_FARVIEW = new HashMap<Integer, Cube>();
	
	static 
	{
		Cube ground = new Cube(null,EXTERNAL,0,0,0,true,false);
		hmKindCubeOverride.put(K_NORMAL_GROUND, ground);
		Cube waterGround = new Cube(null,EXTERNAL_WATER_WOODEN_GROUND,0,0,0,true,false);
		hmKindCubeOverride.put(K_WATER_GROUND, waterGround );		
		Cube stepsEast = new Cube(null,STEPS_EAST,0,0,0,true,false);
		hmKindCubeOverride.put(K_STEEP_EAST, stepsEast);
		Cube stepWest = new Cube(null,STEPS_WEST,0,0,0,true,false);
		hmKindCubeOverride.put(K_STEEP_WEST, stepWest);
		Cube stepNorth = new Cube(null,STEPS_NORTH,0,0,0,true,false);
		hmKindCubeOverride.put(K_STEEP_NORTH, stepNorth);
		Cube stepSouth = new Cube(null,STEPS_SOUTH,0,0,0,true,false);
		hmKindCubeOverride.put(K_STEEP_SOUTH, stepSouth);

		//hmKindCubeOverride_FARVIEW.put(K_NORMAL_GROUND, new Cube(null,House.EXTERNAL,0,0,0));
	}
	
	public HashMap<Integer, Cube> getOverrideMap()
	{
		return hmKindCubeOverride;
	}

	@Override
	public Cube getCubeObject(int kind, int worldX, int worldY, int worldZ, boolean farView) {
		if (worldX%magnification==magnification/2 || worldZ%magnification==magnification/2)
		{
			Cube c = farView?hmKindCubeOverride_FARVIEW.get(kind):getOverrideMap().get(kind);
			return c;
		}
		return null;
	}

	
	
	@Override
	public boolean overrideGeoHeight() {
		return false;
	}

	
	
	@Override
	public float[] getCubeKind(long key, int worldX, int worldY, int worldZ, boolean farView) {
		// let's check for waters here...
		boolean water = false;
		for (Water geo:((World)getRoot()).waters.values())
		{
			{
				if (geo.getBoundaries().isInside(worldX, geo.worldGroundLevel, worldZ))
				{
					if (geo.isWaterPoint(worldX, geo.worldGroundLevel, worldZ, farView)) water = true;
				}
			}
		}
		soilGeo = getSoilGeoAt(worldX, worldZ);
		float[] retKind = null;
		if (soilGeo!=null && soilGeo.getBoundaries().isInside(worldX, worldY, worldZ))
		{
			retKind = soilGeo.getCubeKind(key, worldX,worldY,worldZ, farView);
		} else
		{
			retKind = super.getCubeKindOutside(key, worldX, worldY, worldZ, farView);
		}
		if (water && retKind[4]==K_NORMAL_GROUND)
		{
			retKind[4] = K_WATER_GROUND;
		}
		return retKind;
		
	}

	@Override
	protected float getPointHeightInside(int x, int z, int sizeX, int sizeZ, int worldX, int worldZ, boolean farView) {
		// use the height defined by the geography here...
		soilGeo = getSoilGeoAt(worldX, worldZ);
		if (soilGeo!=null && soilGeo.getBoundaries().isInside(worldX, soilGeo.worldGroundLevel, worldZ))
		{
			int[] values = soilGeo.calculateTransformedCoordinates(worldX, soilGeo.worldGroundLevel, worldZ);
			return soilGeo.getPointHeight(values[3], values[5], values[0], values[2],worldX,worldZ, farView);
		}
		return getPointHeightOutside(worldX, worldZ, farView);
	}

	public static HashSet<Class <? extends Geography>> nonRoadGeos = new HashSet<Class <? extends Geography>>();
	static
	{
		nonRoadGeos.add(Cave.class);
	}
	
	private Geography getSoilGeoAt(int worldX, int worldZ)
	{
		
		for (Geography g:((World)parent).getSurfaceGeographies(worldX,worldZ))
		{
			if (nonRoadGeos.contains(g.getClass())) continue;
			return g;
		}
		return null;
	}

	
	@Override
	public Boundaries getBoundaries() {
		return boundaries;
	}

	@Override
	public void setBoundaries(Boundaries boundaries) {
		this.boundaries = boundaries;
	}
	
	

}
