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

package org.jcrpg.ui.window.interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.TextEntry;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListMultiSelect;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnit;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.skill.EncounterSkill;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Player-AI or forced AI-player encounter decision maker window. Before  a turn based combat or social rivalry begins
 * player can interact with the met group if it isn't too much hostile towards the player. :-)
 * @author pali
 *
 */
public class EncounterWindow extends PagedInputWindow {

	/**
	 * Page where interaction with the encountered is going on, initiated.
	 */
	Node page0 = new Node();

	ListSelect memberSelect;
	ListSelect skillSelect;
	ListSelect skillActFormSelect;
	ListMultiSelect groupSelect;
	TextButton leave;
	TextButton ok;
	TextLabel description;
	
	
	public EncounterWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1_trans.png", 0.75f*core.getDisplay().getWidth(), 1.2f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.38f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);
	    	sQuad = new SharedMesh("",hudQuad);
	    	//page1.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.40f, 0.044f, 0.3f, 0.06f,400f,Language.v("encounterWindow.header"),false);
	    	new TextLabel("",this,page0, 0.23f, 0.075f, 0.3f, 0.06f,600f,"You are facing the inevitable.",false);
	    	new TextLabel("",this,page0, 0.23f, 0.100f, 0.3f, 0.06f,600f,"You have to choose who will act and what.",false);
	    	 
