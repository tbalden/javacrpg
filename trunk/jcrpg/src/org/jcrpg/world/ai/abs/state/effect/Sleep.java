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
package org.jcrpg.world.ai.abs.state.effect;

import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.state.StateEffect;

public class Sleep extends StateEffect {

	public Sleep()
	{
		saverResistances.add(FantasyResistances.RESIST_MENTAL);
	}
	
	@Override
	public boolean updateBeingAttacked() {
		return true;
	}

	@Override
	public boolean canDoActForm(SkillActForm form) {
		return false;
	}

	@Override
	public ImpactUnit impactForTime() {
		return null;
	}

	@Override
	public ImpactUnit impactForTurn() {
		return null;
	}

	@Override
	public String getIcon() {
		return "sleep.png";
	}

	@Override
	public Attributes getBaseAttributes() {
		return null;
	}

	@Override
	public Resistances getBaseResistances() {
		return null;
	}

	@Override
	public boolean canDoUse() {
		return false;
	}

}
