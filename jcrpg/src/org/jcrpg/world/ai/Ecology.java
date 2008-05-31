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
import java.util.HashSet;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.HashUtil;
import org.jcrpg.world.Engine;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.place.TreeLocator;
import org.jcrpg.world.place.World;

import com.jme.math.Vector3f;

public class Ecology {

	
	public transient HashMap<World, TreeLocator> locators;
	
	public static int PHASE_INTERCEPTION = 0;
	public static int PHASE_ENCOUNTER = 1;
	public static int PHASE_TURNACT_SOCIAL_RIVALRY = 2;
	public static int PHASE_TURNACT_COMBAT = 3;
	
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
			for (EntityFragment f:i.fragments.fragments)
			{
				addToLocator(f);
			}
		}
	}
	
	public void addToLocator(EntityFragment i)
	{
		if (i.instance.world==null) return;
		TreeLocator l = locators.get(i.instance.world);
		if (l==null)
		{
			l = new TreeLocator(i.instance.world);
			locators.put(i.instance.world, l);
		}
		l.addEntityFragment(i);
	}
	public void removeFromLocator(EntityFragment i)
	{
		if (i.instance.world==null) return;
		TreeLocator l = locators.get(i.instance.world);
		if (l==null)
		{
			l = new TreeLocator(i.instance.world);
			locators.put(i.instance.world, l);
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
		for (EntityFragment f:entityInstance.fragments.fragments) {
			addToLocator(f);
		}
	}
	
	public Collection<Object> getEntities(World w, int worldX, int worldY, int worldZ)
	{
		TreeLocator l = locators.get(w);
		if (l==null) return null;
		return l.getElements(worldX, worldY, worldZ);		
	}
	
	/**
	 * Calculates groups in the intersection of target with a self instance. 
	 * @param self the interceptor entity fragment.
	 * @param target the encountered entity fragment.
	 * @param radiusRatio the common radius.
	 * @param toFill PreEncounterInfo object to fill
	 * @param fillOwn If this is true preEncoutnerInfo's ownGroupIds' are set, otherwise the ecounteredGroupIds are filled.
	 */
	public static void calcGroupsOfEncounter(EntityFragment self, EntityFragment target, int radiusRatio, EncounterInfo toFill, boolean fillOwn)
	{
		int rand = HashUtil.mix(self.roamingBoundary.posX, self.roamingBoundary.posY, self.roamingBoundary.posZ);
		int[] groupIds = target.instance.description.groupingRule.getGroupIds(target,target.instance, radiusRatio, rand);
		if (fillOwn)
		{
			toFill.ownGroupIds = groupIds;
		} else
		{
			if (self == J3DCore.getInstance().gameState.player.theFragment) {
				Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter ADDING "+groupIds + " WITH RADIUS RATIO = "+radiusRatio+ " SELF COORDS "+self.roamingBoundary.posX+" "+self.roamingBoundary.posZ);
				Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter TARGET = "+target.instance.id);
			}
			toFill.encounteredGroupIds.put(target, groupIds);
		}
	}
	
	/**
	 * To prevent creation of new PreEncounterInfo object instances they are stored
	 * in this list for reuse on each getNearbyEncounters call. 
	 */
	static ArrayList<EncounterInfo> staticEncounterInfoInstances = new ArrayList<EncounterInfo>();
	/**
	 * Returns the possible nearby encounters for a given entity.
	 * @param entity
	 * @return
	 */
	public Collection<EncounterInfo> getNearbyEncounters(EntityInstance entityInstance)
	{
		int counter = 0;
		//ArrayList<PreEncounterInfo> entities = new ArrayList<PreEncounterInfo>();
		for (EntityFragment fragment:entityInstance.fragments.fragments) 
		{
			HashMap<EntityFragment,int[][]> listOfCommonRadiusFragments = new HashMap<EntityFragment,int[][]>();
			TreeLocator loc = new TreeLocator(entityInstance.world);
			for (EntityInstance targetEntityInstance:beings.values())
			{
				// don't check for the identical entity, continue. 
				if (targetEntityInstance==entityInstance) continue;
	
				for (EntityFragment targetFragment:targetEntityInstance.fragments.fragments) {
					if (!targetFragment.availableInThisTurn) continue; // if this fragment was part of an encounter then it shouldn't be reused again.
						
					// calculate the common area sizes.				
					int[][] r = DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(fragment.roamingBoundary, targetFragment.roamingBoundary);
					if (r==DistanceBasedBoundary.zero) continue; // no common part
					if (targetFragment==J3DCore.getInstance().gameState.player.theFragment)
					{
						Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found for player: "+targetFragment.instance.id);
					}
					if (fragment==J3DCore.getInstance().gameState.player.theFragment)
					{
						Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found player ecounter: "+targetFragment.instance.id);
						//System.out.println("## "+counter);
					}
					
					listOfCommonRadiusFragments.put(targetFragment, r);
					loc.addElement(r[1][0], r[1][1], r[1][2], targetFragment);
				}
				
			}
		
			ArrayList<EntityFragment> usedUp = new ArrayList<EntityFragment>();
			for (EntityFragment f:listOfCommonRadiusFragments.keySet())
			{
				if (usedUp.contains(f)) continue;
				usedUp.add(f);
				int[][] r = listOfCommonRadiusFragments.get(f);
				EncounterInfo pre = null;
				if (staticEncounterInfoInstances.size()==counter)
				{
					pre = new EncounterInfo(fragment);
					pre.encountered.put(f, r);
					staticEncounterInfoInstances.add(pre);
				} else
				{
					pre = staticEncounterInfoInstances.get(counter);
					pre.subject = fragment.instance;
					pre.encountered.clear();
					pre.encounteredGroupIds.clear();
					pre.encountered.put(f, r);
				}
				calcGroupsOfEncounter(fragment, f, r[0][1], pre, false);
				// fill how many of the interceptor entity group intercepts the target
				calcGroupsOfEncounter(f, fragment, r[0][0], pre, true);

				ArrayList<Object> elements = loc.getElements(r[1][0], r[1][1], r[1][2]); // TODO this is only partial data!!
				for (Object o:elements)
				{
					EntityFragment fT = ((EntityFragment)o);
					if (fT==f) continue;
					int[][] r2 = listOfCommonRadiusFragments.get(fT);
					Vector3f v1 = new Vector3f(r[1][0],r[1][1],r[1][2]);
					Vector3f v2 = new Vector3f(r2[1][0],r2[1][1],r2[1][2]);
					if (v2.distance(v1)<10)
					{
						usedUp.add(fT);
						pre.encountered.put(fT, r2);
						calcGroupsOfEncounter(fragment, fT, r2[0][1], pre, false);
						// fill how many of the interceptor entity group intercepts the target
						calcGroupsOfEncounter(fT, fragment, r2[0][0], pre, true);
					}						
				}
				for (EntityFragment fr:pre.encountered.keySet()) {
					if (fr==J3DCore.getInstance().gameState.player.theFragment)
						System.out.println("ENCOUNTER = "+entityInstance.description.getClass() + pre.encountered.size()+" "+fr.instance.description.getClass()+" "+pre.encounteredGroupIds.get(fr).length);
				}
				counter++;
				pre.active = true;
			}

		}
		for (int i=counter; i<staticEncounterInfoInstances.size(); i++)
		{
			staticEncounterInfoInstances.get(i).subject = null;
			staticEncounterInfoInstances.get(i).active = false;
		}
		
		return staticEncounterInfoInstances;
	}
	
	byte[] placeBitMap = new byte[] {1,2,4,8,16,32,64,(byte)128};
	byte[] switchArray;	
	int counterOfDoneTurnBeings = 0;
	/**
	 * this is set true if the turn iteration was interrupted by player interaction window,
	 * and doTurn must continue the iteration when called again from j3dcore.
	 */
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
			Collection<EntityInstance> toRemove = new HashSet<EntityInstance>();
			for (EntityInstance i:orderedBeingList)
			{
				if (i.merged)
				{
					toRemove.add(i);
					
				} else {			
					for (EntityFragment f:i.fragments.fragments)
					{
						f.availableInThisTurn = true;
					}
				}
			}
			orderedBeingList.removeAll(toRemove);
			beings.values().removeAll(toRemove);
			
			counterOfDoneTurnBeings = 0;
			switchArray = new byte[orderedBeingList.size()/8+orderedBeingList.size()%8];
			
		} else
		{
			System.out.println("CONTINUING PLAYER INTERRUPTED ECOLOGY TURN...");
			interrupted = false; // interrupted must be set false now.
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
			if (orderedBeingList.get(r).liveOneTurn(getNearbyEncounters(orderedBeingList.get(r))))
			{
				
				// updating tree locator for being...
				for (EntityFragment f:orderedBeingList.get(r).fragments.fragments) {
					if (f.roamingBoundary.changed()) {
						removeFromLocator(f);
						addToLocator(f);
					}
				}
				// interrupt is needed because UI thread of player will be active for interaction. UI will
				// have to call this function again with continue = true in method signature.
				System.out.println("ECOLOGY INTERRUPTED BY: "+orderedBeingList.get(r).description);
				interrupted=true;
				break;
			}
			// updating tree locator for being...
			for (EntityFragment f:orderedBeingList.get(r).fragments.fragments) {
				if (f.roamingBoundary.changed()) {
					removeFromLocator(f);
					addToLocator(f);
				}
			}
		}
		if (interrupted)
		{
			engine.turnInterruptedByPlayerInteraction = true;
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
