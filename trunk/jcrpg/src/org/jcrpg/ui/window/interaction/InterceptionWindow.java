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
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.world.ai.Ecology;
import org.jcrpg.world.ai.PreEncounterInfo;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

/**
 * Pre encounter decisions when player meets AI - not an AI forced encounter. Here player can
 * decide what to do to avoid or to make sure an encounter, or can set preparations before the
 * encounter, or set devices to avoid it (traps, hide, magic etc.). 
 * @author pali
 *
 */
public class InterceptionWindow extends PagedInputWindow {

	
	Node page0 = new Node();

	public InterceptionWindow(UIBase base) {
		super(base);
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/nonPatternFrame.png", 0.8f*core.getDisplay().getWidth(), 1.65f*(core.getDisplay().getHeight() / 2), 
	    			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
	    	hudQuad.setRenderState(base.hud.hudAS);
	    	page0.attachChild(hudQuad);
	    	addPage(0, page0);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		base.addEventHandler("enter", this);
	}
	
	public Collection<PreEncounterInfo> possibleEncounters;

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
