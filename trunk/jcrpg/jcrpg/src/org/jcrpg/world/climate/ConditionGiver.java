package org.jcrpg.world.climate;

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
	public void getConditions(CubeClimateConditions conditions, Time time, int worldX, int worldY, int worldZ);
	
}
