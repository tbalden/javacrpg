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

package org.jcrpg.world.ai;

import java.lang.reflect.Field;

public class AudioDescription {

	
	public String formattedName = null;
	
	public String[] ENCOUNTER = null;
	public String[] ATTACK = null;
	public String[] FRIENDLY = null;
	public String[] PAIN = null;
	public String[] JOY = null;
	public String[] DEATH = null;
	public String[] ENVIRONMENTAL = null;
	public String[] LEVELING = null;
	/**
	 * low stamina
	 */
	public String[] TIRED = null;
	/**
	 * near death
	 */
	public String[] BRUISED= null; 
	
	public static final String T_ENCOUNTER = "ENCOUNTER";
	public static final String T_ATTACK = "ATTACK";
	public static final String T_FRIENDLY = "FRIENDLY";
	public static final String T_PAIN = "PAIN";
	public static final String T_JOY = "JOY";
	public static final String T_DEATH = "DEATH";
	public static final String T_ENVIRONMENTAL = "ENVIRONMENTAL";
	public static final String T_LEVELING = "LEVELING";
	public static final String T_TIRED = "TIRED";
	public static final String T_BRUISED = "BRUISED";
	
	public String getSound(String type)
	{
		String[] audio = null;
		try {
			Field f = this.getClass().getField(type);
			audio = (String[])f.get(this);
		} catch (Exception ex)
		{			
		}
		
		if (audio==null || audio.length==0) return null;
		
		int i = ((int)(Math.random()*100d))%audio.length;
		return audio[i];		
	}
	

	public String getFormattedName()
	{
		return formattedName==null?this.getClass().getSimpleName():formattedName;
	}
}
