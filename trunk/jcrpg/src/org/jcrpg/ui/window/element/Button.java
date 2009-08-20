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

import org.jcrpg.ui.mouse.UiMouseEvent;
import org.jcrpg.ui.mouse.UiMouseEvent.UiMouseEventType;
import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.element.input.InputBase;

import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

public class Button extends InputBase {

	public String name;
	public Quad quad;
	public Node node;
	public InputWindow parent;
	public Button(String name, Quad quad, InputWindow parent) {
		super(""+name,parent, parent.windowNode);
		this.name = name;
		this.quad = quad;
		this.parent = parent;
	}
	public Button(String name, Quad quad, Node n, InputWindow parent) {
		super(""+name,parent, parent.windowNode);
		this.name = name;
		this.quad = quad;
		this.node = n;
		this.parent = parent;
	}
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean handleMouse(UiMouseEvent mouseEvent)
	{
		if(mouseEvent.getEventType()== UiMouseEventType.MOUSE_PRESSED && mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_LEFT))
        {
    		return parent.handleKey("enter");
        }
        else
        {
        	return parent.inputEntered(this, name);
        }
	}
	@Override
	public Node getDeactivatedNode() {
		// TODO Auto-generated method stub
		return null;
	}
}
