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

import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;

import com.jme.math.Vector3f;

/**
 * Class registering roaming fragments of an EntityInstance
 * @author illes
 *
 */
public class EntityFragments {

	
	public EntityInstance instance;
	
	public class MemberAndSkill
	{
		public EntityMemberInstance member;
		public Class<?extends SkillBase> skill;
		public MemberAndSkill(EntityMemberInstance member,
				Class<? extends SkillBase> skill) {
			super();
			this.member = member;
			this.skill = skill;
		}
		
	}
	
	
	public class EntityFragment implements EncounterUnit 
	{
		public EntityFragments parent;
		public EntityInstance instance;
		
		public class EntityFragmentState 
		{
			public boolean isCamping = false;
		}
		
		public EntityFragmentState fragmentState = new EntityFragmentState();
		
		public void replenishInOneRound(int seed)
		{
			for (EntityMemberInstance i:followingMembers)
			{
				if (!i.isDead())
				{
					i.memberState.updateEffects(seed, J3DCore.getInstance().gameState.engine.getWorldMeanTime().getTimeInRound(), J3DCore.getInstance().gameState.engine.getWorldMeanTime());
					i.memberState.replenishInOneRound();
				}
			}
			instance.callbackAfterCampReplenish();
		}
		
		/**
		 * Used for parties, if this is set to true the EncounterInfo will alwass include the followingMembers in its encounterUnitData lists.
		 */
		public boolean alwaysIncludeFollowingMembers = false;
		
		/**
		 * those NPCs (or PCs) that join the fragment in its roaming.
		 */
		private ArrayList<PersistentMemberInstance> followingMembers = new ArrayList<PersistentMemberInstance>();
		public int size;
		public DistanceBasedBoundary roamingBoundary;
		
		public void addFollower(PersistentMemberInstance i)
		{
			followingMembers.add(i);
			parent.recalcBoundaries();
			i.setParentFragment(this);
		}
		public SkillInstance getEncounterSkill(ArrayList<EncounterInfo> encountered)
		{
			return null;
		}
		public DistanceBasedBoundary getEncounterBoundary() {
			return roamingBoundary;
		}
		public int getNumericId() {
			return instance.getNumericId();
		}
		public int getLevel() {
			return instance.entityState.currentLevelOfQuality;
		}
		public DescriptionBase getDescription() {
			return instance.description;
		}
		public ArrayList<EntityMemberInstance> getGroup(int groupId) {
			return instance.description.groupingRule.getGroup(groupId, this);
		}
		public int getGroupSize(int groupId) {
			return instance.getGroupSizes()[groupId];
		}
		public VisibleLifeForm getOne(int groupId) {
			return instance.getOne(groupId);
		}
		public int getRelationLevel(EncounterUnit unit) {
			return instance.relations.getRelationLevel(unit);
		}
		public String getName()
		{
			return Language.v("entity."+instance.description.getClass().getSimpleName());
		}
		public int getSize() {
			return size;
		}
		public int[] getGroupIds(int posX, int posY, int posZ, int radiusRatio, int randomSeed) {
			return instance.description.groupingRule.getGroupIds(this, radiusRatio, randomSeed);
		}
		public ArrayList<EncounterUnit> getSubUnits(int posX, int posY, int posZ) {
			ArrayList<EncounterUnit> list = null;
			for (PersistentMemberInstance pi:followingMembers) {
				if (tmpVector==null) tmpVector = new Vector3f();
				tmpVector.set(posX,posY,posZ);
				if (pi.roamingBoundary.pv.distance(tmpVector)<6)
				{
					if (list==null) list = new ArrayList<EncounterUnit>();
					list.add(pi);
				}
			}
			return list;
		}
		public ArrayList<PersistentMemberInstance> getFollowingMembers() {
			return followingMembers;
		}
		public EntityMember getGroupType(int groupId) {
			return instance.getGroupSizesAndTypes()[groupId].type;
		}
		public int getEncPhasePriority(EncounterInfo info) {
			return parent.instance.entityState.currentLevelOfQuality;
		}
		
		public void notifyImpactResult(EntityMemberInstance member, ArrayList<Integer> result)
		{
			parent.instance.notifyImpactResult( this, member,  result);
		}
		public void notifyEffectChange(EntityMemberInstance member,ArrayList<StateEffect> added, ArrayList<StateEffect> removed)
		{
			parent.instance.notifyEffectChange(member,added,removed);
		}

		public void decreaseSize()
		{
			size--;
			if (size==0)
			{
				parent.fragmentDestroyed(this);
			}
			instance.numberOfMembers--;
			instance.recalcBoundarySizes();
		}

	}
	
	public void fragmentDestroyed(EntityFragment f)
	{
		fragments.remove(f);
		if (fragments.size()==0)
		{
			instance.mergedOrDestroyed = true;
		}
	}
	
	public transient Vector3f tmpVector = new Vector3f();
	
	public ArrayList<EntityFragment> fragments = new ArrayList<EntityFragment>();
	
	public EntityFragments(EntityInstance i)
	{
		instance = i;
	}
	
	public void setupInstance()
	{
		EntityFragment f = new EntityFragment();
		f.parent = this;
		f.instance = instance;
		f.roamingBoundary = new DistanceBasedBoundary(instance.world,instance.domainBoundary.posX,instance.domainBoundary.posY,instance.domainBoundary.posZ, 0);
		f.size = instance.numberOfMembers;
		f.roamingBoundary.radiusInRealCubes = instance.description.getRoamingSize(f);
		fragments.add(f);
	}


	public void recalcBoundaries()
	{
		for (EntityFragment f:fragments) {
			//f.roamingBoundary.radiusInRealCubes = f.size;//instance.description.getRoamingSize(f);
			int sum = f.size; 
			for (EntityMemberInstance m:f.followingMembers)
			{
				sum+=m.description.getRoamingSize();
			}
			sum = Math.max(1,(int)Math.sqrt(sum));
			f.roamingBoundary.radiusInRealCubes=sum*3;
		}
	}
	
	public EntityFragment createFragment(EntityFragment toSplitFragment, int size)
	{
		toSplitFragment.size -=size;
		toSplitFragment.roamingBoundary.radiusInRealCubes = instance.description.getRoamingSize(toSplitFragment);
		EntityFragment f = new EntityFragment();
		f.parent = this;
		f.instance = instance;
		f.roamingBoundary = new DistanceBasedBoundary(toSplitFragment.roamingBoundary);
		f.size = size;
		f.roamingBoundary.radiusInRealCubes = instance.description.getRoamingSize(f);
		fragments.add(f);
		return f;
	}
	
	public void merge(EntityFragments merged)
	{
		for (EntityFragment f:merged.fragments)
		{
			f.parent = this;
			f.instance = this.instance;
		}
	}
	
	
	
}
