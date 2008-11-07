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

/**
 * Side type that cannot be passed through.
 * @author illes
 *
 */
public class NotPassable extends SideSubType {

	public NotPassable(String id) {
		super(id);
	}
	public NotPassable(String id, byte[] color) {
		super(id,color);
	}
	public NotPassable(String id, boolean overrideGeneratedTileMiddleHeight) {
		super(id,overrideGeneratedTileMiddleHeight);
	}

}
