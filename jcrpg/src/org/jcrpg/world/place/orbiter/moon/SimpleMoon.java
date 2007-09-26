/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.world.place.orbiter.moon;

import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.orbiter.Orbiter;
import org.jcrpg.world.time.Time;

import com.jme.renderer.ColorRGBA;

public class SimpleMoon extends Orbiter{

	public static String SIMPLE_MOON_ORBITER = "SIMPLE_MOON";
	
	
	public SimpleMoon(String id)
	{
		this.id = id;
		type = SIMPLE_MOON_ORBITER;
	}
	
	@Override
	public float[] getCurrentCoordinates(Time localTime, CubeClimateConditions conditions) {
		float dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		float dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			return null;
			//dayNightPer = ( 100 - (Math.abs( dayNightPer - 50 ) * 2) ) /2; 
		}
		if ( dayNightPer<0) {
			dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}
		float z=(Math.abs(dayOrNightPeriodPercentage)-50) * 2;
		float y= ( 50-(Math.abs((Math.abs(dayOrNightPeriodPercentage)-50))) ) * 2;
		
		return new float[] {0,y,z};
		
	}

	@Override
	public float[] getLightDirection(Time localTime, CubeClimateConditions conditions) {
		float dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		float dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			return null;
			//dayNightPer = ( 100 - (Math.abs( dayNightPer - 50 ) * 2) ) /2; 
		}
		if ( dayNightPer<0) {
			dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}
		float x= 0.2f;
		float y= -1;
		float z = (Math.abs(dayOrNightPeriodPercentage)-50)/-20;
		
		return new float[] {x,y,z};
		
	}

	@Override
	public float[] getLightPower(Time localTime, CubeClimateConditions conditions) {
		float dayNightPer = conditions.getSeason().dayOrNightPeriodPercentage(localTime);
		
		float dayOrNightPeriodPercentage = dayNightPer; 
		
		// dayNightPer now contain how deep in the period of day / night time is 100 is the middle of the day, 
		// -100 is the middle of the night, 0 is between them.
		if ( dayNightPer>=0) {
			dayNightPer = -1 * ( 100 - Math.abs(( Math.abs( dayNightPer ) - 50 ) * 2) ); 
		}
		if ( dayNightPer<0) {
			dayNightPer = ( 100 - (Math.abs( Math.abs(dayNightPer) - 50 ) * 2) ) /2; 
		}

		
		float v = ((dayNightPer+100)/200f)-0.6f;
		if (v<0.1f) v= 0.07f;
		if (v>1f) v=1f;
		return new float[]{v,v,v};
	}
	
}
