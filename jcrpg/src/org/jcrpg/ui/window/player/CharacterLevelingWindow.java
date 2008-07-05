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
package org.jcrpg.ui.window.player;

import java.util.ArrayList;
import java.util.HashMap;

import org.jcrpg.game.GameLogicConstants;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.UIImageCache;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillContainer;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.ai.profession.Profession;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Player's party character level upgrading window.
 * @author illes
 *
 */
public class CharacterLevelingWindow extends PagedInputWindow {

	Node page0 = new Node();
	
	public TextLabel characterName;
	
	// character data
	Quad currentPic = null;
	ListSelect professionSelect = null;

	ValueTuner level = null;
	ValueTuner health = null;
	ValueTuner stamina = null;
	ValueTuner morale = null;
	ValueTuner sanity = null;
	ValueTuner mana = null;
	HashMap<String, ValueTuner> attributeTuners = new HashMap<String, ValueTuner>();
	HashMap<String, ListSelect> skillSelects = new HashMap<String, ListSelect>();
	
	TextButton finishedButton = null;

	TextLabel attrPointsLeftLabel = null;

	TextLabel skillText;
	ValueTuner skillValueTuner;
	TextLabel skillPointsLeftLabel = null;
	
	// which skillgroup was used to enter modification ValueTuner
	ListSelect skillGroupLeftLast = null;
	Class<? extends SkillBase> skillTuned = null; 

	/**
	 * How many attribute points are left.
	 */
	int attrPointsLeft = 0;
	
	/**
	 * How many attribute points are left.
	 */
	int skillPointsLeft = 0;

	public org.jcrpg.world.ai.abs.attribute.Attributes attributeValues = new FantasyAttributes(false);
	public org.jcrpg.world.ai.abs.attribute.Attributes lowestAttrValues = new FantasyAttributes(false);
	
