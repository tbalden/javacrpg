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
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.Choice;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.choice.Attack;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.state.EntityState;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.World;

public class EntityInstance {
	
	public boolean merged = false;
	
	/**
	 * The "genetic" heritage of the instance.
	 */
	public EntityDescription description;
	
	/**
	 * Relations of this entityI with outher entityInstances.
	 */
	public EntityRelations relations = new EntityRelations();
	
	/**
	 * The "experience" object of the EntityInstance.
	 */
	public EntityState entityState = new EntityState();

	/**
	 * Skills of the instance.
	 */
	public SkillContainer skills = new SkillContainer();
	public Attributes attributes = new Attributes();
	
	/**
	 * currently dominated zone.
	 */
	public DistanceBasedBoundary domainBoundary = null;
	/**
	 * where the instance has its home dwelling.
	 */
	public DistanceBasedBoundary homeBoundary = null;
	/**
	 * Entity instance's home economic (if any). (A humanoid group e.g. if it doesn't have one, will go look
	 * around for a suitable place for home.)
	 */
	public Economic homeEconomy = null;
		

	public static EntityMember NONE_TYPE = new EntityMember("NONE", null);

	private transient int[] groupSizes = null;

	/**
	 * Unique id in the worlds.
	 */
	public String id;
	/**
	 * Unique numeric id.
	 */
	public int numericId;
	public int numberOfMembers = 1;
	public World world;
	public Ecology ecology;
	
	/**
	 * The independently roaming fragments object of this instance.
	 */
	public EntityFragments fragments = new EntityFragments(this);

	public HashMap<String, EntityMemberInstance> fixMembers = new HashMap<String, EntityMemberInstance>();
	
	public EntityInstance(EntityDescription description, World w, Ecology ecology, int numericId, String id, int numberOfMembers, int startX, int startY, int startZ) {
		super();
		this.id = id;
		this.numericId = numericId;
		this.numberOfMembers = numberOfMembers;
		this.world = w;
		this.ecology = ecology;
		this.description = description;
		//roamingBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,description.getRoamingSize(this));
		
		domainBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,description.getDomainSize(this));
		fragments.setupInstance();
		
