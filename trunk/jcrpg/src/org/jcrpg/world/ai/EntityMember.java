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
import java.util.Collection;
import java.util.HashMap;

import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.ai.profession.Profession;
import org.jcrpg.world.object.ObjInstance;


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
	
	public static HashMap<Class<? extends Profession>, Profession> profInstances = new HashMap<Class<? extends Profession>, Profession>();
	
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
		profInstances.put(profession.getClass(), profession);
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
		return Language.v("member."+visibleTypeId);
	}
	
	public TurnActMemberChoice getTurnActMemberChoice(EncounterUnitData selfData, EncounterInfo info, EntityMemberInstance instance)
	{
		if (info.playerIfPresent!=null)
		{
			TurnActMemberChoice choice = new TurnActMemberChoice();
			ArrayList<EncounterUnitData> data = info.getTopology().getFriendlyLineup().getList(0);
			
			// TODO sophisticate this MUCH
			if (data!=null && data.size()>0)
			{
				
				choice.target = data.get(0);
				if (data.get(0).isGroupId)
				{
					choice.targetMember = data.get(0).generatedMembers.get(0);
				} else
				{
					EncounterUnit unit = data.get(0).getUnit();
					if (unit instanceof EntityMemberInstance)
					{
						choice.targetMember = (EntityMemberInstance)unit;
					} else
					{
						return null;
					}
				}
				if (selfData.getRelationLevel(choice.target)<=EntityScaledRelationType.NEUTRAL)
				{
					for (Class<?extends SkillBase> sb:commonSkills.skills.keySet())
					{
						System.out.println("--_ "+sb);
					}
					Collection<Class<?extends SkillBase>> skills = commonSkills.getSkillsOfType(info.getPhase());
					boolean found = false;
					System.out.println("FOUND TURN ACT SKILLS: "+skills);
					if (skills!=null)
					for (Class<? extends SkillBase> s:skills)
					{
						SkillInstance i = commonSkills.skills.get(s);
						SkillBase base = SkillGroups.skillBaseInstances.get(s);
						if (base.needsInventoryItem)
						{
							ArrayList<ObjInstance> objects =  instance.inventory.getObjectsForSkillInInventory(i);
							if (objects==null||objects.size()==0) continue;
							choice.usedObject = objects.get(0);
						}
						for (SkillActForm f:base.getActForms()) {
							if (SkillGroups.negativeSkillActForms.contains(f))
							{
								choice.skill = i;
								choice.skillActForm = f;
								if (f.targetType != SkillActForm.TARGETTYPE_LIVING_MEMBER)
								{
									choice.targetMember = null;
								}
								found = true;
								break;
							}
						}
						if (found) break;
					}
					if (!found) return null;
				}
			} else
			{
				return null;
			}
			return choice;
		}
		return null;
	}
	
}
