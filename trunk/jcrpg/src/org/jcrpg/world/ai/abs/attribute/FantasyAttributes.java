/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.world.ai.abs.attribute;

public class FantasyAttributes extends Attributes {

	public static String STRENGTH = "STRENGTH"; // your muscles
	public static String SPEED = "SPEED"; // your reflexes
	public static String CONCENTRATION = "CONCENTRATION"; // your nerves
	public static String CONSTITUTION = "CONSTITUTION"; // your body's hardiness
	public static String PSYCHE = "PSYCHE"; // buildup of your mind
	public static String PIETY = "PIETY"; // devotion to transcendental
	public static String CHARISMA = "CHARISMA"; // for charming
	public static String KARMA = "KARMA"; // bad karma gives affiliation to bad things
	public static String[] attributeName = new String[] {
		STRENGTH,SPEED,CONCENTRATION,CONSTITUTION, PSYCHE, PIETY, CHARISMA, KARMA
	};
	
	public FantasyAttributes()
	{
		for (String a:attributeName)
		{
			attributes.put(a, 0);
		}
	}
	
	
}