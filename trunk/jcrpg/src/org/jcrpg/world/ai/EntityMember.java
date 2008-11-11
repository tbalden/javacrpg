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

package org.jcrpg.world.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.game.element.TurnActMemberChoice;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.abs.attribute.AttributeRatios;
import org.jcrpg.world.ai.abs.attribute.Attributes;
import org.jcrpg.world.ai.abs.attribute.FantasyAttrRatios;
import org.jcrpg.world.ai.abs.attribute.ResistanceRatios;
import org.jcrpg.world.ai.abs.attribute.Resistances;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.body.BodyBase;
import org.jcrpg.world.ai.body.SinglePartBody;
import org.jcrpg.world.ai.humanoid.EconomyTemplate;
import org.jcrpg.world.ai.profession.Profession;
import org.jcrpg.world.object.InventoryListElement;


/**
 * Specially described dependent member of an EntityDescription.
 * @author pali
 *
 */
public class EntityMember extends DescriptionBase {
	public String visibleTypeId;
	protected SkillContainer memberSkills = new SkillContainer();
	public AttributeRatios commonAttributeRatios = new AttributeRatios();
	public ResistanceRatios commonResistenceRatios = new ResistanceRatios();
	public float[] scale = new float[]{1,1,1};
	private AudioDescription audioDescription = null;
	public Class<? extends Profession> currentProfession;
	
	public Class<? extends BodyBase> bodyType = SinglePartBody.class;
	
	public ArrayList<Class<? extends Profession>> professions = new ArrayList<Class<? extends Profession>>();	
	public ArrayList<Class<? extends Profession>> forbiddenProfessions = new ArrayList<Class <? extends Profession>>();
	
	public EconomyTemplate economyTemplate = new EconomyTemplate();
	
	public int genderType = EntityDescription.GENDER_NEUTRAL;
	
	public EntityMember(String visibleTypeId, Class<? extends BodyBase> bodyType, AudioDescription audioDescription) {
		super();
		this.visibleTypeId = visibleTypeId;
		this.audioDescription = audioDescription;
		this.bodyType = bodyType;
	}
	
	public AttributeRatios getCommonAttributes() {
		return commonAttributeRatios;
	}

	public SkillContainer getCommonSkills() {
		return memberSkills;
	}
	
	
	public Attributes getAttributes(EntityDescription parent)
	{
		return Attributes.getAttributes(parent.attributes, commonAttributeRatios);
	}
	
