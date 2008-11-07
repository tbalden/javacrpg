/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
package org.jcrpg.world.ai.abs.state;

/**
 * Class representing the overall quality of a group, which means how advanced it is 
 * as a web of persons, as a group. On quality level may depend several description specified
 * other qualities. (Only one changing value is stored for an Entity here in this class,
 * to have less memory consumption, and this kind of detailedness is considered enough for now.)
 * @author illes
 *
 */
public class EntityState {

	/**
	 * What quality the given group is at.
	 */
	public int currentLevelOfQuality = 1;
	
	public float detailedQualityPoints = 0;
	
	public Integer moveQuality(int q)
	{
		for (int i=0; i<q; i++) {
			detailedQualityPoints+=1f/(currentLevelOfQuality*1f);
			checkLevelChange();
		}
		return currentLevelOfQuality;
	}
	public boolean checkLevelChange()
	{
		if (currentLevelOfQuality == detailedQualityPoints % 10)
		{
			return false;
		}else
		{
			currentLevelOfQuality = ((int)detailedQualityPoints) % 10;
		}
		return true;
	}
	
}
