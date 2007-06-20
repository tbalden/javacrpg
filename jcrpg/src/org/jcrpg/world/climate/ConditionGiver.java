package org.jcrpg.world.climate;

import java.util.ArrayList;

import org.jcrpg.world.time.Time;

public interface ConditionGiver {

	/**
	 * Returns conditions calculated from time and coordinates.
	 * @param time
	 * @param worldX
	 * @param worldY
	 * @param worldZ
	 * @return
	 */
	public ArrayList<Condition> getConditions(Time time, int worldX, int worldY, int worldZ);
	
}
