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

package org.jcrpg.world.ai.flora.middle.mushroom;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.flora.FloraDescription;
import org.jcrpg.world.ai.flora.middle.MiddlePlant;

public class RedForestMushroom extends MiddlePlant {
	
	public static final String TYPE_MIDDLE = "MIDDLE";
	public static final SideSubType SUBTYPE_REDFORESTMUSHROOM = new SideSubType(TYPE_MIDDLE+"_REDFORESTMUSHROOM");
	
	static Side[][] MUSHROOM = new Side[][] { null, null, null,null,null,{new Side(TYPE_MIDDLE,SUBTYPE_REDFORESTMUSHROOM)} };
	
	public RedForestMushroom()
	{
		super();
		defaultDescription = new FloraDescription(new Cube(null,MUSHROOM,0,0,0),0,false,false);
	}

}
