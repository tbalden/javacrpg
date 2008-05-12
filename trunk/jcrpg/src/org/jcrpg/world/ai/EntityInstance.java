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

import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.World;

public class EntityInstance {
	
	public EntityDescription description;

	/**
	 * Skills of the instance.
	 */
	public SkillContainer skills = new SkillContainer();
	public Attributes attributes = new Attributes();
	

	/**
	 * which the instance is roaming in this turn.
	 */
	public DistanceBasedBoundary roamingBoundary = null;
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

	public HashMap<String, EntityInstance> subEntities = new HashMap<String, EntityInstance>();
	public HashMap<String, EntityMemberInstance> fixMembers = new HashMap<String, EntityMemberInstance>();
	
	public EntityInstance(EntityDescription description, World w, Ecology ecology, int numericId, String id, int numberOfMembers, int startX, int startY, int startZ) {
		super();
		this.id = id;
		this.numericId = numericId;
		this.numberOfMembers = numberOfMembers;
		this.world = w;
		this.ecology = ecology;
		this.description = description;
		roamingBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,description.getRoamingSize(this));
		domainBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,description.getDomainSize(this));
		skills.addSkills(description.getStartingSkills());
		calculateGroupsAndPositions();
	}
	
	public void recalcBoundarySizes()
	{
		roamingBoundary.setRadiusInRealCubes(description.getRoamingSize(this));
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
	
	/**
	 * Living a turn for this being.
	 * @param ecology
	 * @param nearbyEntities
	 * @return should return true if player interaction is needed, and ecology doTurn should be interrupted.
	 */
	public boolean liveOneTurn(Collection<PreEncounterInfo> nearbyEntities)
	{
		int counter = 0;
		//	System.out.println(" - "+roamingBoundary.posX+" "+roamingBoundary.posZ+" : "+roamingBoundary.radiusInRealCubes);
		if (nearbyEntities!=null && nearbyEntities.size()>0) {
			int actions = 0;
			for (PreEncounterInfo info : nearbyEntities)
			{
				if (info.subject==null) continue;
				counter++;
				EntityInstance instance = info.encountered.keySet().iterator().next();
				if (instance.equals(J3DCore.getInstance().gameState.player))
					ecology.callbackMessage(this.description.getClass().getSimpleName()+": "+instance.description.getClass().getSimpleName()+" - "+description.makeTurnChoice(description,instance).getSimpleName());
				else
				{
					description.makeTurnChoice(instance.description, instance);
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
		roamingBoundary.setPosition(1, coords[0], coords[1], coords[2]);
		domainBoundary.setPosition(1, coords[0], coords[1], coords[2]);
	}
}
