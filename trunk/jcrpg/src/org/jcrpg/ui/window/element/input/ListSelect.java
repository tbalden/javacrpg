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
import java.util.ArrayList;

import org.jcrpg.ui.FontUtils;
import org.jcrpg.ui.Window;
import org.jcrpg.ui.window.InputWindow;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;

public class ListSelect extends InputBase {

	public String[] ids;
	public String[] texts;
	public Object[] objects;
	
	public int selected = 0;
	public int fromCount = 0;
	public int maxCount = 0;
	public int maxVisible = 5;
	public boolean reloadNeeded = false;
	
	public ColorRGBA normal = null;
	public ColorRGBA highlighted = null;
	
	public Node deactivatedNode = null;
	public Node activatedNode = null;
	
	public ArrayList<Node> textNodes = new ArrayList<Node>(); 

	public static final String defaultImage = "./data/ui/inputBase.png";
	public String bgImage = defaultImage;
	
	public float fontRatio = 400f;
	
	public ListSelect(String id, InputWindow w, Node parent, float centerX, float centerY, float sizeX, float sizeY, float fontRatio, String[] ids, String[] texts, ColorRGBA normal, ColorRGBA highlighted) {
		super(id, w, parent, centerX, centerY, sizeX, sizeY);
		this.fontRatio = fontRatio;
		this.ids = ids;
		this.texts = texts;
		maxCount = ids.length;		
		this.normal = normal;
		this.highlighted = highlighted;
		deactivate();
		w.base.addEventHandler("lookLeft", w);
		w.base.addEventHandler("lookRight", w);
		w.base.addEventHandler("enter", w);
		parent.updateRenderState();
	}

	public int getSelection()
	{
		return fromCount+selected;
	}
	
	public void setupDeactivated()
	{
		baseNode.removeFromParent();
		parentNode.attachChild(baseNode); // to foreground
		baseNode.detachAllChildren();
		if (deactivatedNode==null) 
		{
			deactivatedNode = new Node();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.gray);
				deactivatedNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		baseNode.attachChild(deactivatedNode);
		if (maxCount==0) {
			baseNode.updateRenderState();
			return;			
		}
		String text = texts[selected+fromCount];
		Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),true);
		slottextNode.setLocalTranslation(dCenterX, dCenterY,0);
		slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		slottextNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio);
		baseNode.attachChild(slottextNode);
		baseNode.updateRenderState();
	}

	public void setupActivated()
	{
		baseNode.removeFromParent();
		parentNode.attachChild(baseNode); // to foreground
		baseNode.detachAllChildren();
		if (activatedNode==null) 
		{
			activatedNode = new Node();
			try {
				Quad w1 = Window.loadImageToQuad(new File(bgImage), dSizeX, dSizeY, dCenterX, dCenterY);
				w1.setSolidColor(ColorRGBA.white);
				activatedNode.attachChild(w1);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		baseNode.attachChild(activatedNode);
		
		if (maxCount==0 || texts.length==0)
		{
			baseNode.updateRenderState();
			return;
		}
		textNodes.clear();
		int size = 0;
		for (int i=0; i<maxVisible; i++) {
			if (i+fromCount<maxCount) {
				String text = texts[i+fromCount];
				Node slottextNode = FontUtils.textVerdana.createOutlinedText(text, 9, new ColorRGBA(1,1,0.1f,1f),new ColorRGBA(0.1f,0.1f,0.1f,1f),true);
				slottextNode.setLocalTranslation(dCenterX, dCenterY - dSizeY*i,0);
				slottextNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
				slottextNode.setLocalScale(w.core.getDisplay().getWidth()/fontRatio);
				if (i==selected)
				{
					colorizeOutlined(slottextNode, ColorRGBA.yellow);
				} else
				{
					colorizeOutlined(slottextNode, ColorRGBA.gray);
				}
				textNodes.add(slottextNode);
				baseNode.attachChild(slottextNode);
				size++;
			}
		}
		activatedNode.getChild(0).setLocalScale(new Vector3f(1f,size,size));
		if (size>0)
		{
			activatedNode.getChild(0).setLocalTranslation(dCenterX, dCenterY - ((size-1) * dSizeY)/2, 0);
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
		
		if (selected+fromCount>=maxCount)
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
				selected = maxVisible-1;
				reloadNeeded = true;
			} else {
				selected = 0;
			}
		}
		return reloadNeeded;
	}
	
	public void colorizeOutlined(Node textNode, ColorRGBA color)
	{
		int cc=0;
		for (Spatial q: textNode.getChildren())
		{
			if (cc%2==1) ((Quad)q).setDefaultColor(color);
			cc++;
		}
		
	}
	
	public boolean handleKey(String key) {
		if (!enabled) return false;
		if (key.equals("lookLeft"))
		{
			if (select(false))
			{
				setupActivated();
			} else
			{
				int i=0;
				for (Node n:textNodes)
				{
					colorizeOutlined(n, i==selected?ColorRGBA.yellow:ColorRGBA.gray);
					i++;
				}
			}
			if (ids.length>0) 
			{
				setValue(ids[fromCount+selected]);
				w.inputChanged(this, key);
			}
		} else
		if (key.equals("lookRight"))
		{
			if (select(true))
			{
				setupActivated();
			} else
			{
				int i=0;
				for (Node n:textNodes)
				{
					colorizeOutlined(n, i==selected?ColorRGBA.yellow:ColorRGBA.gray);
					i++;
				}
			}
			if (ids.length>0) 
			{
				setValue(ids[fromCount+selected]);
				w.inputChanged(this, key);
			}
		} else
		if (key.equals("enter"))
		{
			w.inputUsed(this , key);
		}
		return false;
	}

	@Override
	public void activate() {
		if (updated)
		{
			maxCount = ids.length;
			selected = 0;
			updated = false;
		}
		super.activate();
		setupActivated();
	}

	@Override
	public void deactivate() {
		super.deactivate();
		if (updated)
		{
			maxCount = ids.length;
			selected = 0;
			updated = false;
		}
		setupDeactivated();
	}

	@Override
	public void reset() {
		texts = new String[0];
		ids = new String[0];
		objects = new Object[0];
		selected = 0;
		maxCount = 0;
		fromCount = 0;
		setupDeactivated();
	}
	
}
