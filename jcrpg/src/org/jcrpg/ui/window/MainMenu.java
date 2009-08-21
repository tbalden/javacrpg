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

package org.jcrpg.ui.window;

import java.util.ArrayList;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.mouse.UiMouseEvent;
import org.jcrpg.ui.mouse.UiMouseEvent.UiMouseEventType;
import org.jcrpg.ui.window.element.Button;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.MenuImageButton;
import org.jcrpg.util.saveload.SaveLoadNewGame;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;

public class MainMenu extends InputWindow implements KeyListener {
	
	
	public static String QUIT = "mainMenuButtonQuit.png";
	public static String OPTIONS = "mainMenuButtonOptions.png";
	public static String NEW_GAME = "mainMenuButtonNewGame.png";
	public static String SAVE_GAME = "mainMenuButtonSaveGame.png";
	public static String LOAD_GAME = "mainMenuButtonLoadGame.png";
	
	public String[][] menuImages = new String[][] {
			{NEW_GAME,NEW_GAME}, {SAVE_GAME,SAVE_GAME}, {LOAD_GAME,LOAD_GAME}, {OPTIONS,OPTIONS}, {QUIT,QUIT}
	};
	public float[] sizeXRatios = new float[]{1f,0.5f,0.5f, 0.8f, 0.5f};
	
	int selected = 0;
	
	public ArrayList<Button> buttons = new ArrayList<Button>();
	

	
	public MainMenu(UIBase base) {
		super(base);
		
        try {
        	
        	Quad hudQuad = loadImageToQuad("./data/ui/mainmenu/mainMenu.dds", 0.8f*1.2f*core.getDisplay().getWidth() / 2, 1.4f*(core.getDisplay().getHeight() / 2), 
        			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
        	hudQuad.setRenderState(base.hud.hudAS);
        	//Quad logoQuad = loadImageToQuad("./data/ui/mainmenu/menu-logo.png", 2.23f*core.getDisplay().getWidth() / 5f, 2.23f*(core.getDisplay().getHeight() / 11), 
        		//	core.getDisplay().getWidth() / 2, 1.62f*core.getDisplay().getHeight() / 2);
        	//logoQuad.setRenderState(base.hud.hudAS);
        	
			//windowNode.attachChild(logoQuad);
			windowNode.attachChild(hudQuad);
			
			int counter = 0;
			float sizeX = 1.28f* 1.2f * core.getDisplay().getWidth() / 5f;
			float sizeY = 0.82f* (core.getDisplay().getHeight() / 11);
			float startPosY = 1.31f*core.getDisplay().getHeight() / 2;
			float stepPosY = 0.85f* 1.1f*(core.getDisplay().getHeight() / 11);
			float posX = core.getDisplay().getWidth() / 2;
			int i=0;
			for (String[] image:menuImages)
			{
				Quad button = loadImageToQuad("./data/ui/mainmenu/"+image[1],sizeX*sizeXRatios[i],sizeY, posX, startPosY - stepPosY*counter++);
				button.setRenderState(base.hud.hudAS);
				
				//buttonNode.attachChild(button);
				button.setModelBound(new BoundingBox());
				MenuImageButton b = new MenuImageButton(image[1], this, windowNode, i);
				b.baseNode.attachChild(button);
				b.activate();
				windowNode.attachChild(b.baseNode);
				buttons.add(new Button(image[0],button,this));
				i++;
			}
			highlightSelected();
			windowNode.updateModelBound();
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

	boolean keepPlayingMusic = false;
	
	@Override
	public void hide() {
		//core.audioServer.stopMainMenu();
		//if (core.coreFullyInitialized) {
		if (!keepPlayingMusic) {
			core.audioServer.stopAndResumeOthers("main");
		} else
		{
			keepPlayingMusic = false;
		}
		//}
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(false);
	}
	

	@Override
	public void show() {
		core.audioServer.playOnlyThisMusic("main");
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
		lockLookAndMove(true);
	}

	public void handleChoice()
	{
		String name = buttons.get(selected).name;
		handleChoice(name);
	}
	public void handleChoice(String name)
	{
		if (name.equals(QUIT))
		{
			//
			core.doQuit();
		} else
		if (name.equals(NEW_GAME))
		{
			base.hud.characters.hide();
			keepPlayingMusic = true;
			toggle();
			core.partySetup.toggle();
			
		} else
		if (name.equals(SAVE_GAME))
		{
			if (!core.coreFullyInitialized) return;
			core.getUIRootNode().detachChild(windowNode);
			core.getUIRootNode().updateRenderState();
			core.updateDisplay(null);
			SaveLoadNewGame.saveGame(core);
			core.getUIRootNode().attachChild(windowNode);
			//core.getUIRootNode().updateRenderState();
			toggle();
			core.audioServer.stopAndResumeOthers("main");
			
		} else
		if (name.equals(LOAD_GAME))
		{
			keepPlayingMusic = true;
			if (LoadMenu.updateFromDirectory()) {
				toggle();
				core.loadMenu.toggle();
			}
		} else
		if (name.equals(OPTIONS))
		{
			keepPlayingMusic = true;
			toggle();
			core.optionsMenu.toggle();
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

	@Override
	public boolean inputChanged(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean inputEntered(InputBase base, String message) {
		int newSelected = Integer.parseInt(message);
		if (newSelected != selected)
		{
			selected = newSelected;
			highlightSelected();
			return true;
		}
		return false;
	}



	@Override
	public boolean inputLeft(InputBase base, String message) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean inputUsed(InputBase base, String message) {
		if ("enter".equals(message)) {

			String name = base.baseNode.getChild(0).getName();
			if (name != null) {
				name = name.substring(name.lastIndexOf("/") + 1);
			}
			handleChoice(name);
		}
		return false;
	}
	

}
