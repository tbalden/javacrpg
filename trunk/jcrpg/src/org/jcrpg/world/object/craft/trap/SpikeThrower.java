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

package org.jcrpg.world.object.craft.trap;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.martial.Throwing;
import org.jcrpg.world.object.BonusSkillActFormDesc;
import org.jcrpg.world.object.EntityObjInventory;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.ObjInstance;
import org.jcrpg.world.object.ObjList;
import org.jcrpg.world.object.RawMaterial;
import org.jcrpg.world.object.combat.throwing.ThrowingKnife;
import org.jcrpg.world.object.craft.TrapAndLock;

public class SpikeThrower extends TrapAndLock {


	public SpikeThrower() {
		super(1,1);
	}

	@Override
	public Class<? extends SkillBase> getAdditionalDisarmSkill() {
		return null;
	}

	@Override
	public HashMap<Class<? extends SkillBase>, Integer> getCraftSkillNeeds() {
		return null;
	}

	public static transient ArrayList<BonusSkillActFormDesc> bonusActForms = null;
	public ArrayList<BonusSkillActFormDesc> getSkillActFormBonusEffectTypes() {
		if (bonusActForms==null)
		{
			bonusActForms = new ArrayList<BonusSkillActFormDesc>();
			BonusSkillActFormDesc desc = new BonusSkillActFormDesc();
			desc.form = SkillGroups.getSkillActFormInstance(Throwing.class, Throwing.StraightThrow.class);
			desc.skillLevel = 20;
			bonusActForms.add(desc);
		}
		return bonusActForms;
	}

	@Override
	public HashMap<Class<? extends RawMaterial>, Integer> getMaterialNeeds() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public EntityObjInventory getInventory() {
		inv.add(new ObjInstance(ObjList.getInstance(ThrowingKnife.class)));
		return inv;
	}
	
	EntityObjInventory inv = new EntityObjInventory(null);
	
	@Override
	public InventoryListElement getUsedObject() {
		InventoryListElement element = new InventoryListElement(inv,ObjList.getInstance(ThrowingKnife.class)); 
		return element;
	}

}
