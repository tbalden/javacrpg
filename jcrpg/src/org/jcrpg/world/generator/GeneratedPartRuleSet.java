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

package org.jcrpg.world.generator;

/**
 * Gives information for the GenProgram about how the given container of this ruleset should be used in the
 * generation process.
 * @author illes
 */
public class GeneratedPartRuleSet {
	
	public String geoTypeName = null;
	
	public static String GEN_TYPE_RANDOM = "GEN_TYPE_RANDOM"; 

	public GeneratedPartRuleSet(String geoTypeName) {
		super();
		this.geoTypeName = geoTypeName;
	}

	/**
	 * Tells how likely the part is to be neighbor of a list of parts around
	 * @param neighborNames
	 * @return
	 */
	public int likenessToNeighbor(String[] neighborParts)
	{
		return 50;
	}
	
	/**
	 * Tells how likely the part will be in the same block with another parts
	 * @param parts
	 * @return
	 */
	public int likenessToCoexist(String [] parts)
	{
		return 0;
	}
	
	public boolean presentWhereBaseExists = true;
	
	public boolean presentWhereBaseExists()
	{
		return presentWhereBaseExists;
	}
	
	public String genType = GEN_TYPE_RANDOM;
	public String getGeneratorType()
	{
		return genType;
	}
	
	public Object[] genParams = new Object[0];
	public Object[] getGeneratorParameters()
	{
		return genParams;
	}
}
