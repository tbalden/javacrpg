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
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.window.element.Button;
import org.jcrpg.ui.window.element.TextLabel;

import com.jme.scene.shape.Quad;

/**
 * UI class for changing configuration settings in game.
 *
 * @author goq669
 */
public class OptionsMenu extends PagedInputWindow {
	
	int selected = 0;
	
	int fromSlot = 0;
	
	public static int maxSlots = 4;
	
	public ArrayList<Button> buttons = new ArrayList<Button>();

	FontTT text;
	
	public OptionsMenu(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
		try {
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.dds", 0.8f*core.getDisplay().getWidth(), 1.61f*(core.getDisplay().getHeight() / 2), 
				core.getDisplay().getWidth() / 2, 1.13f*core.getDisplay().getHeight() / 2);
			hudQuad.setRenderState(base.hud.hudAS);
			windowNode.attachChild(hudQuad);

			float sizeX = 1.28f* 1.2f * core.getDisplay().getWidth() / 5f;
			float sizeY = 0.82f* (core.getDisplay().getHeight() / 11);
			float PosY = core.getDisplay().getHeight()-40;
			float posX = core.getDisplay().getWidth() / 2;
			Quad header = loadImageToQuad("./data/ui/mainmenu/"+MainMenu.OPTIONS, sizeX, sizeY, posX, PosY);
			header.setRenderState(base.hud.hudAS);
			windowNode.attachChild(header);
			
			//new TextLabel("",this,windowNode, 0.23f, 0.10f, 0.35f, 0.07f,600f,"Options",false); 
			
			windowNode.updateRenderState();
			base.addEventHandler("lookUp", this);
			base.addEventHandler("lookDown", this);
			base.addEventHandler("enter", this);
			base.addEventHandler("back", this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	@Override
	public void hide() {
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(true);
	}


	public void handleChoice()
	{
	}

	public boolean handleKey(String key) {
		if (!visible) return false;
		if (key.equals("lookUp"))
		{
			selected--;
		} else
		if (key.equals("lookDown"))
		{
			selected++;
		}
		
		if (key.equals("enter"))
		{
			handleChoice();
		}
		if (key.equals("back"))
		{
			toggle();
			core.mainMenu.toggle();
		}
		
		return true;
		//return false;
	}


}
