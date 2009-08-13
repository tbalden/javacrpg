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

package org.jcrpg.ui.window.interaction;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.UIImageCache;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListMultiSelect;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EncounterUnitData;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;
import org.jcrpg.world.object.InventoryListElement;
import org.jcrpg.world.object.ObjInstance;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Post encounter window.
 * @author pali
 *
 */
public class PostEncounterWindow extends PagedInputWindow {

	// selecting handled groups out of intercepted group, leaving non-interesting groups out of scope 
	Node page0 = new Node();

	//ListSelect encList;
	ListSelect groupList;
	ListMultiSelect inventoryList;
	ListSelect partyCharList;
	TextButton take;
	TextButton takeAll;
	TextButton leave;
	
	public PostEncounterWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.75f*core.getDisplay().getWidth(), 0.75f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.58f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	sQuad.setLocalTranslation(hudQuad.getLocalTranslation());
	    	page0.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.40f, 0.044f, 0.3f, 0.06f,400f,"VICTORY!",false);
	    	new TextLabel("",this,page0, 0.27f, 0.075f, 0.3f, 0.06f,600f,"You have prevailed in the encounter!",false);
	    	new TextLabel("",this,page0, 0.27f, 0.100f, 0.3f, 0.06f,600f,"Choose how you will check the remnants.",false);
	    	
	    	
	    	/*String[] ids = new String[] {"1","2"};
	    	String[] texts = new String[] {"One group encounter","Full scale encounter"};
	    	String[] objects = new String[] {"one","full"};	    	
	    	
	    	{
	    		encSelect = new ListSelect("encType", this,page0, 0.3f, 0.15f,0.3f,0.06f,600f,ids,texts,objects,null,null);
	    	}
	    	addInput(0,encSelect);*/

	    	{
	    		groupList= new ListSelect("group", this,page0, 0.3f, 0.15f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}

	    	addInput(0,groupList);
	    	
	    	{
	    		partyCharList = new ListSelect("partyCharList", this,page0, 0.7f, 0.15f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,partyCharList);

	    	{
	    		inventoryList = new ListMultiSelect("inventory", this,page0, 0.3f,0.18f,0.20f, 0.23f, 0.3f,0.06f,600f,new String[0],new String[0],new Object[0], new Quad[0],null,null);
	    	}
	    	addInput(0,inventoryList);

	    	take = new TextButton("ok",this,page0,0.55f, 0.23f, 0.18f, 0.05f,500f,Language.v("postEncounterWindow.take"),"T");
	    	takeAll = new TextButton("ok",this,page0,0.76f, 0.23f, 0.18f, 0.05f,500f,Language.v("postEncounterWindow.takeAll"),"A");
	    	leave = new TextButton("leave",this,page0,0.76f, 0.32f, 0.18f, 0.05f,500f,Language.v("postEncounterWindow.leave"),"L");
	    	new TextLabel("",this,page0, 0.22f, 0.28f, 0.3f, 0.06f,600f,"Use <> for selection.",false);
	    	new TextLabel("",this,page0, 0.18f, 0.32f, 0.3f, 0.06f,600f,"Select group if you want to loot it.",false);
	    	new TextLabel("",this,page0, 0.18f, 0.36f, 0.3f, 0.06f,600f,"The second list shows party members.",false);
	    	addInput(0,take);
	    	addInput(0,takeAll);
	    	addInput(0,leave);

	    	//new TextLabel("",this,page1, 0.4f, 0.045f, 0.3f, 0.06f,400f,"Interception",false); 
	    	//new ListSelect();
	    	
	    	addPage(0, page0);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		base.addEventHandler("enter", this);
	}
	
	public PartyInstance party;
	public EncounterInfo possibleGroups;
	
	private ArrayList<EntityMemberInstance> tmpFilteredMembers = new ArrayList<EntityMemberInstance>();

	/**
	 * This function fills up the required data fields for displaying the lists and elements of the page.
	 * After using this you can call toggle().
	 * @param party
	 * @param possibleEncounters
	 */
	public void setPageData(PartyInstance party, EncounterInfo possibleGroups)
	{
		this.party = party;
		this.possibleGroups = possibleGroups;
		
		int livingMembersCounter = 0;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (!i.memberState.isDead())
			{
				livingMembersCounter++;
				tmpFilteredMembers.add(i);
			}
		}
		String[] ids = new String[livingMembersCounter];
		Object[] objects = new Object[livingMembersCounter];
		String[] texts = new String[livingMembersCounter];
		int counter = 0;
		for (EntityMemberInstance i:tmpFilteredMembers)
		{
			ids[counter] = ""+counter;
			objects[counter] = i;
			texts[counter] = ((MemberPerson)i.description).getForeName();
			counter++;
		}
		partyCharList.reset();
		partyCharList.ids = ids;
		partyCharList.objects = objects;
		partyCharList.texts = texts;
		partyCharList.setUpdated(true);
		partyCharList.deactivate();
		
	}
	
	@Override
	public void hide() {
		super.hide();
	}
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void setupPage() {
		EncounterInfo i = possibleGroups;

		//if (!i.active) continue;
		ArrayList<EncounterUnitData> list = i.getEncounterUnitDataList(party.theFragment);
		ArrayList<EncounterUnitData> removed = i.getRemovedEncounterUnitDataList();
		
		ArrayList<EncounterUnitData> filtered = new ArrayList<EncounterUnitData>();
		for (EncounterUnitData d:removed)
		{
			if (d.partyMember || d.parent == party) continue;
			if (d.deadMembers==null || d.deadMembers.size()==0) continue;
			filtered.add(d);
		}
		list = filtered;

		// groups
		{
			String[] ids = new String[list.size()];
			Object[] objects = new Object[list.size()];
			String[] texts = new String[list.size()];
			int count = 0;
			if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("PostEncounterWindow ENC SIZE = "+list.size());
			for (EncounterUnitData data:list)
			{
				ids[count] = ""+count;
				texts[count] = data.name;
				objects[count] = data;
				count++;
			}
			groupList.reset();
			groupList.ids = ids;
			groupList.objects = objects;
			groupList.texts = texts;
			groupList.setUpdated(true);
			groupList.activate();
			groupList.deactivate();
		}
		inputChanged(groupList, "fake");
		super.setupPage();
	}

	@Override
	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		return false;
	}
	
	private void updateInventoryList()
	{
		EncounterUnitData data = (EncounterUnitData)groupList.getSelectedObject();
		ArrayList<InventoryListElement> listOfAll = new ArrayList<InventoryListElement>();
		if (data!=null && data.deadMembers!=null)
		for (EntityMemberInstance inst: data.deadMembers)
		{
			ArrayList<InventoryListElement> list  = inst.inventory.getInventoryList(false,false);
			listOfAll.addAll(list);
		}
		
		String[] ids = new String[listOfAll.size()];
		Object[] objects = new Object[listOfAll.size()];
		String[] texts = new String[listOfAll.size()];
		Quad[] icons = new Quad[listOfAll.size()];
		int count = 0;
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("PostEncounterWindow ENC SIZE = "+listOfAll.size());
		for (InventoryListElement invListElement:listOfAll)
		{
			ids[count] = ""+count;
			texts[count] = invListElement.getName();
			objects[count] = invListElement;
			try {
				icons[count] = UIImageCache.getImage(invListElement.description.getIconFilePath(), true,15f);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			count++;
		}
		inventoryList.reset();
		inventoryList.ids = ids;
		inventoryList.objects = objects;
		inventoryList.texts = texts;
		inventoryList.icons = icons;
		inventoryList.setUpdated(true);
		inventoryList.activate();
		inventoryList.deactivate();
	}

	@Override
	public boolean inputChanged(InputBase base, String message) {
		
		if (base==groupList)
		{
			updateInventoryList();
			groupList.activate();
			return true;
		}
		
		return false;
	}

	@Override
	public boolean inputEntered(InputBase base, String message) {
		return false;
	}

	@Override
	public boolean inputLeft(InputBase base, String message) {
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==take)
		{
			ArrayList<Object> os = inventoryList.getMultiSelection();
			for (Object o:os)
			{
				InventoryListElement i = (InventoryListElement)o;
				for (ObjInstance oi:i.objects)
				{
					i.inventory.remove(oi);
					EntityMemberInstance ch = (EntityMemberInstance)partyCharList.getSelectedObject();
					ch.inventory.add(oi);
				}
			}
			updateInventoryList();
			return true;
		} else
		if (base==takeAll)
		{
			Object[] os = inventoryList.objects;
			for (Object o:os)
			{
				if (o!=null)
				{
					InventoryListElement i = (InventoryListElement)o;
					for (ObjInstance oi:i.objects)
					{
						i.inventory.remove(oi);
						EntityMemberInstance ch = (EntityMemberInstance)partyCharList.getSelectedObject();
						ch.inventory.add(oi);
					}
				}
			}
			updateInventoryList();
			return true;
		} else
		if (base==leave)
		{
			core.gameState.gameLogic.inEncounter = false;
			core.gameState.engine.turnFinishedForPlayer();
			core.audioServer.stopIdOnAllChannels(core.audioServer.channels, "victory");
			toggle();
			return true;
		}
		return false;
	}

}
