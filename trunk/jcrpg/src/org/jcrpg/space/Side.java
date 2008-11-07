/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.space;

import org.jcrpg.abs.change.ChangingImpl;
import org.jcrpg.space.sidetype.SideSubType;

/**
 * Side of a cube containing the data about the things represented on it.
 * @author pali
 *
 */
public class Side extends ChangingImpl {

	public static final SideSubType DEFAULT_SUBTYPE = new SideSubType("0");
	public static final String DEFAULT_TYPE = "0";

	/**
	 * Color for the maps.
	 */
	public byte[] colorBytes = new byte[] {(byte)100,(byte)145,(byte)100};

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
