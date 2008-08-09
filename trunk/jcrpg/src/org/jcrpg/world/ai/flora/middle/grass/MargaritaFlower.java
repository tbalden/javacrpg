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

package org.jcrpg.world.ai.flora.middle.grass;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.flora.FloraDescription;
import org.jcrpg.world.ai.flora.middle.MiddlePlant;

/**
 * Continental sun flower.
 * @author illes
 *
 */
public class MargaritaFlower extends MiddlePlant {
	
	public static final String TYPE_MIDDLE = "MIDDLE";
	public static final SideSubType SUBTYPE_FLOWER = new SideSubType(TYPE_MIDDLE+"_MARGARITA");
	
	static Side[][] FLOWER = new Side[][] { null, null, null,null,null,{new Side(TYPE_MIDDLE,SUBTYPE_FLOWER)} };
	
	public MargaritaFlower()
	{
		super();
		defaultDescription = new FloraDescription(new Cube(null,FLOWER,0,0,0),0,false,false);
	}

}
