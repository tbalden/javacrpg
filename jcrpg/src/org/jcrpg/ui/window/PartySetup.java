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

package org.jcrpg.ui.window;

import java.util.ArrayList;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.util.saveload.SaveLoadNewGame;
import org.jcrpg.world.ai.player.PartyMember;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class PartySetup extends Window implements KeyListener{

	FontTT text;
	
	Node pageMemberSelection = new Node();
	Node pageCreationFirst = new Node();
	Node pageCreationSecond = new Node();

	int currentPage = 0;
	
	public PartySetup(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
		try {
	    	Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.png", 0.8f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
	    	pageMemberSelection.attachChild(hudQuad);
			base.addEventHandler("lookUp", this);
			base.addEventHandler("lookDown", this);
			base.addEventHandler("enter", this);
			base.addEventHandler("back", this);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void hide() {
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		if (currentPage==0)
		{
			windowNode.detachAllChildren();
			windowNode.attachChild(pageMemberSelection);
		}
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(true);
	}

	public boolean handleKey(String key) {
		if (key.equals("enter")) {
			toggle();
			base.hud.characters.show();
			core.clearCore();
			ArrayList<PartyMember> members = new ArrayList<PartyMember>();
			for (int i=0; i<6; i++)
			{
				members.add(new PartyMember("_"+i));
			}			
			SaveLoadNewGame.newGame(core,members);
			core.init3DGame();
			core.getRootNode().updateRenderState();
			core.gameState.engine.setPause(false);
		} else
		if (key.equals("back"))
		{
			if (core.coreFullyInitialized)
			{
				base.hud.characters.show();
			}
			toggle();
			core.mainMenu.toggle();
		}
		return true;
	}

}
