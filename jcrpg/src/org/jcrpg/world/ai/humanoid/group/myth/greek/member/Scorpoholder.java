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

package org.jcrpg.world.ai.humanoid.group.myth.greek.member;

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.body.HumanoidBody;
import org.jcrpg.world.ai.humanoid.group.myth.member.MythicBaseMember;
import org.jcrpg.world.ai.profession.MonsterNormal;

public class Scorpoholder extends MythicBaseMember {
	public static AudioDescription scorpoAudio = new AudioDescription();
	static {
		scorpoAudio.ENCOUNTER = new String[]{"eyebat/encounter"};
		scorpoAudio.PAIN = new String[]{"eyebat/pain"};
		scorpoAudio.DEATH= new String[]{"eyebat/encounter"};
		scorpoAudio.ATTACK = new String[]{"eyebat/attack"};
		//batEyeAudio.ENVIRONMENTAL = new String[]{"boarman/boarman_env1","boarman/boarman_env2"};
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static Scorpoholder SCORPOHOLDER = new Scorpoholder("SCORPOHOLDER",scorpoAudio);

	//public static MovingModel eyeBat = new MovingModel("models/monster/eyebat/eyebat.obj",null,null,null,false);
	public static MovingModel scorpoholder = new MovingModel("models/monster/scorpoholder/beholder.obj",null,null,null,false);
	static {
		scorpoholder.disposition = new float [] {0,1f,0};
	}
	public static RenderedMovingUnit scorpoholder_unit = new RenderedMovingUnit(new Model[]{scorpoholder});

	public Scorpoholder(String visibleTypeId, AudioDescription audioDescription) {
		super(visibleTypeId, HumanoidBody.class, audioDescription);
		addProfessionInitially(new MonsterNormal());
		memberSkills.setSkillValue(Elementarism.class, 30);
		genderType = EntityDescription.GENDER_NEUTRAL;
	}
	@Override
	public AudioDescription getAudioDesc()
	{
		return scorpoAudio;
	}

}
