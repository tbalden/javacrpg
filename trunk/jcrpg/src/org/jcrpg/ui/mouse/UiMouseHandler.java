/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2009
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

package org.jcrpg.ui.mouse;

import com.jme.input.InputHandler;
import com.jme.input.RelativeMouse;
import com.jme.scene.Node;

/**
 * <code>MenuMouseHandler</code> defines an InputHandler that allows hit detection on menu objects
 * @author mkienenb
 */
public class UiMouseHandler extends InputHandler {

    private UiMouseAction uiMouseAction;

    public UiMouseHandler() {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.registerWithInputHandler( this );

        uiMouseAction = new UiMouseAction(mouse);
        addAction(uiMouseAction);
    }
    
    public UiMouseAction getUiMouseAction() {
        return uiMouseAction;
    }

	public void setRootNode(Node rootNode) {
		uiMouseAction.setRootNode(rootNode);
	}
}
