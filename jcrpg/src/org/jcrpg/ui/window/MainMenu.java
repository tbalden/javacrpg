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

import com.jme.scene.shape.Quad;
import com.jme.scene.state.ColorMaskState;
import com.jme.scene.state.RenderState;

public class MainMenu extends Window implements KeyListener {
	
	public String[] menuImages = new String[] {
			"mainMenuButtonNewGame.png", "mainMenuButtonSaveGame.png",  "mainMenuButtonLoadGame.png","mainMenuButtonOptions.png",  "mainMenuButtonQuit.png"
	};
	
	int selected = 0;
	
	public ArrayList<Button> buttons = new ArrayList<Button>();
	ColorMaskState cMask = null;
	public MainMenu(UIBase base) {
		super(base);
		
		cMask = core.getDisplay().getRenderer().createColorMaskState();
		cMask.setBlue(false);
		cMask.setRed(true);
	
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
			for (String image:menuImages)
			{
				Quad button = loadImageToQuad("./data/ui/mainmenu/"+image,sizeX,sizeY, posX, startPosY - stepPosY*counter++);
				windowNode.attachChild(button);
				buttons.add(new Button(image,button,this));
			}
			highlightSelected();
			windowNode.updateRenderState();
			base.addEventHandler("lookUp", this);
			base.addEventHandler("lookDown", this);
			base.addEventHandler("enter", this);
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
				b.quad.setRenderState(cMask);		
			} else
			{
				b.quad.clearRenderState(RenderState.RS_COLORMASK_STATE);
			}
		}
		windowNode.updateRenderState();
		
	}

	@Override
	public void hide() {
		core.getRootNode().detachChild(windowNode);
		core.getRootNode().updateRenderState();
	}

	@Override
	public void show() {
		core.getRootNode().attachChild(windowNode);
		core.getRootNode().updateRenderState();
	}



	public boolean handleKey(String key) {
		if (!visible) return false;
		selected++;
		if (selected>buttons.size()) selected = 0;
		highlightSelected();
		
		return true;
		//return false;
	}
	

}
