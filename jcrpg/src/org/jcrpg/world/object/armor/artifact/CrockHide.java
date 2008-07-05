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
package org.jcrpg.world.object.armor.artifact;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.object.BonusObject;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.armor.LeatherArmor;

public class CrockHide extends LeatherArmor implements BonusObject{

	public int getDefenseValue() {
		return 15;
	}
	
	static Attributes a = new FantasyAttributes(true);
	static
	{
		a.setAttribute(FantasyAttributes.STRENGTH, 5);
		a.setAttribute(FantasyAttributes.SPEED, 5);
	}
	public Attributes getAttributeValues() {
		return a;
	}

	public Resistances getResistanceValues() {
		return null;
	}

	public ArrayList<BonusSkillActFormDesc> getSkillActFormBonusEffectTypes() {
		return null;
	}

	public boolean isBodyPartBonusOnly() {
		return false;
	}

	public boolean isCursed() {
		return false;
	}

	public boolean isDestructive() {
		return false;
	}

}
