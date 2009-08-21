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

import java.net.URL;

import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.RelativeMouse;
import com.jme.scene.Node;

/**
 * <code>MenuMouseHandler</code> defines an InputHandler that allows hit detection on menu objects
 * @author mkienenb
 */
public class UiMouseHandler extends InputHandler {

    private UiMouseAction uiMouseAction;

    
    public static final String URL_CURSOR_NORMAL = "file:data/cursor/cursor1.png";
    public static final String URL_CURSOR_UP = "file:data/cursor/cursor1Up.png";
    public static final String URL_CURSOR_DOWN = "file:data/cursor/cursor1Down.png";
    public static final String URL_CURSOR_LEFT = "file:data/cursor/cursor1Left.png";
    public static final String URL_CURSOR_RIGHT = "file:data/cursor/cursor1Right.png";
    
    public UiMouseHandler() {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.registerWithInputHandler( this );

        uiMouseAction = new UiMouseAction(mouse);
        addAction(uiMouseAction);
		org.lwjgl.input.Mouse.setGrabbed(true);
		MouseInput.get().setCursorVisible(true);
		try {
			MouseInput.get().setHardwareCursor(new URL(URL_CURSOR_NORMAL));
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
    
    public static void normalCursor()
    {
		try {
			MouseInput.get().setHardwareCursor(new URL(URL_CURSOR_NORMAL));
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
    public static void cursorUp()
    {
		try {
			MouseInput.get().setHardwareCursor(new URL(URL_CURSOR_UP),-5,-5);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
    public static void cursorDown()
    {
		try {
			MouseInput.get().setHardwareCursor(new URL(URL_CURSOR_DOWN),-5,-5);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
    public static void cursorRight()
    {
		try {
			MouseInput.get().setHardwareCursor(new URL(URL_CURSOR_RIGHT),-5,-5);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
    public static void cursorLeft()
    {
		try {
			MouseInput.get().setHardwareCursor(new URL(URL_CURSOR_LEFT),-5,-5);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
    }
    
    public UiMouseAction getUiMouseAction() {
        return uiMouseAction;
    }

	public void setRootNode(Node rootNode) {
		uiMouseAction.setRootNode(rootNode);
	}
	public void setSecondaryFocusNode(Node node) {
		uiMouseAction.setSecondaryFocusNode(node);
	}
}
