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
import org.jcrpg.ui.Window;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;

public class ListSelect extends InputBase {

	public String[] ids;
	public String[] texts;
	
	public int selected = 0;
	public int fromCount = 0;
	public int maxCount = 0;
	public int maxVisible = 5;
	public boolean reloadNeeded = false;
	
	public ColorRGBA normal = null;
	public ColorRGBA highlighted = null;
	
	public Node deactivatedNode = null;
	public Node activatedNode = null;
	
	public ListSelect(Window w, Node parent, float centerX, float centerY, float sizeX, float sizeY, String[] ids, String[] texts, ColorRGBA normal, ColorRGBA highlighted) {
		super(w, centerX, centerY, sizeX, sizeY);
		this.ids = ids;
		this.texts = texts;
		maxCount = texts.length;
		this.normal = normal;
		this.highlighted = highlighted;
		deactive();
		parent.attachChild(baseNode);
		parent.updateRenderState();
	}
	
	public void setupDeactivated()
	{
		baseNode.detachAllChildren();
		String text = texts[selected+fromCount];
		Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
		slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
		slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		slottextNode.setLocalScale(w.core.getDisplay().getWidth()/400f);
		baseNode.attachChild(slottextNode);
		baseNode.updateRenderState();
	}

	public void setupActivated()
	{
		baseNode.detachAllChildren();
		for (int i=0; i<maxVisible; i++) {
			if (i+selected+fromCount<maxCount) {
				String text = texts[i+selected+fromCount];
				Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),false);
				slottextNode.setLocalTranslation(dCenterX, dCenterY - dSizeY*i,0);
				slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				slottextNode.setLocalScale(w.core.getDisplay().getWidth()/400f);
				baseNode.attachChild(slottextNode);
			}
		}
		baseNode.updateRenderState();
	}

	public boolean select(boolean next)
	{
		if (!next) {
			selected--;
		} else
		{
			selected++;
		}
		if (selected+fromCount>=maxVisible)
		{
			selected--;
		}
		if (selected>=maxVisible)
		{
			if (selected+fromCount<maxCount)
			{
				fromCount = selected;
				selected = 0;

				reloadNeeded = true;
			} 
		}
		if (selected<0) {
			if (fromCount-maxVisible>=0)
			{
				fromCount -= maxVisible;
				selected = maxCount-1;
				reloadNeeded = true;
			} else {
				selected = 0;
			}
		}
		return reloadNeeded;
	}
	
	public boolean handleKey(String key) {
		if (!enabled) return false;
		if (key.equals("lookUp"))
		{
			if (select(false))
			{
				
			}
		} else
		if (key.equals("lookDown"))
		{
			if (select(true))
			{
				
			}
		}
		return true;
	}

	@Override
	public void activate() {
		super.activate();
		setupActivated();
	}

	@Override
	public void deactive() {
		super.deactive();
		setupDeactivated();
	}
}
