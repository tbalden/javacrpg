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

package org.jcrpg.ui.window.element;

import java.io.File;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.text.Text;
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
	public boolean centered = false;
	public TextLabel(String id, InputWindow w, Node parentNode, float textProportion, String text, boolean useImage) {
		super(id, w, parentNode);
		this.text = text;
		this.textProportion = textProportion;
		this.useImage = useImage;
		// activates in the init
	}
    public TextLabel(String id, InputWindow w, Node parentNode, float textProportion, String text, boolean useImage, boolean centered) {
        super(id, w, parentNode);
        this.text = text;
        this.textProportion = textProportion;
        this.useImage = useImage;
        this.centered = centered;
        // activates in the init
    }
	public TextLabel(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, String text, boolean useImage) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.text = text;
		this.textProportion = textProportion;
		this.useImage = useImage;
		this.centered = useImage;
		activate();
	}
	public TextLabel(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX,
			float sizeY, float textProportion, String text, boolean useImage, boolean centered, ColorRGBA normalColor) {
		super(id, w, parentNode, centerX, centerY, sizeX, sizeY);
		this.normalColor = normalColor;
		this.text = text;
		this.textProportion = textProportion;
		this.useImage = useImage;
		this.centered = centered;
		activate();
	}
	
	@Override
	public void init(float centerX, float centerY, float sizeX, float sizeY) {
		super.init(centerX, centerY, sizeX, sizeY);
		if (baseNode!=null) {
			activate();
		}
	}
	
	Node activeNode = null;
	Node deactiveNode = null;

	Text textText = null;
	
	@Override
	public void activate() {
		baseNode.detachAllChildren();
		//if (activeNode==null ) 
		{
			freeTextNodes();
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
			if (J3DCore.NATIVE_FONT_RENDER)
			{
				float scale = w.core.getDisplay().getWidth()/textProportion/TEXT_PROP;
				if (textText==null)
				{
					textText = createText(text);
					textText.setLocalScale(scale);
				}
				textText.print(text);
				textText.setLocalTranslation(!centered?dOrigoX:textText.getCenterOrigoX(dCenterX,scale), - textText.getHeight2()/2f*scale + dCenterY,0);
				textText.setTextColor(normalColor);
			} else			
			{

				Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, DEF_FONT_SIZE, normalColor,outlineColor,centered);
				slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
				slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
				currentTextNodes.put(slottextNode, FontUtils.textVerdana);
				activeNode.attachChild(slottextNode);
			}
		}
		if (J3DCore.NATIVE_FONT_RENDER)
		{
			textText.removeFromParent();
			activeNode.attachChild(textText);
		}
		baseNode.attachChild(activeNode);
		baseNode.updateRenderState();
		super.activate();
	}

	@Override
	public void deactivate() {
		baseNode.detachAllChildren();
		
		if (deactiveNode==null ) {
			freeTextNodes();
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
			if (J3DCore.NATIVE_FONT_RENDER)
			{
				float scale = w.core.getDisplay().getWidth()/textProportion/TEXT_PROP;
				if (textText==null)
				{
					textText = createText(text);
					textText.setLocalScale(scale);
				}
				textText.print(text);
				textText.setLocalTranslation(!centered?dOrigoX:textText.getCenterOrigoX(dCenterX,scale), - textText.getHeight2()/2f*scale + dCenterY,0);
				deactiveNode.attachChild(textText);
				textText.setTextColor(deactivatedColor);
			} else			
			{
				Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, DEF_FONT_SIZE, deactivatedColor, outlineColor,centered);
				slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
				slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				slottextNode.setLocalScale(w.core.getDisplay().getWidth()/textProportion);
				currentTextNodes.put(slottextNode, FontUtils.textVerdana);
				deactiveNode.attachChild(slottextNode);
			}
		}
		if (J3DCore.NATIVE_FONT_RENDER)
		{
			textText.removeFromParent();
			deactiveNode.attachChild(textText);
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
	@Override
	public Node getDeactivatedNode() {
		return null;
	}

}
