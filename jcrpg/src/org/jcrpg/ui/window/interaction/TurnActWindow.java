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
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.abs.skill.SkillActForm;
import org.jcrpg.world.ai.abs.skill.SkillBase;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.abs.skill.SkillInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.object.ObjInstance;

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

	ArrayList<TextLabel> memberNames = new ArrayList<TextLabel>();
	ArrayList<ListSelect> skillSelectors = new ArrayList<ListSelect>();
	ArrayList<ListSelect> skillActFormSelectors = new ArrayList<ListSelect>();
	ArrayList<ListSelect> groupSelectors = new ArrayList<ListSelect>();
	ArrayList<ListSelect> inventorySelectors = new ArrayList<ListSelect>();

	
	public TurnActWindow (UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.png", 0.85f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.18f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);
	    	//page1.attachChild(sQuad);
	    	
	    	header = new TextLabel("",this,page0, 0.40f, 0.044f, 0.3f, 0.06f,400f,Language.v("turnActWindow.header"),false);
	    	desc = new TextLabel("",this,page0, 0.23f, 0.075f, 0.3f, 0.06f,600f,"You are facing the inevitable.",false);
	    	new TextLabel("",this,page0, 0.23f, 0.100f, 0.3f, 0.06f,600f,"You have to choose what skills to use.",false);
	    	 

	    	float sizeSelect = 0.1f;
	    	for (int i=0; i<6; i++)
	    	{
	    		skillSelectors.add(new ListSelect("skill"+i, this,page0, 0.38f,0.15f+sizeSelect*i,0.3f,0.04f,600f,new String[0],new String[0],null,null));
	    		skillActFormSelectors.add(new ListSelect("actForm"+i, this,page0, 0.70f,0.15f+sizeSelect*i,0.3f,0.04f,600f,new String[0],new String[0],null,null));
	    		groupSelectors.add(new ListSelect("group"+i, this,page0, 0.38f,0.20f+sizeSelect*i,0.3f,0.04f,600f,new String[0],new String[0],null,null));
	    		inventorySelectors.add(new ListSelect("inv"+i, this,page0, 0.70f,0.20f+sizeSelect*i,0.3f,0.04f,600f,new String[0],new String[0],null,null));
	    		memberNames.add(new TextLabel("name"+i,this,page0,0.15f,0.15f+sizeSelect*i,0.3f,0.04f,600f,"",false));
	    		addInput(0,skillSelectors.get(i));
	    		addInput(0,skillActFormSelectors.get(i));
	    		addInput(0,groupSelectors.get(i));
	    		addInput(0,inventorySelectors.get(i));
	    	}
	    	
	    	
	    	ok = new TextButton("ok",this,page0,0.24f, 0.77f, 0.18f, 0.06f,500f,Language.v("turnActWindow.ok"),"S");
	    	new TextLabel("",this,page0, 0.60f, 0.74f, 0.3f, 0.06f,600f,"Use <>^V for selection.",false);
	    	new TextLabel("",this,page0, 0.60f, 0.78f, 0.3f, 0.06f,600f,"Use S if you are ready.",false);
	    	addInput(0,ok);
	    	leave = new TextButton("leave",this,page0,0.46f, 0.77f, 0.18f, 0.06f,500f,Language.v("turnActWindow.leave"),"L");
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
	public boolean playerInitiated = false;
	public int turnActType;
	
	String langPostfix = "";
	
	boolean combat = false;
	
	public void setPageData(int turnActType, PartyInstance party, ArrayList<EncounterInfo> encountered, boolean playerInitiated)
	{
		this.playerInitiated = playerInitiated;
		if (turnActType == EncounterLogic.ENCOUTNER_PHASE_RESULT_COMBAT)
		{
			langPostfix = "combat";
			combat = true;
		} else
		{
			langPostfix = "social";
			combat = false;
		}
		header.text = Language.v("turnActWindow.header."+langPostfix);
		header.activate();
		desc.text = Language.v("turnActWindow.desc."+langPostfix);
		desc.activate();
		this.party = party;
		this.encountered = encountered;
	}

	
	ArrayList<Class<?extends SkillBase>> tempFilteredSkills = new ArrayList<Class<? extends SkillBase>>();
	public void updateToParty()
	{
		int counter = 0;
		if (party!=null)
		for (EntityMemberInstance i:party.orderedParty)
		{
			if (i!=null) {
				ListSelect select = skillSelectors.get(counter);
				select.reattach();
				select.subject = i;				
				Collection<Class<? extends SkillBase>> skills = i.description.getCommonSkills().getSkillsOfType(combat?Ecology.PHASE_TURNACT_COMBAT:Ecology.PHASE_TURNACT_SOCIAL_RIVALRY);
				if (skills==null) skills = new HashSet<Class<? extends SkillBase>>();
				
				// filtering unusable skills
				tempFilteredSkills.clear();
				for (Class<?extends SkillBase> skill:skills)
				{
					if (SkillGroups.skillBaseInstances.get(skill).needsInventoryItem)
					{
						if (!i.inventory.hasInInventoryForSkillAndLevel(i.description.getCommonSkills().skills.get(skill))) continue;
					}
					tempFilteredSkills.add(skill);
				}
				skills = tempFilteredSkills;
				
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
					if (i.behaviorSkill!=null && i.behaviorSkill.getClass() == b.getClass())
					{
						System.out.println("### FOUND SKILL");
						selected = counter_2;
					}
					counter_2++;
				}
				select.ids = ids;
				select.texts = texts;
				select.objects = objects;
				select.setUpdated(true);
				select.deactivate();
				select.setSelected(selected);
				memberNames.get(counter).text = ((MemberPerson)i.description).foreName;
				memberNames.get(counter).activate();
			}
			counter++;
		}
		// unnecessary selects detach...
		for (int i=counter; i<6; i++)
		{
			ListSelect select = skillSelectors.get(i);
			select.detach();
			ListSelect mselect = groupSelectors.get(i);
			mselect.detach();
			select = skillActFormSelectors.get(i);
			select.detach();
			select = inventorySelectors.get(i);
			select.detach();
		}
	}
	
	
	@Override
	public void setupPage() {
		int listSize = 0;
		for (EncounterInfo i:encountered)
		{
			if (!i.active) continue;
			//int fullSize = 0;
			for (EntityFragment entityFragment:i.encountered.keySet())
			{
				//if (entityFragment == party.theFragment) continue;
				int[] groupIds = i.encounteredGroupIds.get(entityFragment);
				for (int in:groupIds) {
					int size = entityFragment.instance.getGroupSizes()[in];
					if (size>0) listSize++;
					//fullSize+=size;
				}
			}
			//if (fullSize>0)
				//listSize++;
		}
		// groups
		for (ListSelect groupSelect:groupSelectors)
		{
			String[] ids = new String[listSize];
			Object[] objects = new Object[listSize];
			String[] texts = new String[listSize];
			int count = 0;
			System.out.println("ENC SIZE = "+listSize);
			for (EncounterInfo i:encountered)
			{
				int fragmentCount = 0;
				String text = count+"/";
				if (!i.active) continue;
				for (EntityFragment fragment:i.encountered.keySet())
				{
					//if (fragment == party.theFragment) continue;
					System.out.println(fragment.instance.description.getClass().getSimpleName()+" _ "+i.encountered.size());
					int fullSize = 0;
					fragmentCount++;
					int[] groupIds = i.encounteredGroupIds.get(fragment);
					for (int in:groupIds) {
						int size1 = fragment.instance.getGroupSizes()[in];
						if (size1<1) continue;
						fullSize+=size1;
						text=fragmentCount+" ("+size1+") "+fragment.instance.description.getClass().getSimpleName() + " " + in;
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
			groupSelect.reset();
			groupSelect.ids = ids;
			groupSelect.objects = objects;
			groupSelect.texts = texts;
			groupSelect.setUpdated(true);
			groupSelect.deactivate();
		}
		updateToParty();
		
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
		if (skillSelectors.contains(base))
		{
			ListSelect skillSelect = (ListSelect)base;
			EntityMemberInstance i = (EntityMemberInstance)skillSelect.subject;
			int index = skillSelectors.indexOf(skillSelect);
			ListSelect skillActFormSelect = skillActFormSelectors.get(index);			
			ListSelect inventorySelect = inventorySelectors.get(index);
			SkillBase s = (SkillBase)skillSelect.getSelectedObject();
			
			SkillInstance skillInstance = i.description.commonSkills.skills.get(s.getClass());
			{
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
			}
			if (s.needsInventoryItem)
			{
				
				ArrayList<ObjInstance> objInstances = i.inventory.getObjectsForSkillInInventory(i.description.getCommonSkills().skills.get(s.getClass()));
				String[] texts = new String[objInstances.size()];
				Object[] objects = new Object[objInstances.size()];
				String[] ids = new String[objInstances.size()];
				int counter = 0;
				for (ObjInstance objInstance:objInstances)
				{
					ids[counter] = ""+counter;
					texts[counter] = objInstance.description.getClass().getSimpleName();
					objects[counter] = objInstance;				    
					counter++;
				}
				inventorySelect.ids = ids;
				inventorySelect.texts = texts;
				inventorySelect.objects = objects;
				inventorySelect.setUpdated(true);
				inventorySelect.deactivate();
				inventorySelect.setSelected(0);
			} else
			{
				inventorySelect.ids = new String[0];
				inventorySelect.texts = new String[0];
				inventorySelect.setUpdated(true);
				inventorySelect.deactivate();
			}
			return true;
		}
		return false;
	}
	
	public class TurnActPlayerChoiceInfo
	{
		HashMap<EntityMemberInstance, SkillBase> memberToSkill = new HashMap<EntityMemberInstance, SkillBase>();
		HashMap<EntityMemberInstance, Class<?extends SkillActForm>> memberToSkillActForm = new HashMap<EntityMemberInstance, Class<? extends SkillActForm>>();
		HashMap<EntityMemberInstance, Object[]> memberToFragmentAndGroupId = new HashMap<EntityMemberInstance, Object[]>();
	}
	public TurnActPlayerChoiceInfo info = new TurnActPlayerChoiceInfo();
	
	@SuppressWarnings("unchecked")
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
			int counter = 0;
			info.memberToFragmentAndGroupId.clear();
			info.memberToSkill.clear();
			info.memberToSkillActForm.clear();
			for (ListSelect s:skillSelectors)
			{
				if (s.isEnabled()) {
					EntityMemberInstance i = (EntityMemberInstance)s.subject;
					SkillBase sb = null;
					sb = (SkillBase)skillSelectors.get(counter).getSelectedObject();
					Class<?extends SkillActForm> f = null;
					f = (Class<?extends SkillActForm>)skillActFormSelectors.get(counter).getSelectedObject();
					Object[] fragmentAndGroupId = null;
					fragmentAndGroupId = (Object[])groupSelectors.get(counter).getSelectedObject();
					info.memberToSkill.put(i,sb);
					info.memberToSkillActForm.put(i,f);
					info.memberToFragmentAndGroupId.put(i,fragmentAndGroupId);
				}
				counter++;
			}
			
			core.uiBase.hud.mainBox.addEntry("Starting turn!");
			toggle();
			core.gameState.gameLogic.encounterLogic.doTurnActTurn(info,encountered);
			
			return true;
		}
		return false;
	}

}
