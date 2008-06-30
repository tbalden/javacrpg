/*
 *  This file is part of JavaCRPG.
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
	
	public boolean isAttacheable()
	{
		return false;
	}
	public Class getAttacheableToType()
	{
		return null;
	}
	
	public boolean needsAttachmentDependencyForSkill()
	{
		return false;
	}
	
	public Class getAttachmentDependencyType()
	{
		return null;
	}
	
	public boolean isAttacheableAs(Class c)
	{
		Class o = this.getClass();
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
	
}
