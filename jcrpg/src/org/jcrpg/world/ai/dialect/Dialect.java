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
package org.jcrpg.world.ai.dialect;

import java.util.ArrayList;

import org.jcrpg.util.HashUtil;

public class Dialect {
	public ArrayList<String> syllables = new ArrayList<String>();
	
	public Dialect()
	{
		if (syllables.size()==0) {
			syllables.add("aw");
			syllables.add("sho");
			syllables.add("mig");
			syllables.add("tra");
			syllables.add("wam");
			syllables.add("prah");
			syllables.add("bu");
		}
	}
	
	public String getName(int seed, String namedType, Class type, Object instance)
	{
		String name = "";
		int i=0;
		while (true) {
			int r = HashUtil.mixPer1000((int)seed,i++,namedType.hashCode(),0);
			r = r%syllables.size();
			name+=syllables.get(r);
			if (i>5 || i>2 && HashUtil.mixPer1000((int)seed,i,0,0)>500) break;
		}
		return name;
		
	}
}
