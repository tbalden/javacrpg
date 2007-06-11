package org.jcrpg.world.ai.fauna;

import java.util.HashMap;

import org.jcrpg.world.ai.abs.Feeling;
import org.jcrpg.world.ai.abs.Goal;
import org.jcrpg.world.ai.abs.Thinking;

public class Intelligent {

	public String id;
	public HashMap<String,Goal> orders, ownGoals, scriptedGoals;
	public HashMap<String,Feeling> ownFeelings, scriptedFeelings;
	public HashMap<String,Thinking> learnedThinkings, scriptedThinkings;
	
	
}
