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

package org.jcrpg.world.place.orbiter.sun;

import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.orbiter.Orbiter;
import org.jcrpg.world.time.Time;

public class SimpleSun extends Orbiter{

	public static String SIMPLE_SUN_ORBITER = "SIMPLE_SUN";
	
	
	public SimpleSun(String id)
	{
		this.id = id;
		type = SIMPLE_SUN_ORBITER;
	}
	
	@Override
	public float[] getCurrentCoordinates(Time localTime, CubeClimateConditions conditions) {
		float dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		float dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			dayNightPer = ( 100 - (Math.abs( dayNightPer - 50 ) * 2) ) /2; 
		}
		if ( dayNightPer<0) {
			return null;
			//dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}
		float x=(Math.abs(dayOrNightPeriodPercentage)-50) * 10;
		float y= ( 50-(Math.abs((Math.abs(dayOrNightPeriodPercentage)-50))) ) * 10;
		
		return new float[] {-x,y,0};
		
	}

	@Override
	public float[] getLightDirection(Time localTime, CubeClimateConditions conditions) {
		float dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		float dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			dayNightPer = ( 100 - (Math.abs( dayNightPer - 50 ) * 2) ) /2; 
		}
		if ( dayNightPer<0) {
			return null;
			//dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}
		float x=(Math.abs(dayOrNightPeriodPercentage)-50)/-20;
		float y= -1;
		float z = 0.5f;
		
		return new float[] {-x,y,z};

	}

	@Override
	public float getLightPower(Time localTime, CubeClimateConditions conditions) {
		float dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		float dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			dayNightPer = ( 100 - (Math.abs( dayNightPer - 50 ) * 2) ) /2; 
		}
		if ( dayNightPer<0) {
			dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}

		
		float v = ((dayNightPer+100)/200f)+0.3f;
		return v;
	}

}
