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
