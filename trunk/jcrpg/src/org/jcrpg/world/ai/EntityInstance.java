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
import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.map.BlockPattern;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.GroupingRule.GroupSizeAndType;
import org.jcrpg.world.ai.abs.Choice;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.choice.Attack;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.state.EntityState;
import org.jcrpg.world.ai.abs.state.StateEffect;
import org.jcrpg.world.ai.body.MammalBody;
import org.jcrpg.world.ai.body.SinglePartBody;
import org.jcrpg.world.ai.fauna.PerceptVisibleForm;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.ai.fauna.mammals.gorilla.GorillaHorde;
import org.jcrpg.world.ai.humanoid.HumanoidEntityDescription;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.ai.wealth.EntityCommonWealth;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.InfrastructureElementParameters;
import org.jcrpg.world.place.economic.Population;
import org.jcrpg.world.place.economic.residence.House;
import org.jcrpg.world.place.economic.residence.WoodenHouse;

public class EntityInstance {
	
	public boolean mergedOrDestroyed = false;
	
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
	
	public EntityCommonWealth wealth = new EntityCommonWealth(this);

	/**
	 * Skills of the instance.
	 */
	public SkillContainer skills = new SkillContainer();
	public Attributes attributes = new FantasyAttributes(false);	
	public Resistances resistances = new FantasyResistances(false);
	
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
		

	public static EntityMember NONE_TYPE = new EntityMember("NONE", SinglePartBody.class, null);

	private transient GroupSizeAndType[] groupSizesAndTypes = null;
	private transient int[] groupSizes = null;

	/**
	 * Unique id in the worlds.
	 */
	public String id;
	/**
	 * Unique numeric id.
	 */
	private int numericId;
	public int numberOfMembers = 1;
	public World world;
	public Ecology ecology;
	
	public void clear()
	{
		world = null;
		ecology = null;
	}
	
	/**
	 * The independently roaming fragments object of this instance.
	 */
	public EntityFragments fragments = new EntityFragments(this);

	public HashMap<String, PersistentMemberInstance> fixMembers = new HashMap<String, PersistentMemberInstance>();
	
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
		
		if (!(this instanceof PartyInstance) && description!=null && description instanceof HumanoidEntityDescription)
		{
			PersistentMemberInstance p = new PersistentMemberInstance(fragments.fragments.get(0),this, new org.jcrpg.world.ai.fauna.modifier.StrongAnimalMale("GORILLA_MALE",MammalBody.class,GorillaHorde.gorillaAudio),w,Ecology.getNextEntityId(),startX,startY,startZ);
			InfrastructureElementParameters i = new InfrastructureElementParameters();
			i.owner = p;
			
			try { i.type = description.economyTemplate.residenceTypes.values().iterator().next().get(0);
				
			} catch (Exception ex){
			i.type = WoodenHouse.class;}
			if (i.type == WoodenHouse.class || i.type == House.class)
			{
				i.sizeY = 3;
			} else
			{
				i.sizeY = 1;
			}
			
			p.addOwnedInfrastructurePlan(i);
			fixMembers.put("1", p);
			fragments.fragments.get(0).addFollower(p);
		}
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

