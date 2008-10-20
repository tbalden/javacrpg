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

import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.body.HumanoidBody;
import org.jcrpg.world.ai.humanoid.group.myth.member.MythicBaseMember;
import org.jcrpg.world.ai.profession.MonsterNormal;

public class HellPig extends MythicBaseMember {
	public static AudioDescription hellPigAudio = new AudioDescription();
	static {
		hellPigAudio.ENCOUNTER = new String[]{"hellpig/encounter"};
		hellPigAudio.PAIN = new String[]{"hellpig/short"};
		hellPigAudio.DEATH= new String[]{"hellpig/encounter"};
		hellPigAudio.ATTACK = new String[]{"hellpig/short"};
		//hellPigAudio.ENVIRONMENTAL = new String[]{"hellpig/boarman_env1","hellpig/boarman_env2"};
		//boarmanFemaleAudio.ENVIRONMENTAL = new String[]{"human_env1","human_female_env1"};
	}
	
	public static HellPig HELLPIG = new HellPig("HELLPIG",hellPigAudio);

	public static MovingModel hellPig = null;
	static
	{
		MovingModelAnimDescription desc = new MovingModelAnimDescription();
		desc.IDLE = "./data/models/monster/hellpig/idle.md5anim";		
		desc.IDLE_COMBAT = "./data/models/monster/hellpig/idle.md5anim";
		desc.WALK = "./data/models/monster/hellpig/forward.md5anim";
		desc.ATTACK_LOWER = "./data/models/monster/hellpig/attack.md5anim";
		desc.ATTACK_UPPER = "./data/models/monster/hellpig/attack.md5anim";
		desc.DEFEND_LOWER = "./data/models/monster/hellpig/jump.md5anim";
		desc.DEFEND_UPPER = "./data/models/monster/hellpig/jump.md5anim";
		desc.PAIN = "./data/models/monster/hellpig/hit.md5anim";
		desc.DEATH_NORMAL = "./data/models/monster/hellpig/die.md5anim";
		desc.DEAD = "./data/models/monster/hellpig/die.md5anim";
		hellPig = new MovingModel("./data/models/monster/hellpig/hellpig2.md5mesh",desc,null,null,false);
		hellPig.genericScale = 0.11f;
		//hellPig.scale = new float[] {0.5f,0.5f,0.5f};
		
	}
	public static RenderedMovingUnit hellPig_unit = new RenderedMovingUnit(new Model[]{hellPig});

	public HellPig(String visibleTypeId, AudioDescription audioDescription) {
		super(visibleTypeId, HumanoidBody.class, audioDescription);
		addProfessionInitially(new MonsterNormal());
		memberSkills.setSkillValue(BiteFight.class, 10);
		genderType = EntityDescription.GENDER_NEUTRAL;
	}
	
	@Override
	public AudioDescription getAudioDesc()
	{
		return hellPigAudio;
	}

}
