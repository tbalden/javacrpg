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

import java.util.Collection;

import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.window.PagedInputWindow;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.ListMultiSelect;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.util.Language;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.PreEncounterInfo;
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
	// selecting skills which to use for the interception phase
	Node page1 = new Node();

	ListMultiSelect groupSelect;
	TextButton ok;
	
	public PreEncounterWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame.png", 0.6f*core.getDisplay().getWidth(), 0.75f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.58f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);
	    	sQuad = new SharedMesh("",hudQuad);
	    	page1.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.4f, 0.045f, 0.3f, 0.06f,400f,"Interception",false);
	    	new TextLabel("",this,page0, 0.27f, 0.09f, 0.3f, 0.06f,600f,"Groups:",false); 
	    	{
	    		groupSelect = new ListMultiSelect("group", this,page0, 0.4f, 0.27f,0.15f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,groupSelect);
	    	
	    	ok = new TextButton("ok",this,page0,0.4f, 0.3f, 0.18f, 0.06f,500f,Language.v("preEncounterWindow.ok"),"S");
	    	addInput(0,ok);

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
	public Collection<PreEncounterInfo> possibleEncounters;
	
	/**
	 * This function fills up the required data fields for displaying the lists and elements of the page.
	 * After using this you can call toggle().
	 * @param party
	 * @param possibleEncounters
	 */
	public void setPageData(PartyInstance party, Collection<PreEncounterInfo> possibleEncounters)
	{
		this.party = party;
		this.possibleEncounters = possibleEncounters;
	}
	@Override
	public void hide() {
		super.hide();
		lockLookAndMove(false);
	}
	@Override
	public void show() {
		super.show();
		lockLookAndMove(true);
	}
	
	@Override
	public void setupPage() {
		int listSize = 0;
		for (PreEncounterInfo i:possibleEncounters)
		{
			if (!i.active) continue;
			listSize++;
		}
		String[] ids = new String[listSize];
		Object[] objects = new Object[listSize];
		String[] texts = new String[listSize];
		int count = 0;
		System.out.println("ENC SIZE = "+listSize);
		for (PreEncounterInfo i:possibleEncounters)
		{
			int size = 0;
			String text = count+"/";
			if (!i.active) continue;
			for (EntityInstance instance:i.encountered.keySet())
			{
				System.out.println(instance.description.getClass().getSimpleName()+" _ "+i.encountered.size());
				size++;
				
				text+=size+" "+instance.description.getClass().getSimpleName()+" ";
			}
			ids[count] = ""+count;
			texts[count] = text;
			objects[count] = i;
			count++;
		}
		groupSelect.ids = ids;
		groupSelect.objects = objects;
		groupSelect.texts = texts;
		groupSelect.setUpdated(true);
		groupSelect.activate();
		super.setupPage();
	}

	@Override
	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		return false;
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

	@Override
	public boolean inputLeft(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==ok)
		{
			int counter = 0;
			int active = 0;
			for (Object i:groupSelect.objects)
			{
				if (groupSelect.selectedItems[counter])
				{
					((PreEncounterInfo)i).active = true;
					active++;
				} else
				{
					((PreEncounterInfo)i).active = false;
				}
				counter++;
			}
			System.out.println("POSSIBLE ENCOUNTERS : "+possibleEncounters.size()+" COUNTED = "+counter+" ACTIVE = "+active);
			toggle();
			core.gameState.playerTurnLogic.newTurn(possibleEncounters, Ecology.PHASE_ENCOUNTER, true);
			return true;
		}
		return false;
	}

}
