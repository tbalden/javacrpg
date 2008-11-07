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
package org.jcrpg.world.object.magical.music;

import java.util.ArrayList;

import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.magical.Alchemy;
import org.jcrpg.world.ai.abs.skill.mental.HigherMusic;
import org.jcrpg.world.object.BonusObject;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.Obj;

public class LuteOfDreams extends Obj implements BonusObject {

	public LuteOfDreams()
	{
		icon = "music/lute.png";
		requirementSkillAndLevel = new SkillInstance(HigherMusic.class,0);
	}
	
	public Attributes getAttributeValues() {
		return null;
	}

	public Resistances getResistanceValues() {
		return null;
	}

	public static transient ArrayList<BonusSkillActFormDesc> bonusActForms = null;
	public ArrayList<BonusSkillActFormDesc> getSkillActFormBonusEffectTypes() {
		if (bonusActForms==null)
		{
			bonusActForms = new ArrayList<BonusSkillActFormDesc>();
			BonusSkillActFormDesc desc = new BonusSkillActFormDesc();
			desc.form = SkillGroups.getSkillActFormInstance(Alchemy.class, Alchemy.FumesOfTwilight.class);
			desc.skillLevel = 20;
			desc.maxUsePerReplenish = 2;
			desc.replenishFrequency = BonusSkillActFormDesc.FREQUENCY_MINUTE;
			bonusActForms.add(desc);
		}
		return bonusActForms;
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