	public CharacterLevelingWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.9f*core.getDisplay().getWidth(), 1.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.10f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);

	    	//pictureSelect = new PictureSelect("picture_select", this, page0, 0.78f,0.25f,0.15f,0.2f,600f);

	    	new TextLabel("",this,page0, 0.40f, 0.058f, 0.3f, 0.06f,400f,"Character Leveling",false);
    		characterName = new TextLabel("member", this,page0, 0.50f,0.11f,0.3f,0.06f,450f,"",true);
	    	//addInput(0,characterName);
	    	
	    	professionSelect = new ListSelect("profession", this,page0, 0.55f,0.2f,0.3f,0.06f,600f,new String[0],new String[0],null,null);

	    	new TextLabel("level",this,page0,0.45f,0.25f,0.15f,0.04f,600f, "Level", false);
	    	level = new ValueTuner("level",this,page0, 0.47f,0.28f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("health",this,page0,0.45f,0.31f,0.15f,0.04f,600f, "Health", false);
	    	health = new ValueTuner("health",this,page0, 0.47f,0.34f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("stamina",this,page0,0.61f,0.31f,0.15f,0.04f,600f, "Stamina", false);
	    	stamina = new ValueTuner("stamina",this,page0, 0.63f,0.34f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("morale",this,page0,0.45f,0.39f,0.15f,0.04f,600f, "Morale", false);
	    	morale = new ValueTuner("morale",this,page0, 0.47f,0.42f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("sanity",this,page0,0.61f,0.39f,0.15f,0.04f,600f, "Sanity", false);
	    	sanity = new ValueTuner("sanity",this,page0, 0.63f,0.42f,0.15f,0.04f,600f,10,0,100,1);

	    	new TextLabel("mana",this,page0,0.45f,0.46f,0.15f,0.04f,600f, "Mana", false);
	    	mana = new ValueTuner("mana",this,page0, 0.47f,0.49f,0.15f,0.04f,600f,10,0,100,1);

	    	
	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes.short."+s);
	    		System.out.println("TEXT" +text);
	    		new TextLabel(s+"_label",this,page0,0.149f,0.2f+0.05f*posY,0.15f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,page0, 0.317f,0.2f+0.05f*posY,0.15f,0.04f,600f,10,0,100,1);
	    		attributeTuners.put(s, v);
	    		addInput(0,v);
	    		posY++;
	    	}

	    	attrPointsLeftLabel = new TextLabel("",this,page0, 0.63f, 0.50f, 0.2f, 0.07f,500f,attrPointsLeft+" Attr. Points left.",false); 


	    	//addInput(1,pictureSelect);

	    	posY = 0; 
	    	for (String groupId : SkillGroups.orderedGroups)
	    	{
	    		String groupName = Language.v("skillgroups."+groupId);
	    		new TextLabel(groupId+"_label",this,page0,0.149f,0.6f+0.05f*posY,0.15f,0.04f,600f, groupName, false);
	    		ArrayList<String> skillIds = new ArrayList<String>();
	    		ArrayList<String> skillTexts = new ArrayList<String>();
	    		ArrayList<Object> skillObjects = new ArrayList<Object>();
	    		ListSelect sel = new ListSelect("skillgroup", this,page0, 0.39f,0.6f+0.05f*posY,0.3f,0.04f,600f,skillIds.toArray(new String[0]),skillTexts.toArray(new String[0]),skillObjects.toArray(new Object[0]),null,null);
	    		posY++;
	    		skillSelects.put(groupId, sel);
	    		addInput(0,sel);
	    	}
	    	skillPointsLeftLabel = new TextLabel("",this,page0, 0.63f, 0.6f, 0.2f, 0.07f,500f,skillPointsLeft+" Skill Points left.",false); 
	    	
	    	skillText = new TextLabel("",this,page0, 0.61f, 0.65f, 0.3f, 0.06f,600f,Language.v("partySetup.selectSkill"),false); 
	    	skillValueTuner = new ValueTuner("skill_tuner",this,page0, 0.68f,0.7f,0.15f,0.04f,600f,0,0,100,1);
	    	addInput(0,skillValueTuner);
	    	skillValueTuner.setEnabled(false);
	    	
	    	//addInput(0,professionSelect); // TODO multi-profession?
	    	
	    	finishedButton = new TextButton("ready",this,page0, 0.70f, 0.8f, 0.2f, 0.07f,400f,Language.v("partySetup.ready"));
	    	
	    	addInput(0, finishedButton);

	    	addPage(0, page0);
		} catch (Exception ex)
		{	
			ex.printStackTrace();
		}
		
	}

	public PartyInstance party = null;
	public PersistentMemberInstance member = null;
	public Profession profession = null;
	public SkillContainer lowestSkillValues = null;
	
	public void setPageData(PartyInstance party, PersistentMemberInstance member)
	{
		this.party = party;
		this.member = member;
		profession = core.gameState.getCharCreationRules().profInstances.get(member.description.professions.get(0));
		attrPointsLeft = GameLogicConstants.ATTRIBUTE_POINTS_TO_USE_ON_LEVELING;
		skillPointsLeft = GameLogicConstants.SKILL_POINTS_TO_USE_ON_LEVELING;
		lowestSkillValues = member.description.memberSkills.copy();
	}

	public int lastUpdatedLivingPartySize = 0;
	public void updateToParty()
	{
		characterName.text = member.getName();
		characterName.setUpdated(true);
		characterName.deactivate();
		updateToMemberInstance(member);
	}

	public void updateToMemberInstance(EntityMemberInstance instance)
	{
		attrPointsLeftLabel.text = attrPointsLeft + " points left.";
		attrPointsLeftLabel.activate();
		skillPointsLeftLabel.text = skillPointsLeft + " points left.";
		skillPointsLeftLabel.activate();
		
		for (String id: FantasyAttributes.attributeName) {
			//System.out.println("ID = "+id+" = "+attributeValues.attributes.get(id));
			ValueTuner v = attributeTuners.get(id);
			v.value = ((MemberPerson)instance.description).attributes.getAttribute(id);
			attributeValues.setAttribute(id, ((MemberPerson)instance.description).attributes.getAttribute(id));
			lowestAttrValues.setAttribute(id, ((MemberPerson)instance.description).attributes.getAttribute(id));
			v.text = ""+v.value;
			v.deactivate();
		}
		
		
    	for (String groupId : SkillGroups.orderedGroups)
    	{
    		ArrayList<String> skillIds = new ArrayList<String>();
    		ArrayList<String> skillTexts = new ArrayList<String>();
    		ArrayList<Object> skillObjects = new ArrayList<Object>();
    		int counter = 0;
    		for (Class<? extends SkillBase> skill:SkillGroups.groupedSkills.get(groupId))
    		{
    			if (instance.description.memberSkills.skills.containsKey(skill)) {
    				int level = instance.description.memberSkills.skills.get(skill).level;
	    			String id = groupId+"."+counter;
	    			String text = skill.getSimpleName();
	    			int modifier = 1;
	    			try {
	    				modifier = profession.skillLearnModifier.getMultiplier(skill);
	    			} catch (Exception ex)
	    			{}
	    			text = Language.v("skills."+text)+" ("+modifier+"x): "+level;
	    			skillIds.add(id);
	    			skillTexts.add(text);
	    			skillObjects.add(skill);
    			}
    			counter++;
    		}
    		ListSelect sel = skillSelects.get(groupId);
    		sel.ids = skillIds.toArray(new String[0]);
    		sel.texts = skillTexts.toArray(new String[0]);
    		sel.objects = skillObjects.toArray(new Object[0]);
    		sel.setUpdated(true);
    		sel.deactivate();
    	}
		
		//instance.memberState.
    	
		try {

			if (currentPic!=null) currentPic.removeFromParent();
			System.out.println("--- PIC: "+((MemberPerson)instance.description).getPicturePath());
			currentPic = UIImageCache.getImage(((MemberPerson)instance.description).getPicturePath(), false, 75f);
			currentPic.setLocalTranslation(1.6f*core.getDisplay().getWidth()/2,1.55f*core.getDisplay().getHeight()/2,0);
			currentPic.setLocalScale(1f* (core.getDisplay().getWidth()/640f));
			page0.attachChild(currentPic);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		{
			int counter = 0;
			int profSelSize = instance.description.professions.size();
			String[] ids = new String[profSelSize];
			String[] texts = new String[profSelSize];
			Object[] objects = new Object[profSelSize];
			for (Class<?extends Profession> p:instance.description.professions)
			{
				ids[counter] = ""+counter;
				texts[counter] = p.getSimpleName();
				objects[counter] = p;
				counter++;
			}
			professionSelect.ids = ids;
			professionSelect.texts = texts;
			professionSelect.objects = objects;
			professionSelect.setUpdated(true);
			professionSelect.deactivate();
		}
    	
    	
    	level.text = ""+instance.memberState.level;
    	level.deactivate();
		
    	updateAttributePoints();

	}
	
	public void updateAttributePoints()
	{
		health.text = ""+member.memberState.healthPoint+ "/"+member.memberState.maxHealthPoint;
		health.deactivate();

		stamina.text = ""+member.memberState.staminaPoint+ "/"+member.memberState.maxStaminaPoint;
		stamina.deactivate();

		morale.text = ""+member.memberState.moralePoint+ "/"+member.memberState.maxMoralePoint;
		morale.deactivate();

		sanity.text = ""+member.memberState.sanityPoint+ "/"+member.memberState.maxSanityPoint;
		sanity.deactivate();

		mana.text = ""+member.memberState.manaPoint+ "/"+member.memberState.maxManaPoint;
		mana.deactivate();

	}
	
	@Override
	public boolean inputLeft(InputBase base, String message)
	{
		if (base.equals(skillValueTuner))
		{
			Class<? extends SkillBase> skill = (Class<? extends SkillBase>)skillValueTuner.tunedObject;
			member.setSkillLevel(skill, skillValueTuner.value);
			skillGroupLeftLast.setUpdated(true);
			int id = 0;
			for (Object o:skillGroupLeftLast.objects)
			{
				if (o.equals(skillValueTuner.tunedObject))
				{
					// this is the id that's modified:
					int modifier = profession.skillLearnModifier.getMultiplier(skill);
					skillGroupLeftLast.texts[id] = Language.v("skills."+((Class)o).getSimpleName())+" ("+modifier+"x): "+skillValueTuner.value;
				}
				id++;
			}
		}
		return true;		
	}
	
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==finishedButton)
		{
			if (skillPointsLeft==0 && attrPointsLeft==0) {
				member.updateAfterLeveling();
				toggle();
			}
			return true;
		}
		if (base.equals(skillValueTuner))
		{
			if (message.equals("enter")) {
				// ############## SKILL VALUE SET, feed it back into listSelect
				Class<? extends SkillBase> skill = (Class<? extends SkillBase>)skillValueTuner.tunedObject;
				member.setSkillLevel(skill, skillValueTuner.value);
				skillGroupLeftLast.setUpdated(true);
				int id = 0;
				for (Object o:skillGroupLeftLast.objects)
				{
					if (o.equals(skillValueTuner.tunedObject))
					{
						// this is the id that's modified:
						int modifier = profession.skillLearnModifier.getMultiplier(skill);
						skillGroupLeftLast.texts[id] = Language.v("skills."+((Class)o).getSimpleName())+" ("+modifier+"x): "+skillValueTuner.value;
					}
					id++;
				}
				setSelected(skillGroupLeftLast);
			} else 
			{
				Class<? extends SkillBase> skill = (Class<? extends SkillBase>)skillValueTuner.tunedObject;
				// ################ Tuning the skill level value, modifying pointsLeft textlabel
				int val = lowestSkillValues.getSkillLevel(skill, null);
				ValueTuner v = (ValueTuner)base;
				if (v.value<val) return false; // minimum value shouldn't be crossed.
				if (message.equals("lookLeft"))
				{
					skillPointsLeft++;
				} else
				if (message.equals("lookRight"))
				{
					skillPointsLeft--;
				}
				if (skillPointsLeft<0)
				{
					skillPointsLeft = 0;
					return false;
				} else
				{
					skillPointsLeftLabel.text = skillPointsLeft + " points left.";
					skillPointsLeftLabel.activate();
					return true;
				}
			}
		} else
		if (skillSelects.values().contains(base))
		{
			//#################### MODIFYING A SKILL with skillValueTuner... 
			ListSelect select = (ListSelect)base;
			if (select.ids.length==0) return true;
			skillGroupLeftLast = select;
			//String id = select.ids[select.getSelection()];
			Class<?extends SkillBase> skill = (Class<? extends SkillBase>)select.getSelectedObject();
			skillTuned = skill;
			//String group = id.substring(0,id.indexOf('.'));
			//int count = Integer.parseInt(id.substring(id.indexOf('.')+1));
			//System.out.println("GROUP = "+group+ " - "+count);
			skillValueTuner.setEnabled(true);
			skillValueTuner.value = member.getSkillLevel(skill);
			skillValueTuner.setUpdated(true);
			skillValueTuner.tunedObject = skill;
			int modifier = 1;
			try {
				modifier = profession.skillLearnModifier.getMultiplier(skill);
			} catch (Exception ex)
			{	
			}
			skillValueTuner.setStep(modifier);
			skillText.text = Language.v("skills."+skill.getSimpleName())+" ("+modifier+"x):";
			skillText.activate();
			setSelected(skillValueTuner);
		} else		
		if (base instanceof ValueTuner) {
			int count = 0;
			for (ValueTuner v:attributeTuners.values())
			{
				if (base.equals(v))
				{
					int val = lowestAttrValues.getAttribute(v.id); // cannot go under original attributes for race
					if (v.value<val) return false;
					if (message.equals("lookLeft"))
					{
						attrPointsLeft++;
					} else
					if (message.equals("lookRight"))
					{
						attrPointsLeft--;
					}
					if (attrPointsLeft<0)
					{
						attrPointsLeft = 0;
						return false;
					} else
					{
						attrPointsLeftLabel.text = attrPointsLeft + " points left.";
						attrPointsLeftLabel.activate();
						for (String id:attributeTuners.keySet())
						{
							ValueTuner vTuner = attributeTuners.get(id);
							int value = vTuner.getSelection();
							attributeValues.setAttribute(id, value);
							((MemberPerson)member.description).attributes.setAttribute(id, value);
							System.out.println("CHARACTER ATTRIBUTES _ "+id + " = "+value);
						}
						member.memberState.recalculateMaximums(member,false);
						updateAttributePoints();
						return true;
					}
						
				}
				count++;
			}
		}
		return super.inputUsed(base, message);
	}
	
	@Override
	public void hide() {
		super.hide();
		J3DCore.getInstance().gameState.levelingInProgress = false;
		core.uiBase.hud.characters.show();
	}
	@Override
	public void show() {
		super.show();
		updateToParty();
		core.uiBase.hud.characters.hide();
	}
	
}
