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

package org.jcrpg.world.climate.impl.arctic;

import org.jcrpg.world.climate.Climate;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.Season;
import org.jcrpg.world.time.Time;

public class Arctic extends ClimateBelt {

	
	public static String ARCTIC_ID = Arctic.class.getCanonicalName();

	static Spring spring;
	static Winter winter;
	static {
		try {
			spring = new Spring();
			winter = new Winter();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Arctic(String id, Climate parent) {
		super(id, parent);
		STATIC_ID = ARCTIC_ID;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Season getSeason(Time time) {
		
		int p = time.getCurrentYearPercent();
		
		// if on southern hemisphere, percent reversed
		if (time.inverseSeasons) p = 100-p;
		
		if (p>45 && p<55) return spring;
		return winter;
		
	}
	
}
