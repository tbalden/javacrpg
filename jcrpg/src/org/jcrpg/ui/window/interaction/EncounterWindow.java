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
import org.jcrpg.world.ai.PreEncounterInfo;
import org.jcrpg.world.ai.player.PartyInstance;

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

	ListMultiSelect groupSelect;
	TextButton ok;
	
	
	public EncounterWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame1.png", 0.6f*core.getDisplay().getWidth(), 0.75f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.58f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	SharedMesh sQuad = new SharedMesh("",hudQuad);
	    	page0.attachChild(sQuad);
	    	sQuad = new SharedMesh("",hudQuad);
	    	//page1.attachChild(sQuad);

	    	new TextLabel("",this,page0, 0.40f, 0.044f, 0.3f, 0.06f,400f,"Encounter",false);
	    	new TextLabel("",this,page0, 0.27f, 0.075f, 0.3f, 0.06f,600f,"Facing the inevitable.",false);
	    	new TextLabel("",this,page0, 0.27f, 0.100f, 0.3f, 0.06f,600f,"You have to choose who will act and what.",false);
	    	 
	    	{
	    		groupSelect = new ListMultiSelect("group", this,page0, 0.4f, 0.27f,0.15f,0.3f,0.06f,600f,new String[0],new String[0],null,null);
	    	}
	    	addInput(0,groupSelect);
	    	
	    	ok = new TextButton("ok",this,page0,0.66f, 0.22f, 0.18f, 0.06f,500f,Language.v("encounterWindow.ok"),"S");
	    	new TextLabel("",this,page0, 0.56f, 0.28f, 0.3f, 0.06f,600f,"Use Enter for selection.",false);
	    	new TextLabel("",this,page0, 0.56f, 0.32f, 0.3f, 0.06f,600f,"Use S if you are ready.",false);
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
	public Collection<PreEncounterInfo> encountered;
	
	public void setPageData(PartyInstance party, Collection<PreEncounterInfo> encountered)
	{
		this.party = party;
		this.encountered = encountered;
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
			toggle();
			core.gameState.playerTurnLogic.newTurnPhase(encountered, Ecology.PHASE_TURNACT, true);
			return true;
		}
		return false;
	}

}
