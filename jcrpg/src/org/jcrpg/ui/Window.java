/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.ui;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.scene.Node;

public abstract class Window {

	protected boolean visible = false;
	protected Node windowNode;
	
	public UIBase base;
	public J3DCore core;
	
	public Window(UIBase base)
	{
		this.base = base;
		core = base.core;
		windowNode = new Node("windowNode");

	}
	
	public void toggle()
	{
		if (visible)
		{
			core.engine.setPause(false);
			((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).lock = false;
			hide();
		} else
		{
			((ClassicKeyboardLookHandler)core.getInputHandler().getFromAttachedHandlers(0)).lock = true;
			core.engine.setPause(true);
			show();
		}
		visible=!visible;
	}
	
	public abstract void hide();
	public abstract void show();
	
}
