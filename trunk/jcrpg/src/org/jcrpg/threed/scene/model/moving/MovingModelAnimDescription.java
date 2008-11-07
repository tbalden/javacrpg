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

package org.jcrpg.threed.scene.model.moving;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;

public class MovingModelAnimDescription {
	
	
	public static String ANIM_IDLE = "ANIM_IDLE";
	public static String ANIM_IDLE_COMBAT = "ANIM_IDLE_COMBAT";
	public static String ANIM_WALK = "ANIM_WALK";
	public static String ANIM_TURN = "ANIM_TURN";
	public static String ANIM_ATTACK_UPPER = "ANIM_ATTACKU";
	public static String ANIM_ATTACK_LOWER = "ANIM_ATTACKL";
	public static String ANIM_DEFEND_UPPER = "ANIM_DEFENDU";
	public static String ANIM_DEFEND_LOWER = "ANIM_DEFENDL";
	public static String ANIM_THROW = "ANIM_THROW";
	public static String ANIM_SHOOT = "ANIM_SHOOT";
	public static String ANIM_CAST = "ANIM_CAST";
	public static String ANIM_FRIENDLY = "ANIM_FRIENDLY";
	public static String ANIM_COMMUNICATE_NORMAL = "ANIM_COMM";
	public static String ANIM_COMMUNICATE_PATIENT = "ANIM_CPATIENT";
	public static String ANIM_COMMUNICATE_AGRESSIVE = "ANIM_CAGRESSIVE";
	public static String ANIM_COMMUNICATE_HATRED = "ANIM_CHATRED";
	public static String ANIM_PAIN = "ANIM_PAIN";
	public static String ANIM_DEATH_NORMAL = "ANIM_DEATH";
	public static String ANIM_DEATH_QUICK = "ANIM_DQUICK";
	public static String ANIM_DEATH_SLOW = "ANIM_DSLOW";
	public static String ANIM_DEAD = "ANIM_DEAD";
	public static String ANIM_NEUTRALIZED = "ANIM_NEUTRALIZED";

	public String IDLE = null;
	public String IDLE_COMBAT = null;
	public String IDLE_1 = null;
	public String IDLE_2 = null;
	public String WALK = null;
	public String ATTACK_UPPER = null;
	public String ATTACK_LOWER = null;
	public String DEFEND_UPPER = null;
	public String DEFEND_LOWER = null;
	public String THROW = null;
	public String SHOOT = null;
	public String CAST = null;
	public String FRIENDLY = null;
	public String COMMUNICATE_NORMAL = null;
	public String COMMUNICATE_PATIENT = null;
	public String COMMUNICATE_AGRESSIVE = null;
	public String COMMUNICATE_HATRED = null;
	public String PAIN = null;
	public String DEATH_NORMAL = null;
	public String DEATH_QUICK = null;
	public String DEATH_SLOW = null;
	public String DEAD = null;
	public String NEUTRALIZED = null;
	
	public MovingModelAnimDescription()
	{
		
	}
	public MovingModelAnimDescription(String IDLE)
	{
		this.IDLE = IDLE;
	}
	public MovingModelAnimDescription(String[] IDLE)
	{
		this.IDLE = IDLE[0];
		if (IDLE.length>1) this.IDLE_1 = IDLE[1];
		if (IDLE.length>2) this.IDLE_2 = IDLE[2];
	}
	
	public HashMap<String,String> getAnimationNames()
	{
		HashMap<String,String> r = new HashMap<String,String>();
		Field[] fields = this.getClass().getFields();
		for (Field f:fields)
		{
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("F: "+f.getName()+ " "+ f.getType() );
			if (!f.getName().startsWith("ANIM_") && f.getType()==String.class)
			{
				try 
				{
					String v = (String)f.get(this);
					if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("= "+v);
					if (v!=null)
					{
						if (v.charAt(0)!='.')
							v = "./data/models/"+v;
						String key = (String)this.getClass().getField("ANIM_"+f.getName()).get(this);
						if (J3DCore.LOGGING) Jcrpg.LOGGER.finest("__"+key+" : "+v);
						r.put(key,v);
					}
				} catch (IllegalAccessException e)
				{}
				catch (NoSuchFieldException e2)
				{
					
				}
			}
		}
		return r;
	}
	
	
}
