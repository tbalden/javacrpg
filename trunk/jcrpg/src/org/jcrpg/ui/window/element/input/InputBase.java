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

import java.util.HashMap;

import org.jcrpg.ui.text.FontTT;
import org.jcrpg.ui.window.InputWindow;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;


public abstract class InputBase {
	
	public static final int DEF_FONT_SIZE = 10;
	
	public float centerX, centerY;
	public float sizeX, sizeY;
	public float dCenterX, dCenterY;
	public float dSizeX, dSizeY;
	public InputWindow w;
	public Node baseNode;
	boolean active = false;
	boolean enabled = true;
	boolean updated = false;
	
	public Node parentNode = null;
	
	public String id = null;
	
	public static final String SOUND_INPUTSELECTED = "input_selected";
	
	protected ColorRGBA normalColor = new ColorRGBA(0.9f,0.9f,0.1f,1f);
	protected ColorRGBA outlineColor = new ColorRGBA(0.05f,0.05f,0.05f,1f);
	protected ColorRGBA deactivatedColor = new ColorRGBA(0.5f,0.5f,0.1f,1f);
	protected ColorRGBA disabledColor = ColorRGBA.gray;
	
	public InputBase(String id, InputWindow w, Node parentNode, float centerX, float centerY, float sizeX, float sizeY)
	{
		this.id = id;
		this.parentNode = parentNode;
		this.centerX = centerX;
		this.centerY = centerY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.w = w;
		dCenterX = w.core.getDisplay().getWidth()*(centerX);
		dCenterY = w.core.getDisplay().getHeight()*(1f-centerY);
		dSizeX =  w.core.getDisplay().getWidth()*(sizeX);
		dSizeY =  w.core.getDisplay().getHeight()*(sizeY);
		baseNode = new Node("InputBaseNode");
		parentNode.attachChild(baseNode);
		parentNode.updateRenderState();
	}
	
	/**
	 * make this input visible again, and enabled = true.
	 */
	public void reattach()
	{
		parentNode.attachChild(baseNode);
		enabled = true;
	}
	/**
	 * remove this input from visible elements and enabled = false.
	 */
	public void detach()
	{
		baseNode.removeFromParent();
		enabled = false;
	}

	public String value = "";
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	/**
	 * This is run when an input field is selected in an input window.
	 */
	public void activate()
	{
		active = true;
	}
	/**
	 * This is run when an input field is deselected in an input window.
	 */
	public void deactivate()
	{
		active = false;
	}
	
	/**
	 * 
	 * @param key
	 */
	public boolean handleKey(String key)
	{
		return true;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public abstract void reset();
	
	boolean stored = false;
	public boolean isStored()
	{
		return stored;
	}
	public void store()
	{
		stored = true;
	}
	public void restore()
	{
	}
	
	public void colorizeOutlined(Node textNode, ColorRGBA color)
	{
		try {
			int cc=0;
			for (Spatial q: textNode.getChildren())
			{
				if (cc%2==1) ((Quad)q).setDefaultColor(color);
				cc++;
			}
		} catch (Exception ex) {}
		
	}
	public void colorize(Node textNode, ColorRGBA color)
	{
		try {
			int cc=0;
			for (Spatial q: textNode.getChildren())
			{
				((Quad)q).setDefaultColor(color);
				cc++;
			}
		} catch (Exception ex) {}
		
	}

	public HashMap<Node,FontTT> currentTextNodes = new HashMap<Node,FontTT>();
	
	public void freeTextNodes()
	{
		for (Node n:currentTextNodes.keySet())
		{
			FontTT font = currentTextNodes.get(n);
			font.moveFreedToCache(n);
		}
		currentTextNodes.clear();
	}
	
	
}
