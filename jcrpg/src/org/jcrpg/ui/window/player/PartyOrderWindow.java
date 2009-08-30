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
package org.jcrpg.ui.window.player;

import java.util.ArrayList;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.PersistentMemberInstance;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Player's party character's inventory window.
 * @author illes
 *
 */
public class PartyOrderWindow extends PagedInputWindow {

	
	Node page0 = new Node();


	// page 0
	
	public ListSelect characterSelect;
	public ListSelect toCharacterSelect;
	public TextButton swap;
	public TextButton dismiss;
	
	public EntityMemberInstance currentMember = null;
	
	TextButton closeWindow;

	
	public PartyOrderWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.6f*core.getDisplay().getWidth(), 0.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.55f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	sQuad.setLocalTranslation(hudQuad.getLocalTranslation());
	    	page0.attachChild(sQuad);
	    	
	    	float yDelta = 0.04f;

	    	new TextLabel("",this,page0, 0.35f, yDelta+0.058f, 0.0f, 0.06f,400f,"Party Character Order",false);

    		characterSelect = new ListSelect("member", this,page0, 0.50f,yDelta+0.11f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,characterSelect);

	    	new TextLabel("",this,page0, 0.45f, yDelta+0.17f, 0.2f, 0.06f,600f,Language.v("inventory.toCharacter")+":",false); 
    		toCharacterSelect = new ListSelect("tomember", this,page0, 0.50f,yDelta+0.23f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,toCharacterSelect);
	    	
	    	swap = new TextButton("give",this,page0, 0.40f, yDelta+0.30f, 0.15f, 0.07f,500f,Language.v("partyOrderWindow.swap"));
	    	addInput(0,swap);

	    	dismiss = new TextButton("drop",this,page0, 0.60f, yDelta+0.30f, 0.15f, 0.07f,500f,Language.v("partyOrderWindow.dismiss"));
	    	addInput(0,dismiss);

	    	closeWindow = new TextButton("close",this,page0, 0.74f, yDelta+0.060f, 0.02f, 0.045f,600f,"x");
	    	addInput(0,closeWindow);
	    	
	    	addPage(0, page0);
	    	
		} catch (Exception ex)
		{	
			ex.printStackTrace();
		}
	}
	
	public PartyInstance party = null;
	
	public void setPageData(PartyInstance party)
	{
		this.party = party;
	}
	
	public int lastUpdatedLivingPartySize = 0;
	private ArrayList<EntityMemberInstance> tmpFilteredMembers = new ArrayList<EntityMemberInstance>();
	public void updateToParty()
	{
		int livingMembersCounter = 0;
		boolean foundCurrent = false;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (i.isDead()) continue;
			livingMembersCounter++;
			tmpFilteredMembers.add(i);
		}
		if (livingMembersCounter!=lastUpdatedLivingPartySize)
		{
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
			characterSelect.reset();
			characterSelect.ids = ids;
			characterSelect.objects = objects;
			characterSelect.texts = texts;
			characterSelect.setUpdated(true);
			characterSelect.deactivate();
		}
		if (!foundCurrent)
		{
			characterSelect.setSelected(0);
			characterSelect.setUpdated(true);
			characterSelect.deactivate();
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
		}
	}
	
	public void updateToMemberInstance(EntityMemberInstance instance)
	{
		int livingMembersCounter = 0;
		tmpFilteredMembers.clear();
		for (EntityMemberInstance i : party.orderedParty)
		{
			if (!i.memberState.isDead())
			{
				if (i!=instance)
				{
					livingMembersCounter++;
					tmpFilteredMembers.add(i);
				}
			}
		}
		{
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
			toCharacterSelect.reset();
			toCharacterSelect.ids = ids;
			toCharacterSelect.objects = objects;
			toCharacterSelect.texts = texts;
			toCharacterSelect.setUpdated(true);
			toCharacterSelect.deactivate();
		}
		
		currentMember = instance;
		
	}
	
	
	

	
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==characterSelect)
		{
			characterSelect.deactivate();
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
			return true;
		}
		else
		if (base==dismiss)
		{
			// TODO dismiss current selected member?
			
		} else
		if (base==swap)
		{
			EntityMemberInstance toChar = (EntityMemberInstance)toCharacterSelect.getSelectedObject();
			if (toChar==null || toChar == currentMember) return true;
			
			int toIndex = party.orderedParty.indexOf(toChar);
			int fromIndex = party.orderedParty.indexOf(currentMember);
			party.orderedParty.set(toIndex, (PersistentMemberInstance)currentMember);
			party.orderedParty.set(fromIndex, (PersistentMemberInstance)toChar);
			core.uiBase.hud.characters.updateForPartyCreation(party.orderedParty);
			core.uiBase.hud.characters.updatePoints();
			
		}
		if (base == closeWindow)
		{
			toggle();
			return true;
		}

		return super.inputUsed(base, message);
	}
	

	@Override
	public void hide() {
		super.hide();
	}
	@Override
	public void show() {
		super.show();
		setPageData(core.gameState.player);
		updateToParty();
	}
	
}