	public Resistances getResistances(EntityDescription parent)
	{
		return Resistances.getResistances(parent.resistances, commonResistenceRatios);
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
			if (!memberSkills.skills.containsKey(skill)) {
				memberSkills.addSkill(new SkillInstance(skill, profession.additionalLearntSkills.get(skill)));
			} else {
				memberSkills.skills.get(skill).increase(profession.additionalLearntSkills.get(skill));
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
	
	/**
	 * Orders the encountered in a wishlist of this member - who the member wants to do something first, sorting
	 * by it's own intelligence. Override this if want to modify.
	 * @param list
	 * @return
	 */
	public ArrayList<EncounterUnitData> orderUnitDataByEntityMemberIntelligence(ArrayList<EncounterUnitData> list)
	{
		//if (true) return list;
		// this is a randomized order - quite primitive. :D
		TreeMap<Integer, EncounterUnitData> order = new TreeMap<Integer, EncounterUnitData>();
		for (EncounterUnitData data:list)
		{
			int point = (int)(Math.random()*10);
			while (order.get(point)!=null) point++;
			order.put(point, data);
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("??? ORDERED DATA "+data.getName());
		}
		ArrayList<EncounterUnitData> ret = new ArrayList<EncounterUnitData>();
		ret.addAll(order.values());
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("INCOMING SIZE = "+list.size());
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("RET SIZE = "+ret.size());
		return ret;
	}
		
	
	public TurnActMemberChoice getTurnActMemberChoice(EncounterUnitData selfData, EncounterInfo info, EntityMemberInstance instance)
	{
		if (info.playerIfPresent!=null)
		{
			TurnActMemberChoice choice = new TurnActMemberChoice();
			choice.member = instance;
			
			// list for destructive choices...
			ArrayList<EncounterUnitData> enemyData = null;

			// for healing and positive operation choices...
			ArrayList<EncounterUnitData> friendlyData = null;
			
			if (!selfData.friendly)
			{
				// i'm an enemy of player...
				
				enemyData = info.getTopology().getFriendlyLineup().getAllUnits(); // player friendly is my enemy
				enemyData.addAll(info.getTopology().getPartyLineup().getAllUnits());
				enemyData = orderUnitDataByEntityMemberIntelligence(enemyData);
				friendlyData = info.getTopology().getEnemyLineup().getAllUnits(); // player enemy is my friend.
				friendlyData = orderUnitDataByEntityMemberIntelligence(friendlyData);
			} else
			{
				// i'm a friend of player...
				
				enemyData = info.getTopology().getEnemyLineup().getAllUnits(); // player enemy is my enemy
				enemyData = orderUnitDataByEntityMemberIntelligence(enemyData);
				friendlyData = info.getTopology().getFriendlyLineup().getAllUnits(); // player friend is my friend
				friendlyData = orderUnitDataByEntityMemberIntelligence(friendlyData);
				friendlyData.addAll(info.getTopology().getPartyLineup().getAllUnits());
			}
			
			// TODO sophisticate this MUCH
			if (enemyData!=null && enemyData.size()>0)
			{	
				//boolean foundTarget = false;
				for (EncounterUnitData unitData:enemyData) 
				{
					if (unitData.isNeutralized()) 
					{
						if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("EntityMember.getTurnActMemberChoice ## NOT (dead or neut) for choice : "+unitData.getName());
						continue;
					}
					choice.target = unitData;
					
					if (selfData.getRelationLevel(choice.target)>EntityScaledRelationType.NEUTRAL) // TODO later >=
						continue;			
					
					choice.targetMember = unitData.getFirstLivingMember();
					if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("EntityMember.getTurnActMemberChoice ====== "+(choice.targetMember==null?"NULL":choice.targetMember.description.getName()));
					if (choice.targetMember==null) continue;
					else
					{
						if (!choice.targetMember.memberState.isDead())
						{
							if (selfData.getRelationLevel(choice.target)<=EntityScaledRelationType.NEUTRAL)
							{
								for (Class<?extends SkillBase> sb:memberSkills.skills.keySet())
								{
									//if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("--_ "+sb);
								}
								int lineUpDistance = instance.encounterData.getCurrentLine()+choice.targetMember.encounterData.getCurrentLine();
								Collection<Class<?extends SkillBase>> skills = memberSkills.getTurnActSkillsOrderedBySkillLevel(info.getPhase(),null,lineUpDistance);
								//boolean found = false;
								if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("FOUND TURN ACT SKILLS: "+skills);
								if (skills!=null)
								for (Class<? extends SkillBase> s:skills)
								{
									SkillInstance i = memberSkills.skills.get(s);
									SkillBase base = SkillGroups.skillBaseInstances.get(s);
									if (base.needsInventoryItem)
									{
										ArrayList<InventoryListElement> objects =  instance.inventory.getObjectsForSkillInInventory(i,lineUpDistance);
										if (objects==null||objects.size()==0) continue;
										choice.usedObject = objects.get(0);
									}
									for (Class<? extends SkillActForm> fDef:instance.getDoableActForms(s)) {
										SkillActForm f = base.getActForm(fDef);
										if (SkillGroups.negativeSkillActForms.contains(f))
										{
											choice.skill = i;
											choice.skillActForm = f;
											if (f.targetType != SkillActForm.TARGETTYPE_LIVING_MEMBER)
											{
												choice.targetMember = null;
											}
											if (f.targetType!=SkillActForm.TARGETTYPE_LIVING_MEMBER)
											{
												if (unitData.parent == info.playerIfPresent)
												{
													choice.target = info.playerPartyUnitData;
												}
											}
											return choice;
										}
									}
								}
							}
						}
					}
				}
				// no target found.
				choice.doNothing = true;
				choice.targetMember = null;
				choice.target = null;
				return choice;
				
			} else
			{
				choice.doNothing = true;
				choice.targetMember = null;
				choice.target = null;
				return choice;
			}
		}
		return null;
	}
	
	public String getSound(String type)
	{
		if (audioDescription==null) return null;
		return audioDescription.getSound(type);
	}
	
	public BodyBase getBodyType()
	{
		return BodyBase.bodyBaseInstances.get(bodyType);
	}
	
	public SkillContainer getMemberSkills()
	{
		return memberSkills;
	}
	
	public static AttributeRatios nonSpecificLevelingRatios = new FantasyAttrRatios();
	
	static
	{
		for (String attr:nonSpecificLevelingRatios.attributeRatios.keySet())
		{
			nonSpecificLevelingRatios.setAttributeRatio(attr, 1);
		}
	}
	
	public AttributeRatios getLevelingAttributeRatioHint(EntityMemberInstance instance)
	{
		return nonSpecificLevelingRatios;
	}
	
	public class SkillPreferenceHint
	{
		HashMap<Class<? extends SkillBase>, Float> map = new HashMap<Class<? extends SkillBase>, Float>();
	}
	
	public SkillPreferenceHint getLevelingSkillPreferenceHint(EntityMemberInstance instance)
	{
		return new SkillPreferenceHint();
		
	}
	
	public AudioDescription getAudioDesc()
	{
		return audioDescription;
	}
	public void setAudioDesc(AudioDescription desc)
	{
		audioDescription = desc;
	}
	
}
