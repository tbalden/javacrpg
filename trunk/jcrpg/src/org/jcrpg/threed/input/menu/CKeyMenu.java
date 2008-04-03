/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.threed.input.menu;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;

public class CKeyMenu extends KeyInputAction{


	ClassicKeyboardLookHandler handler;
	J3DCore core;
	public CKeyMenu(ClassicKeyboardLookHandler handler)
	{
		this.handler = handler;
		core = handler.core;
	}
	public void performAction(InputActionEvent evt) {
		handler.eventCatched = false;
		String event = evt.getTriggerName();
		if (!core.uiBase.handleWindowEvent(event))
		{
			core.uiBase.handleEvent(event);
		}
	}
	

}