		skills.addSkills(description.getStartingSkills());
		calculateGroupsAndPositions();
	}
	
	public void recalcBoundarySizes()
	{
		fragments.recalcBoundaries();
		domainBoundary.setRadiusInRealCubes(description.getDomainSize(this));
	}
	
	public void calculateGroupsAndPositions()
	{
		calcGroupSizes();
	}

	public int[] calcGroupSizes()
	{
		if (groupSizes==null)
			groupSizes = description.groupingRule.getGroupSizes(this);
		return groupSizes;
	}
	public int[][] calcGroupPositions()
	{
		//description.calcTypes.get(description.positionCalcType).getVisiblePositionsInArea(seer, watched)
		return null;
	}
	
	public int[] getGroupSizes()
	{
		if (groupSizes==null)
		{
			calculateGroupsAndPositions();
		}
		return groupSizes;
	}
	
	ArrayList<EncounterInfo> infos = new ArrayList<EncounterInfo>();
	/**
	 * Living a turn for this being.
	 * @param ecology
	 * @param nearbyEntities
	 * @return should return true if player interaction is needed, and ecology doTurn should be interrupted.
	 */
	public boolean liveOneTurn(Collection<EncounterInfo> nearbyEntities)
	{
		int counter = 0;
		//	System.out.println(" - "+roamingBoundary.posX+" "+roamingBoundary.posZ+" : "+roamingBoundary.radiusInRealCubes);
		//System.out.println()
		System.out.println(" LIVE "+this.description.getClass().getSimpleName() + " "+ nearbyEntities.size());
		if (nearbyEntities!=null && nearbyEntities.size()>0) {
			int actions = 0;
			for (EncounterInfo info : nearbyEntities)
			{
				if (info.subject==null) continue;
				counter++;
				EntityFragment fragment = info.encountered.keySet().iterator().next();
				if (info.encountered.keySet().contains(J3DCore.getInstance().gameState.player.theFragment)) {
					fragment = J3DCore.getInstance().gameState.player.theFragment;
					Class<?extends Choice> c = description.makeTurnChoice(fragment.instance.description, fragment.instance, fragment);
					ecology.callbackMessage(this.description.getClass().getSimpleName()+": "+fragment.instance.description.getClass().getSimpleName()+" - "+c.getSimpleName());
					if (c == Attack.class)
					{
						fragment.availableInThisTurn = false;
						infos.clear();
						// ! filtering actives -> statically used PreEncounterInfo instances need a copy for thread safe use!
						int listSize = 0;
						EncounterInfo i = info; 
						{
							if (!i.active) continue;
							int fullSize = 0;
							for (EntityFragment entityFragment:i.encountered.keySet())
							{
								int[] groupIds = i.encounteredGroupIds.get(entityFragment);
								if (groupIds.length==0) {
									System.out.println("NO GROUPID IN ARRAY: "+entityFragment.instance.description+" - "+entityFragment.size);
								}
								for (int in:groupIds) {
									int size = entityFragment.instance.getGroupSizes()[in];
									if (size==0) System.out.println("SIZE ZERO: "+entityFragment.instance.description);
									fullSize+=size;
								}
							}
							if (fullSize>0) {
								infos.add(i.copy());
								listSize++;
							}
						}
						if (listSize>0) // only if groups can be encountered should we trigger newturn
						{
							J3DCore.getInstance().gameState.gameLogic.newTurnPhase(infos,Ecology.PHASE_TURNACT_COMBAT,false);
							return true; // interrupt ecology!
						}
						return false; // don't interrupt ecology
					}
				}
				else
				{
					description.makeTurnChoice(fragment.instance.description, fragment.instance, fragment);
				}
				actions++;
				//if (numberOfActionsPerTurn==actions) break;
			}
		}
		//System.out.println("LIVE ONE TURN "+this.getClass()+" "+id + " | Nearby: "+counter);
		return false;
	}

	public static int visibleSequence = 0;
	public static Object mutex = new Object();

	public int nextVisibleSequence()
	{
		synchronized (mutex) {
			visibleSequence++;
		}
		return visibleSequence;
	}
	
	public static int getVisibleSequence() {
		return visibleSequence;
	}
	public static void setVisibleSequence(int visibleSequence) {
		EntityInstance.visibleSequence = visibleSequence;
	}
	
	public static void getInstance(EntityDescription desc, World w, int numericId, String id, int size, int startX, int startY, int startZ)
	{
		new EntityInstance(desc,w,null,numericId, id,size,startX,startY,startZ);
	}
	
	


	public VisibleLifeForm getOne(EntityMember member, EntityMemberInstance instance)
	{
		return new VisibleLifeForm(this.getClass().getName()+nextVisibleSequence(),member,this,instance);
	}
	
	public void setPosition(int[] coords)
	{
		//roamingBoundary.setPosition(1, coords[0], coords[1], coords[2]);
		domainBoundary.setPosition(1, coords[0], coords[1], coords[2]);
	}
	
	public boolean wouldMergeWithOther(EntityInstance instance)
	{
		if (instance.description.equals(instance.description)) return true; // TODO interrelation check.
		return false;
	}

	public boolean isFriendly(EntityInstance instance)
	{
		if (instance.description.equals(instance.description)) return true; // TODO interrelation check.
		return false;
	}

	/**
	 * merging two entities.
	 * @param instance
	 */
	public void merge(EntityInstance instance)
	{
		System.out.println("# MERGING: "+instance);
		numberOfMembers+=instance.numberOfMembers;
		calculateGroupsAndPositions();
		fragments.merge(instance.fragments);
		instance.merged = true;
	}
	
	
}
