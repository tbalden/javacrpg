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

import org.jcrpg.game.EncounterLogic;
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
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.skill.EncounterSkill;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Social rivalry or combat with turn based acting on behalf of player and AI, decisions of Player are done through this window.
 * Player should be able to select skills to use, select groups to use the skill on, select way of using a skill, etc.
 * @author pali
 *
 */
public class TurnActWindow extends PagedInputWindow {

	/**
	 * Page where selecting what to do in a turn is being done.
	 */
	Node page0 = new Node();

	ListSelect memberSelect;
	ListSelect skillSelect;
	ListMultiSelect groupSelect;
	TextButton leave;
	TextButton ok;
	
	TextLabel header, desc;
	
	
	public TurnActWindow (UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.8f*core.getDisplay().getWidth(), 0.8f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.58f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);
	    	sQuad = new SharedMesh("",hudQuad);
	    	//page1.attachChild(sQuad);
	    	
	    	header = new TextLabel("",this,page0, 0.40f, 0.044f, 0.3f, 0.06f,400f,Language.v("turnActWindow.header"),false);
	    	desc = new TextLabel("",this,page0, 0.23f, 0.075f, 0.3f, 0.06f,600f,"You are facing the inevitable.",false);
	    	new TextLabel("",this,page0, 0.23f, 0.100f, 0.3f, 0.06f,600f,"You have to choose what skills to use.",false);
	    	 
	    	{
	    		memberSelect = new ListSelect("member", this,page0, 0.30f,0.15f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	}
	    	addInput(0,memberSelect);
	    	
	    	{
	    		skillSelect = new ListSelect("skill", this,page0, 0.70f,0.15f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	}
	    	addInput(0,skillSelect);

	    	{
	    		groupSelect = new ListMultiSelect("group", this,page0, 0.30f, 0.18f,0.22f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,groupSelect);
	    	
	    	ok = new TextButton("ok",this,page0,0.50f, 0.29f, 0.18f, 0.06f,500f,Language.v("encounterWindow.ok"));
	    	new TextLabel("",this,page0, 0.60f, 0.34f, 0.3f, 0.06f,600f,"Use <>^V for selection.",false);
	    	new TextLabel("",this,page0, 0.60f, 0.38f, 0.3f, 0.06f,600f,"Use S if you are ready.",false);
	    	addInput(0,ok);
	    	leave = new TextButton("leave",this,page0,0.72f, 0.29f, 0.18f, 0.06f,500f,Language.v("encounterWindow.leave"),"L");
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
	public ArrayList<EncounterInfo> encountered;
	public int turnActType;
	
	String langPostfix = "";
	
	public void setPageData(int turnActType, PartyInstance party, ArrayList<EncounterInfo> encountered)
	{
		if (turnActType == EncounterLogic.ENCOUTNER_PHASE_RESULT_COMBAT)
		{
			langPostfix = "combat";
		} else
		{
			langPostfix = "social";
		}
		header.text = Language.v("turnActWindow.header."+langPostfix);
		header.activate();
		desc.text = Language.v("turnActWindow.desc."+langPostfix);
		desc.activate();
		this.party = party;
		this.encountered = encountered;
	}

	
	@Override
	public void setupPage() {
		int listSize = 0;
		for (EncounterInfo i:encountered)
		{
			if (!i.active) continue;
			int fullSize = 0;
			for (EntityFragment entityFragment:i.encountered.keySet())
			{
				int[] groupIds = i.encounteredGroupIds.get(entityFragment);
				for (int in:groupIds) {
					int size = entityFragment.instance.getGroupSizes()[in];
					fullSize+=size;
				}
			}
			if (fullSize>0)
				listSize++;
		}
		// groups
		{
			String[] ids = new String[listSize];
			Object[] objects = new Object[listSize];
			String[] texts = new String[listSize];
			int count = 0;
			System.out.println("ENC SIZE = "+listSize);
			for (EncounterInfo i:encountered)
			{
				int size = 0;
				String text = count+"/";
				if (!i.active) continue;
				int fullSize = 0;
				for (EntityFragment fragment:i.encountered.keySet())
				{
					System.out.println(fragment.instance.description.getClass().getSimpleName()+" _ "+i.encountered.size());
					size++;
					int[] groupIds = i.encounteredGroupIds.get(fragment);
					for (int in:groupIds) {
						int size1 = fragment.instance.getGroupSizes()[in];
						fullSize+=size1;
					}				
					text+=size+" "+fragment.instance.description.getClass().getSimpleName()+" ";
				}
				if (fullSize==0) continue;
				ids[count] = ""+count;
				texts[count] = text;
				objects[count] = i;
				count++;
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
			}
			memberSelect.reset();
			memberSelect.ids = ids;
			memberSelect.objects = objects;
			memberSelect.texts = texts;
			memberSelect.setUpdated(true);
			memberSelect.activate();
			//Collection<Class<? extends SkillBase>> skills = i.description.getCommonSkills().getSkillsOfType(InterceptionSkill.class);
		}
		
		super.setupPage();
	}	
	@Override
	public boolean inputChanged(InputBase base, String message) {
		// TODO Auto-generated method stub
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
			
			int counter_2 = 0;
			int selected = 0;
			for (Class<?extends SkillBase> skill:skills)
			{
				String text = Language.v("skills."+skill.getSimpleName())+" ("+i.description.getCommonSkills().getSkillLevel(skill,null)+")";
				texts[counter_2]=text;
				ids[counter_2]=""+counter_2;
				SkillBase b = (SkillBase)SkillGroups.skillBaseInstances.get(skill);
				objects[counter_2]=b;
				System.out.println("--- "+skill);
				if (hmMemberSelectedSkill.get(i)!=null && hmMemberSelectedSkill.get(i).getClass() == b.getClass())
				{
					System.out.println("### FOUND SKILL");
					selected = counter_2;
				}
				counter_2++;
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
			
			//EntityMemberInstance i = (EntityMemberInstance)memberSelect.getSelectedObject();
			//SkillBase b = (SkillBase)skillSelect.getSelectedObject();
			//SkillInstance s = i.description.getCommonSkills().skills.get(b.getClass()); //TODO modifier in EntityMemberInstance!!
			//int result = core.gameState.gameLogic.encounterLogic.doEncounterTurn(i, s, encountered);
			
			//if (result==EncounterLogic.ENCOUTNER_PHASE_RESULT_COMBAT || result==EncounterLogic.ENCOUTNER_PHASE_RESULT_SOCIAL_RIVALRY)
			{
				//core.gameState.gameLogic.newTurnPhase(encountered, Ecology.PHASE_TURNACT, true);
			} 
			//else
			{
				core.uiBase.hud.mainBox.addEntry("Next turn comes...");
			}
			
			return true;
		}
		return false;
	}

}
