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

package org.jcrpg.world.ai.flora;

/**
 * Used to add data to a flora in a Belt/Level map.
 * @author pali
 *
 */
public class FloraListElement {
	
	public Flora flora;
	public boolean alwaysPresent = false;
	/**
	 * 0-1000, likeness to grow, X:1000.
	 */
	public int likenessToGrow = 0;
	
	public FloraListElement(Flora flora, int likenessToGrow) {
		super();
		this.flora = flora;
		this.alwaysPresent = false;
		this.likenessToGrow = likenessToGrow;
	}

	/**
	 * 
	 * @param flora
	 * @param alwaysPresent
	 */
	public FloraListElement(Flora flora) {
		super();
		this.flora = flora;
		this.alwaysPresent = true;
		this.likenessToGrow = 1000;
	}
	
	
}
