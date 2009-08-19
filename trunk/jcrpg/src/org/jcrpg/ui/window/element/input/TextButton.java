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

package org.jcrpg.ui.window.element.input;

import java.io.File;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class TextButton extends InputBase {

	public String text;
	public String shortCut;
	
	public static final String defaultImage = "./data/ui/buttonBase.png";
	public String bgImage = defaultImage; 
	public float textProportion = 400f;
	
	boolean centered = true;

	public TextButton(String id, InputWindow w, Node parentNode, float textProportion, String text) {
		super(id, w, parentNode);
		this.text = text;
		this.textProportion = textProportion;
		this.centered = true;
	}
	public TextButton(String id, InputWindow w, Node parentNode, float textProportion, String text, String shortcut) {
		this(id,w,parentNode,textProportion,text);
		w.base.addEventHandler(shortcut, w); // save
		this.shortCut = shortcut;
		this.centered = true;
	}

	public TextButton(String id, InputWindow w, Node parentNode, float textProportion, String text, boolean centered) {
		super(id, w, parentNode);
		this.text = text;
		this.textProportion = textProportion;
		this.centered = centered;
	}
	public TextButton(String id, InputWindow w, Node parentNode, float textProportion, String text, boolean centered, String shortcut) {
		this(id,w,parentNode,textProportion,text,centered);
		w.base.addEventHandler(shortcut, w); // save
		this.shortCut = shortcut;
	}

	public TextButton(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, String text) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = text;
		this.textProportion = textProportion;
		deactivate();
	}
	public TextButton(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, String text, String shortcut) {
		this(id,w,parentNode,centerX,centerY,sizeX,sizeY,textProportion,text);
		w.base.addEventHandler(shortcut, w); // save
		this.shortCut = shortcut;
	}
	
	Node activeNode = null;
	Node deactiveNode = null;

	@Override
	public void init(float centerX, float centerY, float sizeX, float sizeY) {
		super.init(centerX, centerY, sizeX, sizeY);
		if (baseNode!=null) {
			deactivate(); 
		}
	}

	@Override
	public void activate() {
		baseNode.detachAllChildren();
		if (activeNode==null ) {
			activeNode = new Node(""+id);
			//activeNode.setUserData("uiElement", arg1)
			//freeTextNodes();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, !centered?dOrigoXCenter*0.95f:dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.white);
				activeNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			Node slottextNode = FontUtils.textVerdana.createText(text, DEF_FONT_SIZE, new ColorRGBA(0.6f,0.6f,0.1f,1f),true);
			slottextNode.setLocalTranslation(!centered?dOrigoXCenter*0.95f:dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
			//currentTextNodes.put(slottextNode, FontUtils.textVerdana);
			activeNode.attachChild(slottextNode);
		}
		baseNode.attachChild(activeNode);
		activeNode.setModelBound(new BoundingBox());
		baseNode.updateRenderState();
		baseNode.updateModelBound();
		super.activate();
	}

	@Override
	public void deactivate() {
		baseNode.detachAllChildren();
		if (deactiveNode==null ) {
			deactiveNode = new Node(""+id);
			//freeTextNodes();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, !centered?dOrigoXCenter*0.95f:dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.gray);
				deactiveNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			Node slottextNode = FontUtils.textVerdana.createText(text, DEF_FONT_SIZE, new ColorRGBA(0.5f,0.5f,0.1f,1f),true);
			slottextNode.setLocalTranslation(!centered?dOrigoXCenter*0.95f:dCenterX, dCenterY,0);
			slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
			slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
			//currentTextNodes.put(slottextNode, FontUtils.textVerdana);
			deactiveNode.attachChild(slottextNode);
		}
		baseNode.attachChild(deactiveNode);
		deactiveNode.setModelBound(new BoundingBox());
		baseNode.updateRenderState();
		baseNode.updateModelBound();
		super.deactivate();
	}

	@Override
	public boolean handleKey(String key) {
		if (key.equals("enter") || shortCut!=null && key.equals(shortCut)) // enter or shortcut
		{
			w.core.audioServer.play(SOUND_INPUTSELECTED);
			w.inputUsed(this, key);
			return true;
		}
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("--- "+id+" "+key);
		return false;
	}

	@Override
	public void reset() {
	}

}
