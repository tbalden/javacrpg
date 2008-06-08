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
	
	static int entityIdSequence = 0;
	
	public synchronized static int getNextEntityId()
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
	 * @param encounterInfo PreEncounterInfo object to fill
	 * @param fillOwn If this is true preEncoutnerInfo's ownGroupIds' are set, otherwise the ecounteredGroupIds are filled.
	 */
	public static void calcGroupsOfEncounter(EncounterUnit self, EncounterUnit target, int posX, int posY, int posZ, int radiusRatio, EncounterInfo encounterInfo, boolean fillOwn)
	{
		int rand = HashUtil.mix(self.getEncounterBoundary().posX, self.getEncounterBoundary().posY, self.getEncounterBoundary().posZ);
		ArrayList<EncounterUnit> units = null;
		int[] groupIds = null;
		if (target instanceof PersistentMemberInstance)
		{
			System.out.println(fillOwn + "SELF = "+self.getName()+" TARGET = "+target.getName());
			units = target.getSubUnits(posX, posY, posZ);
			System.out.println(units==null?"0_":units.size());
		} else 
		{
			groupIds = target.getGroupIds(posX,posY,posZ,radiusRatio, rand);
			
		}
		if (fillOwn)
		{
			if (groupIds!=null && groupIds.length>0) encounterInfo.appendOwnGroupIds(self,groupIds);
			if (units!=null) encounterInfo.appendOwnSubUnits(self, units);
			if (groupIds!=null && groupIds.length>0) encounterInfo.encounteredGroupIds.put(target, groupIds);
			if (units!=null) encounterInfo.encounteredSubUnits.put(target, units);
		} else
		{
			if (self == J3DCore.getInstance().gameState.player.theFragment) {
				Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter ADDING "+groupIds + " WITH RADIUS RATIO = "+radiusRatio+ " SELF COORDS "+self.getEncounterBoundary().posX+" "+self.getEncounterBoundary().posZ);
				Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter TARGET = "+target.getNumericId());
			}
			if (groupIds!=null && groupIds.length>0) encounterInfo.encounteredGroupIds.put(target, groupIds);
			if (units!=null) 
			{
				System.out.println("APPENDING "+self.getName()+" "+target.getName()+" - "+encounterInfo);
				encounterInfo.encounteredSubUnits.put(target, units);
			}
		}
	}
	
	/**
	 * To prevent creation of new PreEncounterInfo object instances they are stored
	 * in this list for reuse on each getNearbyEncounters call. 
	 */
	static ArrayList<EncounterInfo> staticEncounterInfoInstances = new ArrayList<EncounterInfo>();
	
	public void intersectTwoUnits(EncounterUnit fragment, EncounterUnit targetFragment, HashMap<EncounterUnit,int[][]> listOfCommonRadiusFragments, TreeLocator loc, int joinLimit)
	{
		int[][] r = DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(fragment.getEncounterBoundary(), targetFragment.getEncounterBoundary());
		if (r==DistanceBasedBoundary.zero) return; // no common part
		if (targetFragment==J3DCore.getInstance().gameState.player.theFragment)
		{
			Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found for player: "+targetFragment.getDescription().getClass().getSimpleName());
		}
		if (fragment==J3DCore.getInstance().gameState.player.theFragment)
		{
			Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found player ecounter: "+targetFragment.getDescription().getClass().getSimpleName());
			//System.out.println("Ecology.getNearbyEncounters(): Found player ecounter: "+targetFragment.getDescription().getClass().getSimpleName());
			//System.out.println("## "+counter);
		}
		
		listOfCommonRadiusFragments.put(targetFragment, r);
		loc.addElement(r[1][0], r[1][1], r[1][2], targetFragment);
		
		loc.addElement(r[1][0]+joinLimit, r[1][1], r[1][2], targetFragment);
		loc.addElement(r[1][0]+joinLimit, r[1][1], r[1][2]+joinLimit, targetFragment);
		loc.addElement(r[1][0], r[1][1], r[1][2]+joinLimit, targetFragment);
		loc.addElement(r[1][0]-joinLimit, r[1][1], r[1][2], targetFragment);
		loc.addElement(r[1][0]-joinLimit, r[1][1], r[1][2]+joinLimit, targetFragment);
		loc.addElement(r[1][0]-joinLimit, r[1][1], r[1][2]-joinLimit, targetFragment);
		loc.addElement(r[1][0], r[1][1], r[1][2]-joinLimit, targetFragment);
		loc.addElement(r[1][0]+joinLimit, r[1][1], r[1][2]-joinLimit, targetFragment);
		
	}
	
	/**
	 * Returns the possible nearby encounters for a given entity.
	 * @param entity
	 * @return
	 */
	public Collection<EncounterInfo> getNearbyEncounters(EntityInstance entityInstance)
	{
		// TODO info for fixed PersistentMemberInstances with help of EncounterUnit!!!
		
		int joinLimit = 10;
		int counter = 0;
		//ArrayList<PreEncounterInfo> entities = new ArrayList<PreEncounterInfo>();
		for (EntityFragment fragment:entityInstance.fragments.fragments) 
		{
			HashMap<EncounterUnit,int[][]> listOfCommonRadiusFragments = new HashMap<EncounterUnit,int[][]>();
			TreeLocator loc = new TreeLocator(entityInstance.world);
			for (EntityInstance targetEntityInstance:beings.values())
			{
				// don't check for the identical entity, continue. 
				if (targetEntityInstance==entityInstance) continue;
	
				for (EntityFragment targetFragment:targetEntityInstance.fragments.fragments) {
					// calculate the common area sizes.
					intersectTwoUnits(fragment, targetFragment, listOfCommonRadiusFragments, loc, joinLimit);
					
					if (!targetFragment.alwaysIncludeFollowingMembers)
					{
						for (PersistentMemberInstance pmiTarget:targetFragment.getFollowingMembers())
						{
							//System.out.println("--- $$$$ ###### "+pmiTarget.getName()+pmiTarget.getEncounterBoundary().posX+" "+pmiTarget.getEncounterBoundary().posZ);
							intersectTwoUnits(fragment, pmiTarget, listOfCommonRadiusFragments, loc, joinLimit);
						}
					} else
					{
						// in this case the EncounterInfo.getEncounterUnitDataList will add all PersistenMemberInstance 
						// to the list, so no intersection is needed.
					}
					
					for (PersistentMemberInstance pmi:fragment.getFollowingMembers())
					{
						//System.out.println("--- ###### "+pmi.getName()+pmi.getEncounterBoundary().posX+" "+pmi.getEncounterBoundary().posZ);
						intersectTwoUnits(pmi, targetFragment, listOfCommonRadiusFragments, loc, joinLimit);
						for (PersistentMemberInstance pmiTarget:targetFragment.getFollowingMembers())
						{
							//System.out.println("--- $$$$ ###### "+pmiTarget.getName()+pmiTarget.getEncounterBoundary().posX+" "+pmiTarget.getEncounterBoundary().posZ);
							intersectTwoUnits(pmi, pmiTarget, listOfCommonRadiusFragments, loc, joinLimit);
						}
					}
				}
				
			}
			
			// TODO still no luck with "attacking" PersistentMembers doesnt show up in the menu, only
			// it's parent fragment! Also humans don't show up in the list, if memberinstance boundary size is
			// set to 60!!
		
			ArrayList<EncounterUnit> usedUp = new ArrayList<EncounterUnit>();
			for (EncounterUnit f:listOfCommonRadiusFragments.keySet())
			{
				if (usedUp.contains(f)) continue;
				usedUp.add(f);
				int[][] r = listOfCommonRadiusFragments.get(f);
				EncounterInfo pre = null;
				if (staticEncounterInfoInstances.size()==counter)
				{
					pre = new EncounterInfo(fragment);
					pre.encountered.put(fragment, r); // put self too
					pre.encountered.put(f, r);
					staticEncounterInfoInstances.add(pre);
				} else
				{
					pre = staticEncounterInfoInstances.get(counter);
					pre.subject = fragment.instance;
					pre.subjectFragment = fragment;
					pre.encountered.clear();
					pre.encounteredUnitsAndOwnGroupIds.clear();
					pre.encounteredSubUnits.clear();
					pre.encounteredUnitsAndOwnSubUnits.clear();
					pre.encounteredGroupIds.clear();
					pre.encountered.put(fragment, r); // put self too
					pre.encountered.put(f, r);
				}
				calcGroupsOfEncounter(fragment, f, r[1][0], r[1][1], r[1][2], r[0][1], pre, false);
				// fill how many of the interceptor entity group intercepts the target
				calcGroupsOfEncounter(f, fragment, r[1][0], r[1][1], r[1][2], r[0][0], pre, true);

				Vector3f v1 = new Vector3f(r[1][0],r[1][1],r[1][2]);
				ArrayList<Object> elements1 = loc.getElements(r[1][0]+joinLimit, r[1][1], r[1][2]); // TODO this is only partial data!!
				ArrayList<Object> elements2 = loc.getElements(r[1][0]+joinLimit, r[1][1], r[1][2]+joinLimit);
				ArrayList<Object> elements3 = loc.getElements(r[1][0], r[1][1], r[1][2]+joinLimit);
				ArrayList<Object> elements4 = loc.getElements(r[1][0]+joinLimit, r[1][1], r[1][2]-joinLimit);
				ArrayList<Object> elements5 = loc.getElements(r[1][0], r[1][1], r[1][2]-joinLimit);
				ArrayList<Object> elements6 = loc.getElements(r[1][0]-joinLimit, r[1][1], r[1][2]-joinLimit);
				ArrayList<Object> elements7 = loc.getElements(r[1][0]-joinLimit, r[1][1], r[1][2]+joinLimit);
				ArrayList<Object> elements8 = loc.getElements(r[1][0]-joinLimit, r[1][1], r[1][2]);
				ArrayList<Object> elements9 = loc.getElements(r[1][0], r[1][1], r[1][2]);
				ArrayList<Object> elements = new ArrayList<Object>();
				if (elements1!=null) elements.addAll(elements1);
				if (elements2!=null) elements.addAll(elements2);
				if (elements3!=null) elements.addAll(elements3);
				if (elements4!=null) elements.addAll(elements4);
				if (elements5!=null) elements.addAll(elements5);
				if (elements6!=null) elements.addAll(elements6);
				if (elements7!=null) elements.addAll(elements7);
				if (elements8!=null) elements.addAll(elements8);
				if (elements9!=null) elements.addAll(elements9);
				System.out.println("________________________");
				if (elements!=null)
				for (Object o:elements)
				{
					EncounterUnit fT = ((EncounterUnit)o);
					if (fT==f || usedUp.contains(fT)) continue;
					int[][] r2 = listOfCommonRadiusFragments.get(fT);
					Vector3f v2 = new Vector3f(r2[1][0],r2[1][1],r2[1][2]);
					if (v2.distance(v1)<joinLimit)
					{
						//System.out.println(" __ "+r[1][0]+" "+r[1][2]);
						//System.out.println(" __ "+r2[1][0]+" "+r2[1][2]);
						//System.out.println( " ___ "+ f.roamingBoundary.posX +" "+f.roamingBoundary.posZ);
						//System.out.println( " ___ "+ fT.roamingBoundary.posX +" "+fT.roamingBoundary.posZ);
						System.out.println("DIFF 10 > "+v2.distance(v1) + fT.getName());
						usedUp.add(fT);
						pre.encountered.put(fT, r2);
						calcGroupsOfEncounter(fragment, fT, r2[1][0], r2[1][1], r2[1][2], r2[0][1], pre, false);
						// fill how many of the interceptor entity group intercepts the target
						calcGroupsOfEncounter(fT, fragment, r2[1][0], r2[1][1], r2[1][2], r2[0][0], pre, true);
					} else
					{
						//System.out.println(" __ "+r[1][0]+" "+r[1][2]);
						//System.out.println(" __ "+r2[1][0]+" "+r2[1][2]);
						//System.out.println( " ___ "+ f.roamingBoundary.posX +" "+f.roamingBoundary.posZ);
						//System.out.println( " ___ "+ fT.roamingBoundary.posX +" "+fT.roamingBoundary.posZ);
						//System.out.println("!! DIFF 10 < "+v2.distance(v1) + fT.getName());
					}
				}
				//for (EncounterUnit fr:pre.encountered.keySet()) 
				{
					//if (entityInstance == J3DCore.getInstance().gameState.player || fr==J3DCore.getInstance().gameState.player.theFragment)
						//System.out.println("ENCOUNTER = "+entityInstance.description.getClass() + pre.encountered.size()+" "+fr.getDescription().getClass()+" "+pre.encounteredGroupIds.get(fr).length
							//	+ " " + fragment.roamingBoundary.posX+ " / "+fragment.roamingBoundary.posZ
							//	+ " " +fr.getEncounterBoundary().posX+ " / "+fr.getEncounterBoundary().posZ + " "+r[1][0] +" / "+r[1][2]
						//);
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

		long time = System.currentTimeMillis();
		
		/*
		 * Every turn do a random order liveOneTurn for the beings of ecology with
		 * helf of hashutil.
		 */			
		if (!interrupted)
		{
			J3DCore.getInstance().uiBase.hud.sr.setVisibility(true, "DICE");
			J3DCore.getInstance().updateDisplay(null);

			Collection<EntityInstance> toRemove = new HashSet<EntityInstance>();
			for (EntityInstance i:orderedBeingList)
			{
				if (i.merged)
				{
					toRemove.add(i);
					
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
			engine.ecologyTurnInterruptedByPlayerInteraction();
		} else
		{
			engine.ecologyTurnFinished();
			J3DCore.getInstance().uiBase.hud.sr.setVisibility(false, "DICE");
			J3DCore.getInstance().updateDisplay(null);
		}
		Jcrpg.LOGGER.info("TURN TIME "+ (time - System.currentTimeMillis())/1000f);
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
