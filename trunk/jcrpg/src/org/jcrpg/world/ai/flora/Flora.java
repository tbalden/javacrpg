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

package org.jcrpg.world.ai.flora;

import java.util.HashMap;

public class Flora {

	/**
	 * Maps flora descriptions to Season and DayTime type.
	 */
	public HashMap<String, FloraDescription> statesToFloraDescription = new HashMap<String, FloraDescription>();
	
	public static final String POSITION_GROUND = "__GROUND";
	public static final String POSITION_MIDDLE = "__MIDDLE";
	public static final String POSITION_TOP = "__TOP";
	
	public boolean growsOnSteep = false; 
	
	public String floraPosition = POSITION_TOP;
	
	/**
	 * Fallback description if not flora description found for a state.
	 */
	public FloraDescription defaultDescription;
	
	public FloraDescription getFloraDescription(String key)
	{
		FloraDescription d = statesToFloraDescription.get(key);
		if (d==null) d = defaultDescription;
		return d;
	}
	
}
