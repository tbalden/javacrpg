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

import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.fauna.VisibleLifeForm;
import org.jcrpg.world.place.Economic;
import org.jcrpg.world.place.World;
import org.jcrpg.world.place.economic.InfrastructureElementParameters;

/**
 * The members of an EntityInstance that are persistent (stored) -> Something like NPCs.
 * They have strong relation to their EntityInstance, and bigger influence on the parent entityInstance's
 * life. Like a kind of leader or wisest wise and so on.
 * @author pali
 *
 */
public class PersistentMemberInstance extends EntityMemberInstance implements EncounterUnit {


	/**
	 * Fixed members have their own roaming boundary.
	 */
	public DistanceBasedBoundary roamingBoundary = null;
	

	/**
	 * Those infrastructures (buildings) owned by this member in its home population.
	 * Not with coordinates, only size and type and owner are set, the build program will put it in an appropriate place.
	 */
	private ArrayList<InfrastructureElementParameters> ownedInfrastructures = null;
	
	/**
	 * The list of the infrastructures that was actually generated by the build program of a population in which the member
	 * lives. (it is transient, as population hierarchy (and so this) is not a persisted data!)
	 */
	protected transient ArrayList<Economic> generatedOwnInfrastructures = null;
	
	/**
	 * The existing relations' class of this member instance.
	 */
	public EntityMemberRelations personalRelations = new EntityMemberRelations();
	
	public PersistentMemberInstance(EntityFragment parent, EntityInstance instance, EntityMember description, World w,
			int numericId, int startX, int startY, int startZ) {
		super(parent, instance, description, numericId);
		if (w!=null) {
			roamingBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,5);//TODO description.getRoamingSize());
		}
	}
	
	public void addOwnedInfrastructurePlan(InfrastructureElementParameters i)
	{
		if (ownedInfrastructures==null)
		{
			ownedInfrastructures = new ArrayList<InfrastructureElementParameters>();
		}
		ownedInfrastructures.add(i);
	}
	public ArrayList<InfrastructureElementParameters> getOwnedInfrastructures() {
		return ownedInfrastructures;
	}
	
	public void addGeneratedOwnInfrastructure(Economic i)
	{
		if (generatedOwnInfrastructures==null)
		{
			generatedOwnInfrastructures = new ArrayList<Economic>();
		}
		generatedOwnInfrastructures.add(i);
	}
	public ArrayList<Economic> getGeneratedOwnInfrastructures() {
		return generatedOwnInfrastructures;
	}
	

	public DistanceBasedBoundary getEncounterBoundary() {
		return roamingBoundary;
	}

	public int getNumericId() {
		return numericId;
	}

	public long getLevel() {
		return memberState.level;
	}

	public DescriptionBase getDescription() {
		return description;
	}

	public transient ArrayList<EntityMemberInstance> tmpList = null;
	public ArrayList<EntityMemberInstance> getGroup(int groupId) {
		if (tmpList==null)
		{
			 tmpList = new ArrayList<EntityMemberInstance>();
			 tmpList.add(this);
		}
		return tmpList;
	}

	public int getGroupSize(int groupId) {
		return 1;
	}

	public VisibleLifeForm getOne(int groupId) {
		return new VisibleLifeForm(this.getClass().getName()+""+this,description,getParentFragment().instance,this);
	}

	public int getRelationLevel(EncounterUnit unit) {
		return getParentFragment()==null?0:getParentFragment().instance.relations.getRelationLevel(unit);
	}

	public String getName() {
		return Language.v("member."+description.getName());
	}

	public int getSize() {
		return 1;
	}
	
	static final int[] groupId = new int[]{0};
	public int[] getGroupIds(int posX, int posY, int posZ, int radiusRatio, int randomSeed) {
		return groupId;
	}

	public transient ArrayList<EncounterUnit> tmpList2 = null;
	public ArrayList<EncounterUnit> getSubUnits(int posX, int posY, int posZ) {
		if (tmpList2==null)
		{
			 tmpList2 = new ArrayList<EncounterUnit>();
			 tmpList2.add(this);
		}
		return tmpList2;
	}


	public EntityMember getGroupType(int groupId) {
		return description;
	}

	public int getEncPhasePriority(EncounterInfo info) {
		return memberState.level+2;
	}

	@Override
	public ArrayList<Integer> applyImpactUnit(ImpactUnit unit) {
		ArrayList<Integer> result = memberState.applyImpactUnit(unit);
		parentFragment.notifyImpactResult(this,result);
		return result;
		
	}

}
