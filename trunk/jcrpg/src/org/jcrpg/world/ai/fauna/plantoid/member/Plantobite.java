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

package org.jcrpg.world.ai.fauna.plantoid.member;


import org.jcrpg.threed.scene.model.Model;
import org.jcrpg.threed.scene.model.moving.MovingModel;
import org.jcrpg.threed.scene.model.moving.MovingModelAnimDescription;
import org.jcrpg.threed.scene.moving.RenderedMovingUnit;
import org.jcrpg.world.ai.AudioDescription;
import org.jcrpg.world.ai.EntityDescription;
import org.jcrpg.world.ai.EntityMember;
import org.jcrpg.world.ai.abs.skill.martial.BiteFight;
import org.jcrpg.world.ai.body.HumanoidBody;
import org.jcrpg.world.ai.profession.MonsterNormal;

public class Plantobite extends EntityMember {
	public static AudioDescription plantobiteAudio = new AudioDescription();
	static {
		plantobiteAudio.ENCOUNTER = new String[]{"plant/plantenc"};
		plantobiteAudio.PAIN = new String[]{"plant/plantscream","plant/plantscream2"};
		plantobiteAudio.DEATH= new String[]{"plant/plantscream"};
		plantobiteAudio.ATTACK = new String[]{"eyebat/plantattack"};
		plantobiteAudio.ENVIRONMENTAL = new String[]{"plant/plantscream2"};
	}
	
	public static Plantobite PLANTOBITE = new Plantobite("PLANTOBITE",plantobiteAudio);

	public static MovingModel plantobite = new MovingModel("models/monster/plantobite/plantobite.obj",null,null,null,false);
	static 
	{
		MovingModelAnimDescription desc = new MovingModelAnimDescription();

		desc.IDLE = "./data/models/monster/plantobite/plantoid1_idle.md5anim";		
		desc.IDLE_COMBAT = "./data/models/monster/plantobite/plantoid1_idle.md5anim";
		desc.WALK = "./data/models/monster/plantobite/plantoid1_idle.md5anim";
		desc.ATTACK_LOWER = "./data/models/monster/plantobite/plantoid1_attack.md5anim";
		desc.ATTACK_UPPER = "./data/models/monster/plantobite/plantoid1_attack.md5anim";
		desc.DEFEND_LOWER = "./data/models/monster/plantobite/plantoid1_hit.md5anim";
		desc.DEFEND_UPPER = "./data/models/monster/plantobite/plantoid1_hit.md5anim";
		desc.PAIN = "./data/models/monster/plantobite/plantoid1_hit.md5anim";
		desc.DEATH_NORMAL = "./data/models/monster/plantobite/plantoid1_hit.md5anim";
		desc.DEAD = "./data/models/monster/plantobite/plantoid1_hit.md5anim";

		plantobite = new MovingModel("./data/models/monster/plantobite/plantoid1.md5mesh",desc,null,null,false);
		plantobite.disposition = new float[]{0,1f,0};
		plantobite.genericScale = 1.1f;

		//plantobite.disposition = new float[]{0,0f,0};
	}
	public static RenderedMovingUnit plantobite_unit = new RenderedMovingUnit(new Model[]{plantobite});

	public Plantobite(String visibleTypeId, AudioDescription audioDescription) {
		super(visibleTypeId,HumanoidBody.class,audioDescription);
		addProfessionInitially(new MonsterNormal());
		getMemberSkills().setSkillValue(BiteFight.class, 20);
		genderType = EntityDescription.GENDER_NEUTRAL;
	}
	@Override
	public AudioDescription getAudioDesc()
	{
		return plantobiteAudio;
	}

}
