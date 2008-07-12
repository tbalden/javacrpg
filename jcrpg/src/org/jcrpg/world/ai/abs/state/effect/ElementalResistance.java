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
package org.jcrpg.world.ai.abs.state.effect;

import org.jcrpg.game.logic.ImpactUnit;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyResistances;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.state.StateEffect;

public class ElementalResistance extends StateEffect {

	public ElementalResistance()
	{
		
	}
	
	@Override
	public boolean updateBeingAttacked() {
		return false;
	}

	@Override
	public boolean canDoActForm(SkillActForm form) {
		return true;
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
		return "elemental_resistance.png";
	}

	@Override
	public Attributes getBaseAttributes() {
		return null;
	}

	
	static Resistances resis = new FantasyResistances();
	static
	{
		resis.setResistance(FantasyResistances.RESIST_COLD, 10);
		resis.setResistance(FantasyResistances.RESIST_HEAT, 10);
		resis.setResistance(FantasyResistances.RESIST_CHEMICAL, 10);
	}
	
	@Override
	public Resistances getBaseResistances() {
		return resis;
	}

	@Override
	public boolean canDoUse() {
		return true;
	}

}
