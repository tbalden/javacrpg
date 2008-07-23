/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
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
package org.jcrpg.world.ai.dialect;

import org.jcrpg.util.HashUtil;

public class DialectTool {

	
	public static String getName(Dialect dialect, String namedType, int seed)
	{
		String name = "";
		int i=0;
		while (true) {
			int r = HashUtil.mixPer1000((int)seed,i++,namedType.hashCode(),0);
			r = r%dialect.syllables.size();
			name+=dialect.syllables.get(r);
			if (i>5 || i>2 && HashUtil.mixPer1000((int)seed,i,0,0)>500) break;
		}
		return name.substring(0,1).toUpperCase()+name.substring(1);
	}
	
}
