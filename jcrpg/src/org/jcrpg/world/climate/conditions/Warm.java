package org.jcrpg.world.climate.conditions;

import org.jcrpg.world.climate.Condition;

public class Warm extends Condition {

	public Warm(int weightPercentage) throws Exception {
		super(weightPercentage);
		// TODO Auto-generated constructor stub
	}

	static
	{
		ID = Warm.class.getSimpleName();
	}


}
