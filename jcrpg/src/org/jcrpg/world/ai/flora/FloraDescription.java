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

import org.jcrpg.space.Cube;

/**
 * Describes a flora element's one state depending on Season and 
 * DayTime. FloraGenerator returns FloraCube which contains flora descriptions (this).
 * This describes the cubic form too (cubicForm field).
 * @author pali
 *
 */
public class FloraDescription {

	/**
	 * The plant will be put into this cube's side(s).
	 */
	public Cube cubicForm;
	// TODO specify attribute classes later, this is just example
	public int herbalType;
	public boolean hasBlossom;
	public boolean hasFruit;
	// fruit...
	public FloraDescription(Cube cubicForm, int herbalType, boolean hasBlossom, boolean hasFruit) {
		super();
		this.cubicForm = cubicForm;
		this.herbalType = herbalType;
		this.hasBlossom = hasBlossom;
		this.hasFruit = hasFruit;
	}
	
}
