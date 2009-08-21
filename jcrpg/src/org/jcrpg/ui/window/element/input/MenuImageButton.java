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

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.ui.mouse.UiMouseEvent;
import org.jcrpg.ui.mouse.UiMouseEvent.UiMouseEventType;
import org.jcrpg.ui.window.InputWindow;

import com.jme.scene.Node;

public class MenuImageButton extends InputBase
{
	public int selectedValue;
	
	public MenuImageButton(String id, InputWindow w, Node parentNode, int selectedValue) {
		super(id, w, parentNode);
		
		this.selectedValue = selectedValue;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean handleKey(String key) {
		if (key.equals("enter")) // enter or shortcut
		{
			w.core.audioServer.play(SOUND_INPUTSELECTED);
			w.inputUsed(this, key);
			return true;
		}
		if (key.equals("right")) // right
		{
			w.core.audioServer.play(SOUND_INPUTSELECTED);
			w.inputUsed(this, key);
			return true;
		}
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("--- "+id+" "+key);
		return false;
	}
	@Override
	public boolean handleMouse(UiMouseEvent mouseEvent)
	{
		if(mouseEvent.getEventType()== UiMouseEventType.MOUSE_PRESSED && mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_LEFT))
        {
    		return handleKey("enter");
        }
		if(mouseEvent.getEventType()== UiMouseEventType.MOUSE_PRESSED && mouseEvent.isButtonPressed(UiMouseEvent.BUTTON_RIGHT))
        {
    		return handleKey("right");
        }
		if (mouseEvent.getEventType()==UiMouseEventType.MOUSE_ENTERED)
		{
			return w.inputEntered(this, ""+selectedValue);
			
		}
		
		return false;
	}
	@Override
	public Node getDeactivatedNode() {
		return null;
	}
}
