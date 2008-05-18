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

/**
 * Class registering roaming fragments of an EntityInstance
 * @author illes
 *
 */
public class EntityFragments {

	
	public EntityInstance instance;
	
	public class EntityFragment
	{
		public EntityFragments parent;
		public EntityInstance instance;
		/**
		 * those NPCs (or PCs) that join the fragment in its roaming.
		 */
		public ArrayList<EntityMemberInstance> followingMembers = new ArrayList<EntityMemberInstance>();
		public int size;
		public DistanceBasedBoundary roamingBoundary;
		
		public void addFollower(EntityMemberInstance i)
		{
			followingMembers.add(i);
			parent.recalcBoundaries();
		}
	}
	
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
			f.roamingBoundary.radiusInRealCubes = instance.description.getRoamingSize(f);
			for (EntityMemberInstance m:f.followingMembers)
			{
				f.roamingBoundary.radiusInRealCubes+=m.description.getRoamingSize();
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
