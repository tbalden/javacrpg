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

package org.jcrpg.world.ai.flora.ground;

import org.jcrpg.audio.AudioServer;
import org.jcrpg.space.Cube;
import org.jcrpg.space.Side;
import org.jcrpg.space.sidetype.GroundSubType;
import org.jcrpg.space.sidetype.SideSubType;
import org.jcrpg.world.ai.flora.FloraDescription;

public class Snow extends Ground {

	public static final String TYPE_SNOW = "SNOW";
	public static final SideSubType SUBTYPE_SNOW = new GroundSubType(TYPE_SNOW+"_SNOW",new byte[] { (byte)240,(byte)240,(byte)240 });
	
	static Side[][] SNOW = new Side[][] { null, null, null,null,null,{new Side(TYPE_SNOW,SUBTYPE_SNOW)} };
	static 
	{
		SUBTYPE_SNOW.continuousSoundType = "arctic_wind";
		SUBTYPE_SNOW.audioStepType = AudioServer.STEP_SNOW;
	}	
	public Snow()
	{
		super();
		growsOnSteep = true;
		defaultDescription = new FloraDescription(new Cube(null,SNOW,0,0,0),0,false,false);
	}
}
