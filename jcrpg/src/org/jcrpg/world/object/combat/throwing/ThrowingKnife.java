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
package org.jcrpg.world.object.combat.throwing;

import org.jcrpg.threed.jme.program.impl.ArrowNoEffect;
import org.jcrpg.threed.scene.model.SimpleModel;
import org.jcrpg.threed.scene.model.effect.EffectProgram;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.abs.skill.martial.Throwing;

public class ThrowingKnife extends ThrowingWeapon {

	static SimpleModel effectProgramModel = new SimpleModel("models/item/ammo/throwingknife.obj",null,false);
	static EffectProgram effectProgram = new EffectProgram(ArrowNoEffect.class,effectProgramModel);
	
	@Override
	public EffectProgram getEffectProgram() {
		return effectProgram;
	}

	public ThrowingKnife()
	{
		super();
		icon = "weapon/throwingknife.png"; 
		requirementSkillAndLevel = new SkillInstance(Throwing.class,0);
		
	}

	public float getAttackMultiplicator() {
		return 1.5f;
	}

	public float getDefenseMultiplicator() {
		return 0.5f;
	}

	public String getHitSound() {
		return null;
	}

	public int getMaxDamage() {
		return 4;
	}

	public String getMissSound() {
		return null;
	}

	public int getSpeed() {
		return 10;
	}
	public String getDamageTypeResistance() {
		return FantasyResistances.RESIST_PIERCE;
	}

}
