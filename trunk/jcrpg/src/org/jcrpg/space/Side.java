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

package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.space.sidetype.SideSubType;

public class Side extends ChangingImpl {

	public static final SideSubType DEFAULT_SUBTYPE = new SideSubType("0");
	public static final String DEFAULT_TYPE = "0";

	public Side()
	{
		this.type = DEFAULT_TYPE;
		this.subtype = DEFAULT_SUBTYPE;
	}

	public Side(String type)
	{
		this.type = type;
		this.subtype = DEFAULT_SUBTYPE;
	}

	public Side(String type, SideSubType subType)
	{
		this.type = type;
		this.subtype = subType;
	}
	
	/**
	 * id of the main side type for determining continousity
	 */
	public String type;
	
	/**
	 * side subtype for determining further precision
	 */
	public SideSubType subtype;

	
	
}
