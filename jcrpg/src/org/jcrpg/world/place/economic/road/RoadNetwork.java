package org.jcrpg.world.place.economic.road;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.EconomyContainer;
import org.jcrpg.world.place.FlowGeography;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.WorldSizeFlowDirections;
import org.jcrpg.world.place.economic.Town;

import com.jme.math.Vector2f;

/**
 * Class of all the overland roads - connecting towns.
 * @author pali
 *
 */
public class RoadNetwork extends Economic implements FlowGeography {
	

	public EconomyContainer container;
	
	public RoadNetwork(String id, EconomyContainer container) throws Exception
	{
		super(id,null, container.w,null, null,null); 
		this.container = container;
		magnification = blockSize;
		flowDirections = new WorldSizeFlowDirections(magnification,(World)parent);
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
	
	ArrayList<Connection> connections = new ArrayList<Connection>();

	public int TOWNSIZE_THRESHOLD = 2;

	/**
	 * Updates road network, based on size and distance (closest bigger cities are connected). 
	 */
	public void updateRoads()
	{
		connections.clear();
		
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
						getWorldSizeFlowDirections().setCubeFlowDirection(x, worldGroundLevel, z, J3DCore.WEST, true); 
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
							getWorldSizeFlowDirections().setCubeFlowDirection(x, worldGroundLevel, z, J3DCore.NORTH, true);
							getWorldSizeFlowDirections().setCubeFlowDirection(x, worldGroundLevel, z, J3DCore.WEST, true);
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
						getWorldSizeFlowDirections().setCubeFlowDirection(x, worldGroundLevel, z, J3DCore.NORTH, true); 
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
							getWorldSizeFlowDirections().setCubeFlowDirection(x, worldGroundLevel, z, J3DCore.NORTH, true);
							getWorldSizeFlowDirections().setCubeFlowDirection(x, worldGroundLevel, z, J3DCore.WEST, true);
						} catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
					
					
				}

			}
			
			
		}
		
		
		
	}

	public WorldSizeFlowDirections flowDirections;

	public WorldSizeFlowDirections getWorldSizeFlowDirections() {
		return flowDirections;
	}
	
	
	

}
