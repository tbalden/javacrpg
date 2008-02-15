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

import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.element.Button;
import org.jcrpg.util.saveload.SaveLoadNewGame;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;

public class MainMenu extends Window implements KeyListener {
	
	
	public String QUIT = "mainMenuButtonQuit.png";
	public String OPTIONS = "mainMenuButtonOptions.png";
	public String NEW_GAME = "mainMenuButtonNewGame.png";
	public String SAVE_GAME = "mainMenuButtonSaveGame.png";
	public String LOAD_GAME = "mainMenuButtonLoadGame.png";
	
	public String[][] menuImages = new String[][] {
			{NEW_GAME,NEW_GAME}, {SAVE_GAME,SAVE_GAME}, {LOAD_GAME,LOAD_GAME}, {OPTIONS,OPTIONS}, {QUIT,QUIT}
	};
	
	int selected = 0;
	
	public ArrayList<Button> buttons = new ArrayList<Button>();

	public MainMenu(UIBase base) {
		super(base);
		
        try {
        	
        	Quad hudQuad = loadImageToQuad("./data/ui/mainmenu/mainMenu.png", 0.6f*1.2f*core.getDisplay().getWidth() / 2, 0.7f*1.2f*(core.getDisplay().getHeight() / 2), 
        			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
        	Quad logoQuad = loadImageToQuad("./data/ui/mainmenu/logo.png", 1.2f*core.getDisplay().getWidth() / 5f, 1.2f*(core.getDisplay().getHeight() / 11), 
        			core.getDisplay().getWidth() / 2, 1.63f*core.getDisplay().getHeight() / 2);
        	logoQuad.setRenderState(base.hud.hudAS);
        	
			windowNode.attachChild(hudQuad);
			windowNode.attachChild(logoQuad);
			
			int counter = 0;
			float sizeX = 1.00f* 1.2f * core.getDisplay().getWidth() / 5f;
			float sizeY = 0.65f* (core.getDisplay().getHeight() / 11);
			float startPosY = 1.34f*core.getDisplay().getHeight() / 2;
			float stepPosY = 0.66f* 1.1f*(core.getDisplay().getHeight() / 11);
			float posX = core.getDisplay().getWidth() / 2;
			for (String[] image:menuImages)
			{
				Quad button = loadImageToQuad("./data/ui/mainmenu/"+image[1],sizeX,sizeY, posX, startPosY - stepPosY*counter++);
				windowNode.attachChild(button);
				buttons.add(new Button(image[0],button,this));
			}
			highlightSelected();
			windowNode.updateRenderState();
			base.addEventHandler("lookUp", this);
			base.addEventHandler("lookDown", this);
			base.addEventHandler("enter", this);
			base.addEventHandler("back", this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	public void highlightSelected()
	{
		for (int i=0; i<buttons.size(); i++)
		{
			Button b = buttons.get(i);
			if (i==selected)
			{
				b.quad.setSolidColor(ColorRGBA.white);
						
			} else
			{
				b.quad.setSolidColor(ColorRGBA.gray);
				
			}
		}
		windowNode.updateRenderState();
		
	}

	@Override
	public void hide() {
		//core.audioServer.stopMainMenu();
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(false);
	}

	@Override
	public void show() {
		core.audioServer.playOnlyThis("main");
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
		lockLookAndMove(true);
	}


	public void handleChoice()
	{
		String name = buttons.get(selected).name;
		if (name.equals(QUIT))
		{
			//
			core.doQuit();
		} else
		if (name.equals(NEW_GAME))
		{
			base.hud.characters.hide();
			toggle();
			core.partySetup.toggle();
			
		} else
		if (name.equals(SAVE_GAME))
		{
			if (!core.coreFullyInitialized) return;
			toggle();
			core.updateDisplay(null);
			core.getRootNode().updateRenderState();
			SaveLoadNewGame.saveGame(core);
			core.audioServer.stopStart("main","ingame");
			
		} else
		if (name.equals(LOAD_GAME))
		{
			if (LoadMenu.updateFromDirectory()) {
				toggle();
				core.loadMenu.toggle();
			}
		}
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
		if (key.equals("back"))
		{
			if (core.coreFullyInitialized) {
				toggle();
				core.audioServer.stopAndResumeOthers("main");
				return true;
			}
		}
		selected = selected%buttons.size();
		if (selected<0) selected = buttons.size()-1;
		highlightSelected();
		
		if (key.equals("enter"))
		{
			handleChoice();
		}
		
		return true;
		//return false;
	}
	

}
