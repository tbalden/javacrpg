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
import org.jcrpg.world.place.World.WorldTypeDesc;

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
		staticEncounterInfoInstances = new ArrayList<EncounterInfo>();
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
		beings.put(entityInstance.getNumericId(), entityInstance);
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
			// this is a subunit (member), not a group based entity, so we should get the subunits of the member.
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer(fillOwn + "SELF = "+self.getName()+" TARGET = "+target.getName());
			units = target.getSubUnits(posX, posY, posZ);
		} else 
		{
			// this is an entity so group ids are needed..
			groupIds = target.getGroupIds(posX,posY,posZ,radiusRatio, rand);			
		}
		
		// this is an own group filling call of this method, so we must fill 'own' data only 
		if (fillOwn)
		{
			if (groupIds!=null && groupIds.length>0) encounterInfo.appendOwnGroupIds(self,groupIds);
			if (units!=null) encounterInfo.appendOwnSubUnits(self, units);
			if (groupIds!=null && groupIds.length>0) encounterInfo.encounteredGroupIds.put(target, groupIds);
			if (units!=null) encounterInfo.encounteredSubUnits.put(target, units);
		} else
		{
			// this is the target group filling call of this method, so we must fill the encountered data...
			if (self == J3DCore.getInstance().gameState.player.theFragment) {
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter ADDING "+groupIds + " WITH RADIUS RATIO = "+radiusRatio+ " SELF COORDS "+self.getEncounterBoundary().posX+" "+self.getEncounterBoundary().posZ);
				if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Ecology.calcGroupsOfEncounter TARGET = "+target.getName());
			}
			if (groupIds!=null && groupIds.length>0) encounterInfo.encounteredGroupIds.put(target, groupIds);
			if (units!=null) 
			{
				encounterInfo.encounteredSubUnits.put(target, units);
			}
		}
	}
	
	/**
	 * To prevent creation of new PreEncounterInfo object instances they are stored
	 * in this list for reuse on each getNearbyEncounters call. 
	 */
	static transient ArrayList<EncounterInfo> staticEncounterInfoInstances = new ArrayList<EncounterInfo>();
	
	public boolean isReachableWorldType(WorldTypeDesc sourceDesc, WorldTypeDesc encounterlocation)
	{
		if (playerDebug)
		{
			System.out.println("sourceDesc LOCATION: "+sourceDesc.g+" / "+sourceDesc.surfaceY+" : "+(sourceDesc.population==null?sourceDesc.population:sourceDesc.population.foundationName));
			System.out.println("ENCOUNTER LOCATION: "+encounterlocation.g+" / "+encounterlocation.surfaceY+" : "+(encounterlocation.population==null?encounterlocation.population:encounterlocation.population.foundationName));
			
		}
		if (sourceDesc.g!=null && sourceDesc.g.placeNeedsToBeEnteredForEncounter)
		{
			if (sourceDesc.g!=encounterlocation.g) 
			{
				if (playerDebug)System.out.println("g _1");
				return false;
			}
		}
		if (encounterlocation.g!=null && encounterlocation.g.placeNeedsToBeEnteredForEncounter)
		{
			if (encounterlocation.g!=sourceDesc.g) 
			{
				if (playerDebug)System.out.println("g _2");
				return false;
			}
		}
		
		if (sourceDesc.population!=null && sourceDesc.population.placeNeedsToBeEnteredForEncounter)
		{
			if (sourceDesc.population!=encounterlocation.population) 
			{
				if (playerDebug)System.out.println("p _3");
				return false;
			}
				
		}
		if (encounterlocation.population!=null && encounterlocation.population.placeNeedsToBeEnteredForEncounter)
		{
			if (encounterlocation.population!=sourceDesc.population) 			
			{
				if (playerDebug)System.out.println("p _4");
				return false;
			}

		}
		
		if (sourceDesc.detailedEconomic!=null && sourceDesc.detailedEconomic.placeNeedsToBeEnteredForEncounter)
		{
			if (sourceDesc.detailedEconomic!=encounterlocation.detailedEconomic) 
			{
				if (playerDebug)System.out.println("d _5");
				return false;
			}

		}
		if (encounterlocation.detailedEconomic!=null && encounterlocation.detailedEconomic.placeNeedsToBeEnteredForEncounter)
		{
			if (encounterlocation.detailedEconomic!=sourceDesc.detailedEconomic)
			{
				if (playerDebug)System.out.println("d _6");
				return false;
			}
				
		}
		
		return true;
	}
	
	public WorldTypeDesc getEntityFragmentWorldTypeDesc(EntityFragment fragment)
	{
		int worldX = fragment.getEncounterBoundary().posX;
		int worldY = fragment.getEncounterBoundary().posY;
		int worldZ = fragment.getEncounterBoundary().posZ;
		WorldTypeDesc desc =  fragment.instance.world.getWorldDescAtPosition(worldX, worldY, worldZ,false);
		if (fragment.enteredPopulation!=null)
			desc.population = fragment.enteredPopulation;
		return desc;
	}
	
	public WorldTypeDesc getUnitStandingWorldTypeDesc(EncounterUnit unit)
	{
		int worldX = unit.getEncounterBoundary().posX;
		int worldY = unit.getEncounterBoundary().posY;
		int worldZ = unit.getEncounterBoundary().posZ;
		WorldTypeDesc desc =  unit.getFragment().instance.world.getWorldDescAtPosition(worldX, worldY, worldZ,false);
		if (unit.getFragment().enteredPopulation!=null)
			desc.population = unit.getFragment().enteredPopulation;
		if (playerDebug)
			System.out.println("UNIT DESC: "+unit.getName()+" -- "+(desc.population==null?"null":desc.population.foundationName)+" : "+worldX+"/"+worldY+"/"+worldZ);

		return desc;
	}
	boolean playerDebug = false;
	public void intersectTwoUnits(EncounterUnit initiatorUnit, EncounterUnit targetUnit, HashMap<EncounterUnit,int[][]> listOfCommonRadiusFragments, TreeLocator loc, int joinLimit)
	{
		int[][] r = DistanceBasedBoundary.getCommonRadiusRatiosAndMiddlePoint(initiatorUnit.getEncounterBoundary(), targetUnit.getEncounterBoundary());
		if (r==DistanceBasedBoundary.zero) return; // no common part detected, return..

		playerDebug = false;
		// matching geography/economic things now...
		int wX = r[1][0];
		int wY = r[1][1];
		int wZ = r[1][2];
		//if (initiatorUnit.getFragment()==J3DCore.getInstance().gameState.player.theFragment) playerDebug = true;
		//if (targetUnit.getFragment()==J3DCore.getInstance().gameState.player.theFragment) playerDebug = true;
		if (playerDebug)
		{
			System.out.println("######### initiator "+initiatorUnit.getName() + " target "+targetUnit.getName());
		}
		
		World w = initiatorUnit.getFragment().instance.world;
		if (targetUnit.getFragment().instance.world!=w) return;
		WorldTypeDesc desc = w.getWorldDescAtPosition(wX, wY, wZ, true);
		if (playerDebug)
		{
			System.out.println("Encounter LOC: "+desc.g+" / "+(desc.population!=null?desc.population.foundationName:"null")+" "+desc.surfaceY+" -- "+wX+"/"+wY+"/"+wZ);
		}
		if (!isReachableWorldType(getUnitStandingWorldTypeDesc(initiatorUnit),desc)) 
		{
			if (playerDebug)
			{
				System.out.println("NOT REACHABLE FOR initiator "+initiatorUnit.getName());
			}
			return;
		}
		if (!isReachableWorldType(getUnitStandingWorldTypeDesc(targetUnit),desc)) 
		{
			if (playerDebug)
			{
				System.out.println("NOT REACHABLE FOR target "+targetUnit.getName());
			}
			return;
		}
		
		// both units can reach the area , we can go on... 
		
		if (targetUnit==J3DCore.getInstance().gameState.player.theFragment)
		{
			if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found for player: "+targetUnit.getName());
		}
		if (initiatorUnit==J3DCore.getInstance().gameState.player.theFragment)
		{
			if (J3DCore.LOGGING) Jcrpg.LOGGER.info("Ecology.getNearbyEncounters(): Found player ecounter: "+targetUnit.getName());
		}
		
		// storing intersection numeric data for target
		listOfCommonRadiusFragments.put(targetUnit, r);
		
		// filling tree locator with the targetFragment for helping intersection group determination later...
		loc.addElement(r[1][0], r[1][1], r[1][2], targetUnit);		
		loc.addElement(r[1][0]+joinLimit, r[1][1], r[1][2], targetUnit);
		loc.addElement(r[1][0]+joinLimit, r[1][1], r[1][2]+joinLimit, targetUnit);
		loc.addElement(r[1][0], r[1][1], r[1][2]+joinLimit, targetUnit);
		loc.addElement(r[1][0]-joinLimit, r[1][1], r[1][2], targetUnit);
		loc.addElement(r[1][0]-joinLimit, r[1][1], r[1][2]+joinLimit, targetUnit);
		loc.addElement(r[1][0]-joinLimit, r[1][1], r[1][2]-joinLimit, targetUnit);
		loc.addElement(r[1][0], r[1][1], r[1][2]-joinLimit, targetUnit);
		loc.addElement(r[1][0]+joinLimit, r[1][1], r[1][2]-joinLimit, targetUnit);
		
	}
	
	/**
	 * Returns the possible nearby encounters for a given entity.
	 * @param entity
	 * @return
	 */
	public Collection<EncounterInfo> getNearbyEncounters(EntityInstance entityInstance)
	{
		
		int joinLimit = 10; // the circle's radius that will group together intersection points of different EncounterUnits.
		int counter = 0;
		
		// going through entity's fragments looking for intersections with other instances' fragments and following members...
		for (EntityFragment fragment:entityInstance.fragments.fragments) 
		{
			// the list that collects intersection data (for subunits or fragments as EncounterUnit and int[][] as intersection numeric data)
			HashMap<EncounterUnit,int[][]> listOfCommonRadiusFragments = new HashMap<EncounterUnit,int[][]>();
			// the locator to help grouping intersection points that will build up an EncounterInfo
			TreeLocator loc = new TreeLocator(entityInstance.world);
			
			// all active beings are being iterated...
			for (EntityInstance targetEntityInstance:beings.values())
			{
				// don't check for the identical entity, continue. 
				if (targetEntityInstance==entityInstance) continue;
	
				// iterating through fragments...
				for (EntityFragment targetFragment:targetEntityInstance.fragments.fragments) {
					// calculate the common area sizes.
					intersectTwoUnits(fragment, targetFragment, listOfCommonRadiusFragments, loc, joinLimit);
					
					// only calculate intersections if the targetfragment is not an automatic member including fragment...
					if (!targetFragment.alwaysIncludeFollowingMembers)
					{
						for (PersistentMemberInstance pmiTarget:targetFragment.getFollowingMembers())
						{
							intersectTwoUnits(fragment, pmiTarget, listOfCommonRadiusFragments, loc, joinLimit);
						}
					} else
					{
						// ... in this case the EncounterInfo.getEncounterUnitDataList will add all PersistenMemberInstance 
						// to the list, so no intersection is needed.
					}
					
					// intersecting source fragment's following members with targetFragment, plus with target fragment's following members
					for (PersistentMemberInstance pmi:fragment.getFollowingMembers())
					{
						intersectTwoUnits(pmi, targetFragment, listOfCommonRadiusFragments, loc, joinLimit);
						for (PersistentMemberInstance pmiTarget:targetFragment.getFollowingMembers())
						{
							intersectTwoUnits(pmi, pmiTarget, listOfCommonRadiusFragments, loc, joinLimit);
						}
					}
				}
				
			}
		
			// creating encounterInfos based on the collected intersection data...
			
			ArrayList<EncounterUnit> usedUp = new ArrayList<EncounterUnit>();
			for (EncounterUnit f:listOfCommonRadiusFragments.keySet())
			{
				if (usedUp.contains(f)) continue;
				usedUp.add(f);
				int[][] r = listOfCommonRadiusFragments.get(f);
				EncounterInfo pre = null;
				if (staticEncounterInfoInstances.size()==counter)
				{
					pre = new EncounterInfo(fragment,null);
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
				// add the base unit into the encounter info...				
				calcGroupsOfEncounter(fragment, f, r[1][0], r[1][1], r[1][2], r[0][1], pre, false);
				// fill how many of the interceptor entity group intercepts the target
				calcGroupsOfEncounter(f, fragment, r[1][0], r[1][1], r[1][2], r[0][0], pre, true);

				Vector3f v1 = new Vector3f(r[1][0],r[1][1],r[1][2]); // this is the vector of intersection point to measure to the others distance.
				
				// looking for nearby intersection points in the treelocator...
				ArrayList<Object> elements1 = loc.getElements(r[1][0]+joinLimit, r[1][1], r[1][2]);
				ArrayList<Object> elements2 = loc.getElements(r[1][0]+joinLimit, r[1][1], r[1][2]+joinLimit);
				ArrayList<Object> elements3 = loc.getElements(r[1][0], r[1][1], r[1][2]+joinLimit);
				ArrayList<Object> elements4 = loc.getElements(r[1][0]+joinLimit, r[1][1], r[1][2]-joinLimit);
				ArrayList<Object> elements5 = loc.getElements(r[1][0], r[1][1], r[1][2]-joinLimit);
				ArrayList<Object> elements6 = loc.getElements(r[1][0]-joinLimit, r[1][1], r[1][2]-joinLimit);
				ArrayList<Object> elements7 = loc.getElements(r[1][0]-joinLimit, r[1][1], r[1][2]+joinLimit);
				ArrayList<Object> elements8 = loc.getElements(r[1][0]-joinLimit, r[1][1], r[1][2]);
				ArrayList<Object> elements9 = loc.getElements(r[1][0], r[1][1], r[1][2]);
				// joining the found intersection units into one list.
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
				
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finer(entityInstance.description.getClass().getSimpleName() + "#"+entityInstance.getNumericId()+" ________________________ "+f.getName());
				if (elements!=null) // going through the nearby intersection point units...
				for (Object o:elements)
				{
					EncounterUnit fT = ((EncounterUnit)o);
					if (fT==f || usedUp.contains(fT)) continue;
					int[][] r2 = listOfCommonRadiusFragments.get(fT);
					Vector3f v2 = new Vector3f(r2[1][0],r2[1][1],r2[1][2]); // the comparator vector for the intersection point
					
					// for the player merge all, for others intersection points must be inside the limit (joinLimit) (checking with the intersecion point
					// vectors)
					if (fragment == J3DCore.getInstance().gameState.player.theFragment || v2.distance(v1)<joinLimit) 
					{
						// joining the Encounter unit into the EncounterInfo
						
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer(" __ "+r[1][0]+" "+r[1][2]);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer(" __ "+r2[1][0]+" "+r2[1][2]);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer( " ___ "+ f.roamingBoundary.posX +" "+f.roamingBoundary.posZ);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer( " ___ "+ fT.roamingBoundary.posX +" "+fT.roamingBoundary.posZ);
						if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("DIFF 10 > "+v2.distance(v1) + fT.getName());
						usedUp.add(fT);
						pre.encountered.put(fT, r2);
						calcGroupsOfEncounter(fragment, fT, r2[1][0], r2[1][1], r2[1][2], r2[0][1], pre, false);
						// fill how many of the interceptor entity group intercepts the target
						calcGroupsOfEncounter(fT, fragment, r2[1][0], r2[1][1], r2[1][2], r2[0][0], pre, true);
					} else
					{
						// not joining this, skipping.
						
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer(" __ "+r[1][0]+" "+r[1][2]);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer(" __ "+r2[1][0]+" "+r2[1][2]);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer( " ___ "+ f.roamingBoundary.posX +" "+f.roamingBoundary.posZ);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer( " ___ "+ fT.roamingBoundary.posX +" "+fT.roamingBoundary.posZ);
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("!! DIFF 10 < "+v2.distance(v1) + fT.getName());
					}
				}
				//for (EncounterUnit fr:pre.encountered.keySet()) 
				{
					//if (entityInstance == J3DCore.getInstance().gameState.player || fr==J3DCore.getInstance().gameState.player.theFragment)
						//if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ENCOUNTER = "+entityInstance.description.getClass() + pre.encountered.size()+" "+fr.getDescription().getClass()+" "+pre.encounteredGroupIds.get(fr).length
							//	+ " " + fragment.roamingBoundary.posX+ " / "+fragment.roamingBoundary.posZ
							//	+ " " +fr.getEncounterBoundary().posX+ " / "+fr.getEncounterBoundary().posZ + " "+r[1][0] +" / "+r[1][2]
						//);
				}
				counter++;
				pre.active = true;
			}

		}

		// the remaining static encounter instances are set inactive...
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
		if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ALL ENTITIES IN ECOLOGY IN TURN NO. "+engine.numberOfTurn+" = "+beings.size());

		long time = System.currentTimeMillis();
		
		int seed = engine.getNumberOfTurn();
		
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
				if (i.mergedOrDestroyed)
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
			if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("CONTINUING PLAYER INTERRUPTED ECOLOGY TURN...");
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
			if (orderedBeingList.get(r).liveOneTurn(seed, getNearbyEncounters(orderedBeingList.get(r))))
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
				if (J3DCore.LOGGING) Jcrpg.LOGGER.finer("ECOLOGY INTERRUPTED BY: "+orderedBeingList.get(r).description);
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
		if (J3DCore.LOGGING) Jcrpg.LOGGER.info("TURN TIME "+ (time - System.currentTimeMillis())/1000f);
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
	
	public void gameLost()
	{
		J3DCore.getInstance().uiBase.hud.sr.setVisibility(false, "DICE");
		J3DCore.getInstance().updateDisplay(null);		
	}
	
}
