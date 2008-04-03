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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;

import com.jme.math.Vector3f;

public class Ecology {

	
	public static int PHASE_INTERCEPTION = 0;
	public static int PHASE_ENCOUNTER = 1;
	public static int PHASE_TURNACT = 2;
	
	public HashMap<String, EntityInstance> beings = new HashMap<String, EntityInstance>();
	
	
	public void addEntity(EntityInstance description)
	{
		beings.put(description.id, description);
	}
	
	public Collection<EntityDescription> getEntities(int worldX, int worldY, int worldZ)
	{
		return null;
	}
	
	/**
	 * Calculates groups in the intersection of target with a self instance. 
	 * @param self the interceptor entity
	 * @param target the encountered entity
	 * @param radiusRatio the common radius.
	 * @param toFill PreEncounterInfo object to fill
	 * @param fillOwn If this is true preEncoutnerInfo's ownGroupIds' are set, otherwise the ecounteredGroupIds are filled.
	 */
	public static void calcGroupsOfEncounter(EntityInstance self, EntityInstance target, int radiusRatio, PreEncounterInfo toFill, boolean fillOwn)
	{
		int rand = HashUtil.mix(self.roamingBoundary.posX, self.roamingBoundary.posY, self.roamingBoundary.posZ);
		int[] groupIds = target.description.groupingRule.getGroupIds(target, radiusRatio, rand);
		if (fillOwn)
		{
			toFill.ownGroupIds = groupIds;
		} else
		{
			if (self == J3DCore.getInstance().gameState.player) {
				Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter ADDING "+groupIds + " WITH RADIUS RATIO = "+radiusRatio+ " SELF COORDS "+self.roamingBoundary.posX+" "+self.roamingBoundary.posZ);
				Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter TARGET = "+target.id);
			}
			toFill.encounteredGroupIds.put(target, groupIds);
		}
	}
	
	/**
	 * To prevent creation of new PreEncounterInfo object instances they are stored
	 * in this list for reuse on each getNearbyEncounters call. 
	 */
	static ArrayList<PreEncounterInfo> staticEncounterInfoInstances = new ArrayList<PreEncounterInfo>();
	/**
	 * Returns the possible nearby encounters for a given entity.
	 * @param entity
	 * @return
	 */
	public Collection<PreEncounterInfo> getNearbyEncounters(EntityInstance entity)
	{
		int counter = 0;
		//ArrayList<PreEncounterInfo> entities = new ArrayList<PreEncounterInfo>();
		for (EntityInstance targetEntity:beings.values())
		{
			// don't check for the identical entity, continue. 
			if (targetEntity==entity) continue;

			// calculate the common area sizes.
			int[][] r = DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(entity.roamingBoundary, targetEntity.roamingBoundary);
			if (r==DistanceBasedBoundary.zero) continue; // no common part
			if (entity==J3DCore.getInstance().gameState.player)
			{
				Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found for player: "+targetEntity.id);
			}
			PreEncounterInfo pre = null;
			if (staticEncounterInfoInstances.size()<=counter)
			{
				pre = new PreEncounterInfo(entity);
				pre.encountered.put(targetEntity, r);
				staticEncounterInfoInstances.add(pre);
			} else
			{
				pre = staticEncounterInfoInstances.get(counter);
				pre.subject = entity;
				pre.encountered.clear();
				pre.encounteredGroupIds.clear();
				pre.encountered.put(targetEntity, r);
			}
			counter++;
			// fill how many of the target group is intercepted by the given entity
			calcGroupsOfEncounter(entity, targetEntity, r[0][1], pre, false);
			// fill how many of the interceptor entity group intercepts the target
			calcGroupsOfEncounter(targetEntity, entity, r[0][0], pre, true);
			pre.active = true;
		}
		for (int i=counter; i<staticEncounterInfoInstances.size(); i++)
		{
			staticEncounterInfoInstances.get(i).subject = null;
			staticEncounterInfoInstances.get(i).active = false;
		}
		
		
		if (true==false) {
			for (PreEncounterInfo info1:staticEncounterInfoInstances)
			{
				if (info1.encountered==null) continue;
				int[][] r = info1.encountered.values().iterator().next();
				Vector3f v1 = new Vector3f(r[1][0],r[1][1],r[1][2]);
				for (PreEncounterInfo info2:staticEncounterInfoInstances)
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
			for (PreEncounterInfo targetEntity:staticEncounterInfoInstances)
			{
				if (targetEntity.encountered==null) continue;
				newEntities.add(targetEntity);
			}
			return newEntities;
		}
		
		
		return staticEncounterInfoInstances;
	}
	
	public void doTurn()
	{
		J3DCore.getInstance().uiBase.hud.sr.setVisibility(true, "DICE");
		J3DCore.getInstance().updateDisplay(null);

		long time = System.currentTimeMillis();
		for (EntityInstance entity:beings.values())
		{
			entity.liveOneTurn(getNearbyEncounters(entity));
		}
		Jcrpg.LOGGER.info("TURN TIME "+ (time - System.currentTimeMillis())/1000f);
		J3DCore.getInstance().uiBase.hud.sr.setVisibility(false, "DICE");
		J3DCore.getInstance().updateDisplay(null);
	}
	
	public void callbackMessage(String message)
	{
		// TODO this is just for the testing period
		J3DCore.getInstance().uiBase.hud.mainBox.addEntry(message);
	}
	
	public void clearAll()
	{
		beings.clear();
	}
	
}
