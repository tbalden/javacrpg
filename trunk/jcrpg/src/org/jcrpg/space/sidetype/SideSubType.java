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

package org.jcrpg.space.sidetype;

import org.jcrpg.audio.AudioServer;

public class SideSubType {

	public String id;
	public String audioStepType = AudioServer.STEP_SOIL;
	public byte[] colorBytes = new byte[] {(byte)100,(byte)145,(byte)100};
	public boolean colorOverwrite = false;
	
	public boolean overrideGeneratedTileMiddleHeight = false;

	public SideSubType(String id) {
		super();
		this.id = id;
	}
	public SideSubType(String id,boolean overrideGeneratedTileMiddleHeight) {
		super();
		this.id = id;
		this.overrideGeneratedTileMiddleHeight = overrideGeneratedTileMiddleHeight;
	}
	public SideSubType(String id, byte[] color) {
		super();
		this.id = id;
		this.colorBytes = color;
		this.colorOverwrite = true;
	}
	
}
