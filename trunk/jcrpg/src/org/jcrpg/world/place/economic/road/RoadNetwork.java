package org.jcrpg.world.place.economic.road;

import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.EconomyContainer;

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
	

	public void updateRoads()
	{
		//for container.towns
		// TODO check how to build the graph, and store it efficiently to read up which
		// block coordinates of the world contains a road - in !transient! form (not saved) 
		// + getCube and such...
	}
	

}
