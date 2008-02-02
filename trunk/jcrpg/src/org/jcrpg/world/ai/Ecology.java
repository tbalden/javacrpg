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

package org.jcrpg.world.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;

import com.jme.math.Vector3f;

public class Ecology {

	HashMap<String, EntityInstance> beings = new HashMap<String, EntityInstance>();
	
	
	public void addEntity(EntityInstance description)
	{
		beings.put(description.id, description);
	}
	
	public Collection<EntityDescription> getEntities(int worldX, int worldY, int worldZ)
	{
		return null;
	}
	
	static ArrayList<PreEncounterInfo> staticEntities = new ArrayList<PreEncounterInfo>();
	public Collection<PreEncounterInfo> getNearbyEncounters(EntityInstance entity)
	{
		int counter = 0;
		//ArrayList<PreEncounterInfo> entities = new ArrayList<PreEncounterInfo>();
		for (EntityInstance targetEntity:beings.values())
		{
			if (targetEntity==entity) continue;
			int[][] r = DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(entity.roamingBoundary, targetEntity.roamingBoundary);
			if (r==DistanceBasedBoundary.zero) continue; // no common part
			if (staticEntities.size()<=counter)
			{
				PreEncounterInfo pre = new PreEncounterInfo(entity);
				pre.encountered.put(targetEntity, r);
				staticEntities.add(pre);
			} else
			{
				PreEncounterInfo pre = staticEntities.get(counter++);
				pre.subject = entity;
				pre.encountered.clear();
				pre.encountered.put(targetEntity, r);
			}
		}
		for (int i=counter; i<staticEntities.size(); i++)
		{
			staticEntities.get(i).subject = null;
		}
		if (true==false) {
			for (PreEncounterInfo info1:staticEntities)
			{
				if (info1.encountered==null) continue;
				int[][] r = info1.encountered.values().iterator().next();
				Vector3f v1 = new Vector3f(r[1][0],r[1][1],r[1][2]);
				for (PreEncounterInfo info2:staticEntities)
				{
					if (info2.encountered==null) continue;
					if (info2!=info1)
					{
						int[][] r2 = info2.encountered.values().iterator().next();
						Vector3f v2 = new Vector3f(r2[1][0],r2[1][1],r2[1][2]);
						if (v2.distance(v1)<10)
						{
							info1.encountered.putAll(info2.encountered);
							info2.encountered = null;
						}
					}
				}
			}
			ArrayList<PreEncounterInfo> newEntities = new ArrayList<PreEncounterInfo>();
			for (PreEncounterInfo targetEntity:staticEntities)
			{
				if (targetEntity.encountered==null) continue;
				newEntities.add(targetEntity);
			}
			return newEntities;
		}
		return staticEntities;
	}
	
	public void doTurn()
	{
		long time = System.currentTimeMillis();
		for (EntityInstance entity:beings.values())
		{
			entity.liveOneTurn(getNearbyEncounters(entity));
		}
		System.out.println("TURN TIME "+ (time - System.currentTimeMillis())/1000f);
	}
	
	public void callbackMessage(String message)
	{
		// TODO this is just for the testing period
		J3DCore.getInstance().uiBase.hud.mainBox.addEntry(message);
	}
	
}
