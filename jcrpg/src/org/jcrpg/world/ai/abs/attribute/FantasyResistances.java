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

package org.jcrpg.world.ai.abs.attribute;

import org.jcrpg.game.GameLogicConstants;
import org.jcrpg.util.Language;

public class FantasyResistances extends Resistances {

	public static String RESIST_PIERCE = "RESIST_PIERCE";
	public static String RESIST_BLUDGEON = "RESIST_BLUDGEON";
	public static String RESIST_COLD = "RESIST_COLD";
	public static String RESIST_HEAT = "RESIST_HEAT";
	public static String RESIST_CHEMICAL = "RESIST_CHEMICAL";
	public static String RESIST_MENTAL = "RESIST_MENTAL";
	public static String RESIST_GOOD = "RESIST_GOOD";
	public static String RESIST_EVIL = "RESIST_EVIL";	
	
	public static String[] resistanceName = new String[] {
		RESIST_PIERCE, RESIST_BLUDGEON, RESIST_COLD,RESIST_HEAT,RESIST_CHEMICAL,RESIST_MENTAL, RESIST_GOOD, RESIST_EVIL
	};
	
	public FantasyResistances()
	{
		this(true);
	}
	public FantasyResistances(boolean zero)
	{
		for (String a:resistanceName)
		{
			resistances.put(a, zero?0:GameLogicConstants.BASE_RESISTANCE_VALUE);
		}
	}
	@Override
	public String getShortestName(String attr) {
		return Language.v("fantasyresistances.shortest."+attr);
	}
	
	
}
