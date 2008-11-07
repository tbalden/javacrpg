/*
 *  This file is part of JavaCRPG.
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


package org.jcrpg.world.climate;

/**
 * 
 * @author pali
 *
 */
public abstract class Condition {

	/**
	 * static Unique ID of the Condition
	 */
	public String ID;
	
	/**
	 * The weight of the Condition
	 */
	public int weightPercentage;
	
	public Condition(int weightPercentage) throws Exception
	{
		if (weightPercentage<0 || weightPercentage>100)
			throw new Exception("Invalid weight percentage for climate Condition "+ID+" "+weightPercentage);
		
		this.weightPercentage = weightPercentage;
		
	}
	public void addPercentage(int perc)
	{
		weightPercentage+=perc;
		if (weightPercentage>100) weightPercentage = 100;
		if (weightPercentage<0) weightPercentage = 0;
	}
	
}
