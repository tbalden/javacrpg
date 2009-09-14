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

import org.jcrpg.world.ai.EntityFragments.EntityFragment;

public class PerceptedEntityData {
	public EntityFragment source = null;

	public EntityFragment fragment = null;
	public boolean percepted = false, distanceKnown = false, kindKnown = false, groupSizeKnown = false;

	public float distance = 0;
	public int groupSize = 0;
	public String kind = "unknown";
	
	public void updateToResultRatio(float result, float resultIdentification)
	{
		if (result > 0.2f)
		{
			percepted = true;
		}
		if (result > 0.8f)
		{
			distanceKnown = true;
			
		}
		if (result > 0.9f)
		{
			groupSizeKnown = true;
		}
		if (resultIdentification>0.7f)
		{
			kindKnown = true;
		}
		
	}
	
	public void mergeBest(PerceptedEntityData data)
	{
		if (data.distanceKnown) distanceKnown = true;
		if (data.kindKnown) kindKnown = true;
		if (data.groupSizeKnown) groupSizeKnown = true;
	}
	
	public String toString()
	{
		return "Ped: "+fragment.getName()+" : perc "+percepted + " / kind "+kindKnown;
	}
	
}
