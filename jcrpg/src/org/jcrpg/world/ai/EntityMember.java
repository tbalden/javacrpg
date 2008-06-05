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

package org.jcrpg.world.ai;

import java.util.ArrayList;

import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.ai.profession.Profession;


/**
 * Specially described dependent member of an EntityDescription.
 * @author pali
 *
 */
public class EntityMember extends DescriptionBase {
	public String visibleTypeId;
	public SkillContainer commonSkills = new SkillContainer();
	public AttributeRatios commonAttributeRatios = new AttributeRatios();
	public float[] scale = new float[]{1,1,1};
	public AudioDescription audioDescription = null;
	public Class<? extends Profession> currentProfession;
	public ArrayList<Class<? extends Profession>> professions = new ArrayList<Class<? extends Profession>>();
	
	public ArrayList<Class<? extends Profession>> forbiddenProfessions = new ArrayList<Class <? extends Profession>>();
	
	public EconomyTemplate economyTemplate = new EconomyTemplate();
	
	public int genderType = EntityDescription.GENDER_NEUTRAL;
	
	public EntityMember(String visibleTypeId, AudioDescription audioDescription) {
		super();
		this.visibleTypeId = visibleTypeId;
		this.audioDescription = audioDescription;
	}
	
	public AttributeRatios getCommonAttributes() {
		return commonAttributeRatios;
	}

	public SkillContainer getCommonSkills() {
		return commonSkills;
	}
	
	public Attributes getAttributes(EntityDescription parent, String attr)
	{
		return Attributes.getAttributes(parent.attributes, commonAttributeRatios);
	}

	public float[] getScale() {
		return scale;
	}
	
	public void addProfessionInitially(Profession profession) {
		if (forbiddenProfessions.contains(profession.getClass())) return;
		if (professions.contains(profession.getClass()))
			return;
		professions.add(profession.getClass());
		for (Class<? extends SkillBase> skill : profession.additionalLearntSkills.keySet()) {
			if (!commonSkills.skills.containsKey(skill)) {
				commonSkills.addSkill(new SkillInstance(skill, profession.additionalLearntSkills.get(skill)));
			} else {
				commonSkills.skills.get(skill).increase(profession.additionalLearntSkills.get(skill));
			}
		}
		currentProfession = profession.getClass();

	}

	public boolean isProfessionForbidden(Class<?extends Profession> p)
	{
		if (forbiddenProfessions.contains(p)) return true;
		return false;
	}
	
	public int getRoamingSize()
	{
		return 1;
	}
	
	public String getName()
	{
		return Language.v("member."+this.getClass().getSimpleName());
	}
	
}
