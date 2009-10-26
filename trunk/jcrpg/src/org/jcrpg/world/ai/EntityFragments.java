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

import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.game.logic.PerceptionEvaluator;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.fauna.PerceptedVisibleForm;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.Geography;
import org.jcrpg.world.place.SurfaceHeightAndType;
import org.jcrpg.world.place.economic.Population;

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
		
		public ArrayList<PerceptedEntityData> perceptedEntities;
		
		public boolean settledAtHome = false;
		
		/**
		 * The population which the fragment entered. If null, it's not in a population currently.
		 * If a population is updated and fragment is fallen of, next round this should be set 0.? TODO
		 */
		public Population enteredPopulation = null;
		
		/**
		 * currently tread geography.
		 */
		public Geography nearGeography = null;
		
		/**
		 * All additional stateful data of the fragment.
		 * @author illes
		 *
		 */
		public class EntityFragmentState 
		{
			public boolean isCamping = false;
			public boolean isLighting = false;
		}
		
		/**
		 * The state description of the fragment.
		 */
		public EntityFragmentState fragmentState = new EntityFragmentState();
		
		/**
		 * Replenishing in a round - following non-dead members are resting now, state effects updated.
		 * @param seed the random seed used for saving a bad state effect.
		 */
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
		 * move the fragment, and followers to a coordinate.
		 * @param worldX
		 * @param worldY
		 * @param worldZ
		 */
		public void roamTo(int worldX, int worldY, int worldZ)
		{
			this.roamingBoundary.setPosition(1, worldX, worldY, worldZ);
			if (alwaysIncludeFollowingMembers)
			{
				for (PersistentMemberInstance i:followingMembers)
				{
					i.roamTo(worldX,worldY,worldZ);
				}
			}
		}
		
		/**
		 * The fragment enters its owned population and settles down now,
		 * following members (persistent NPCs) return to their owned infrastructures.
		 * @param population
		 */
		public void populatePopulation(Population population)
		{
			if (instance.homeEconomy==population)
			{
				settledAtHome = true;
				ArrayList<int[][]> settleList = population.getPossibleSettlePlaces();
				if (settleList==null || settleList.size()==0) return;
				enteredPopulation = population;
				
				roamTo(settleList.get(0)[0][0],settleList.get(0)[0][1],settleList.get(0)[0][2]); 
				instance.recalcBoundarySizes();
				for (PersistentMemberInstance pMI:followingMembers)
				{
					ArrayList<Economic> list = pMI.getGeneratedOwnInfrastructures();
					if (list!=null && list.size()>0)
					{
						for (Economic e:list)
						{
							ArrayList<int[][]> pP = e.getPossibleSettlePlaces();
							if (pP==null || pP.size()==0) continue;
							pMI.roamTo(pP.get(0)[0][0], pP.get(0)[0][1], pP.get(0)[0][2]);
							//System.out.println("pMI SETTLE "+pMI.getName()+" "+e.getClass().getSimpleName()+" "+ pP.get(0)[0]+","+ pP.get(0)[1] +","+ pP.get(0)[2] );
						}
					}
				}
			}
		}
		
		/**
		 * Settled down fragment leaves town in a given direction specified by coordinates.
		 * @param worldX
		 * @param worldY
		 * @param worldZ
		 */
		public void leavePopulation(int worldX, int worldY, int worldZ)
		{
			settledAtHome = false;
			enteredPopulation = null;
			roamTo(worldX, worldY, worldZ);
			instance.recalcBoundarySizes();
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
		
		/**
		 * where the fragment is located at - if in population, pop's geo.
		 * @return
		 */
		public Geography getNearGeo()
		{
			if (enteredPopulation!=null) return enteredPopulation.soilGeo;
			return nearGeography;
		}

		public ArrayList<PerceptedVisibleForm> getPerceptedForms(PerceptedEntityData perceptedData) {
			ArrayList<PerceptedVisibleForm> forms = new ArrayList<PerceptedVisibleForm>();
			
			int id = 0;
			if (perceptedData.groupIds!=null)
			{
				id = perceptedData.groupIds[0];
			}
			
			PerceptedVisibleForm form2 = instance.getPerceptedOne(this,id);
			for (int i=0; i<form2.getSize();i++)
			{
				PerceptedVisibleForm form = instance.getPerceptedOne(this,id);
				form.enteredPopulation = enteredPopulation;
				form.nearGeography = getNearGeo();
				form.uniqueId=i;
				form.worldX = (int)getRoamingPosition().x;
				form.worldY = (int)getRoamingPosition().y;
				form.worldZ = (int)getRoamingPosition().z;
				
				boolean positioned = false;
				if (enteredPopulation!=null)
				{
					ArrayList<int[][]> list = enteredPopulation.getPossibleSettlePlaces();
					if (list!=null && list.size()>0)
					{
						form.worldX = list.get(0)[0][0];
						form.worldY = list.get(0)[0][1];
						form.worldZ = list.get(0)[0][2];
					}
					form.setPossiblePlaces(list);
				}
				if (!positioned)
				{
					ArrayList<SurfaceHeightAndType[]> surface = instance.world.getSurfaceData(form.worldX, form.worldZ);
					for (SurfaceHeightAndType[] type: surface)
					{
						for (SurfaceHeightAndType s:type)
						{
							if (enteredPopulation!=null)
							{
								if (s.self == enteredPopulation.soilGeo)
								{
									form.worldY = s.surfaceY;
									positioned = true;
									break;
								}
							} else
							{
								form.worldY = s.surfaceY;
								positioned = true;
								break;
							}
						}
					}
				}
				if (positioned)
				{
					forms.add(form);
					
				}
			}
			return forms;
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
		
		public void notifyImpactResult(EntityMemberInstance member, ArrayList<Integer> result, ImpactUnit unit)
		{
			parent.instance.notifyImpactResult( this, member,  result, unit);
		}
		public void notifyEffectChange(EntityMemberInstance member,ArrayList<StateEffect> added, ArrayList<StateEffect> removed)
		{
			parent.instance.notifyEffectChange(member,added,removed);
		}
		public void notifyUnpercepted()
		{
			parent.instance.notifyUnpercepted();
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
		public EntityFragment getFragment() {
			return this;
		}
		
		public void clearPerceptedData()
		{
			if (perceptedEntities!=null)
			{
				perceptedEntities.clear();
			} else
			{
				perceptedEntities = new ArrayList<PerceptedEntityData>();
				
			}
		}
		
		/**
		 * 
		 * @param seed
		 * @param target
		 * @return True if target is percepted.
		 */
		public boolean fillPerceptedEntityData(int seed, EncounterUnit target, int[] groupIds)
		{
			if (perceptedEntities==null)
			{
				perceptedEntities = new ArrayList<PerceptedEntityData>();
			}
			PerceptedEntityData rData = new PerceptedEntityData();
			for (PersistentMemberInstance i:followingMembers)
			{
				PerceptedEntityData data = i.percept(seed, target);
				rData.mergeBest(data);
			}
			PerceptedEntityData data = percept(seed, 0, target);
			rData.mergeBest(data);
			rData.unit = target;
			rData.groupIds = groupIds;
			rData.source = this;
			perceptedEntities.add(rData);
			if (rData.percepted==true) return true;
			return false;
		}
		
		public Vector3f getRoamingPosition()
		{
			return new Vector3f(roamingBoundary.posX,roamingBoundary.posY,roamingBoundary.posZ);
		}

		
		public PerceptedEntityData percept(int seed, int karma, EncounterUnit target)
		{
			int likeness = PerceptionEvaluator.likenessLevelOfPerception(this, target);
			if (target == J3DCore.getInstance().gameState.player.theFragment)
			{
				System.out.println("PECEPTING PARTY: "+likeness);
			}
			float result = PerceptionEvaluator.success(seed, karma, likeness);
			int likenessIdent = PerceptionEvaluator.likenessLevelOfIdentification(this, target);
			float resultIdent = PerceptionEvaluator.success(seed,karma, likenessIdent);
			PerceptedEntityData data = new PerceptedEntityData();
			data.updateToResultRatio(result,resultIdent,getRoamingPosition(),target);
			return data;
		}
	
		/**
		 * 
		 * @return max level of (if actively with 3x bonus) chosen behavior skill level in this fragment.
		 */
		public int getActiveBehaviorSkillLevel(Class <? extends SkillBase> skill)
		{
			int maxLevel = 0;
			int counter = 0;
			for (PersistentMemberInstance i:followingMembers)
			{
				boolean active = i.getFragment().fragmentState.isCamping!=false && i.behaviorSkill!=null && i.behaviorSkill.getClass().equals(skill);
				//if (i.description instanceof MemberPerson) System.out.println("ACTIVE "+active+" "+i.behaviorSkill +" ? "+skill);
				int level = i.getSkillLevel(skill)/ (active?1:3);
				maxLevel += level;
				counter++;
			}
			int level = instance.getActiveBehaviorSkillLevel(skill);
			maxLevel += level;
			return maxLevel/(counter+1);
		}

		
		/**
		 * 
		 * @return max level of helper skill for a given kind of activity (tag).
		 */
		public int getHelperSkillLevel(String tag)
		{
			int maxLevel = 0;
			int counter = 0;
			for (PersistentMemberInstance i:followingMembers)
			{
				int level = i.getSkills().getHighestLevelHelperSkill(null, tag).level;
				maxLevel += level;
				counter++;
			}
			return maxLevel/(counter);
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
			if (f.settledAtHome)
			{
				f.roamingBoundary.radiusInRealCubes=instance.world.magnification/2;
			} else
			{
				//f.roamingBoundary.radiusInRealCubes = f.size;//instance.description.getRoamingSize(f);
				int sum = f.size;
				for (EntityMemberInstance m:f.followingMembers)
				{
					if (!m.isDead())
					{
						sum+=m.description.getRoamingSize();
					}
				}
				sum = Math.max(1,(int)Math.sqrt(sum));
				f.roamingBoundary.radiusInRealCubes=sum*3;
			}
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
