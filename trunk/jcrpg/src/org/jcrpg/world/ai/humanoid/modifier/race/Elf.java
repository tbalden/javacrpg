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

package org.jcrpg.world.ai.humanoid.modifier.race;

import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.humanoid.MemberPerson;

public class Elf extends MemberPerson {

	public Elf(String visibleTypeId, AudioDescription audio) {
		super(visibleTypeId, audio);
		pictureRoot = "elf";
		commonAttributeRatios.setAttributeRatio(FantasyAttributes.STRENGTH, 0.8f);
		commonAttributeRatios.setAttributeRatio(FantasyAttributes.CONSTITUTION, 0.8f);
		commonAttributeRatios.setAttributeRatio(FantasyAttributes.PSYCHE, 1.2f);
		commonAttributeRatios.setAttributeRatio(FantasyAttributes.CONCENTRATION, 1.2f);
	}

	@Override
	public MemberPerson copy(MemberPerson copy) {
		copy = new Elf(visibleTypeId,audioDescription);
		return super.copy(copy);
	}
	
}
