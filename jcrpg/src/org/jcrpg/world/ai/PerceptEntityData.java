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

import java.util.ArrayList;

import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.fauna.PerceptVisibleForm;

import com.jme.math.Vector3f;

/**
 * Percept entity's data for Entity'O'Meter and other use. Stored in the EntityFragment, put there by Ecology encounter calculation.
 * Uses EncounterUnit for the target, perception's source is an EntityFragment.
 * @author illes
 *
 */
public class PerceptEntityData {
	public EntityFragment source = null;

	public EncounterUnit unit = null;
	public int[] groupIds = null;
	public boolean percepted = false, distanceKnown = false, kindKnown = false, groupSizeKnown = false;

	public Float distance = null;
	public Integer groupSize = null;
	public String kind = "?";
	
	public Float getUpdatedDist()
	{
		if (distanceKnown)
		{
			Vector3f point = unit.getFragment().getRoamingPosition();//new Vector3f(commonRadius[1][0],commonRadius[1][1],commonRadius[1][2]);
			distance =  source.getRoamingPosition().distance(point);
		}
		return distance;
	}
	
	public void updateToResultRatio(float result, float resultIdentification, Vector3f sourcePos, EncounterUnit target)
	{
		if (result > 0.1f)
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

		if (distanceKnown)
		{
			// TODO get Persistent coordinates instead!
			Vector3f point = target.getFragment().getRoamingPosition();//new Vector3f(commonRadius[1][0],commonRadius[1][1],commonRadius[1][2]);
			distance =  sourcePos.distance(point);
		} else
		{
			distance = null;
		}
		if (kindKnown)
		{
			kind = target.getName();
		} else
		{
			kind = "?";
		}
		if (groupSizeKnown)
		{
			groupSize = target.getSize();
		} else
		{
			groupSize = null;
		}
		
	}
	
	public void mergeBest(PerceptEntityData data)
	{
		if (data.percepted) 
		{
			percepted = true;
		}
		if (data.distanceKnown) 
		{
			distanceKnown = true;
			distance = data.distance;
		}
		if (data.kindKnown) {
			kindKnown = true;
			kind = data.kind;
		}
		if (data.groupSizeKnown) {
			groupSizeKnown = true;
			groupSize = data.groupSize;
		}
	}
	
	public String toString()
	{
		return "Ped: "+unit.getName()+" -- perc "+percepted + " kind "+kindKnown+" size "+groupSizeKnown+" dist "+distanceKnown;
	}

	public ArrayList<PerceptVisibleForm> getPerceptedForms() {
		
		return unit.getPerceptedForms(this);
	}

	
}
