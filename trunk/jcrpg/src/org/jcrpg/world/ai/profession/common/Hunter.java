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

package org.jcrpg.world.ai.profession.common;

import org.jcrpg.world.ai.abs.skill.martial.StaffsAndWands;
import org.jcrpg.world.ai.abs.skill.mental.Architecture;
import org.jcrpg.world.ai.abs.skill.physical.Agriculture;
import org.jcrpg.world.ai.abs.skill.physical.AnimalHandling;
import org.jcrpg.world.ai.profession.HumanoidNormal;
import org.jcrpg.world.object.combat.staffwand.Club;

public class Hunter extends HumanoidNormal {

	public Hunter()
	{
		super();
		addMajorSkill(StaffsAndWands.class);
		//addMinorSkill(Alchemy.class);
		generationNewInstanceObjects.add(Club.class);
	}
}
