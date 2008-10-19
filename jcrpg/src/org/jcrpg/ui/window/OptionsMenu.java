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

import java.util.logging.Level;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.UIBase;

import com.jme.scene.shape.Quad;

/**
 * UI class for changing configuration settings in game.
 *
 * @author goq669
 */
public class OptionsMenu extends PagedInputWindow {
	
	public OptionsMenu(UIBase base) {
		super(base);
		try {
			// background
			Quad hudQuad = loadImageToQuad("./data/ui/baseWindowFrame.dds", 0.8f*core.getDisplay().getWidth(), 1.61f*(core.getDisplay().getHeight() / 2), 
				core.getDisplay().getWidth() / 2, 1.13f*core.getDisplay().getHeight() / 2);
			hudQuad.setRenderState(base.hud.hudAS);
			windowNode.attachChild(hudQuad);

			// header
			float sizeX = 1.28f* 1.2f * core.getDisplay().getWidth() / 5f;
			float sizeY = 0.82f* (core.getDisplay().getHeight() / 11);
			float posY = core.getDisplay().getHeight()*0.92f;
			float posX = core.getDisplay().getWidth() / 2;
			Quad header = loadImageToQuad("./data/ui/mainmenu/"+MainMenu.OPTIONS, sizeX, sizeY, posX, posY);
			header.setRenderState(base.hud.hudAS);
			windowNode.attachChild(header);
			
			//new TextLabel("",this,windowNode, 0.23f, 0.10f, 0.35f, 0.07f,600f,"Mouse Look",false); 
			
			// 
			windowNode.updateRenderState();
			base.addEventHandler("back", this);
		} catch (Exception ex) {
			if (J3DCore.LOGGING) { Jcrpg.LOGGER.log(Level.SEVERE, "OptionsMenu creation error: "+ex.getMessage(), ex); }
			ex.printStackTrace();
		}
	}

	public boolean handleKey(String key) {
		if (super.handleKey(key)) return true;
		if (key.equals("back"))
		{
			toggle();
			core.mainMenu.toggle();
		}
		
		return true;
	}

}
