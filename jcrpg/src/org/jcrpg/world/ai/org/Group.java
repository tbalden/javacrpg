package org.jcrpg.world.ai.org;

import java.util.HashMap;

import org.jcrpg.world.ai.fauna.Intelligent;
import org.jcrpg.world.place.Place;

public class Group {

	public HashMap<String, Intelligent> directMembers;
	public HashMap<String, GroupRelation> groupRelations;
	public HashMap<String, Object> properties;	
	/**
	 * Gets leader for a certain aspect of the world.
	 * @param filter
	 * @return
	 */
	public Intelligent calculateLeader(Object filter)
	{
		return null;		
	}
	
}
