package org.jcrpg.world.place.economic.road;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.jcrpg.world.ai.DistanceBasedBoundary;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.EconomyContainer;
import org.jcrpg.world.place.economic.Town;

import com.jme.math.Vector2f;

/**
 * Class of all the overland roads - connecting towns.
 * @author pali
 *
 */
public class RoadNetwork extends Economic {
	

	public EconomyContainer container;
	
	public RoadNetwork(String id, EconomyContainer container) throws Exception
	{
		super(id,null, container.w,null, null,null); 
		this.container = container;
		
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
	
	public HashMap<Town, ArrayList<Town>> connections = new HashMap<Town, ArrayList<Town>>();
	
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
		ArrayList<Connection> connections = new ArrayList<Connection>();
		
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
		
		System.out.println("TOWN CONNECTIONS CREATED: ");
		for (Connection c:connections)
		{
			System.out.println("---------- "+c.toString());
		}
		
		//for container.towns
		// TODO check how to build the graph, and store it efficiently to read up which
		// block coordinates of the world contains a road - in !transient! form (not saved) 
		// + getCube and such...
	}
	

}
