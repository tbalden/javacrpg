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

package org.jcrpg.world.ai.flora.ground;

import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.flora.FloraDescription;

public class Grass extends Ground {

	public static final String TYPE_GRASS = "GRASS";
	public static final SideSubType SUBTYPE_GRASS = new GroundSubType(TYPE_GRASS+"_GRASS",new byte[] { (byte)40,(byte)200,(byte)100 });
	static 
	{
		SUBTYPE_GRASS.continuousSoundType = "meadow";
	}
	
	static Side[][] GRASS = new Side[][] { null, null, null,null,null,{new Side(TYPE_GRASS,SUBTYPE_GRASS)} };
	
	public Grass()
	{
		super();
		growsOnSteep = true;
		defaultDescription = new FloraDescription(new Cube(null,GRASS,0,0,0),0,false,false);
	}
}
