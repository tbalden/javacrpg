/*
 *  This file is part of JavaCRPG.
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
package org.jcrpg.world.object;

import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillInstance;

public class Obj {
	String id;
	public String icon;
	public SkillInstance requirementSkillAndLevel  = null;
	
	public Attributes objectAttributeBonus = null;
	public Resistances objectResistanceBonus = null;
	
	public EffectProgram effectProgram = null;
	
	public String getName()
	{
		return Language.v("obj."+this.getClass().getSimpleName());
	}

	public Attributes getAttributeBonus() {
		return objectAttributeBonus;
	}

	public Resistances getResistanceBonus() {
		return objectResistanceBonus;
	}
	
	public EffectProgram getEffectProgram()
	{
		return effectProgram;
	}
	
	/**
	 * Tells if this one is an object that can be attached (e.g. loaded as ammunition type) for an other object.
	 * @return
	 */
	public boolean isAttacheable()
	{
		return false;
	}
	/**
	 * Returns the type class to witch this one can be attached.
	 * @return
	 */
	public Class getAttachableToType()
	{
		return null;
	}
	
	/**
	 * 
	 * @return Tells if this is an object that needs an attached object type (ammo) and some available instance
	 * to be used for a skill (e.g. shoot).
	 */
	public boolean needsAttachmentDependencyForSkill()
	{
		return false;
	}
	
	/**
	 * @return the type which the item can have as attachment (e.g. type of ammo base class).
	 */
	public Class getAttachmentDependencyType()
	{
		return null;
	}
	
	/**
	 * 
	 * @param c
	 * @return Tells if this object type can be attached as a type of object (so does it have a superclass
	 * equal to specified class c).
	 */
	public boolean isIdentifiableAs(Class c)
	{
		Class o = this.getClass();
		for (Class iface:o.getInterfaces())
		{
			if (iface==c) return true;
		}
		if (o != c)
		{
			o = o.getSuperclass();
			if (o==null) return false;
			if (o!=null && o != c)
			{
				o = o.getSuperclass();
				if (o==null) return false;
				if (o!=null && o != c)
				{
					o = o.getSuperclass();
					if (o==null) return false;
					if (o!=null && o != c)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public int maxNumberOfUsage = 0; // 0 is endless
	public int maxNumberOfUsage()
	{
		return maxNumberOfUsage;
	}
	
	public String getIconFilePath()
	{
		return "./data/icons/objects/"+icon;
	}
	
	public static final int NO_RANGE = -1;
	/**
	 * what is the use range of the object when lineup is present. 0 is the first line.
	 * 0 -> dagger, 1 -> sword, quarterstaff 2 -> long staff, pike, -1 -> bows, thrown
	 */
	protected int useRangeInLineup = NO_RANGE;
	/**
	 * @return get what is the use range of the object when lineup is present. 0 is the first line.
	 */
	public int getUseRangeInLineup()
	{
		if (useRangeInLineup==NO_RANGE) return useRangeInLineup;
		return useRangeInLineup+2; // +2 is for letting line 3rd attack enemy line 1st -> calculation
		// is like 2 (your line) + 0 (enemy line) <= range, so adding +2 allows 3rd line to attack at least
		// 1st enemy line.
	}
	
	/**
	 * Tells if this is a group item, Override this in constructor.
	 */
	protected boolean groupable = false;
	/**
	 * 
	 * @return If this object type should be grouped in lists and such, this returns true.
	 */
	public boolean isGroupable()
	{
		return groupable;
	}
	
	/**
	 * Return calculated absolute gold value of this object type.
	 * @return Value.
	 */
	public int getGoldValue()
	{
		// TODO this fully...
		if (this instanceof BonusObject) return 10;
		return 1;
	}
	
}
