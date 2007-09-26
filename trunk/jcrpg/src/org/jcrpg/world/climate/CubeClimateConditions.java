/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.jcrpg.world.climate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * For carrying climate info of one Cube.
 * @author pali
 *
 */
public class CubeClimateConditions {

	public HashMap<String, Condition> conditions;
	
	public ClimateBelt belt;
	public ClimateLevel level;
	public DayTime dayTime;
	public Season season;
	

	public CubeClimateConditions()
	{
		conditions = new HashMap<String, Condition>();
	}
	
	public void mergeCondition(Condition condition)
	{
		if (conditions.containsKey(condition.ID))
		{
			Condition c = conditions.get(condition.ID);
			c.addPercentage(condition.weightPercentage);
		}
		else
		{
			conditions.put(condition.ID, condition);
		}
		
	}
	
	public void mergeConditions(ArrayList<Condition> cs)
	{
		for (Condition condition : cs) {
			mergeCondition(condition);
		}
	}

	
	public ClimateBelt getBelt() {
		return belt;
	}

	public void setBelt(ClimateBelt belt) {
		this.belt = belt;
	}

	public ClimateLevel getLevel() {
		return level;
	}

	public void setLevel(ClimateLevel level) {
		this.level = level;
	}

	public DayTime getDayTime() {
		return dayTime;
	}

	public void setDayTime(DayTime dayTime) {
		this.dayTime = dayTime;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}
	
	/**
	 * Key for climatic belt and level 
	 * @return
	 */
	public String getPartialBeltLevelKey()
	{
		return belt.STATIC_ID+" "+level.STATIC_ID;
	}

	/**
	 * Key for climatic belt and level 
	 * @return
	 */
	public String getPartialSeasonDaytimelKey()
	{
		return season.STATIC_ID+" "+dayTime.STATIC_ID;
	}

}
