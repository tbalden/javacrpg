/*
 * Java Classic RPG
 * Copyright 2007, JCRPG Team, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jcrpg.world.ai.flora;

import org.jcrpg.space.Cube;

/**
 * Describes a flora element's one state depending on Season and 
 * DayTime.
 * @author pali
 *
 */
public class FloraDescription {

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
