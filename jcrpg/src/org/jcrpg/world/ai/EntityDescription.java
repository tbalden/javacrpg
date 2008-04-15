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
import java.util.HashMap;

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
import org.jcrpg.world.place.World;

/**
 * All moving beings's base class which should interact between group and individual intelligence.
 * @author pali
 *
 */
public class EntityDescription {
	public static final int GENDER_NEUTRAL = 0;
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;
	public static final int GENDER_BOTH = 3;
	
	public int genderType = GENDER_NEUTRAL;
	public SkillContainer skills = new SkillContainer();
	public Attributes attributes = new Attributes();
	/**
	 * Tells how to divide the non-specific masses of an Entity into EntityMembers.
	 */
	public GroupingRule groupingRule = new GroupingRule();
	public int numberOfActionsPerTurn = 1;
	
	public static Class<? extends PositionCalculus> positionCalcType = PositionCalculus.class;
	public static HashMap<Class<? extends PositionCalculus>, PositionCalculus> calcTypes = new HashMap<Class<? extends PositionCalculus>, PositionCalculus>();
	
	public ArrayList<SkillInstance> startingSkills = new ArrayList<SkillInstance>();
	public ArrayList<Class <? extends Behavior>> behaviors = new ArrayList<Class<? extends Behavior>>();
	
	static 
	{
		calcTypes.put(PositionCalculus.class, new PositionCalculus());
	}
	
	public String iconPic = "unknown"; 
	
	/**
	 * Tells if entity can go to water cube.
	 */
	public boolean waterDweller = false;
	/**
	 * Tells if entity can go on land.
	 */
	public boolean landDweller = true;
	
	/**
	 * entity can fly?
	 */
	public boolean airDweller = false;
	
	/**
	 * Tells if entity can go indoor places.
	 */
	public boolean indoorDweller = true;
	/**
	 * Tells if entity can go outdoor places.
	 */
	public boolean outdoorDweller = true;
	

	public ArrayList<SkillInstance> getStartingSkills()
	{
		ArrayList<SkillInstance> a = startingSkills;
		ArrayList<SkillInstance> sSkills= new ArrayList<SkillInstance>();
		for (SkillInstance i: a)
		{
			sSkills.add(i.copy());
		}
		return sSkills;
	}
	
	public ArrayList<Class <? extends Behavior>> getBehaviors()
	{
			return behaviors;
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
	
	public Class <? extends Choice> makeTurnChoice(EntityDescription desc, EntityInstance instance)
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
	
	protected void setAverageGroupSizeAndDeviation(int size, int dev)
	{
		groupingRule.averageSize = size;
		groupingRule.sizeDeviation = dev;
	}
	protected void addGroupingRuleMember(EntityMember member)
	{
		addGroupingRuleMember(member,50,1,1);
	}
	protected void addGroupingRuleMember(EntityMember member, int likeness, int min, int max)
	{
		groupingRule.possibleMembers.add(new GroupingMemberProps(likeness,min,max,member));
	}
	
	/**
	 * Returns roaming size calculation result, override it in specific descriptions if you like.
	 * @param instance
	 * @return
	 */
	public int getRoamingSize(EntityInstance instance)
	{
		return instance.numberOfMembers;
	}
	/**
	 * Returns domain size calculation result, override it in specific descriptions if you like.
	 * @param instance
	 * @return
	 */
	public int getDomainSize(EntityInstance instance)
	{
		return instance.numberOfMembers;
	}

	public int getNumberOfActionsPerTurn() {
		return numberOfActionsPerTurn;
	}

	public boolean isWaterDweller() {
		return waterDweller;
	}

	public boolean isLandDweller() {
		return landDweller;
	}

	public boolean isIndoorDweller() {
		return indoorDweller;
	}

	public boolean isOutdoorDweller() {
		return outdoorDweller;
	}

	public boolean isAirDweller() {
		return airDweller;
	}
	
	/**
	 * This must be called when new ecology is generated to let the desciption settleg things for the instance.
	 * @param instance
	 * @param world
	 * @param ecology
	 */
	public void setupNewInstance(EntityInstance instance, World world, Ecology ecology)
	{
		
	}
}
