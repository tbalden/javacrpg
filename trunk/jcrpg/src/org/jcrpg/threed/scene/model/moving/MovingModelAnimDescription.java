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

package org.jcrpg.threed.scene.model.moving;

import java.lang.reflect.Field;
import java.util.HashSet;

public class MovingModelAnimDescription {

	public String[] IDLE = null;
	public String WALK = null;
	public String ATTACK_UPPER = null;
	public String ATTACK_LOWER = null;
	public String DEFEND_UPPER = null;
	public String DEFEND_LOWER = null;
	public String THROW = null;
	public String CAST = null;
	public String FRIENDLY = null;
	public String COMMUNICATE_NORMAL = null;
	public String COMMUNICATE_PATIENT = null;
	public String COMMUNICATE_AGRESSIVE = null;
	public String COMMUNICATE_HATERED = null;
	public String PAIN = null;
	public String DEATH_NORMAL = null;
	public String DEATH_QUICK = null;
	public String DEATH_SLOW = null;
	
	public MovingModelAnimDescription()
	{
		
	}
	public MovingModelAnimDescription(String IDLE0)
	{
		IDLE = new String[] {IDLE0};
	}
	
	public HashSet<String> getAnimationNames()
	{
		HashSet<String> r = new HashSet<String>();
		if (IDLE!=null)
		{
			for (String s:IDLE)
			{
				if (s.charAt(0)!='.') s = "./data/models/"+s;
				r.add(s);
			}
		}
		Field[] fields = this.getClass().getFields();
		for (Field f:fields)
		{
			if (f.getClass()==String.class && f.isAccessible())
			{
				try 
				{
					String v = (String)f.get(this);
					if (v!=null)
					{
						if (v.charAt(0)!='.')
							v = "./data/models/"+v;
						r.add(v);
					}
				} catch (IllegalAccessException e)
				{}
			}
		}
		return r;
	}
	
}
