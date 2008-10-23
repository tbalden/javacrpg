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

package org.jcrpg.threed.scene.model;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;

public class TriggeredAnimDescription {
	
	
	public static String ANIM_DEFAULT = "ANIM_DEFAULT";
	public static String ANIM_OPEN = "ANIM_OPEN";
	public static String ANIM_CLOSED = "ANIM_CLOSED";
	public static String ANIM_OPENING = "ANIM_OPENING";

	public String DEFAULT = null;
	public String OPEN = null;
	public String CLOSED = null;
	public String OPENING = null;
	
	public HashMap<String, Boolean> oneFrameAnim = new HashMap<String, Boolean>();
	
	public TriggeredAnimDescription()
	{
		
	}
	public TriggeredAnimDescription(String DEFAULT)
	{
		this.DEFAULT = DEFAULT;
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
