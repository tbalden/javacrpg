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
package org.jcrpg.world.object;

import java.util.HashMap;

import org.jcrpg.world.object.armor.LeatherArmor;
import org.jcrpg.world.object.combat.blade.Dagger;
import org.jcrpg.world.object.combat.blade.LongSword;
import org.jcrpg.world.object.combat.bow.ShortBow;
import org.jcrpg.world.object.combat.bow.arrow.CrudeArrow;
import org.jcrpg.world.object.combat.staffwand.Club;
import org.jcrpg.world.object.combat.staffwand.QuarterStaff;

/**
 * You must append this with new object types instances - otherwise objects are unusable / buggy in game.
 * @author illes
 *
 */
public class ObjList {
	
	private static HashMap<Class<?extends Obj>,Obj> objects = new HashMap<Class<? extends Obj>, Obj>();
	
	static
	{
		// weapons
		objects.put(LongSword.class, new LongSword());
		objects.put(QuarterStaff.class, new QuarterStaff());
		objects.put(Dagger.class, new Dagger());
		objects.put(Club.class, new Club());
		
		// bows
		objects.put(ShortBow.class, new ShortBow());
		
		// ammunitions
		objects.put(CrudeArrow.class, new CrudeArrow());
		
		// armors
		objects.put(LeatherArmor.class, new LeatherArmor());
	}
	
	public static Obj getInstance(Class<? extends Obj> obj)
	{
		Obj o = objects.get(obj);
		if (o==null)
		{
			try {
				o = obj.newInstance();
				objects.put(obj, o);
			} catch (InstantiationException iex)
			{
				iex.printStackTrace();
			}
			catch (IllegalAccessException ex)
			{
				ex.printStackTrace();
			}
				
		}
		return o;	
	}

}
