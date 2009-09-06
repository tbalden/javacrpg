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

import java.util.TreeMap;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.window.element.SaveSlotData;
import org.jcrpg.ui.window.element.TextLabel;
import org.jcrpg.ui.window.element.input.InputBase;
import org.jcrpg.ui.window.element.input.TextButton;
import org.jcrpg.ui.window.element.input.TextInputField;
import org.jcrpg.util.Language;
import org.jcrpg.util.saveload.SaveLoadNewGame;

import com.jme.scene.shape.Quad;

public class SaveMenu extends InputWindow implements KeyListener {
	
	
	int selected = 0;
	
	int fromSlot = 0;
	
	public static int maxSlots = 4;
	

	FontTT text;
	
	Quad hudQuad;
	
	TextButton closeWindow;
	
	TextInputField saveGameName;

	TextButton nextWindow;
	
	public SaveMenu(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
        try {
        	
        	hudQuad = loadImageToQuad("./data/ui/mainmenu/mainMenu.png", 0.8f*1.2f*core.getDisplay().getWidth() / 2, 1.4f*(core.getDisplay().getHeight() / 2), 
        			core.getDisplay().getWidth() / 2, 1.1f*core.getDisplay().getHeight() / 2);
         	hudQuad.setRenderState(base.hud.hudAS);
        	//Quad logoQuad = loadImageToQuad("./data/ui/mainmenu/menu-logo.png", 2.23f*core.getDisplay().getWidth() / 5f, 2.23f*(core.getDisplay().getHeight() / 11), 
        		//	core.getDisplay().getWidth() / 2, 1.62f*core.getDisplay().getHeight() / 2);
        	//logoQuad.setRenderState(base.hud.hudAS);

         	
			//windowNode.attachChild(logoQuad);
			windowNode.attachChild(hudQuad);

	    	closeWindow = new TextButton("close",this,windowNode, 0.63f, 0.310f, 0.03f, 0.045f,600f," <-");
	    	addInput(closeWindow);

	    	new TextLabel("",this,windowNode, 0.5f, 0.42f, 0.3f, 0.06f,600f,Language.v("saveMenu.SaveName")+":",false); 
	    	saveGameName = new TextInputField("saveGameName",this,windowNode, 0.5f, 0.48f, 0.3f, 0.06f,600f,"",15,false);
	    	addInput(saveGameName);

	    	
	    	nextWindow = new TextButton("next",this,windowNode, 0.50f, 0.610f, 0.08f, 0.06f,600f,"SAVE");
	    	addInput(nextWindow);

			
			
			windowNode.updateRenderState();
			base.addEventHandler("lookUp", this);
			base.addEventHandler("lookDown", this);
			base.addEventHandler("enter", this);
			base.addEventHandler("back", this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	static TreeMap<String, SaveSlotData> dataList = null;
	

	
	
	

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
		saveGameName.activate();
	}
	
	public void handleChoice()
	{
		toggle();		
		core.updateDisplay(null);
		core.clearCore();
		base.hud.characters.hide();
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("---------------------- LOADING GAME ----------------------------");
		core.init3DGame();
		core.getClassicInputHandler().enableMouse(true);
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("---------------------- END LOADING GAME ------------------------");
		core.uiBase.hud.characters.update();
		core.uiBase.hud.characters.show();
		core.getUIRootNode().updateRenderState();
		core.gameState.engine.setPause(false);
		core.audioServer.stopAndResumeOthers("main");
	}

	public boolean handleKey(String key) {
		if (!visible) return false;
		if (super.handleKey(key)) return true;
		
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


	@Override
	public boolean inputChanged(InputBase base, String message) {
		return false;
	}


	@Override
	public boolean inputEntered(InputBase base, String message) {
		return false;
	}


	@Override
	public boolean inputLeft(InputBase base, String message) {
		return false;
	}


	@Override
	public boolean inputUsed(InputBase base, String message) {
		if (base==closeWindow)
		{
			toggle();
			core.mainMenu.toggle();
		}
		if (base==nextWindow)
		{
			//handleKey("lookDown");
		}
		if (base == saveGameName || base == nextWindow)
		{
			if (saveGameName.text!=null && saveGameName.text.length()>0)
			{
				core.getUIRootNode().detachChild(windowNode);
				core.getUIRootNode().updateRenderState();
				core.updateDisplay(null);
				SaveLoadNewGame.saveGame(core, saveGameName.text);
				core.getUIRootNode().attachChild(windowNode);
				//core.getUIRootNode().updateRenderState();
				toggle();
				core.audioServer.stopAndResumeOthers("main");
			}
		}
		return false;
	}


}
