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
		needsShadowPass = true;
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
		float x=(Math.abs(dayOrNightPeriodPercentage)-50) * 1.8f;
		float y= ( 50-(Math.abs((Math.abs(dayOrNightPeriodPercentage)-50))) ) * 1.8f;
		
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
		float z = 0.1f;
		
		return new float[] {-x,y,z};

	}

	@Override
	public float[] getLightPower(Time localTime, CubeClimateConditions conditions) {
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
		
		float r = ((dayNightPer+100)/200f);
		float g = ((dayNightPer+100)/200f);
		float b = ((dayNightPer+100)/200f);
		
		if (dayNightPer<10)
		{
			r*= 0.9  + (0.2f * ((1/10f) * (10-dayNightPer)));
			g*= 0.9f - (0.4f * ((1/10f) * (10-dayNightPer)));
			b*= 0.8f - (0.4f * ((1/10f) * (10-dayNightPer)));
		} else
		if (dayNightPer<20)
		{
			r*=0.9f;
			b*=0.8f;
		} else
		{
			r*=1.2f;
			g*=1.2f;
			b*=1.2f;
			
		}

		if (r>0.8f) r=0.8f;
		if (g>0.8f) b=0.8f;
		
		return new float[]{r+0.3f,g+0.1f,b-0.2f};
	}

}
