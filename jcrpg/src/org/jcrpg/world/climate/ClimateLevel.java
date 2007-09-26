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

/**
 * Horizontal splitting of Climate
 * @author pali
 *
 */
public class ClimateLevel extends ClimatePart{

	public String STATIC_ID = ClimateLevel.class.getCanonicalName();
	public static String CLIMATELEVEL_ID = ClimateLevel.class.getCanonicalName();

	int percentFrom, percentTo;
	
	public ClimateLevel(String id, Climate parent, int percentFrom, int percentTo) {
		super(id,parent);
		this.percentFrom = percentFrom;
		this.percentTo = percentTo;
	}

}
