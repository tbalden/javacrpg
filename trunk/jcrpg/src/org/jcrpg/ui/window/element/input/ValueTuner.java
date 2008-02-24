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

import java.io.File;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

/**
 * Numeric value selection.
 * @author pali
 *
 */
public class ValueTuner extends InputBase {

	public static final int UNDEFINED = -999999;
	public String text;
	
	public static final String defaultImage = "./data/ui/tunerBase.png";
	public String bgImage = defaultImage; 
	public float textProportion = 400f;
	
	public int oldValue, value, minValue, maxValue, step = 1;
	public Object tunedObject;
	
	public ValueTuner(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, int value, int minValue, int maxValue, int step) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = ""+value;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
		this.textProportion = textProportion;
		deactivate();
		w.base.addEventHandler("lookLeft", w);
		w.base.addEventHandler("lookRight", w);
		w.base.addEventHandler("enter", w);
	}
	
	Node activeNode = null;
	Node deactiveNode = null;
	
	public int getSelection()
	{
		return value;
	}

	@Override
	public void activate() {
		if (updated)
		{
			updated = false;
			text = ""+value;
		}
		baseNode.detachAllChildren();
		{
			activeNode = new Node();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.white);
				activeNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.8f,0.8f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),true);
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
		if (updated)
		{
			updated = false;
			text = ""+value;
		}
		baseNode.detachAllChildren();
		{
			deactiveNode = new Node();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.gray);
				deactiveNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(0.6f,0.6f,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),true);
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
		if (key.equals("lookLeft"))
		{
			if (value-step<minValue) return true;
			int orig = value;
			value-=step;
			if (!w.inputUsed(this, key)) value=orig;
			text = ""+value;
			setValue(text);
			activate();
			return true;
		} else
		if (key.equals("lookRight"))
		{
			if (value+step>maxValue) return true;
			int orig = value;
			value+=step;
			if (!w.inputUsed(this, key)) value=orig;
			text = ""+value;
			setValue(text);
			activate();
			return true;
		} else
		if (key.equals("enter"))
		{
			w.core.audioServer.play(SOUND_INPUTSELECTED);
			w.inputUsed(this, key);
			return true;
		}
		return false;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public void reset() {
		value = oldValue;
		text = ""+value;
		
	}

}
