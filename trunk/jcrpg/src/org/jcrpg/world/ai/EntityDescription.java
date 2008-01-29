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
import org.jcrpg.world.ai.abs.Behavior;
import org.jcrpg.world.ai.abs.Choice;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.behavior.Aggressive;
import org.jcrpg.world.ai.abs.behavior.Escapist;
import org.jcrpg.world.ai.abs.choice.Attack;
import org.jcrpg.world.ai.abs.choice.Hide;
import org.jcrpg.world.ai.abs.choice.Indifference;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.fauna.DistanceBasedBoundary;
import org.jcrpg.world.place.World;

/**
 * All moving beings's base class which should interact between group and individual intelligence.
 * @author pali
 *
 */
public class EntityDescription {
	public DistanceBasedBoundary roamingBoundary = null;
	public DistanceBasedBoundary domainBoundary = null;
	public SkillContainer skills = new SkillContainer();
	public Attributes attributes = new Attributes();
	/**
	 * Unique id in the worlds.
	 */
	public String id;
	public int numberOfMembers = 1;
	public World world;
	public Ecology ecology;
	public int numberOfActionsPerTurn = 1;
	
	public static Class<? extends PositionCalculus> positionCalcType = PositionCalculus.class;
	public static HashMap<Class<? extends PositionCalculus>, PositionCalculus> calcTypes = new HashMap<Class<? extends PositionCalculus>, PositionCalculus>();
	
	static 
	{
		calcTypes.put(PositionCalculus.class, new PositionCalculus());
	}
	
	public HashMap<String, EntityDescription> subEntities = null;

	public EntityDescription(World w, Ecology ecology, String id, int numberOfMembers, int startX, int startY, int startZ) {
		super();
		this.id = id;
		this.numberOfMembers = numberOfMembers;
		this.world = w;
		this.ecology = ecology;
		roamingBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,0);
		domainBoundary = new DistanceBasedBoundary(w,startX,startY,startZ,0);
		skills.addSkills(getStartingSkills());
	}
	public void liveOneTurn(Collection<EntityDescription> nearbyEntities)
	{
			System.out.print("LIVE ONE TURN "+this.getClass()+" "+id + " | Nearby: "+nearbyEntities.size());
			System.out.println(" - "+roamingBoundary.posX+" "+roamingBoundary.posZ+" : "+roamingBoundary.radiusInRealCubes);
		if (nearbyEntities!=null && nearbyEntities.size()>0) {
			int actions = 0;
			for (EntityDescription desc : nearbyEntities)
			{
				if (desc.equals(J3DCore.getInstance().player))
					ecology.callbackMessage(this.getClass().getSimpleName()+": "+desc.getClass().getSimpleName()+" - "+makeTurnChoice(desc).getSimpleName());
				actions++;
				//if (numberOfActionsPerTurn==actions) break;
			}
		}
		
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
		EntityDescription.visibleSequence = visibleSequence;
	}
	
	public static void getInstance(World w, String id, int size, int startX, int startY, int startZ)
	{
		new EntityDescription(w,null,id,size,startX,startY, startZ);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SkillInstance> getStartingSkills()
	{
		try {
			ArrayList<SkillInstance> a = (ArrayList<SkillInstance>) getClass().getField("startingSkills").get(this);
			ArrayList<SkillInstance> sSkills= new ArrayList<SkillInstance>();
			for (SkillInstance i: a)
			{
				sSkills.add(i.copy());
			}
			return sSkills;
		} catch (Exception ex)
		{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Class <? extends Behavior>> getBehaviors()
	{
		try {
			return (ArrayList<Class <? extends Behavior>>) getClass().getField("behaviors").get(this);
		} catch (Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * Return a list of skills, filled with the best available.
	 * @return
	 */
	public HashMap<Class<? extends SkillBase>,SkillInstance> getBestSkillsOfGroup()
	{
		return skills.skills;
	}
	/**
	 * Return a list of skills, filled with the worst available.
	 * @return
	 */
	public HashMap<Class<? extends SkillBase>,SkillInstance> getWorstSkillsOfGroup()
	{
		return skills.skills;
	}
	
	public boolean isPrey(EntityDescription desc)
	{
		return false;
	}
	
	public Class <? extends Choice> makeTurnChoice(EntityDescription desc)
	{
		if (getBehaviors()!=null) 
		{
			if (getBehaviors().contains(Aggressive.class))
			{
				if (isPrey(desc))
				{
					return Attack.class;
				}
			} else
			if (getBehaviors().contains(Escapist.class))
			{
				return Hide.class;
			}
		}
		return Indifference.class;
	}

	public PositionCalculus getPositionCalculus()
	{
		return calcTypes.get(positionCalcType);
	}
}
