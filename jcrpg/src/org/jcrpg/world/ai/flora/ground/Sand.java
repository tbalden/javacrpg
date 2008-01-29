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

public class Sand extends Ground {

	public static final String TYPE_SAND = "SAND";
	public static final SideSubType SUBTYPE_SAND = new GroundSubType(TYPE_SAND+"_SAND");
	
	static Side[][] SAND = new Side[][] { null, null, null,null,null,{new Side(TYPE_SAND,SUBTYPE_SAND)} };
	
	public Sand()
	{
		super();
		growsOnSteep = true;
		defaultDescription = new FloraDescription(new Cube(null,SAND,0,0,0),0,false,false);
	}
}