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

package org.jcrpg.world.ai.humanoid.group.myth.greek.member;

import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.body.HumanoidBody;
import org.jcrpg.world.ai.humanoid.group.myth.member.MythicBaseMember;
import org.jcrpg.world.ai.profession.adventurer.Mage;

public class EyeBat extends MythicBaseMember {

	public EyeBat(String visibleTypeId, AudioDescription audioDescription) {
		super(visibleTypeId, HumanoidBody.class, audioDescription);
		addProfessionInitially(new Mage());
		memberSkills.setSkillValue(Elementarism.class, 10);
		genderType = EntityDescription.GENDER_NEUTRAL;
	}

}
