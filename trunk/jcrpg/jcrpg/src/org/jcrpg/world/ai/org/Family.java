package org.jcrpg.world.ai.org;

import java.util.HashMap;

import org.jcrpg.world.ai.fauna.Intelligent;
import org.jcrpg.world.place.economic.House;

public class Family extends Group {

	public Intelligent father, mother;
	public HashMap<String, Intelligent> children;
	public HashMap<String, House> homeEstates; 
	
	public Family()
	{
		directMembers.put(father.id, father);
		directMembers.put(mother.id, mother);
		directMembers.putAll(children);
		properties.putAll(homeEstates);
	}
}
