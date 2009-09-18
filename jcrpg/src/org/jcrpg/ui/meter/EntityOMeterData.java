/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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

package org.jcrpg.ui.meter;

public class EntityOMeterData {

	public String picture;
	public String radius;
	public String pos;
	public Float dist;
	public Float angle;
	public String kind;
	public String realKind;
	
	@Override
	public boolean equals(Object o)
	{
		if (o!=null && o instanceof EntityOMeterData)
		{
			if (realKind!=null && realKind.equals(((EntityOMeterData)o).realKind))
					return true;
		}
		return false;
	}
	
}
