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

import org.jcrpg.ui.Window;

import com.jme.scene.Node;


public abstract class InputBase {
	
	public float centerX, centerY;
	public float sizeX, sizeY;
	public float dCenterX, dCenterY;
	public Window w;
	public Node baseNode;
	boolean active = false;
	boolean enabled = true;
	
	public InputBase(Window w, float centerX, float centerY, float sizeX, float sizeY)
	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.w = w;
		dCenterX = w.core.getDisplay().getWidth()*(centerX);
		dCenterY = w.core.getDisplay().getHeight()*(1f-centerY);
		baseNode = new Node("InputBaseNode");
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
	public void deactive()
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
	
	
}