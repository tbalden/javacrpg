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

package org.jcrpg.space.sidetype;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.skill.SkillBase;


/**
 * The side's sub element types like wall, ground etc. Other more detailed types
 * are extending this, like GroundType, NotPassable, Climbing, StickingOut.
 * @author illes
 */
public class SideSubType {

	/**
	 * Unique id for model mapping and such.
	 */
	public String id;
	/**
	 * Sound for walking.
	 */
	public String audioStepType = null;
	
	/**
	 * Sound that is played if player is near it.
	 */
	public String continuousSoundType = null;
	
	/**
	 * Color on map.
	 */
	public byte[] colorBytes = new byte[] {(byte)100,(byte)145,(byte)100};
	/**
	 * Tells if this side's color is overwriting other colors on minimap.
	 */
	public boolean colorOverwrite = false;
	
	/**
	 * Tells if this side is present middle height for camera positioning should be not used -
	 * instead use the fix height of Steep or Ground.
	 */
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
	
	public ArrayList<Class<? extends SkillBase>> getSkillNeeded()
	{
		return null;
	}
	
}
