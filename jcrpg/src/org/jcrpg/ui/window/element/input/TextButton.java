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

package org.jcrpg.ui.window.element.input;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;

public class TextButton extends InputBase {

	public String text;
	
	public TextButton(InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, String text) {
		super(w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = text;
		deactive();
	}

	@Override
	public void activate() {
		Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
		slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
		slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		slottextNode.setLocalScale(w.core.getDisplay().getWidth()/400f);
		baseNode.attachChild(slottextNode);
		baseNode.updateRenderState();
		super.activate();
	}

	@Override
	public void deactive() {
		Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.5f,0.5f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
		slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
		slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		slottextNode.setLocalScale(w.core.getDisplay().getWidth()/400f);
		baseNode.attachChild(slottextNode);
		baseNode.updateRenderState();
		super.deactive();
	}

	@Override
	public boolean handleKey(String key) {
		if (key.equals("enter"))
		{
			w.inputUsed(this, key);
			
		}
		return true;
	}

}
