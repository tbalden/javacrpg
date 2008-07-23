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

package org.jcrpg.world.ai.profession.common;

import org.jcrpg.world.ai.abs.skill.martial.HammerAndAxe;
import org.jcrpg.world.ai.abs.skill.mental.Architecture;
import org.jcrpg.world.ai.abs.skill.physical.Metalcraft;
import org.jcrpg.world.ai.profession.HumanoidNormal;
import org.jcrpg.world.object.combat.hammeraxe.PickAxe;
import org.jcrpg.world.object.magical.potion.MinorHealingPotion;

public class Miner extends HumanoidNormal {

	public Miner()
	{
		super();
		addMinorSkill(HammerAndAxe.class);
		addMajorSkill(Metalcraft.class);
		addMinorSkill(Architecture.class);
		generationNewInstanceObjects.add(PickAxe.class);
		generationNewInstanceObjects.add(MinorHealingPotion.class);
	}
}
