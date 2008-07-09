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

import org.jcrpg.util.Language;

public class SkillBase {
	
	public boolean needsInventoryItem = false;
	
	public static class NoActForm extends SkillActForm 
	{
		public NoActForm(SkillBase skill) {
			super(skill);
		}

		@Override
		public String getSound() {
			return null;
		}
	}
	
	public static ArrayList<SkillActForm> noActFormList = new ArrayList<SkillActForm>();
	public static SkillBase noActFormBase = new SkillBase();
	static
	{
		noActFormList.add(new NoActForm(noActFormBase));
	}

	protected ArrayList<SkillActForm> actForms = new ArrayList<SkillActForm>();
	
	public ArrayList<SkillActForm> getActForms()
	{
		if (actForms.size()>0) return actForms;
		return noActFormList;
	}
	public SkillActForm getActForm(Class<? extends SkillActForm> type)
	{
		for (SkillActForm form:actForms)
		{
			if (form.getClass()==type)
				return form;
		}
		return null;
	}
	
	public String getName()
	{
		return Language.v("skills."+this.getClass().getSimpleName());
	}
	
	public static final ArrayList<Class<? extends SkillBase>> zeroLengthContra = new ArrayList<Class<? extends SkillBase>>();
	public ArrayList<Class<? extends SkillBase>> getContraSkillTypes()
	{
		return zeroLengthContra;
	}

}
