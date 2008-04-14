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
import org.jcrpg.world.Engine;
import org.jcrpg.world.place.TreeLocator;
import org.jcrpg.world.place.World;

import com.jme.math.Vector3f;

public class Ecology {

	
	public transient HashMap<World, TreeLocator> locators;
	
	public static int PHASE_INTERCEPTION = 0;
	public static int PHASE_ENCOUNTER = 1;
	public static int PHASE_TURNACT = 2;
	
	public HashMap<Integer, EntityInstance> beings = new HashMap<Integer, EntityInstance>();
	public ArrayList<EntityInstance> orderedBeingList = new ArrayList<EntityInstance>();
	
	
	public Engine engine; 
	
	public Ecology(Engine engine)
	{
		initTransient();
		this.engine = engine;
	}
	
	public void initTransient()
	{
		locators = new HashMap<World, TreeLocator>();
	}
	
	public void onLoad()
	{
		initTransient();
		for (EntityInstance i:orderedBeingList)
		{
			addToLocator(i);
		}
	}
	
	public void addToLocator(EntityInstance i)
	{
		if (i.world==null) return;
		TreeLocator l = locators.get(i.world);
		if (l==null)
		{
			l = new TreeLocator(i.world);
			locators.put(i.world, l);
		}
		l.addEntityInstance(i);
	}
	public void removeFromLocator(EntityInstance i)
	{
		if (i.world==null) return;
		TreeLocator l = locators.get(i.world);
		if (l==null)
		{
			l = new TreeLocator(i.world);
			locators.put(i.world, l);
		}
		l.removeAllOfAnObject(i);
	}
	
	int entityIdSequence = 0;
	
	public synchronized int getNextEntityId()
	{
		return entityIdSequence++;
	}
	
	public void addEntity(EntityInstance entityInstance)
	{
		beings.put(entityInstance.numericId, entityInstance);
		orderedBeingList.add(entityInstance);
		addToLocator(entityInstance);
	}
	
	public Collection<Object> getEntities(World w, int worldX, int worldY, int worldZ)
	{
		TreeLocator l = locators.get(w);
		if (l==null) return null;
		return l.getElements(worldX, worldY, worldZ);		
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
			if (targetEntity==J3DCore.getInstance().gameState.player)
			{
				Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found for player: "+targetEntity.id);
			}
			if (entity==J3DCore.getInstance().gameState.player)
			{
				Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found player ecounter: "+targetEntity.id);
				//System.out.println("## "+counter);
			}
			PreEncounterInfo pre = null;
			if (staticEncounterInfoInstances.size()==counter)
			{
				//System.out.println("New Static Encounter Info created (name, target, counter): "+entity.description+" "+entity.description+" "+counter);
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
	
	byte[] placeBitMap = new byte[] {1,2,4,8,16,32,64,(byte)128};
	byte[] switchArray;	
	int counterOfDoneTurnBeings = 0;
	boolean interrupted = false;
	public void doTurn()
	{
		System.out.println("ALL ENTITIES IN ECOLOGY IN TURN NO. "+engine.numberOfTurn+" = "+beings.size());
		J3DCore.getInstance().uiBase.hud.sr.setVisibility(true, "DICE");
		J3DCore.getInstance().updateDisplay(null);

		long time = System.currentTimeMillis();
		
		/*
		 * Every turn do a random order liveOneTurn for the beings of ecology with
		 * helf of hashutil.
		 */			
		if (!interrupted)
		{
			counterOfDoneTurnBeings = 0;
			switchArray = new byte[orderedBeingList.size()/8+orderedBeingList.size()%8];
			
		} else
		{
			System.out.println("CONTINUING PLAYER INTERRUPTED ECOLOGY TURN...");
			interrupted = false;
		}
		for (int i=counterOfDoneTurnBeings; i<orderedBeingList.size(); i++)
		{
			int r = HashUtil.mix((int)engine.numberOfTurn, i, 0);// get a quasi-random number 
			r = r%orderedBeingList.size();
			while (true) { // iterate as long as we have found a yet not acting being of this turn... 
				int bitPlace = r%8;
				bitPlace = placeBitMap[bitPlace];
				if ((switchArray[r/8]&bitPlace)==bitPlace)
				{
					r++;
					if (r>=orderedBeingList.size()) r = 0;
				} else
				{
					byte b = switchArray[r/8];
					b = (byte)((byte)b|(byte)bitPlace);
					switchArray[r/8] = b;
					break;
				}
			}
			counterOfDoneTurnBeings++;
			removeFromLocator(orderedBeingList.get(r));
			if (orderedBeingList.get(r).liveOneTurn(getNearbyEncounters(orderedBeingList.get(r))))
			{
				// updating tree locator for being...
				addToLocator(orderedBeingList.get(r));
				// interrupt is needed because UI thread of player will be active for interaction. UI will
				// have to call this function again with continue = true in method signature.
				System.out.println("ECOLOGY INTERRUPTED BY: "+orderedBeingList.get(r).description);
				interrupted=true;
				break;
			}
			// updating tree locator for being...
			addToLocator(orderedBeingList.get(r));
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
