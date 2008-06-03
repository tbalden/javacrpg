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

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EncounterInfo;
import org.jcrpg.world.ai.EntityFragments.EntityFragment;
import org.jcrpg.world.ai.player.PartyInstance;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Pre encounter decisions when player meets AI - not an AI forced encounter. Here player can
 * decide which groups to meet and which not of the possible ones. 
 * @author pali
 *
 */
public class PreEncounterWindow extends PagedInputWindow {

	// selecting handled groups out of intercepted group, leaving non-interesting groups out of scope 
	Node page0 = new Node();

	ListSelect encSelect;
	ListSelect groupList;
	TextButton ok;
	TextButton leave;
	
	public PreEncounterWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.75f*core.getDisplay().getWidth(), 0.75f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.58f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.40f, 0.044f, 0.3f, 0.06f,400f,"Interception",false);
	    	new TextLabel("",this,page0, 0.27f, 0.075f, 0.3f, 0.06f,600f,"You sense nearby groups of lifeforms.",false);
	    	new TextLabel("",this,page0, 0.27f, 0.100f, 0.3f, 0.06f,600f,"You may face one or all of the groups.",false);
	    	
	    	
	    	String[] ids = new String[] {"1","2"};
	    	String[] texts = new String[] {"One group encounter","Full scale encounter"};
	    	String[] objects = new String[] {"one","full"};	    	
	    	
	    	{
	    		encSelect = new ListSelect("encType", this,page0, 0.3f, 0.15f,0.3f,0.06f,600f,ids,texts,objects,null,null);
	    	}
	    	addInput(0,encSelect);

	    	{
	    		groupList= new ListSelect("group", this,page0, 0.7f, 0.15f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,groupList);

	    	ok = new TextButton("ok",this,page0,0.45f, 0.22f, 0.18f, 0.06f,500f,Language.v("preEncounterWindow.ok"));
	    	leave = new TextButton("leave",this,page0,0.68f, 0.22f, 0.18f, 0.06f,500f,Language.v("preEncounterWindow.leave"),"L");
	    	new TextLabel("",this,page0, 0.26f, 0.28f, 0.3f, 0.06f,600f,"Use <> for selection.",false);
	    	new TextLabel("",this,page0, 0.16f, 0.32f, 0.3f, 0.06f,600f,"Select group if you want to meet a single group.",false);
	    	new TextLabel("",this,page0, 0.22f, 0.36f, 0.3f, 0.06f,600f,"The second list shows nearby groups.",false);
	    	addInput(0,ok);
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
		int listSize = 0;
		//int fullSize = 0;
		for (EntityFragment entityFragment:i.encountered.keySet())
		{
			if (entityFragment == party.theFragment) continue;
			int[] groupIds = i.encounteredGroupIds.get(entityFragment);
			for (int in:groupIds) {
				int size = entityFragment.instance.getGroupSizes()[in];
				if (size>0) listSize++;
				//fullSize+=size;
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
				int size = 0;
				String text = count+"/";
				for (EntityFragment fragment:i.encountered.keySet())
				{
					if (fragment == party.theFragment) continue;
					System.out.println(fragment.instance.description.getClass().getSimpleName()+" _ "+i.encountered.size());
					int fullSize = 0;
					size++;
					int[] groupIds = i.encounteredGroupIds.get(fragment);
					for (int in:groupIds) {
						int size1 = fragment.instance.getGroupSizes()[in];
						fullSize+=size1;
						text=size+" ("+size1+") "+fragment.instance.description.getClass().getSimpleName() + " " + in;
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
			groupList.reset();
			groupList.ids = ids;
			groupList.objects = objects;
			groupList.texts = texts;
			groupList.setUpdated(true);
			groupList.activate();
		}
		super.setupPage();
	}

	@Override
	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		return false;
	}

	@Override
	public boolean inputChanged(InputBase base, String message) {
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
		if (base==ok)
		{
			EncounterInfo i = possibleGroups;
			
			if (encSelect.getSelectedObject().equals("one")) {			
				i = i.copy();
				i.encountered.clear();
				i.encounteredGroupIds.clear();
				Object[] fragmentAndGroupId = (Object[])groupList.getSelectedObject();
				EntityFragment fragment = (EntityFragment)fragmentAndGroupId[0];
				int groupId = (Integer)fragmentAndGroupId[1];
				int[][] r = possibleGroups.encountered.get(fragment);
				i.encountered.put(fragment, r);
				i.encounteredGroupIds.put(fragment, new int[]{groupId});
			}
			
			i.active = true;
			toggle();
			core.gameState.gameLogic.newTurnPhase(i, Ecology.PHASE_ENCOUNTER, true);
			return true;
		}
		if (base==leave)
		{
			core.gameState.gameLogic.inEncounter = false;
			core.gameState.engine.turnFinishedForPlayer();
			toggle();
			return true;
		}
		return false;
	}

}
