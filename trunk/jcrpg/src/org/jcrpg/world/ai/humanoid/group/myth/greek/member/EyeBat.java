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
import org.jcrpg.world.ai.abs.skill.InterceptionSkill;
import org.jcrpg.world.ai.abs.skill.magical.Elementarism;
import org.jcrpg.world.ai.body.HumanoidBody;
import org.jcrpg.world.ai.humanoid.group.myth.member.MythicBaseMember;
import org.jcrpg.world.ai.profession.MonsterNormal;

public class EyeBat extends MythicBaseMember {
	public static AudioDescription eyeBatAudio = new AudioDescription();
	static {
		eyeBatAudio.ENCOUNTER = new String[]{"eyebat/encounter"};
		eyeBatAudio.PAIN = new String[]{"eyebat/pain"};
		eyeBatAudio.DEATH= new String[]{"eyebat/encounter"};
		eyeBatAudio.ATTACK = new String[]{"eyebat/attack"};
		//batEyeAudio.ENVIRONMENTAL = new String[]{"boarman/boarman_env1","boarman/boarman_env2"};
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static EyeBat EYEBAT = new EyeBat("EYEBAT",eyeBatAudio);

	public static MovingModel eyeBat = new MovingModel("models/monster/eyebat/eyebat.obj",null,null,null,false);
	//public static MovingModel eyeBat = new MovingModel("models/monster/evilrip/beast_stand.obj",null,null,null,false);

	public static RenderedMovingUnit eyeBat_unit = new RenderedMovingUnit(new Model[]{eyeBat});

	public EyeBat(String visibleTypeId, AudioDescription audioDescription) {
		super(visibleTypeId, HumanoidBody.class, audioDescription);
		addProfessionInitially(new MonsterNormal());
		getMemberSkills().setSkillValue(Elementarism.class, 10);
		genderType = EntityDescription.GENDER_NEUTRAL;
	}
	@Override
	public AudioDescription getAudioDesc()
	{
		return eyeBatAudio;
	}

}
