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
import org.jcrpg.ui.window.element.input.ListSelect;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.EntityInstance;
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.ai.player.PartyInstance;

import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.shape.Quad;

/**
 * Here player can decide what to do to avoid or to make sure an encounter, or can set preparations before the
 * encounter, or set devices to avoid it (traps, hide, magic etc.). Also rules to what Player based encounters
 * to pop up as interception/encounter and what not - friendly/neutral/hostile. 
 * @author pali
 */
public class BehaviorWindow extends PagedInputWindow {

	// selecting skills which to use for the interception phase
	Node page0 = new Node();

	ListSelect groupSelect;
	
	public BehaviorWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame.png", 0.7f*core.getDisplay().getWidth(), 1.35f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.18f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);
	    	sQuad = new SharedMesh("",hudQuad);

	    	new TextLabel("",this,page0, 0.4f, 0.11f, 0.3f, 0.06f,400f,"Party Behavior",false);
	    	new TextLabel("",this,page0, 0.27f, 0.16f, 0.3f, 0.06f,600f,"Members:",false);
	    	float sizeSelect = 0.05f;
	    	for (int i=0; i<6; i++)
	    	{
	    		groupSelect = new ListSelect("member"+i, this,page0, 0.53f,0.19f+sizeSelect*i,0.3f,0.04f,1000f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,groupSelect);

	    	
	    	addPage(0, page0);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		base.addEventHandler("enter", this);
	}
	
	public PartyInstance party;
	public Collection<PreEncounterInfo> possibleEncounters;
	
	@Override
	public void setupPage() {
		/*int listSize = 0;
		for (PreEncounterInfo i:possibleEncounters)
		{
			if (i.encountered.size()<1) continue;
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
			String text = count+" ";
			if (i.encountered.size()<1) continue;
			for (EntityInstance instance:i.encountered.keySet())
			{
				size++;
				
				text+=instance.description.getClass().getSimpleName()+", ";
			}
			ids[count] = ""+count;
			texts[count] = text;
			objects[count] = i;
			count++;
		}5
		groupSelect.ids = ids;
		groupSelect.objects = objects;
		groupSelect.texts = texts;
		groupSelect.setUpdated(true);
		groupSelect.activate();*/
		super.setupPage();
	}

	@Override
	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		if ("enter".equals(key)) 
		{
			toggle();
			core.gameState.playerTurnLogic.newTurn(possibleEncounters, Ecology.PHASE_ENCOUNTER, true);
			return true;
		}
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
		// TODO Auto-generated method stub
		return false;
	}

}
