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

package org.jcrpg.ui.window.element;

import java.io.File;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class TextLabel extends InputBase {

	public String text;
	
	public static final String defaultImage = "./data/ui/buttonBase.png";
	public String bgImage = defaultImage; 
	public float textProportion = 400f;
	public boolean useImage = false;
	public TextLabel(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, String text, boolean useImage) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = text;
		this.textProportion = textProportion;
		activate();
	}
	
	Node activeNode = null;
	Node deactiveNode = null;

	@Override
	public void activate() {
		baseNode.detachAllChildren();
		//if (activeNode==null ) 
		{
			activeNode = new Node();
			if (useImage)
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.white);
				activeNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.8f,0.8f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),useImage);
			slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
			activeNode.attachChild(slottextNode);
		}
		baseNode.attachChild(activeNode);
		baseNode.updateRenderState();
		super.activate();
	}

	@Override
	public void deactivate() {
		baseNode.detachAllChildren();
		if (deactiveNode==null ) {
			deactiveNode = new Node();
			if (useImage)
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.gray);
				deactiveNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.5f,0.5f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),useImage);
			slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
			deactiveNode.attachChild(slottextNode);
		}
		baseNode.attachChild(deactiveNode);
		baseNode.updateRenderState();
		super.deactivate();
	}

	@Override
	public boolean handleKey(String key) {
		if (key.equals("enter"))
		{
			w.inputUsed(this, key);
			return true;
		}
		return false;
	}

	@Override
	public void reset() {
	}

}