	public GroupSizeAndType[] calcGroupSizes()
	{
		if (groupSizesAndTypes==null) 
		{
			groupSizesAndTypes = description.groupingRule.getGroupSizesAndTypes(this);
			groupSizes = new int[groupSizesAndTypes.length];
			int count = 0;
			for (GroupSizeAndType t:groupSizesAndTypes)
			{
				groupSizes[count++] = t.size; 
			}
		}
			
		return groupSizesAndTypes;
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

	
	public transient ArrayList<EntityFragment> tmpCamperList = new ArrayList<EntityFragment>();

	/**
	 * The units that remain standing for resting in an ecology round.
	 * @return
	 */
	public ArrayList<EntityFragment> decideCampingAndGetCamperFragments()
	{
		if (tmpCamperList==null) 
		{
			tmpCamperList = new ArrayList<EntityFragment>();
		}
		// TODO 
		return tmpCamperList;
	}
	
	
	/**
	 * Does replenishing, deciding who will do it, execute replenish, and return campers.
	 * @param seed
	 * @return
	 */
	public ArrayList<EntityFragment> doReplenishAndGetCampers(int seed)
	{
		ArrayList<EntityFragment> camperFragments = decideCampingAndGetCamperFragments();
		
		for (EntityFragment f:camperFragments)
		{
			f.replenishInOneRound(seed);
		}
		return camperFragments;		
	}
	
	ArrayList<EncounterInfo> infos = new ArrayList<EncounterInfo>();
	/**
	 * Living a turn for this being.
	 * @param ecology
	 * @param nearbyEncounters
	 * @return should return true if player interaction is needed, and ecology doTurn should be interrupted.
	 */
	public boolean liveOneTurn(int seed, Collection<EncounterInfo> nearbyEncounters)
	{
		int counter = 0;
		// For debugging without combats
		if (J3DCore.SETTINGS.WITHOUT_COMBATS) {
			return false;
		}
				
		ArrayList<EntityFragment> camperFragments = doReplenishAndGetCampers(seed);
		
		//	if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest(" - "+roamingBoundary.posX+" "+roamingBoundary.posZ+" : "+roamingBoundary.radiusInRealCubes);
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest()
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest(" LIVE "+this.description.getClass().getSimpleName() + " "+ nearbyEncounters.size());
		if (nearbyEncounters!=null && nearbyEncounters.size()>0) {
			for (EncounterInfo info : nearbyEncounters)
			{
				if (info.subject==null) continue;
				counter++;
				
				HashMap<Class<?extends Choice>, ArrayList<EncounterUnit>> choiceMap = description.getBehaviorsAndFragments(info);
				HashMap<Integer, ArrayList<EncounterUnit>> levelMap = description.getRelationLevelsAndFragments(this,info);
				if (choiceMap.get(Attack.class)!=null && choiceMap.get(Attack.class).contains(J3DCore.getInstance().gameState.player.theFragment))
				{
					int level = description.getFullscaleEncounterRelationBalance(levelMap, info);
					if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("RELATION SUM LEVEL FOR FULLSCALE = "+level);
					if (level>0)
					{
						EncounterInfo i = info.copy();						
						J3DCore.getInstance().gameState.gameLogic.newEncounterPhase(i,Ecology.PHASE_TURNACT_COMBAT,false);
						ecology.callbackMessage(this.description.getClass().getSimpleName()+" initiates a full scale encounter!");
					} else
					{
						EncounterInfo i = info.copyForFragment(J3DCore.getInstance().gameState.player.theFragment);						
						J3DCore.getInstance().gameState.gameLogic.newEncounterPhase(i,Ecology.PHASE_TURNACT_COMBAT,false);
						ecology.callbackMessage(this.description.getClass().getSimpleName()+" initiates a single group encounter!");
					}
					return true;
				}
				// TODO other mechanisms to happen, without UI
				
			}
		}
		//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("LIVE ONE TURN "+this.getClass()+" "+id + " | Nearby: "+counter);
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
	
	


	public VisibleLifeForm getOne(int groupId)
	{
		return new VisibleLifeForm(this.getClass().getName()+nextVisibleSequence(),getGroupSizesAndTypes()[groupId].type,this,groupId);
	}

	public PerceptVisibleForm getPerceptedOne(EntityFragment fragment,int groupId)
	{
		return new PerceptVisibleForm(this.getClass().getName()+nextVisibleSequence(),calcGroupSizes()[groupId].type,this,fragment,groupId);
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
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finer("EntityInstance.merge # MERGING: "+instance);
		numberOfMembers+=instance.numberOfMembers;
		calculateGroupsAndPositions();
		fragments.merge(instance.fragments);
		instance.mergedOrDestroyed = true;
	}

	public int getNumericId() {
		return numericId;
	}

	public GroupSizeAndType[] getGroupSizesAndTypes() {
		return groupSizesAndTypes;
	}

	/**
	 * If an impact was applied for a member of this instance, this should be called for post processing the event.
	 * @param fragment The fragment.
	 * @param member The member.
	 * @param result The results (what points decreased to 0) .
	 */
	public void notifyImpactResult(EntityFragment fragment, EntityMemberInstance member, ArrayList<Integer> result, ImpactUnit unit)
	{
		return;
		
	}
	
	/**
	 * Call this when addition or removal of state effect.
	 * @param added
	 * @param removed
	 */
	public void notifyEffectChange(EntityMemberInstance member,ArrayList<StateEffect> added, ArrayList<StateEffect> removed)
	{
		return;
	}
	
	/**
	 * Call this when unit is unpercepted.
	 */
	public void notifyUnpercepted()
	{
		return;
	}
	
	/**
	 * This should be called by a sub fragment after camping replenish for a round was done.
	 */
	public void callbackAfterCampReplenish()
	{
		return;
	}
	
	public byte[] getPopulationMapColor()
	{
		return description.getPopulationMapColor();
	}
	
	public BlockPattern getPopulationMapPattern(Population p)
	{
		return description.getPopulationPattern(p);
	}
	
	public int getActiveBehaviorSkillLevel(Class<? extends SkillBase> skill)
	{
		return skills.getSkillLevel(skill, null)/3;
	}

}
