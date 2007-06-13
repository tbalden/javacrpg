package org.jcrpg.world.ai.abs.step;

import org.jcrpg.world.ai.abs.Step;
import org.jcrpg.world.ai.abs.StepResult;
import org.jcrpg.world.place.Place;

public class Move extends Step {

	public Place to;
	
	@Override
	public StepResult doStep() {
		return new StepResult(true);
	}

}