	    	{
	    		memberSelect = new ListSelect("member", this,page0, 0.30f,0.15f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	}
	    	addInput(0,memberSelect);
	    	
	    	{
	    		skillSelect = new ListSelect("skill", this,page0, 0.70f,0.15f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	}
	    	addInput(0,skillSelect);
	    	{
	    		skillActFormSelect = new ListSelect("skillActForm", this,page0, 0.30f,0.22f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	}
	    	addInput(0,skillActFormSelect);

	    	{
	    		groupSelect = new ListMultiSelect("group", this,page0, 0.70f, 0.58f,0.22f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,groupSelect);
	    	
	    	ok = new TextButton("ok",this,page0,0.50f, 0.49f, 0.18f, 0.06f,500f,Language.v("encounterWindow.ok"));
	    	description = new TextLabel("",this,page0, 0.20f, 0.54f, 0.4f, 0.06f,600f,"",false);
	    	new TextLabel("",this,page0, 0.60f, 0.54f, 0.3f, 0.06f,600f,"Use <>^V for selection.",false);
	    	new TextLabel("",this,page0, 0.60f, 0.58f, 0.3f, 0.06f,600f,"Use S if you are ready.",false);
	    	addInput(0,ok);
	    	leave = new TextButton("leave",this,page0,0.72f, 0.49f, 0.18f, 0.06f,500f,Language.v("encounterWindow.leave"),"L");
	    	addInput(0,leave);

	    	//new TextLabel("",this,page1, 0.4f, 0.045f, 0.3f, 0.06f,400f,"Interception",false); 
	    	//new ListSelect();
	    	
	    	addPage(0, page0);
	    	//addPage(1, page1);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		base.addEventHandler("enter", this);
	}
	
	public PartyInstance party;
	public EncounterInfo encountered;
	public boolean playerInitiated = false;
	
	public void setPageData(PartyInstance party, EncounterInfo encountered, boolean playerInitiated)
	{
		this.party = party;
		this.encountered = encountered;
		this.playerInitiated = playerInitiated;
	}

	
	@Override
	public void setupPage() {
		int listSize = 0;
		{
			EncounterInfo i = encountered;
			if (i.active) {
				//int fullSize = 0;
				for (EncounterUnit entityFragment:i.encountered.keySet())
				{
					if (entityFragment == party.theFragment) continue;
					int[] groupIds = i.encounteredGroupIds.get(entityFragment);
					for (int in:groupIds) {
						int size = entityFragment.getGroupSize(in);
						if (size>0) listSize++;
						//fullSize+=size;
					}
				}
				//if (fullSize>0)
					//listSize++;
			}
		}
		// groups
		{
			String[] ids = new String[listSize];
			Object[] objects = new Object[listSize];
			String[] texts = new String[listSize];
			int count = 0;
			System.out.println("ENC SIZE = "+listSize);
			{
				EncounterInfo i = encountered;
				int fragmentCount = 0;
				String text = count+"/";
				if (i.active) {
					for (EncounterUnit fragment:i.encountered.keySet())
					{
						if (fragment == party.theFragment) continue;
						System.out.println(fragment.getName()+" _ "+i.encountered.size());
						int fullSize = 0;
						fragmentCount++;
						int[] groupIds = i.encounteredGroupIds.get(fragment);
						for (int in:groupIds) {
							int size1 = fragment.getGroupSize(in);
							if (size1<1) continue;
							fullSize+=size1;
							text=fragmentCount+" ("+size1+") "+fragment.getName() + " " + in;
							ids[count] = ""+count;
							texts[count] = text;
							Object[] fragmentAndGroupId = new Object[2];
							fragmentAndGroupId[0] = fragment;
							fragmentAndGroupId[1] = in;
							objects[count] = fragmentAndGroupId;
							count++;
						}				
					}
				}
			}
			groupSelect.reset();
			groupSelect.ids = ids;
			groupSelect.objects = objects;
			groupSelect.texts = texts;
			groupSelect.setUpdated(true);
			groupSelect.deactivate();
		}
		// party memebers
		{
			String[] texts = new String[party.orderedParty.size()];
			Object[] objects = new Object[party.orderedParty.size()];
			String[] ids = new String[party.orderedParty.size()];
			int counter = 0;
			if (party!=null)
			for (EntityMemberInstance i:party.orderedParty)
			{
				String n = ((MemberPerson)i.description).foreName;
				objects[counter] = i;
				texts[counter] = n;
				ids[counter] = ""+counter;
				counter++;
			}
			memberSelect.reset();
			memberSelect.ids = ids;
			memberSelect.objects = objects;
			memberSelect.texts = texts;
			memberSelect.setUpdated(true);
			memberSelect.activate();
			//Collection<Class<? extends SkillBase>> skills = i.description.getCommonSkills().getSkillsOfType(InterceptionSkill.class);
		}
		inputChanged(groupSelect, "");
		super.setupPage();
	}	
	@Override
	public boolean inputChanged(InputBase base, String message) {
		// TODO Auto-generated method stub
		if (base == groupSelect)
		{
			Object[] fragmentAndGroupId = (Object[])groupSelect.getSelectedObject();
			int groupId = (Integer)fragmentAndGroupId[1];
			EncounterUnit fragment = (EncounterUnit)fragmentAndGroupId[0];
			int size = fragment.getGroupSize(groupId);
			description.text = "Qual.:"+fragment.getLevel();
			description.text += " Relation: "+fragment.getRelationLevel(party.theFragment);
			description.text += " Size: "+size+"/"+fragment.getSize();
			//description.setUpdated(true);
			description.activate();
			return true;
		}
		return false;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public HashMap<EntityMemberInstance, SkillBase> hmMemberSelectedSkill = new HashMap<EntityMemberInstance, SkillBase>();

	@Override
	public boolean inputLeft(InputBase base, String message) {
		if (base==memberSelect)
		{
			EntityMemberInstance i = (EntityMemberInstance)memberSelect.getSelectedObject();
			
			Collection<Class<? extends SkillBase>> skills = i.description.getCommonSkills().getSkillsOfType(EncounterSkill.class);
			if (skills==null) skills = new HashSet<Class<? extends SkillBase>>();
			String[] texts = new String[skills.size()];
			Object[] objects = new Object[skills.size()];
			String[] ids = new String[skills.size()];
			
			int counter = 0;
			int selected = 0;
			for (Class<?extends SkillBase> skill:skills)
			{
				String text = Language.v("skills."+skill.getSimpleName())+" ("+i.description.getCommonSkills().getSkillLevel(skill,null)+")";
				texts[counter]=text;
				ids[counter]=""+counter;
				SkillBase b = (SkillBase)SkillGroups.skillBaseInstances.get(skill);
				objects[counter]=b;
				System.out.println("--- "+skill);
				if (hmMemberSelectedSkill.get(i)!=null && hmMemberSelectedSkill.get(i).getClass() == b.getClass())
				{
					System.out.println("### FOUND SKILL");
					selected = counter;
				}
				counter++;
			}
			skillSelect.ids = ids;
			skillSelect.texts = texts;
			skillSelect.objects = objects;
			skillSelect.setUpdated(true);
			skillSelect.deactivate();
			skillSelect.setSelected(selected);
			return true;
		} else
		if (base==skillSelect)
		{
			hmMemberSelectedSkill.put((EntityMemberInstance)memberSelect.getSelectedObject(), (SkillBase)skillSelect.getSelectedObject());
			
			EntityMemberInstance i = (EntityMemberInstance)memberSelect.getSelectedObject();
			SkillBase s = (SkillBase)skillSelect.getSelectedObject();
			SkillInstance skillInstance = i.description.commonSkills.skills.get(s.getClass());
			ArrayList<Class<?extends SkillActForm>> forms = skillInstance.aquiredActForms;
			String[] texts = new String[forms.size()];
			Object[] objects = new Object[forms.size()];
			String[] ids = new String[forms.size()];
			int counter = 0;
			for (Class<? extends SkillActForm> form:skillInstance.aquiredActForms)
			{
				ids[counter] = ""+counter;
				texts[counter] = form.getSimpleName();
				objects[counter] = form;				    
				counter++;
			}
			skillActFormSelect.ids = ids;
			skillActFormSelect.texts = texts;
			skillActFormSelect.objects = objects;
			skillActFormSelect.setUpdated(true);
			skillActFormSelect.deactivate();
			skillActFormSelect.setSelected(0);			
			return true;
		}

		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==leave)
		{
			if (core.gameState.gameLogic.encounterLogic.checkLeaveEncounterPhase(party.theFragment, encountered))
			{
				toggle();
				core.uiBase.hud.mainBox.addEntry("Your party is able to leave the encounter.");
				core.uiBase.hud.mainBox.addEntry(new TextEntry("Encounters finished", ColorRGBA.yellow));
				core.gameState.gameLogic.endPlayerEncounters();
				core.gameState.engine.turnFinishedForPlayer();
			} else
			{
				core.uiBase.hud.mainBox.addEntry("Your party couldn't leave the encounter.");
			}
			return true;
		}
		if (base==ok)
		{
			//
			//
			
			EntityMemberInstance i = (EntityMemberInstance)memberSelect.getSelectedObject();
			SkillBase b = (SkillBase)skillSelect.getSelectedObject();
			SkillInstance s = i.description.getCommonSkills().skills.get(b.getClass()); //TODO modifier in EntityMemberInstance!!
			toggle(); // TODO use EncounterInfo internal list instead of encounterInfos selected...
			core.gameState.gameLogic.encounterLogic.doEncounterRound(i, s, encountered);
			
			return true;
		}
		return false;
	}

}
