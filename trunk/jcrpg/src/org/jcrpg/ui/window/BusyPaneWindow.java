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

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.KeyListener;
import org.jcrpg.ui.UIBase;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.text.FontTT;

import com.jme.scene.shape.Quad;

/**
 * Used to hide loading and such behind a quad.
 * @author illes
 *
 */
public class BusyPaneWindow extends Window implements KeyListener {
	
	FontTT text;
	
	Quad loadQuad;
	
	public BusyPaneWindow(UIBase base) {
		super(base);
		text = FontUtils.textVerdana;
        try {
        	
        	loadQuad = loadImageToQuad("./data/ui/loading1.dds", core.getDisplay().getWidth(), core.getDisplay().getHeight(), 
        			core.getDisplay().getWidth() / 2, core.getDisplay().getHeight() / 2);
        	loadQuad.setRenderState(base.hud.hudAS);
         	
			windowNode.attachChild(loadQuad);
			
			windowNode.updateRenderState();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	@Override
	public void hide() {
		core.getUIRootNode().detachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}

	@Override
	public void show() {
		core.getUIRootNode().attachChild(windowNode);
		core.getUIRootNode().updateRenderState();
	}
	
	public boolean handleKey(String key) {
		return true;
	}
	



}
