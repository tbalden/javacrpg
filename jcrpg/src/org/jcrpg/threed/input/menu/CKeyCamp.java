/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.threed.input.menu;

import org.jcrpg.apps.Jcrpg;
import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;

public class CKeyCamp extends KeyInputAction{


	ClassicKeyboardLookHandler handler;
	J3DCore core;
	public CKeyCamp(ClassicKeyboardLookHandler handler)
	{
		this.handler = handler;
		core = handler.core;
	}
	
	long lastPerformTime = 0;
	
	public void performAction(InputActionEvent evt) {
		if (lastPerformTime-System.currentTimeMillis()>-500)
		{
			return;
		}
		lastPerformTime = System.currentTimeMillis();
		
		if (J3DCore.LOGGING()) Jcrpg.LOGGER.finest("### CAMP KEY");
		core.gameState.switchCamping();
	}
	

}
