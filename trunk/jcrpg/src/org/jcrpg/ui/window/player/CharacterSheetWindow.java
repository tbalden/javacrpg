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

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.ui.window.element.input.PictureSelect;
import org.jcrpg.ui.window.element.input.ValueTuner;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.EntityMemberInstance;
import org.jcrpg.world.ai.abs.attribute.FantasyAttributes;
import org.jcrpg.world.ai.abs.skill.SkillGroups;
import org.jcrpg.world.ai.humanoid.MemberPerson;
import org.jcrpg.world.ai.player.PartyInstance;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Player's party character sheet window.
 * @author illes
 *
 */
public class CharacterSheetWindow extends PagedInputWindow {

	Node page0 = new Node();
	
	public ListSelect characterSelect;
	
	// character data
	PictureSelect pictureSelect = null;
	ListSelect professions = null;
	ValueTuner level = null;
	ValueTuner health = null;
	ValueTuner stamina = null;
	ValueTuner morale = null;
	ValueTuner sanity = null;
	ValueTuner mana = null;
	HashMap<String, ValueTuner> attributeTuners = new HashMap<String, ValueTuner>();
	HashMap<String, ListSelect> skillSelects = new HashMap<String, ListSelect>();

	
	public CharacterSheetWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.75f*core.getDisplay().getWidth(), 1.7f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.10f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.40f, 0.058f, 0.3f, 0.06f,400f,"Character Sheet",false);
    		characterSelect = new ListSelect("member", this,page0, 0.50f,0.11f,0.3f,0.06f,600f,new String[0],new String[0], new Object[0],null,null);
	    	addInput(0,characterSelect);
	    	
	    	
	    	int posY = 0;
	    	for (String s: FantasyAttributes.attributeName)
	    	{
	    		String text = Language.v("fantasyattributes.short."+s);
	    		System.out.println("TEXT" +text);
	    		new TextLabel(s+"_label",this,page0,0.149f,0.2f+0.05f*posY,0.15f,0.04f,600f, text, false);
	    		ValueTuner v = new ValueTuner(s,this,page0, 0.317f,0.2f+0.05f*posY,0.15f,0.04f,600f,10,0,100,1);
	    		attributeTuners.put(s, v);
	    		//addInput(1,v);
	    		posY++;
	    	}

	    	pictureSelect = new PictureSelect("picture_select", this, page0, 0.73f,0.2f,0.15f,0.2f,600f);
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
			if (i.memberState.healthPoint>=0)
			{
				// living, TODO tune to better state values
				livingMembersCounter++;
				if (i == characterSelect.getSelectedObject())
				{
					foundCurrent = true;
				}
				tmpFilteredMembers.add(i);
			}
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
	}
	
	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==characterSelect)
		{
			characterSelect.deactivate();
			updateToMemberInstance((EntityMemberInstance)characterSelect.getSelectedObject());
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
		updateToParty();
	}
	
}
