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

package org.jcrpg.world.ai.abs.skill;

import java.util.ArrayList;

import org.jcrpg.threed.J3DCore;

/**
 * Entities may have this containing a skill with certain level.
 * @author pali
 */
public class SkillInstance {

	public Class <? extends SkillBase> skill;
	public int level;
	
	public ArrayList<Class<? extends SkillActForm>> aquiredActForms = new ArrayList<Class<? extends SkillActForm>>();
	
	public SkillInstance(Class <? extends SkillBase> skill, int level) {
		super();
		this.skill = skill;
		this.level = level;
		for (SkillActForm form:SkillGroups.skillBaseInstances.get(skill).getActForms())
		{
			if (form.skillRequirementLevel<=level)
			{
				aquiredActForms.add(form.getClass());
			}
		}
	}
	
	/**
	 * Adds a skill act form if level is high enough.
	 * @param f
	 * @return true if level was high enough.
	 */
	public boolean addSkillActForm(Class <? extends SkillActForm> f)
	{
		for (SkillActForm form:SkillGroups.skillBaseInstances.get(skill).actForms)
		{
			if (f == form.getClass() && form.skillRequirementLevel<=level)
			{
				if (!aquiredActForms.contains(form.getClass()))
					aquiredActForms.add(form.getClass());
				return true;
			}
		}
		return false;
	}
	
	public void updateAvailableActForms()
	{
		for (SkillActForm form:SkillGroups.skillBaseInstances.get(skill).actForms)
		{
			if (form.skillRequirementLevel<=level)
			{
				if (!aquiredActForms.contains(form.getClass())) 
				{
					J3DCore.getInstance().uiBase.hud.mainBox.addEntry("New Skill Act Form: "+form.getName()+ " ("+SkillGroups.skillBaseInstances.get(skill).getName()+")");
					aquiredActForms.add(form.getClass());
				} else
				{
					//J3DCore.getInstance().uiBase.hud.mainBox.addEntry("Already have Skill Act Form: "+form.getName());
				}
			}
		}
	}
	
	
	public SkillInstance copy()
	{
		return new SkillInstance(skill,level);
	}
	public void increase(int value)
	{
		level+=value;
	}
	
	public SkillBase getSkill()
	{
		return SkillGroups.skillBaseInstances.get(skill);
	}
	
	
}
